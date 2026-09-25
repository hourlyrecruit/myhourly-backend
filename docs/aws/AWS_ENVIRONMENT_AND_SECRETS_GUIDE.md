# AWS Environment & Secrets Guide

Configuration reference for deploying this backend on AWS with the **`aws`** Spring profile:
Amazon RDS for PostgreSQL, Amazon SES for email, Amazon S3 for file storage.

| Item | Value |
|---|---|
| Source of truth | `src/main/resources/application-aws.properties` |
| Base file (always loaded) | `src/main/resources/application.properties` |
| Activate with | `SPRING_PROFILES_ACTIVE=aws` |
| Companion doc | [`AWS_DEPLOYMENT_GUIDE.html`](AWS_DEPLOYMENT_GUIDE.html) — infrastructure/CLI walkthrough |
| Runtime | Spring Boot 4.0.7, Java 21, Tomcat on port 8080 |

> `AWS_DEPLOYMENT_GUIDE.html` describes the older **`prod`** profile, which reads the same settings
> from **`B2_*`** environment variables (`B2_BUCKET_NAME`, `B2_ACCESS_KEY`, …). The `aws` profile
> uses **`AWS_*`** names instead. The two profiles are not interchangeable — pick one and use the
> variable names from that profile's table below.

---

## 1. How configuration is assembled

```
application.properties          always loaded  → app name, profile switch, port, multipart, Flyway baseline
└── application-aws.properties  profile "aws"  → RDS, JWT, SES, password reset, S3
```

Two rules govern every value in the AWS profile:

1. **`${ENV_VAR:default}`** — if the environment variable is absent, the default is used.
2. **`${ENV_VAR}`** — no default means **required**. The application refuses to start and logs
   `Could not resolve placeholder 'ENV_VAR'`. Values are checked one at a time, so you may need to
   add variables in several passes before the context comes up.

Injection happens through the process environment. There is no `.env` loading: `dotenv-java` is on
the classpath but nothing in the code calls it.

**Minimal working set** — these seven are enough to boot and serve traffic:

```
SPRING_PROFILES_ACTIVE=aws
DATABASE_URL  DATABASE_USER  DATABASE_PASSWORD
JWT_SECRET
AWS_S3_BUCKET  AWS_ACCESS_KEY_ID  AWS_SECRET_ACCESS_KEY
```

Everything else falls back to a default. Mail is the important exception: with no `SMTP_USERNAME` /
`SMTP_PASSWORD` the app boots, but every send fails at runtime.

---

## 2. Variables inherited from `application.properties`

| Env var | Property | Required | Default | AWS value |
|---|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `spring.profiles.active` | **Yes** | `dev` | `aws` |
| `PORT` | `server.port` | No | `8080` | `8080` (keep the container port at 8080) |

Also fixed in that base file and *not* env-driven: `spring.servlet.multipart.max-file-size` and
`max-request-size` (both 100MB), `spring.flyway.baseline-on-migrate=true`,
`spring.flyway.baseline-version=1`, and the seeded `app.super-admin.*` / `app.manager.*` /
`app.hr.*` values.

---

## 3. Required by the `aws` profile

| Env var | Property | Notes |
|---|---|---|
| `DATABASE_URL` | `spring.datasource.url` | JDBC URL **including `?sslmode=require`** — so the connection is encrypted even if `rds.force_ssl` is off |
| `DATABASE_USER` | `spring.datasource.username` | RDS master user |
| `DATABASE_PASSWORD` | `spring.datasource.password` | **Secret** |
| `JWT_SECRET` | `jwt.secret` | **Secret** — at least 64 random characters |
| `AWS_S3_BUCKET` | `b2.bucket` | Bucket name |
| `AWS_ACCESS_KEY_ID` | `b2.access-key` | **Secret** — IAM user scoped to the bucket |
| `AWS_SECRET_ACCESS_KEY` | `b2.secret-key` | **Secret** |

---

## 4. Optional variables (defaults supplied)

