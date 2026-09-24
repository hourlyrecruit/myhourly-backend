# MyHourly Backend — DigitalOcean Deployment Guide

Backend-only deployment. The frontend lives in its own repository and is deployed
independently; neither deployment can break the other.

```
GitHub  hourlyrecruit/myhourly-backend  (branch: main)
   │
   │  push to main
   ▼
GitHub Actions  ── build + tests (PostgreSQL service container) ──►  gate
   │  only if green
   │  SSH (dedicated deploy user)
   ▼
DigitalOcean Droplet
   /opt/myhourly/backend     <- git checkout, pulled by the pipeline
   /opt/myhourly/docker-compose.yml
   │
   │  docker compose up -d --build backend   (image built ON the Droplet)
   ▼
Spring Boot container  myhourly-backend   (Java 21, port 8080)
   │  Flyway runs db/migration on startup
   ▼
DigitalOcean Managed PostgreSQL   +   DigitalOcean Spaces (file storage)
```

There is **no image registry** in this flow — no DigitalOcean Container Registry
and no Docker Hub. The image is built on the Droplet from the pulled source.

> **Reading order.** If you are deploying for the first time, follow §2 → §8.
> If a deploy is failing, jump to §13.

---

## Contents

1. [Required DigitalOcean resources](#1-required-digitalocean-resources)
2. [Required server software](#2-required-server-software)
3. [Directory structure](#3-directory-structure)
4. [Environment variables](#4-environment-variables)
5. [GitHub secrets](#5-github-secrets)
6. [SSH setup](#6-ssh-setup)
7. [Docker setup](#7-docker-setup)
8. [Docker Compose setup](#8-docker-compose-setup)
9. [Flyway behaviour](#9-flyway-behaviour)
10. [GitHub Actions deployment flow](#10-github-actions-deployment-flow)
11. [Verification, logs, restart](#11-verification-logs-restart)
12. [Rollback](#12-rollback)
13. [Common deployment errors](#13-common-deployment-errors)
14. [Security considerations](#14-security-considerations)
15. [What changed in this repository](#15-what-changed-in-this-repository)

---

## 1. Required DigitalOcean resources

| Resource | Purpose | Notes |
|---|---|---|
| **Droplet** | Runs the backend container | 2 vCPU / 4 GB RAM minimum. The Maven build stage runs on the Droplet but not on the RAM of a small Droplet — the runtime JVM is capped at 75 % of container RAM, so 2 GB is enough to *run* the app, but *building* on a 1 GB Droplet can be killed by the OOM killer. Prefer `s-2vcpu-4gb`. |
| **Managed PostgreSQL** | Production database | Add the Droplet as a **Trusted Source**. Not a container — see §8. |
| **Spaces bucket** | File storage (employee photos, logos, attachments) | Keep it **private**. The app serves files through 15-minute presigned URLs. |
| **Spaces access key** | S3-compatible credentials | Spaces → **API** → Access Keys. Separate from your DigitalOcean API token. |
| **Reserved IP** *(optional)* | Stable address for DNS | Recommended if the frontend or DNS points at the Droplet. |

---

## 2. Required server software

The Droplet should run Ubuntu 24.04 with:

* Docker Engine (with the Compose v2 plugin, i.e. `docker compose`, not `docker-compose`)
* Git
* UFW (or equivalent)

Initial setup, as root:

```bash
# Docker Engine + Compose plugin
curl -fsSL https://get.docker.com | sh

# Git
apt-get update && apt-get install -y git

# Firewall: only SSH needs to be reachable from the internet.
# The backend is published on loopback only (see §8).
ufw allow OpenSSH
ufw --force enable
ufw status
```

Nothing else is required. There is no PostgreSQL, no Java, and no Maven on the
host — the JVM and Maven both come from the Docker build images.

---

## 3. Directory structure

```text
/opt/myhourly/
├── docker-compose.yml          # server-side only, NOT in this repository (see §8)
├── backend/                    # git clone of hourlyrecruit/myhourly-backend
│   ├── .env                    # production secrets, never committed
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
└── frontend/                   # separate repository, deployed independently
```

Create it:

```bash
useradd --create-home --shell /bin/bash deploy
usermod -aG docker deploy          # lets the pipeline run docker without sudo

mkdir -p /opt/myhourly
chown -R deploy:deploy /opt/myhourly

su - deploy
git clone https://github.com/hourlyrecruit/myhourly-backend.git /opt/myhourly/backend
cd /opt/myhourly/backend
git checkout main
```

The pipeline expects `/opt/myhourly/backend` to be a checkout of this repository
on branch `main`, and `/opt/myhourly/backend/.env` to exist.

---

## 4. Environment variables

All production configuration is read from `/opt/myhourly/backend/.env`, which
Docker Compose loads through `env_file`. **This file must never be committed** —
`.gitignore` now blocks `.env`, `.env.*` and `*.env`.

The application is started with `SPRING_PROFILES_ACTIVE=prod`, so
`application-prod.properties` is the profile in effect. The variable names below
are the ones that file actually reads; nothing is invented.

### 4.1 Required

The application **fails to start** with
`Could not resolve placeholder '<NAME>'` if any of these is missing.

| Variable | Property | Notes |
|---|---|---|
| `DATABASE_URL` | `spring.datasource.url` | JDBC URL from the database's *Connection Details* panel. Keep `?sslmode=require` — Managed PostgreSQL requires TLS. |
| `DATABASE_USER` | `spring.datasource.username` | e.g. `doadmin` |
| `DATABASE_PASSWORD` | `spring.datasource.password` | **Secret** |
| `JWT_SECRET` | `jwt.secret` | **Secret.** 64+ random characters. Rotating it invalidates every issued token, so everyone is signed out. |
| `DO_SPACES_BUCKET` | `b2.bucket` | Spaces bucket name. Must be DNS-compatible (lowercase, no underscores) or presigned URLs cannot resolve. |
| `DO_SPACES_KEY` | `b2.access-key` | **Secret** — Spaces access key |
| `DO_SPACES_SECRET` | `b2.secret-key` | **Secret** — Spaces secret key |
| `PASSWORD_RESET_FRONTEND_URL` | `app.password-reset.frontend-url` | Public HTTPS page of the frontend, e.g. `https://app.example.com/reset-password`. A wrong value makes emailed reset links unusable. |
| `PASSWORD_RESET_EMAIL` | `app.password-reset.from` | From address on reset emails. Must be a mailbox your SMTP provider will send as. |

`DO_SPACES_*` map onto properties still named `b2.*` in the code
(`B2StorageConfig`, `B2FileStorageServiceImpl`). That prefix is historical — the
client has always spoken the S3 API, and Spaces is S3-compatible, so no code
change was needed. There is **no Backblaze dependency** in the `prod` profile.

### 4.2 Optional

Every one of these has a working default. Set them only to override.

| Variable | Default | Purpose |
|---|---|---|
| `DO_SPACES_REGION` | `blr1` | Spaces region slug (`blr1`, `nyc3`, `sgp1`, `fra1`, `ams3`, `sfo3`, `syd1`). **Must match the bucket's region** — the SigV4 signature covers it. |
| `DO_SPACES_ENDPOINT` | `https://<DO_SPACES_REGION>.digitaloceanspaces.com` | Override only for a CDN or custom host. |
| `DO_SPACES_PUBLIC_URL` | *(empty)* | Not read by any code; kept for parity with a CDN URL. |
| `ACCESS_TOKEN_EXPIRATION` | `9000000` (2.5 h) | Access token lifetime in ms |
| `REFRESH_TOKEN_EXPIRATION` | `604800000` (7 d) | Refresh token lifetime in ms |
| `HR_EMAIL` | `hr@company.com` | Recipient of leave/attendance notifications |
| `SMTP_HOST` | `smtp.gmail.com` | Mail relay |
| `SMTP_PORT` | `587` | STARTTLS |
| `SMTP_USERNAME` | `hourlyrecruittechlabs@gmail.com` | **Set this.** The default is the development Gmail account. |
| `SMTP_PASSWORD` | *(empty)* | **Secret.** Empty means mail fails at send time (bounded by 5 s timeouts) rather than blocking startup. |
| `SMTP_STARTTLS_ENABLE` | `true` | Keep `true` for port 587 |
| `SMTP_SSL_ENABLE` | `false` | Set `true` only for port 465 |
| `PASSWORD_RESET_EXPIRATION` | `30` | Reset link lifetime in minutes |

### 4.3 Supplied by Compose, not by `.env`

| Variable | Value | Why |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` | `application.properties` defaults to `dev`, which points at the **development Aiven database**. This must be `prod` in production. It is set in `docker-compose.yml` so the backend cannot accidentally start on the wrong profile. |
| `PORT` | `8080` | Container-internal port. Do not change; `docker-compose.yml` maps it. |

### 4.4 Creating the file

```bash
su - deploy
cd /opt/myhourly/backend
umask 077                                  # create it unreadable to others
cat > .env <<'EOF'
# --- Managed PostgreSQL -------------------------------------------------------
DATABASE_URL=jdbc:postgresql://<host>:25060/<database>?sslmode=require
DATABASE_USER=doadmin
DATABASE_PASSWORD=<password>

# --- Application -------------------------------------------------------------
JWT_SECRET=<64+ random characters>

# --- Frontend / email --------------------------------------------------------
PASSWORD_RESET_FRONTEND_URL=https://<frontend-domain>/reset-password
PASSWORD_RESET_EMAIL=noreply@<your-domain>
SMTP_USERNAME=<smtp user>
SMTP_PASSWORD=<smtp password>

# --- DigitalOcean Spaces -----------------------------------------------------
DO_SPACES_BUCKET=<bucket>
DO_SPACES_REGION=blr1
DO_SPACES_KEY=<spaces access key>
DO_SPACES_SECRET=<spaces secret key>
EOF
chmod 600 .env
```

Generate a JWT secret with:

```bash
openssl rand -base64 64 | tr -d '\n'; echo
```

---

## 5. GitHub secrets

Add these under **Settings → Secrets and variables → Actions** on
`hourlyrecruit/myhourly-backend`. They are referenced by
`.github/workflows/deploy.yml`.

| Secret | Required | Value |
|---|---|---|
| `DROPLET_HOST` | Yes | Droplet IP or hostname |
| `DROPLET_USER` | Yes | `deploy` — the dedicated deployment user, **not** `root` |
| `DROPLET_SSH_KEY` | Yes | Full contents of the private key, including the `-----BEGIN`/`-----END` lines |
| `DROPLET_PORT` | No | SSH port; defaults to `22` when unset |

The pipeline never prints these values. The deploy step only ever echoes the
short commit SHA.

---

## 6. SSH setup

Generate a key pair **for deployment only**, on your workstation:

```bash
ssh-keygen -t ed25519 -C "github-actions-deploy" -f ~/.ssh/myhourly_deploy -N ""
```

Install the public key on the Droplet for the `deploy` user:

```bash
# from your workstation
ssh root@<droplet-ip> "install -d -m 700 -o deploy -g deploy /home/deploy/.ssh"
ssh root@<droplet-ip> "cat >> /home/deploy/.ssh/authorized_keys" < ~/.ssh/myhourly_deploy.pub
ssh root@<droplet-ip> "chown deploy:deploy /home/deploy/.ssh/authorized_keys && chmod 600 /home/deploy/.ssh/authorized_keys"
```

Verify before touching GitHub — this is the exact authentication the pipeline uses:

```bash
ssh -i ~/.ssh/myhourly_deploy -o IdentitiesOnly=yes deploy@<droplet-ip> 'docker ps && cd /opt/myhourly/backend && git remote -v'
```

Then paste the **private** key into the `DROPLET_SSH_KEY` secret:

```bash
cat ~/.ssh/myhourly_deploy
```

The workflow pins the host key with `ssh-keyscan` and connects with
`StrictHostKeyChecking=yes`, so a changed host key fails the deploy instead of
silently connecting elsewhere.

---

## 7. Docker setup

`Dockerfile` is a two-stage build:

* **Build stage** — `maven:3.9.9-eclipse-temurin-21`. It copies `pom.xml` first and
  runs `mvn dependency:go-offline` so dependency downloads are their own cached
  layer; only `src/` changes invalidate the build. Tests are skipped here because
  GitHub Actions runs them first.
* **Runtime stage** — `eclipse-temurin:21-jre` (based on `ubuntu:24.04`). Maven and
  the JDK do **not** ship to production. The app runs as a non-root `spring` user,
  and `EXPOSE 8080` matches the application's port.

The base image already sets `ENTRYPOINT ["/__cacert_entrypoint.sh"]`, which keeps
the JDK truststore in sync and `exec`s the `CMD`. That is deliberate: the JVM stays
PID 1, so `docker stop` delivers `SIGTERM` and Spring shuts down gracefully. It is
also what keeps the TLS connection to Managed PostgreSQL healthy.

Runtime JVM flags (heap cap, GC, timezone) can be added without rebuilding by
setting `JAVA_TOOL_OPTIONS`, which the JVM reads natively:

```bash
JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60 -Xss512k"
```

---

## 8. Docker Compose setup

The Compose file lives at **`/opt/myhourly/docker-compose.yml`** — on the server,
next to (not inside) the backend checkout, because it also coordinates the
frontend container. It is intentionally **not committed to this repository**: it
references a sibling directory from a different repository and is server-specific.
The deploy pipeline fails with a clear message if it is missing.

```yaml
name: myhourly

services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    image: myhourly-backend:local
    container_name: myhourly-backend
    env_file:
      - ./backend/.env
    environment:
      # application.properties defaults to the `dev` profile, which points at the
      # shared development database. Pin it here.
      SPRING_PROFILES_ACTIVE: prod
    ports:
      # Loopback only: reachable by a reverse proxy on the host, not from the
      # internet. Change the left-hand port if 8081 is taken.
      - "127.0.0.1:8081:8080"
    restart: unless-stopped

  # The frontend is a separate repository deployed independently. If you later run
  # it as a service in this file, it reaches the backend at http://backend:8080
  # over the default compose network - no host port needed.

# No PostgreSQL service: production uses DigitalOcean Managed PostgreSQL.
```

Apply it:

```bash
su - deploy
cd /opt/myhourly
docker compose up -d --build backend
docker compose ps
```

Notes:

* **No `postgres` service and no `frontend` service.** PostgreSQL is managed and
  external; the frontend is a separate deployment.
* Deploys run `docker compose up -d --build backend`, never `docker compose down`.
  Only `myhourly-backend` is recreated, so a frontend container keeps running.
* `container_name: myhourly-backend` is required — the deploy pipeline uses it for
  the readiness check and log tail.

---

## 9. Flyway behaviour

The schema is owned by Flyway. Migrations live in
`src/main/resources/db/migration/` and run **automatically at application startup**
inside the container:

| Version | Description |
|---|---|
| `V1__baseline_schema.sql` | Baseline schema. Skipped on databases that existed before Flyway was introduced (they are baselined at version 1). |
| `V2__payrolls_date_of_joining_and_template_constraint.sql` | Schema repairs `ddl-auto=update` had silently skipped. Written defensively (`add column if not exists`). |
| `V3__add_unique_employee_code.sql` | Unique constraint on `employees.employee_code` |
| `V4__change_employee_type_to_string.sql` | Widens `salary_templates.employee_type` |
| `V5__form16_table_suite.sql` | **Added by this change.** Creates the 10 missing Form 16 tables. See below. |

Profiles run `spring.jpa.hibernate.ddl-auto=validate`, so a successful start proves
the migrations and the entities agree. Any drift fails startup loudly.

### Why V5 existed

`V1` was generated from the entities before the `form_16` / `Form_16A` modules were
written, and no migration was ever added for them. Those 10 tables only ever
existed in development, created by `ddl-auto=update`.

The consequence on a brand-new database — which is exactly what a fresh Managed
PostgreSQL instance is — was that Flyway finished at V4 and Hibernate then aborted
startup with:

```
org.hibernate.tool.schema.spi.SchemaManagementException: Schema validation: missing table [form16]
```

So a first production deployment could not boot until V5 existed.

**Verified:** against an empty PostgreSQL 18 database, Flyway applied V1–V5
(`Successfully applied 5 migrations, now at version v5`), 41 tables and all 9
`form16` foreign keys were created, Hibernate validation passed and the application
started — with no schema errors.

### First deploy to an *existing* database

An empty database needs nothing special. A database that already has the schema
(e.g. the legacy development database) is **baselined** instead of migrated:
`spring.flyway.baseline-on-migrate=true` and `baseline-version=1` mark V1 as applied
and then run V2–V5.

Those scripts are written to tolerate objects that already exist, with one
exception: **V3 is not idempotent** (`ADD CONSTRAINT` fails if the constraint is
already there). If the target database already has `uk_employee_employee_code`,
baseline past V1–V4 so only V5 runs:

```bash
# add to /opt/myhourly/backend/.env for that first boot only
SPRING_FLYWAY_BASELINE_VERSION=4
```

Flyway property overrides work as environment variables because Spring Boot's
relaxed binding maps `SPRING_FLYWAY_BASELINE_VERSION` onto
`spring.flyway.baseline-version`.

### Rules

* **Never edit a migration that has already been applied.** Add `V6`, `V7`, … instead.
* **Never switch `ddl-auto` to `update` in any environment.** It is `validate` on
  purpose.
* Migrations run inside the container's startup. If a migration fails, the
  container exits and the previous container keeps serving until the new one is
  healthy-ish — the pipeline fails the deploy (see §11).

---

## 10. GitHub Actions deployment flow

`.github/workflows/deploy.yml` runs on **push to `main`** (and can be triggered
manually with *Run workflow*).

```
job: test                                  job: deploy (needs: test)
─────────────────────────────              ──────────────────────────────────────
checkout                                   checkout
setup Java 21 (temurin, maven cache)       write SSH key, chmod 600
start postgres:18 service container        ssh-keyscan -> known_hosts
./mvnw -B -ntp verify                      ssh deploy@droplet:
   └─ fails => deploy never runs              git fetch/checkout/pull --ff-only main
                                              verify .env and docker-compose.yml exist
                                              docker compose up -d --build backend
                                              poll `docker logs` for
                                                "Started MyHourlyApplication"
                                              docker ps + last 25 log lines
```

Two details worth knowing:

* The test job starts a **throwaway `postgres:18` container**, because
  `MyHourlyApplicationTests` is a full `@SpringBootTest` that boots the whole
  context. `application-dev.properties` hard-codes the shared Aiven database, so
  the job overrides it with `SPRING_DATASOURCE_URL` /
  `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`, which outrank any
  properties file. Tests therefore run against a scratch database and never touch
  the shared one. Verified locally: 22 tests, 0 failures.
* `concurrency: deploy-backend` guarantees two deploys never interleave. It does not
  cancel an in-progress deploy, so a running rebuild is never left half-done.

---

## 11. Verification, logs, restart

### Check the container and the app

```bash
ssh deploy@<droplet-ip>

docker ps --filter name=myhourly-backend \
  --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'

docker logs --tail 100 myhourly-backend
```

In the logs, look for these three components:

```text
HikariPool-1 - Start completed.                              # database reachable
o.f.core.internal.command.DbMigrate : Successfully applied … # Flyway ran
com.my_hourly.MyHourlyApplication   : Started MyHourlyApplication in N s
```

Then confirm the app answers HTTP from the Droplet:

```bash
curl -s -o /dev/null -w "%{http_code}\n" http://127.0.0.1:8081/swagger-ui/index.html
```

`200` means the API is up. **Do not use `/actuator/health`** for this:
`SecurityConstants` whitelists it, but `spring-boot-starter-actuator` is **not** a
dependency of this project, so that path returns **500**, not 200. It is a
misleading health check and should not be wired into a load balancer or monitor.

If Swagger is later disabled in production (`springdoc.swagger-ui.enabled=false`),
use the log line `Started MyHourlyApplication` as the readiness signal — that is
what the deploy pipeline itself waits for.

### Confirm Flyway state

```bash
docker exec myhourly-backend sh -c 'echo "see logs"'   # no psql in the image
# From the Droplet, if psql is installed:
psql "$DATABASE_URL" -c "select installed_rank, version, description, success from flyway_schema_history order by 1;"
```

### Restart the backend only

```bash
cd /opt/myhourly
docker compose restart backend          # fast, keeps the current image
# or, to pick up a new image:
docker compose up -d --build backend
```

Never use `docker compose down` on the whole project — it stops the frontend too.

### Resource use

```bash
docker stats --no-stream myhourly-backend
docker compose logs --tail 100 backend
```

---

## 12. Rollback

Rollback is **redeploying an earlier commit**. Migrations are forward-only.

```bash
ssh deploy@<droplet-ip>
cd /opt/myhourly/backend
git log --oneline -10                  # find the last known-good commit

git checkout <good-commit-sha>
cd /opt/myhourly
docker compose up -d --build backend
docker logs --tail 50 myhourly-backend
```

To return to the tip afterwards:

```bash
cd /opt/myhourly/backend && git checkout main && git pull --ff-only origin main
```

### Database rollback limitation

**There is no automatic migration rollback, and V5 must not be undone.**

Because the deployed schema is shared, an older build runs against a database that
still contains everything newer migrations added. Two things follow:

* Going back to a revision **before** V5 does **not** fail. Verified on a database
  where V5 is applied but the running build has no V5: Flyway logged
  `Current version of schema "public": 5` and `Schema "public" is up to date`,
  Hibernate validation passed (extra tables are ignored), and the app started
  normally. The `form16_*` tables simply sit unused.
* Rolling back is only safe when the older build **still has the schema it needs**.
  A future migration that drops or narrows a column an older release depends on
  makes that release unusable, and no code rollback will fix it.

So:

* Prefer **rolling forward** with a fix commit.
* Roll back only to a commit whose required columns and tables still exist.
* Never edit or delete rows in `flyway_schema_history` to force a rollback.
* If a migration must be undone, write a **new forward migration** (e.g. `V6`) that
  reverses it.

---

## 13. Common deployment errors

| Symptom | Cause | Fix |
|---|---|---|
| `Could not resolve placeholder 'DATABASE_URL'` (or any other name) | Variable missing from `/opt/myhourly/backend/.env` | Add it. Placeholders **without** a default are required; the app names the missing one. |
| `Connection refused` / `Connection timed out` to the database | Droplet not a Trusted Source for the Managed Database | Add the Droplet (or its Reserved IP) under **Databases → <db> → Trusted Sources** |
| `FATAL: no pg_hba.conf entry` / SSL error | `sslmode` missing from `DATABASE_URL` | Append `?sslmode=require` |
| `Schema validation: missing table [...]` at startup | Migrations and entities disagree | This was the V5 bug — ensure `V5__form16_table_suite.sql` is present and applied. For any new drift, add a `V6` migration. |
| `Schema validation: missing column [...]` | An entity changed without a migration | Add a new forward migration; never switch to `ddl-auto=update` |
| `Detected applied migration not resolved locally` | A migration was deleted, renamed or edited after being applied | Restore the file. Do not delete history rows. |
| `Detected resolved migration not applied to database` | `out-of-order` state after a rollback | Deploy the newest code again, or set `SPRING_FLYWAY_OUT_OF_ORDER=true` |
| `V3__add_unique_employee_code.sql` fails with `constraint already exists` | Existing database baselined at V1 while the constraint was already present | Set `SPRING_FLYWAY_BASELINE_VERSION=4` for that first boot (§9) |
| `ERROR: relation "flyway_schema_history" does not exist` in a log grep | Harmless: grepping before Flyway ran | Ignore |
| Port `8081` already in use | Another service on the Droplet | Change the host side of the mapping in `docker-compose.yml` — leave container port `8080` alone |
| Deploy fails with `ERROR: /opt/myhourly/backend/.env is missing` | `.env` not created yet | See §4.4. Key permissions: `600`. |
| Deploy fails with `ERROR: /opt/myhourly/docker-compose.yml is missing` | Compose file not created | See §8 |
| `git pull --ff-only` fails: `Not possible to fast-forward` | The Droplet checkout has diverged (someone edited files there) | Inspect with `git status` / `git log`. Do not blindly `git reset --hard`; move the local change out of the way first. |
| Docker build fails: `COPY failed: file not found ... my_hourly-0.0.1-SNAPSHOT.jar` | The `<version>` in `pom.xml` was changed, so the JAR is named differently | Update the JAR filename in the `COPY --from=build` line of the `Dockerfile` (it is written explicitly so a stray artifact cannot be picked up) |
| Deploy times out waiting for `Started MyHourlyApplication` | Startup failure, or a slow first build | `docker logs --tail 100 myhourly-backend`. First builds take several minutes because images are pulled and dependencies are resolved; later builds are much faster thanks to the cached dependency layer. |
| Container restarts in a loop (`Restarting`) | Startup crash | `docker logs --tail 200 myhourly-backend` for the stack trace |
| Files 403 when downloading from the frontend | Presigned URL expired (15 min) or the Spaces key lacks read access | Re-request the download link; verify the Spaces key |
| Images upload but never load | Bucket not reachable / wrong region | `DO_SPACES_REGION` must match the bucket's region — the SigV4 signature covers it |
| Passwords reset emails never arrive | `SMTP_*` unset, so mail fails silently at send time | Set `SMTP_USERNAME` / `SMTP_PASSWORD`; check the 5-second timeout warnings in the logs |

### Noise that is not a failure

These appear in a healthy startup and can be ignored:

```
WARN  InitializeUserDetailsManagerConfigurer : Global AuthenticationManager configured with an AuthenticationProvider bean…
WARN  DefaultSqlScriptExecutor : DB: column "date_of_joining" of relation "payrolls" already exists, skipping
WARN  SpringDocAppInitializer : SpringDoc /v3/api-docs endpoint is enabled by default…
ERROR SchedulingConfig : Scheduled task failed  (ResourceNotFoundException: Attendance settings not found.)
```

The last one is a pre-existing startup race: the minute-interval scheduled job can
tick before `DataInitializer` has seeded `attendance_settings` on a brand-new
database. It self-corrects on the next tick. It is not caused by this deployment
setup and does not affect the running application.

---

## 14. Security considerations

**`.env` never goes into git.** It lives only at `/opt/myhourly/backend/.env`, mode
`600`. `.gitignore` blocks `.env`, `.env.*` and `*.env`.

**Rotate the seeded accounts before going live — this is the most important item
here.** `src/main/resources/seed/users.csv` ships inside the JAR and is committed
to this repository, and it contains plaintext passwords:

```csv
username,email,password,role,user_status
superadmin,superadmin@myhourly.com,Admin@1234,SUPER_ADMIN,ACTIVE
anagha,manager@myhourly.com,Manager@1234,MANAGER,ACTIVE
harshitha,Harshitha@gmail.com,Hr@1234,HR_ADMIN,ACTIVE
```

`SeedRunner` (an `ApplicationRunner`) calls `UserSeeder.seed()` on **every**
startup with no profile gate, and it inserts any user whose username does not yet
exist. On a fresh production database this creates three privileged accounts with
publicly-known passwords. Anyone who can read the repository can log in as
`SUPER_ADMIN`.

Mitigate before, or immediately after, the first production boot:

1. Do not expose the API publicly until the passwords are changed.
2. Change all three passwords through the application.
3. Delete or scrub `seed/users.csv`, or make `SeedRunner` conditional on a property
   (e.g. `@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")`)
   so seeding cannot run in production. Deleting the file without changing the
   seeding behaviour still stops the inserts, because `UserSeeder` iterates over
   the CSV.

This is pre-existing behaviour and was deliberately **not** changed here, because it
is application logic rather than deployment configuration — but it must not ship as
is.

**Other points**

* Use the dedicated `deploy` user, not `root`, for automated deployments. Adding it
  to the `docker` group is effectively root-equivalent for the Docker daemon, so
  keep the key restricted to the deployment workflow.
* Only SSH is exposed to the internet. The backend binds to `127.0.0.1:8081` and
  PostgreSQL is only reachable through Managed PostgreSQL's Trusted Sources.
* Keep the Spaces bucket private and leave Block Public Access ON. Downloads are
  served as 15-minute presigned URLs, which is why `s3:GetObject` is needed.
* Rotation impact: rotating `JWT_SECRET` signs everyone out; rotating
  `DATABASE_PASSWORD` requires a redeploy; rotating Spaces keys requires a redeploy
  and invalidates outstanding presigned URLs.
* **`spring.jpa.show-sql=true` is set in `application-prod.properties`.** It logs
  every SQL statement at INFO. That is log volume and noise in production, and it
  should be turned off (`show-sql=false`) — left as-is here because it is existing
  application configuration rather than a deployment concern.
* `Jenkinsfile` is retained deliberately. It still targets AWS ECR and is **not**
  part of this pipeline; make sure no Jenkins job builds `main` any more, or two
  systems will deploy the same application.
* The `aws` profile (`application-aws.properties`) is untouched and still aims at
  AWS RDS/SES/S3. `prod` is now the DigitalOcean profile.

---

## 15. What changed in this repository

### Files added

| File | Purpose |
|---|---|
| `.dockerignore` | Keeps `target/`, `.git/`, `HRMS/`, IDE files, docs and local secrets out of the build context. Nothing the Maven build or runtime needs is excluded. |
| `.github/workflows/deploy.yml` | CI (Java 21 + PostgreSQL 18 service container + `./mvnw verify`) then SSH deploy that rebuilds only `myhourly-backend`. No registry involved. |
| `src/main/resources/db/migration/V5__form16_table_suite.sql` | The 10 missing Form 16 tables. Without it a fresh managed database could not start. |
| `DEPLOYMENT.md` | This guide. |

### Files modified

| File | Why |
|---|---|
| `pom.xml` | Added `spring-boot-starter-flyway` and `flyway-database-postgresql`. **Flyway was not on the classpath at all** — the migration files and every `spring.flyway.*` setting were inert, so migrations had never run. Version managed by the Spring Boot 4.0.7 BOM; no libraries were otherwise introduced. |
| `src/main/resources/application-prod.properties` | Repointed storage from Backblaze B2 (`B2_*`) to DigitalOcean Spaces (`DO_SPACES_*`); the endpoint is derived from the region. Documented the required/optional variables. Gave the two token lifetimes and the reset-link lifetime defaults so a missing optional value cannot block startup. No property *names* changed, so the application code is untouched. |
| `Dockerfile` | Cached the dependency layer (previously `COPY . .` re-downloaded everything on every deploy), pinned the exact JAR name, run as a non-root `spring` user, added container-aware JVM flags. The base images and port are unchanged. |
| `.gitignore` | Blocks `.env`, `.env.*` and `*.log` so production secrets cannot be committed. |

### Files intentionally unchanged

| File | Reason |
|---|---|
| `Jenkinsfile` | Not asked for, and it is still a valid (if now unused) pipeline. Nothing in the new flow depends on it. |
| `V1`–`V4` migrations | Already applied to existing databases. Editing them would break every environment. |
| `application.properties` | Already externalises the profile (`SPRING_PROFILES_ACTIVE`) and port (`PORT`), and already carries the Flyway baseline settings. No change was needed. |
| `application-dev.properties` | Development-only. It does still contain a live Aiven database password, B2 keys and a Gmail app password **committed to the repository** — worth rotating and externalising, but changing it would break local development for the team, so it was left alone. |
| `application-aws.properties` | AWS-specific; irrelevant to DigitalOcean. |
| All `src/main/java/**` | No business logic, entity, API contract, security or calculation code was modified. |
| `docker-compose.yml` | Does not exist in this repository by design — it belongs to `/opt/myhourly` on the server (§8). |

### Not done, on purpose

* No DOCR, no Docker Hub, no Kubernetes, no Jenkins changes.
* No `postgres` service in Compose — the database is managed and external.
* No new file-storage module — the existing S3-compatible client was already
  suitable for Spaces.
* No actuator added: the project does not have it, and it was not needed. The
  deploy pipeline waits on the application's own startup log line instead.
