# MyHourly HRMS --- AWS EC2 Deployment Runbook

## 1. Scope

This runbook documents the AWS deployment process used for the MyHourly
HRMS backend and is intended to be reusable for future deployments.

Architecture:

``` text
Developer
   |
   | git push origin deployment
   v
GitHub
   |
   | GitHub Actions
   v
EC2
   |
   | Docker Compose
   v
Spring Boot :8080
   |                    v                  v
RDS PostgreSQL       S3
```

Components: - EC2: application runtime - RDS PostgreSQL: production
database - S3: file/object storage - IAM Role: AWS permissions for EC2 -
Docker/Docker Compose: application runtime - GitHub: source control -
GitHub Actions: CI/CD - Security Groups: network access - Swagger: API
testing

------------------------------------------------------------------------

# 2. AWS Region

The deployment used:

``` text
ap-south-2
```

Keep EC2, RDS and S3 configuration consistent with the selected region.

------------------------------------------------------------------------

# 3. RDS PostgreSQL

Example configuration used:

``` text
Identifier: database-1
Engine: PostgreSQL
Version: PostgreSQL 18.3-R2
Instance: db.t4g.micro
vCPU: 2
RAM: 1 GiB
Storage: 20 GiB
Database: myhourly
Username: postgres
Port: 5432
Region: ap-south-2
```

For production, RDS should normally be private.

RDS security group:

``` text
PostgreSQL / TCP / 5432
Source: EC2 security group
```

Do not expose PostgreSQL publicly with `0.0.0.0/0`.

Example application URL:

``` properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
```

Example `DATABASE_URL`:

``` text
jdbc:postgresql://database-1.xxxxxxxxx.ap-south-2.rds.amazonaws.com:5432/myhourly?sslmode=require
```

------------------------------------------------------------------------

# 4. S3

Bucket used:

``` text
myhourlys3
```

Region:

``` text
ap-south-2
```

Keep the bucket private unless public access is explicitly required.

S3 IAM policy used:

``` json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["s3:ListBucket"],
      "Resource": "arn:aws:s3:::myhourlys3"
    },
    {
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject"
      ],
      "Resource": "arn:aws:s3:::myhourlys3/*"
    }
  ]
}
```

------------------------------------------------------------------------

# 5. EC2 IAM Role

Attach an IAM role to EC2, for example:

``` text
Myhourly-EC2-ECR-Role
```

The important point is that the application uses the EC2 IAM role
instead of static AWS credentials.

Verify from EC2:

``` bash
aws sts get-caller-identity
```

Test S3:

``` bash
aws s3 ls s3://myhourlys3
```

Do NOT put static credentials in `.env`:

``` env
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
```

Use:

``` env
AWS_REGION=ap-south-2
AWS_S3_BUCKET=myhourlys3
```

------------------------------------------------------------------------

# 6. EC2 Creation

Example:

``` text
Name: myhourly-production
OS: Ubuntu
SSH key: myhourly-aws.pem
Region: ap-south-2
```

Required EC2 security rules depend on the final architecture.

For initial testing:

    Port Purpose               Recommended source
  ------ --------------------- ----------------------
      22 SSH                   Your IP /32
    8080 Spring Boot testing   Your IP /32
      80 HTTP                  Internet if required
     443 HTTPS                 Internet

Port 8080 was temporarily opened to `0.0.0.0/0` during Swagger testing.
Do not treat that as the final production configuration.

------------------------------------------------------------------------

# 7. Connect to EC2

From Windows PowerShell:

``` powershell
ssh -i "myhourly-aws.pem" ubuntu@EC2_PUBLIC_DNS
```

Verbose SSH troubleshooting:

``` powershell
ssh -vvv -i ".\myhourly-aws.pem" ubuntu@EC2_PUBLIC_DNS
```

If SSH reaches:

``` text
Connection established.
debug1: Local version string SSH-2.0-OpenSSH_for_Windows_9.5
```

but does not continue, check SSH from EC2 Instance Connect:

``` bash
sudo systemctl status ssh
sudo systemctl restart ssh
sudo systemctl status ssh
```

------------------------------------------------------------------------

# 8. Install Git and Docker