| Env var | Property | Default | Purpose |
|---|---|---|---|
| `AWS_REGION` | — derives three values | `ap-south-1` | Master region knob: fills `b2.region` and `SMTP_HOST` |
| `AWS_S3_REGION` | `b2.region` | `AWS_REGION` | Bucket region, when it differs from SES |
| `AWS_S3_ENDPOINT` | `b2.endpoint` | `https://s3.<region>.amazonaws.com` | VPC endpoint or custom host only |
| `AWS_S3_PUBLIC_URL` | `b2.public-url` | empty | Not read by any code; kept for parity |
| `SUPER_ADMIN_EMAIL` | `app.super-admin.email` | `superadmin@myhourly.com` | Seeding is currently commented out; the variable only satisfies the placeholder |
| `ACCESS_TOKEN_EXPIRATION` | `jwt.access-token-expiration` | `9000000` (2.5 h) | Milliseconds |
| `REFRESH_TOKEN_EXPIRATION` | `jwt.refresh-token-expiration` | `604800000` (7 d) | Milliseconds |
| `HR_EMAIL` | `hr.email` | `hr@company.com` | Recipient of leave / attendance-regularization notifications |
| `SMTP_HOST` | `spring.mail.host` | `email-smtp.<region>.amazonaws.com` | SES SMTP endpoint |
| `SMTP_PORT` | `spring.mail.port` | `587` | STARTTLS port |
| `SMTP_USERNAME` | `spring.mail.username` | empty | **Secret** — SES SMTP user (per region) |
| `SMTP_PASSWORD` | `spring.mail.password` | empty | **Secret** |
| `SMTP_STARTTLS_ENABLE` | `mail.smtp.starttls.enable` | `true` | Keep `true` on 587 |
| `SMTP_SSL_ENABLE` | `mail.smtp.ssl.enable` | `false` | Set `true` only for port 465 |
| `PASSWORD_RESET_EXPIRATION` | `app.password-reset.token-expiration-minutes` | `30` | Minutes |
| `PASSWORD_RESET_FRONTEND_URL` | `app.password-reset.frontend-url` | `http://localhost:3000/reset-password` | Must be the real HTTPS page |
| `PASSWORD_RESET_EMAIL` | `app.password-reset.from` | `noreply@myhourly.com` | Must be a verified SES identity |

`AWS_REGION` is the single knob for a same-region deployment: changing it moves S3 and SES together.
`AWS_S3_REGION` / `AWS_S3_ENDPOINT` exist only for the case where the bucket lives elsewhere.

---

## 5. AWS Secrets Manager

Four values (plus the SES pair) are genuinely sensitive. They are split into four secrets so that
rotating one service never touches another.

| Secret name | JSON keys | Service |
|---|---|---|
| `myhourly/db` | `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` | RDS |
| `myhourly/jwt` | `JWT_SECRET` | Token signing |
| `myhourly/storage` | `AWS_S3_BUCKET`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` | Uploads + presigned URLs |
| `myhourly/ses` | `SMTP_USERNAME`, `SMTP_PASSWORD` | Password reset and notification email |

```bash
aws secretsmanager create-secret --name myhourly/db \
  --description "RDS connection for the MyHourly backend" \
  --secret-string '{
    "DATABASE_URL":      "jdbc:postgresql://myhourly.xxxx.ap-south-1.rds.amazonaws.com:5432/myhourly?sslmode=require",
    "DATABASE_USER":     "myhourly_app",
    "DATABASE_PASSWORD": "<password>"
  }'

aws secretsmanager create-secret --name myhourly/jwt \
  --secret-string '{"JWT_SECRET":"<64+ random characters>"}'

aws secretsmanager create-secret --name myhourly/storage \
  --secret-string '{
    "AWS_S3_BUCKET":         "myhourly-files",
    "AWS_ACCESS_KEY_ID":     "AKIA...",
    "AWS_SECRET_ACCESS_KEY": "<secret>"
  }'

aws secretsmanager create-secret --name myhourly/ses \
  --secret-string '{
    "SMTP_USERNAME": "<SES SMTP user, NOT an IAM access key>",
    "SMTP_PASSWORD": "<SES SMTP password>"
  }'
