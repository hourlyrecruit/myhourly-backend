# MyHourly HRMS: Environment Variables
# Secret manager variables

| Secret Name | Variable | Secret? | Purpose |
|---|---|---|---|
| `myhourly/db` | `DATABASE_URL` | Yes | RDS PostgreSQL JDBC connection URL |
| `myhourly/db` | `DATABASE_USER` | Yes | RDS database username |
| `myhourly/db` | `DATABASE_PASSWORD` | **Yes** | RDS database password |
| `myhourly/jwt` | `JWT_SECRET` | **Yes** | JWT token signing secret |
| `myhourly/storage` | `AWS_S3_BUCKET` | Yes | S3 bucket name |
| `myhourly/storage` | `AWS_ACCESS_KEY_ID` | **Yes** | IAM access key for S3 |
| `myhourly/storage` | `AWS_SECRET_ACCESS_KEY` | **Yes** | IAM secret key for S3 |
| `myhourly/ses` | `SMTP_USERNAME` | **Yes** | Amazon SES SMTP username |
| `myhourly/ses` | `SMTP_PASSWORD` | **Yes** | Amazon SES SMTP password |

### Variables that should **not** go into Secrets Manager

| Variable | Value / Example | Where to put |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `aws` | ECS Environment |
| `PORT` | `8080` | ECS Environment |
| `AWS_REGION` | `ap-south-1` | ECS Environment |
| `SUPER_ADMIN_EMAIL` | Your admin email | ECS Environment |
| `HR_EMAIL` | HR email | ECS Environment |
| `SMTP_PORT` | `587` | ECS Environment |
| `SMTP_STARTTLS_ENABLE` | `true` | ECS Environment |
| `SMTP_SSL_ENABLE` | `false` | ECS Environment |
| `PASSWORD_RESET_FRONTEND_URL` | Your HTTPS frontend URL | ECS Environment |
| `PASSWORD_RESET_EMAIL` | Verified SES email | ECS Environment |
| `ACCESS_TOKEN_EXPIRATION` | `9000000` | ECS Environment |
| `REFRESH_TOKEN_EXPIRATION` | `604800000` | ECS Environment |
| `PASSWORD_RESET_EXPIRATION` | `30` | ECS Environment |

The configuration explicitly recommends keeping these non-secret settings in the ECS `environment` section.

**In short: 9 variables → Secrets Manager, everything else → ECS Environment.**



---