Update Ubuntu:

``` bash
sudo apt update
sudo apt upgrade -y
```

Install Git:

``` bash
sudo apt install git -y
git --version
```

Install Docker Engine and Docker Compose using Docker's official Ubuntu
installation instructions.

Verify:

``` bash
docker --version
docker compose version
```

Allow Ubuntu to use Docker without sudo:

``` bash
sudo usermod -aG docker ubuntu
newgrp docker
```

Test:

``` bash
docker ps
```

------------------------------------------------------------------------

# 9. EC2 → GitHub SSH Authentication

There are two different SSH directions:

``` text
PC → EC2
```

uses the EC2 `.pem` key.

``` text
EC2 → GitHub
```

uses a separate GitHub authentication key.

Generate the EC2 → GitHub key:

``` bash
ssh-keygen -t ed25519 -C "ec2-github-deployment"
```

Display public key:

``` bash
cat ~/.ssh/id_ed25519.pub
```

Add that public key in:

``` text
GitHub repository
→ Settings
→ Deploy keys
→ Add deploy key
```

Test:

``` bash
ssh -T git@github.com
```

------------------------------------------------------------------------

# 10. Application Directory

Create:

``` bash
sudo mkdir -p /opt/myhourly
sudo chown ubuntu:ubuntu /opt/myhourly
```

Target structure:

``` text
/opt/myhourly/
└── backend/
    ├── .env
    ├── .git/
    ├── Dockerfile
    ├── docker-compose.yml
    ├── pom.xml
    └── src/
```

------------------------------------------------------------------------

# 11. Clone the Deployment Branch

For the initial manual setup:

``` bash
cd /opt/myhourly
git clone -b deployment git@github.com:YOUR_USERNAME/YOUR_REPOSITORY.git backend
```

Verify:

``` bash
cd /opt/myhourly/backend
git status
```

Expected:

``` text
On branch deployment
Your branch is up to date with 'origin/deployment'.

nothing to commit, working tree clean
```

------------------------------------------------------------------------

# 12. Production `.env`

`.env` remains on EC2 and must not be committed to Git.

Example:

``` env
SPRING_PROFILES_ACTIVE=aws

DATABASE_URL=jdbc:postgresql://database-1.xxxxxxxxx.ap-south-2.rds.amazonaws.com:5432/myhourly?sslmode=require
DATABASE_USER=postgres
DATABASE_PASSWORD=YOUR_DATABASE_PASSWORD

JWT_SECRET=YOUR_JWT_SECRET
ACCESS_TOKEN_EXPIRATION=9000000
REFRESH_TOKEN_EXPIRATION=604800000

SUPER_ADMIN_EMAIL=superadmin@myhourly.com
HR_EMAIL=hr@company.com

AWS_REGION=ap-south-2
AWS_S3_BUCKET=myhourlys3

SMTP_HOST=...
SMTP_PORT=587
SMTP_USERNAME=...
SMTP_PASSWORD=...

PASSWORD_RESET_EXPIRATION=30
PASSWORD_RESET_FRONTEND_URL=https://your-frontend-domain/reset-password
PASSWORD_RESET_EMAIL=noreply@myhourly.com
```

Use the exact variable names required by the application's
`application-aws.properties`.

Secure it:

``` bash
sudo chown ubuntu:ubuntu /opt/myhourly/backend/.env
chmod 600 /opt/myhourly/backend/.env
```

Check:

``` bash
ls -l /opt/myhourly/backend/.env
```

Never commit `.env`, `.pem`, private keys, passwords, JWT secrets or AWS
secret keys.

------------------------------------------------------------------------

# 13. Spring Boot AWS Properties

Example:

``` properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.datasource.hikari.maximum-pool-size=8
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=30000

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.open-in-view=false

jwt.secret=${JWT_SECRET}

aws.s3.bucket=${AWS_S3_BUCKET:myhourlys3}
aws.s3.region=${AWS_REGION:ap-south-2}
```

The AWS profile should be activated through Compose:

``` text
SPRING_PROFILES_ACTIVE=aws
```

------------------------------------------------------------------------

# 14. S3 Spring Configuration

The application should rely on the EC2 IAM role.

Example:

``` java
@Configuration
public class S3StorageConfig {

    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .build();
    }
}
```

Do not hard-code AWS access keys in this configuration.

------------------------------------------------------------------------

# 15. Dockerfile

The project uses a multi-stage Java 21 build.

``` dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn -B -ntp dependency:go-offline

COPY src ./src

RUN mvn -B -ntp -DskipTests package

FROM eclipse-temurin:21-jre

ENV TZ=Asia/Kolkata

RUN groupadd --system spring     && useradd --system --gid spring --create-home spring

WORKDIR /app

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

USER spring

EXPOSE 8080

CMD ["java",      "-XX:MaxRAMPercentage=75.0",      "-XX:+ExitOnOutOfMemoryError",      "-Duser.timezone=Asia/Kolkata",      "-Djava.security.egd=file:/dev/./urandom",      "-jar", "app.jar"]
```

------------------------------------------------------------------------

# 16. Docker Compose

RDS is external, so there is no PostgreSQL container.

Example:

``` yaml
name: myhourly-aws

services:
  backend:
    build:
      context: .
      dockerfile: Dockerfile
    image: myhourly-backend:aws
    container_name: myhourly-backend

    env_file:
      - .env

    environment:
      SPRING_PROFILES_ACTIVE: aws
      PORT: 8080

    ports:
      - "8080:8080"

    restart: unless-stopped

    stop_grace_period: 30s

    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"
```

------------------------------------------------------------------------

# 17. Validate Compose

From:

``` bash
cd /opt/myhourly/backend
```

run:

``` bash
docker compose config
```

If:

``` text
.env: permission denied
```

fix:

``` bash
sudo chown ubuntu:ubuntu /opt/myhourly/backend/.env
chmod 600 /opt/myhourly/backend/.env
```

Then:

``` bash
docker compose config
```

------------------------------------------------------------------------

# 18. Check Git Conflict Markers

Do not search for every `=======` because Java comments can contain
separator lines.

Use:

``` bash
grep -R -nE '^[[:space:]]*<<<<<<< |^[[:space:]]*>>>>>>> |^[[:space:]]*=======[[:space:]]*$' src/main/java
```

No output means no matching conflict markers were found.

If actual markers such as these appear:

``` text
<<<<<<< Updated upstream
=======
>>>>>>> Stashed changes
```

stop and resolve the Git conflict before building.

------------------------------------------------------------------------

# 19. First Docker Build

Clean build:

``` bash
docker compose build --no-cache backend
```

The first clean build can take longer because Maven dependencies and
Docker layers must be downloaded again.

After the first successful build:

``` bash
docker compose build backend
```

can reuse cached layers.

------------------------------------------------------------------------

# 20. Start Backend

``` bash
docker compose up -d backend
```

Check:

``` bash
docker ps
```

Expected:

``` text
myhourly-backend
0.0.0.0:8080->8080/tcp
```

Logs:

``` bash
docker logs --tail 150 myhourly-backend
```

Follow:

``` bash
docker logs -f myhourly-backend
```

Look for:

``` text
The following 1 profile is active: "aws"
Tomcat started on port 8080
HikariPool-1 - Start completed
Successfully validated migrations
Schema "public" is up to date
Started MyHourlyApplication
```

------------------------------------------------------------------------

# 21. Verify EC2 → Application

``` bash
curl http://localhost:8080
```

If the API requires authentication, a response such as:

``` json
{
  "success": false,
  "message": "Authentication is required to access this resource.",
  "errorCode": "UNAUTHORIZED"
}
```

still proves that Spring Boot is reachable.

Check listening port:

``` bash
sudo ss -lntp | grep 8080
```

Expected:

``` text
0.0.0.0:8080
```

------------------------------------------------------------------------

# 22. Swagger

Temporary direct access:

``` text
http://EC2_PUBLIC_IP:8080/swagger-ui/index.html
```

Use HTTP unless HTTPS has been configured.

For the test deployment, port 8080 was temporarily allowed from:

``` text
0.0.0.0/0
```

This should not be considered the final production security
configuration.

------------------------------------------------------------------------

# 23. GitHub Actions CI/CD

## Objective

After initial server configuration, the normal workflow should be:

``` bash
git add .
git commit -m "Update feature"
git push origin deployment
```

GitHub Actions should then:

``` text
GitHub
  ↓
deployment branch
  ↓
SSH to EC2
  ↓
git fetch/reset or pull
  ↓
docker compose build
  ↓
docker compose up -d
```

The application source should be maintained in GitHub. The production
`.env` remains on EC2.

------------------------------------------------------------------------

# 24. GitHub Actions Variables/Secrets

Recommended variables:

``` yaml
env:
  APP_DIR: /opt/myhourly/backend
  COMPOSE_DIR: /opt/myhourly/backend
  COMPOSE_SERVICE: backend
  CONTAINER_NAME: myhourly-backend
  PRODUCTION_BRANCH: deployment
```

Recommended GitHub Secrets:

``` text
EC2_HOST
EC2_USER
EC2_PORT
EC2_SSH_KEY
```

Example:

``` text
EC2_USER=ubuntu
EC2_PORT=22
```

`EC2_SSH_KEY` should contain the private key used by GitHub Actions to
SSH into EC2.

Do not put the private key directly into YAML.

------------------------------------------------------------------------

# 25. Important: Two SSH Directions

### GitHub Actions → EC2

Used to deploy:

``` text
GitHub Actions
      ↓
EC2
```

Uses the private key stored as:

``` text
EC2_SSH_KEY
```

### EC2 → GitHub

Used to clone/pull:

``` text
EC2
      ↓
GitHub
```

Uses the EC2 GitHub deploy key:

``` text
~/.ssh/id_ed25519
```

These are separate credentials.

------------------------------------------------------------------------

# 26. Example GitHub Actions Workflow

``` yaml
name: Deploy Backend to AWS

on:
  push:
    branches:
      - deployment
  workflow_dispatch:

env:
  APP_DIR: /opt/myhourly/backend
  COMPOSE_DIR: /opt/myhourly/backend
  COMPOSE_SERVICE: backend
  CONTAINER_NAME: myhourly-backend
  PRODUCTION_BRANCH: deployment

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Deploy to EC2
        uses: appleboy/ssh-action@v1.2.2
        with:
          host: ${{ secrets.EC2_HOST }}
          username: ${{ secrets.EC2_USER }}
          key: ${{ secrets.EC2_SSH_KEY }}
          port: ${{ secrets.EC2_PORT }}
          script: |
            set -e

            cd ${{ env.APP_DIR }}

            git fetch origin
            git checkout ${{ env.PRODUCTION_BRANCH }}
            git reset --hard origin/${{ env.PRODUCTION_BRANCH }}

            docker compose build ${{ env.COMPOSE_SERVICE }}
            docker compose up -d ${{ env.COMPOSE_SERVICE }}

            docker ps
            docker logs --tail 100 ${{ env.CONTAINER_NAME }}
```

The exact action version can be changed according to the project's
current workflow/security requirements.

------------------------------------------------------------------------

# 27. First Clone Can Also Be Automated

If desired, the workflow can handle both a first deployment and future
deployments.

Conceptually:

``` bash
if [ ! -d "$APP_DIR/.git" ]; then
    git clone -b deployment git@github.com:YOUR_USERNAME/YOUR_REPOSITORY.git "$APP_DIR"
else
    cd "$APP_DIR"
    git fetch origin
    git checkout deployment
    git reset --hard origin/deployment
fi

cd "$APP_DIR"

docker compose build backend
docker compose up -d backend
```

This removes the need for a manual initial clone, provided the new EC2
already has GitHub authentication and the required infrastructure.

------------------------------------------------------------------------

# 28. What Is Manual With the Current Setup?

For an already-configured EC2:

``` text
Manual:
- Change application code
- git commit
- git push

Automatic:
- GitHub → EC2 connection
- source update
- Docker build
- container restart
```

For a completely new EC2, the current setup still requires
infrastructure/server preparation:

``` text
1. Create EC2
2. Configure Security Group
3. Attach IAM Role
4. Install Docker
5. Install Git
6. Configure GitHub authentication
7. Create production .env
```

After that, CI/CD can handle application deployment.

------------------------------------------------------------------------