```

Generate the JWT secret without shell history noise:

```bash
openssl rand -base64 48
```

### Task execution role policy

The ECS agent, not the application, reads these secrets, so the **execution** role needs access:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": "secretsmanager:GetSecretValue",
      "Resource": [
        "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/db-*",
        "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/jwt-*",
        "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/storage-*",
        "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/ses-*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": "kms:Decrypt",
      "Resource": "arn:aws:kms:ap-south-1:ACCOUNT_ID:key/<default-secrets-key-id>"
    }
  ]
}
```

The `-*` suffix is required: Secrets Manager appends a six-character version hash to the ARN.

### What must NOT go into Secrets Manager

Secrets Manager bills per secret and per 10k API calls. Keep this in the task definition's plain
`environment` array:

```
SPRING_PROFILES_ACTIVE=aws        PORT=8080                 AWS_REGION=ap-south-1
SUPER_ADMIN_EMAIL=...             HR_EMAIL=...              SMTP_PORT=587
ACCESS_TOKEN_EXPIRATION=9000000   REFRESH_TOKEN_EXPIRATION=604800000
SMTP_STARTTLS_ENABLE=true         SMTP_SSL_ENABLE=false
PASSWORD_RESET_EXPIRATION=30      PASSWORD_RESET_FRONTEND_URL=https://...
PASSWORD_RESET_EMAIL=...
```

For non-secret settings that still need to differ per environment, SSM Parameter Store standard
parameters are free and can be referenced with the same `valueFrom` syntax. Leave `AWS_S3_ENDPOINT`,
`AWS_S3_REGION` and `AWS_S3_PUBLIC_URL` unset unless a specific need exists.

### Rotation notes

| Secret | Rotation impact |
|---|---|
| `JWT_SECRET` | Every issued access and refresh token becomes invalid — all users are signed out. Rotate during a maintenance window. |
| `DATABASE_PASSWORD` | Hikari retires connections after `max-lifetime` (30 min), so a rolling restart is the reliable way to pick up a new password. |
| `AWS_SECRET_ACCESS_KEY` | Restart the service; the S3 client holds static credentials created at startup and never re-reads them. |
| `SMTP_PASSWORD` | Same — restart required. SES SMTP passwords can be regenerated per region at any time. |

---

## 6. ECS task definition wiring

`environment` is plaintext; `secrets` is resolved by the execution role at task start. Note the
`:KEY::` suffix that selects one key out of the secret's JSON.

```json
{
  "name": "myhourly-backend",
  "image": "ACCOUNT_ID.dkr.ecr.ap-south-1.amazonaws.com/myhourly:latest",
  "essential": true,
  "portMappings": [{ "containerPort": 8080, "protocol": "tcp" }],
  "environment": [
    { "name": "SPRING_PROFILES_ACTIVE",       "value": "aws" },
    { "name": "PORT",                         "value": "8080" },
    { "name": "AWS_REGION",                   "value": "ap-south-1" },
    { "name": "SUPER_ADMIN_EMAIL",            "value": "superadmin@yourdomain.com" },
    { "name": "HR_EMAIL",                     "value": "hr@yourdomain.com" },
    { "name": "PASSWORD_RESET_FRONTEND_URL",  "value": "https://app.yourdomain.com/reset-password" },
    { "name": "PASSWORD_RESET_EMAIL",         "value": "noreply@yourdomain.com" }
  ],
  "secrets": [
    { "name": "DATABASE_URL",          "valueFrom": "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/db:DATABASE_URL::" },
    { "name": "DATABASE_USER",         "valueFrom": "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/db:DATABASE_USER::" },
    { "name": "DATABASE_PASSWORD",     "valueFrom": "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/db:DATABASE_PASSWORD::" },
    { "name": "JWT_SECRET",            "valueFrom": "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/jwt:JWT_SECRET::" },
    { "name": "AWS_S3_BUCKET",         "valueFrom": "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/storage:AWS_S3_BUCKET::" },
    { "name": "AWS_ACCESS_KEY_ID",     "valueFrom": "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/storage:AWS_ACCESS_KEY_ID::" },
    { "name": "AWS_SECRET_ACCESS_KEY", "valueFrom": "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/storage:AWS_SECRET_ACCESS_KEY::" },
    { "name": "SMTP_USERNAME",         "valueFrom": "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/ses:SMTP_USERNAME::" },
    { "name": "SMTP_PASSWORD",         "valueFrom": "arn:aws:secretsmanager:ap-south-1:ACCOUNT_ID:secret:myhourly/ses:SMTP_PASSWORD::" }
  ]
}
```

