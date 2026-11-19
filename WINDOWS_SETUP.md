# PeopleDesk HRMS — Phase 1 (Windows)

## Included in Phase 1
- Java 25 (LTS) / Spring Boot 3.5.7 application
- Thymeleaf + Bootstrap 5 web interface
- PostgreSQL database with Flyway migration
- Spring Security login, BCrypt password hashing, CSRF protection and role-based URL access
- Organization bootstrap, departments and employee creation/listing
- Initial roles: SUPER_ADMIN, HR_ADMIN, HR_MANAGER, MANAGER, EMPLOYEE, FINANCE

> This is a development/staging foundation, not a production-certified HR/payroll system. Before production, complete the security and tenant-isolation checklist below.

## 1. Install prerequisites
1. Install a Java 25 JDK (for example, Eclipse Temurin 25).
2. Install Apache Maven 3.9+ and add `JAVA_HOME` and Maven `bin` to PATH.
3. Install PostgreSQL 16 (or run only the database using Docker Desktop).
4. Install Git and an editor such as VS Code or IntelliJ Community Edition.
5. Open a new PowerShell window and verify:

```powershell
java -version
mvn -version
```

## 2. Create the PostgreSQL database and user
Open SQL Shell (psql) as a PostgreSQL administrator and run (choose your own strong password):

```sql
CREATE USER hrms_user WITH PASSWORD 'REPLACE_WITH_A_LONG_RANDOM_PASSWORD';
CREATE DATABASE hrms_db OWNER hrms_user;
```

Do not use the sample password in a real deployment.

## 3. Set environment variables (PowerShell)
From the project root, set values for the current terminal session:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/hrms_db"
$env:DB_USERNAME = "hrms_user"
$env:DB_PASSWORD = "REPLACE_WITH_THE_DATABASE_PASSWORD"
$env:ADMIN_USERNAME = "admin"
$env:ADMIN_PASSWORD = "REPLACE_WITH_A_UNIQUE_PASSWORD_AT_LEAST_14_CHARACTERS"
$env:ADMIN_EMAIL = "hr-admin@yourcompany.com"
$env:COMPANY_NAME = "Your Company Pvt Ltd"
$env:SERVER_ADDRESS = "127.0.0.1"
```

Use a unique admin password. Keep credentials out of source control and screenshots.

## 4. Run the application

```powershell
mvn spring-boot:run
```

Flyway automatically creates the schema and initial roles. On first startup, the application creates the organization and initial SUPER_ADMIN account from the environment variables.

Open: http://localhost:8080

Sign in using the `ADMIN_USERNAME` and `ADMIN_PASSWORD` you configured.

## 5. First-use checklist
1. Sign in as the initial administrator.
2. Open Departments and create departments (for example, HR, Finance, Operations, IT).
3. Open Employees and add a test employee.
4. Confirm the employee appears in the directory and dashboard count.

## 6. Access from other office computers (LAN test only)
For a temporary LAN test, set the application bind address before starting:

```powershell
$env:SERVER_ADDRESS = "0.0.0.0"
```

Find the server PC's IPv4 address using `ipconfig`, then from another office PC open `http://SERVER-IP:8080` (example only: `http://192.168.1.50:8080`). Allow inbound TCP 8080 only on the Windows Private network profile if required.

**Do not expose port 8080 directly to the public internet.** This LAN HTTP setup is for a trusted-network test only. For real office use, put the application behind HTTPS on a properly configured reverse proxy, restrict access with firewall rules/VPN, and harden the server.

## 7. Optional: run PostgreSQL with Docker Desktop
Set the password in PowerShell, then start the database:

```powershell
$env:DB_PASSWORD = "REPLACE_WITH_THE_DATABASE_PASSWORD"
docker compose up -d db
```

Then set the matching Spring variables from section 3 and run `mvn spring-boot:run`.

## 8. Useful commands

```powershell
mvn clean test
mvn clean package
java -jar target/java-hrms-1.0.0.jar
```

## Important production work before employee data is loaded
- Add tenant-scoped repository/service authorization to every query and mutation. The initial UI currently assumes a single organization; do not use it as a multi-company SaaS yet.
- Replace the bootstrap admin process with a controlled one-time provisioning flow; rotate the initial admin password and protect operational secrets.
- Add account lockout/rate limiting, MFA for privileged users, password reset, invitation workflow and session revocation.
- Add complete audit logging for sensitive changes (the audit table is created, but automatic audit events are not yet wired).
- Add employee edit/deactivate workflows, data-retention rules, consent/privacy notices and access reviews.
- Configure HTTPS, firewall, least-privilege database networking, encrypted backups and tested restore procedures.
- Add automated tests, dependency scanning, load testing and a disaster recovery plan.
- Payroll and statutory compliance must be designed for the relevant country before implementation.

## Notes
- Bootstrap CSS is loaded from jsDelivr CDN in this starter. If the office internet is unavailable, download Bootstrap 5.3.3 assets and serve them locally from `static/vendor`.
- Java mobile client is a later phase. Phase 1 provides the web app and its foundations; the same secured service layer can later expose mobile APIs.