# 29. If EC2 Is Terminated

Terminating EC2 does not normally delete separately managed:

``` text
RDS
S3
IAM Role
GitHub repository
```

But the new EC2 will not automatically contain:

``` text
Docker
Git
.env
application directory
GitHub SSH configuration
```

With the current setup, those server/infrastructure steps need to be
repeated.

For full infrastructure automation, introduce:

``` text
Terraform
or
CloudFormation
or
AWS CDK
```

That is separate from application CI/CD.

------------------------------------------------------------------------

# 30. Clean Reset of a Broken Backend Checkout

If the Git working tree is corrupted and a fresh checkout is
intentionally required:

First preserve the production `.env` outside the repository:

``` bash
sudo cp /opt/myhourly/backend/.env /opt/myhourly/.env.backup
```

Delete only the application folder:

``` bash
sudo rm -rf /opt/myhourly/backend
```

Clone again:

``` bash
cd /opt/myhourly
git clone -b deployment git@github.com:YOUR_USERNAME/YOUR_REPOSITORY.git backend
```

Restore:

``` bash
sudo cp /opt/myhourly/.env.backup /opt/myhourly/backend/.env
sudo chown ubuntu:ubuntu /opt/myhourly/backend/.env
chmod 600 /opt/myhourly/backend/.env
```

Then:

``` bash
cd /opt/myhourly/backend
git status
docker compose config
docker compose build --no-cache backend
docker compose up -d backend
```

Do not delete `/opt/myhourly` itself unless you intentionally want to
remove everything stored there.

------------------------------------------------------------------------

# 31. Troubleshooting Summary

## SSH hangs

``` bash
sudo systemctl status ssh
sudo systemctl restart ssh
```

Use EC2 Instance Connect if necessary.

## Docker permission problem

``` bash
sudo usermod -aG docker ubuntu
newgrp docker
docker ps
```

## `.env` permission problem

``` bash
sudo chown ubuntu:ubuntu /opt/myhourly/backend/.env
chmod 600 /opt/myhourly/backend/.env
```

## Docker build

``` bash
docker compose build --no-cache backend
```

## Container status

``` bash
docker ps
```

## Logs

``` bash
docker logs --tail 150 myhourly-backend
```

## Application locally

``` bash
curl http://localhost:8080
```

## Port

``` bash
sudo ss -lntp | grep 8080
```

## Compose validation

``` bash
docker compose config
```

## Git state

``` bash
git status
git log -1 --oneline
```

## GitHub connectivity from EC2

``` bash
ssh -T git@github.com
```

## S3 permissions

``` bash
aws sts get-caller-identity
aws s3 ls s3://myhourlys3
```

------------------------------------------------------------------------

# 32. Production Hardening Checklist

Before considering the deployment production-ready:

``` text
[ ] Do not expose RDS publicly
[ ] Restrict SSH to trusted IPs
[ ] Do not permanently expose port 8080 publicly
[ ] Use HTTPS
[ ] Use a domain/reverse proxy
[ ] Keep .env out of Git
[ ] Never commit private keys
[ ] Never commit AWS secret keys
[ ] Use EC2 IAM Role for AWS SDK access
[ ] Restrict S3 permissions to required actions
[ ] Configure backups for RDS
[ ] Configure application/server monitoring
[ ] Configure log rotation
[ ] Configure CI/CD
[ ] Test rollback procedure
```

------------------------------------------------------------------------

# 33. Final Deployment Model

The desired deployment model is:

``` text
                     Developer
                         |
                         | git push
                         v
                +------------------+
                |      GitHub      |
                | deployment branch|
                +--------+---------+
                         |
                         | GitHub Actions
                         v
                +------------------+
                |       EC2        |
                |                  |
                | git update       |
                | Docker build     |
                | Docker Compose   |
                +--------+---------+
                         |
                    Spring Boot
                    /                            /                             v             v
          RDS PostgreSQL       S3
```

Core separation:

``` text
GitHub        = source code
EC2           = application runtime
.env          = production secrets
RDS           = database
S3            = file storage
IAM Role      = AWS permissions
GitHub Actions= CI/CD
```

This separation makes the application deployment repeatable and keeps
production secrets outside the repository.