### IAM user policy for S3

The storage client is built from static credentials (`StaticCredentialsProvider`), so the IAM user
whose keys land in `myhourly/storage` needs exactly this:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:PutObject", "s3:GetObject"],
      "Resource": "arn:aws:s3:::myhourly-files/*"
    }
  ]
}
```

`GetObject` is required because every upload returns a **presigned GET URL** (valid 15 minutes);
without the permission the URLs are generated but rejected on use. No `s3:ListBucket`, no public
bucket, Block Public Access should stay **on**. Using the ECS task role instead of static keys
requires a code change to `B2StorageConfig`, which currently always builds credentials from
`b2.access-key` / `b2.secret-key`.

### Network requirements

| From | To | Port | Why |
|---|---|---|---|
| ECS task SG | RDS SG | 5432 | Database |
| ECS task SG | `email-smtp.<region>.amazonaws.com` | 587 | SES SMTP — SES has no VPC interface endpoint for SMTP, so this needs NAT or internet egress |
| ECS task SG | `s3.<region>.amazonaws.com` | 443 | Uploads and presigned URLs (or use an S3 gateway endpoint) |
| ALB SG | ECS task SG | 8080 | Traffic |

### ALB health check

Point the target group at **`/swagger-ui/index.html`** (HTTP 200).

`/actuator/health` is listed in `SecurityConstants.PUBLIC_ENDPOINTS`, but
`spring-boot-starter-actuator` is **not** on the classpath, so that path returns 404. Adding
actuator is the better long-term fix, since it also survives the next point.

⚠️ `/swagger-ui/**` and `/v3/api-docs/**` are publicly reachable on every environment by design of
that whitelist. If you disable SpringDoc for security (`springdoc.api-docs.enabled=false`,
`springdoc.swagger-ui.enabled=false`), the health check above breaks — add actuator **in the same
change**.

---

## 7. Verifying a configuration

1. **Placeholders resolve.** Run the app with the variables set, or check the log for
   `Could not resolve placeholder`. A successful run reaches database connection attempts with no
   such line.
2. **Database.** `psql "$DATABASE_URL" -c 'select 1'` using the same URL, then confirm the app log
   contains no Flyway or Hibernate validation error.
3. **Storage.**
   ```bash
   AWS_ACCESS_KEY_ID=... AWS_SECRET_ACCESS_KEY=... aws s3 ls s3://myhourly-files/ \
     --endpoint-url https://s3.ap-south-1.amazonaws.com
   ```
   then upload any PDF through an endpoint that accepts attachments and confirm the returned
   presigned URL opens in a browser.
4. **Email.** The strongest signal is an end-to-end password-reset request: a 200 with no
   `AuthenticationFailedException` and a delivered message. SES must be out of the sandbox, and
   `PASSWORD_RESET_EMAIL` must be a verified identity in the same region as `SMTP_HOST`.

---

## 8. Operational notes and traps

**`AWS_REGION` is not injected by ECS.** Unlike Lambda, ECS does not set `AWS_REGION` in the
container. If the bucket is not in `ap-south-1`, set it explicitly or S3 and SES will point at the
wrong region.

**A `PORT` of `0` binds a random port.** `server.port=${PORT:8080}` means a `PORT` variable that is
missing gives 8080, but one set to `0` — or to an empty value — gives an ephemeral port, and the
target group's health check fails with no obvious cause. Set `PORT=8080` explicitly. Confirm with
the startup line `Tomcat initialized with port 8080 (http)`; a `port 0` there is the smoking gun.

**Pool size versus RDS.** `maximum-pool-size=8` per task. Multiply by the task count and stay under
the instance's `max_connections` (reserve a few for administration).

**Schema is owned by Flyway,** and the profile runs `ddl-auto=validate`. Entity/DB drift stops
startup rather than silently altering tables. Never switch it to `update` against RDS; add a
migration in `src/main/resources/db/migration` instead.

**Upload size limits disagree, deliberately or not.** Spring accepts up to 100MB
(`spring.servlet.multipart.*`), but `B2FileStorageServiceImpl` rejects anything over **25MB** with a
400, and only allows `pdf`, `png`, `jpg`, `jpeg`. Validate on the client to avoid a wasted upload.

**Objects are not explicitly encrypted by the code.** No SSE headers are set on `PutObjectRequest`,
so bucket default encryption applies. If SSE-KMS with a customer-managed key is required, that is a
code change.

**Presigned URLs expire in 15 minutes.** Upload returns a URL rather than a stable object key, so any
URL persisted in the database stops working after that. Re-fetch flows must regenerate it.

**Storage errors still mention Backblaze.** Upload failures surface as
`"Unable to upload file to Backblaze B2: <message>"` with `ErrorCode.INVALID_REQUEST`. Cosmetic
only — the frontend sees a misleading vendor name on S3.

**Seeded credentials live in the base profile file.** `app.super-admin.password=Admin@1234` and the
manager/HR equivalents are hardcoded there and are not env-driven. The seeding calls in
`DataInitializer` are currently commented out, so nothing is created at startup — but the values
remain in source control and must not be reintroduced as real credentials.

---

## 9. Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| `Could not resolve placeholder 'X'` | Required variable missing | Add it; the message names the variable |
| `Tomcat initialized with port 0` | `PORT` set to `0` or empty | Set `PORT=8080` |
| `PSQLException: Connection refused` | Wrong host/port, or SG does not allow 5432 | Check the JDBC URL and security groups |
| `FATAL: no pg_hba.conf entry ... no encryption` | `?sslmode=require` missing from the URL while `rds.force_ssl=1` is set on the instance | Add it |
| Schema validation error from Hibernate at startup | Entity changes without a matching migration | Add a Flyway migration |
| `SignatureDoesNotMatch` on upload | Wrong `AWS_SECRET_ACCESS_KEY`, or it belongs to another key | Re-issue the IAM user keys |
| HTTP 301 `PermanentRedirect` on upload | `AWS_S3_REGION` does not match the bucket's region | Set the correct region (endpoint follows it) |
| `AccessDenied` on upload | IAM policy missing `s3:PutObject` / `s3:GetObject` | Attach the policy in §6 |
| Uploaded URL later returns 403 | Presigned URL older than 15 minutes | Regenerate the URL |
| `AuthenticationFailedException` sending mail | SES SMTP credentials are wrong, or IAM keys were used by mistake | Recreate the SMTP user in the SES console |
| `Could not connect to SMTP host` | Egress on 587 blocked | Allow outbound 587 (NAT/internet) |
| `Email address is not verified` | SES sandbox, or the From identity is unverified | Verify the identity, request production access |
| Password-reset link points at localhost | `PASSWORD_RESET_FRONTEND_URL` unset | Set the public HTTPS URL |
| Every request returns 401 after a deploy | `JWT_SECRET` changed | Expected; users must sign in again |
| Health check failing, `/actuator/health` 404 | Actuator not on the classpath | Health-check `/swagger-ui/index.html`, or add actuator |

---

## 10. Related files

| File | Role |
|---|---|
| `src/main/resources/application-aws.properties` | The profile documented here |
| `src/main/resources/application.properties` | Base values, profile switch, hardcoded seeds |
| `src/main/java/com/my_hourly/common/configStorage/B2StorageConfig.java` | Builds `S3Client` / `S3Presigner` from `b2.*` |
| `src/main/java/com/my_hourly/common/service/impl/B2FileStorageServiceImpl.java` | Upload, validation, 15-minute presigned URL |
| `src/main/java/com/my_hourly/security/config/SecurityConstants.java` | Public endpoint whitelist (swagger, actuator health) |
| `src/main/resources/db/migration/` | Flyway migrations — the schema's source of truth |
| `docs/AWS_DEPLOYMENT_GUIDE.html` | End-to-end AWS setup, infrastructure side |
