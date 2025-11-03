# demo2 — Quickstart for developers

This README helps developers get the project running quickly locally, in Docker, or on Render. It includes copy-paste commands for Windows (cmd.exe / PowerShell) and POSIX shells.

Summary
- Java 17 Spring Boot application (module: `demo2`)
- JPA + PostgreSQL
- SendGrid HTTP API for transactional emails (avoids SMTP blocking on PaaS)
- Root `Dockerfile` builds the project (multi-stage) and produces a runnable image

Contents
- Prereqs
- Build & run (JAR)
- Run in IDE
- Docker: build & run
- Tests
- API examples (PowerShell + curl) — including create 5 users and delete
- Render deployment notes
- GHCR image usage (CI publishes image)
- Troubleshooting

---

Prerequisites
- Java 17
- Docker (if you run the container locally)
- Maven (optional; used locally — the Dockerfile runs Maven inside the build stage)
- PowerShell recommended for Windows examples

---

1) Build the JAR locally
From repository root run (Windows cmd):

```cmd
mvnw.cmd -f demo2/pom.xml clean package -DskipTests
```

Or with global Maven:

```cmd
mvn -f demo2/pom.xml clean package -DskipTests
```

The produced artifact will be:
```
demo2/target/demo2-0.0.1-SNAPSHOT.jar
```

2) Run the JAR locally
Example (Windows cmd). Replace DB values with your Postgres connection. Setting `SENDGRID_API_KEY` empty will skip sending emails.

```cmd
java -jar demo2/target/demo2-0.0.1-SNAPSHOT.jar --server.port=8080 \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb \
  --spring.datasource.username=youruser \
  --spring.datasource.password=yourpass \
  --SENDGRID_API_KEY=""
```

Open: http://localhost:8080/users

---

3) Run in your IDE
- Import the Maven project `demo2/pom.xml` into your IDE (IntelliJ, Eclipse).
- Set the active profile or VM options to provide environment properties when running from the IDE: pass `-Dserver.port=8080 -DSENDGRID_API_KEY="" -Dspring.datasource.url=...` etc., or configure run configuration environment variables.

---

4) Docker: build & run locally
Build (from repo root):

```cmd
docker build -t demo2:local .
```

Run (cmd.exe single-line):

```cmd
docker run --rm -p 8080:8080 -e PORT=8080 -e SPRING_DATASOURCE_URL="jdbc:postgresql://host:5432/dbname" -e SPRING_DATASOURCE_USERNAME="dbuser" -e SPRING_DATASOURCE_PASSWORD="dbpass" -e SENDGRID_API_KEY="" demo2:local
```

PowerShell multi-line:

```powershell
docker run --rm -p 8080:8080 `
  -e PORT=8080 `
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host:5432/dbname" `
  -e SPRING_DATASOURCE_USERNAME="dbuser" `
  -e SPRING_DATASOURCE_PASSWORD="dbpass" `
  -e SENDGRID_API_KEY="" `
  demo2:local
```

Logs:
```
docker logs <container-id>
```

---

5) Run tests
From repo root (Windows cmd):

```cmd
mvnw.cmd -f demo2/pom.xml test
```

Unit/integration tests will run against the test configuration.

---

6) API examples (create, list, delete)
Base URL (local): http://localhost:8080

Create one user (PowerShell preferred on Windows):

```powershell
$body = @{ name = 'Alice'; email = 'alice@example.com' } | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/users' -Method Post -Body $body -ContentType 'application/json'
```

Create one user (curl, POSIX):

```bash
curl -X POST http://localhost:8080/users -H "Content-Type: application/json" -d '{"name":"Alice","email":"alice@example.com"}'
```

List users:

```bash
curl http://localhost:8080/users
# or
Invoke-RestMethod -Uri 'http://localhost:8080/users' -Method Get
```

Delete a user (PowerShell using Invoke-RestMethod):

```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/users/1' -Method Delete
```

Delete using curl (POSIX):

```bash
curl -X DELETE http://localhost:8080/users/1
```

Create 5 users (PowerShell loop):

```powershell
$users = @(
  @{name='User1'; email='u1@example.com'},
  @{name='User2'; email='u2@example.com'},
  @{name='User3'; email='u3@example.com'},
  @{name='User4'; email='u4@example.com'},
  @{name='User5'; email='u5@example.com'}
)
foreach ($u in $users) {
  $json = $u | ConvertTo-Json
  Invoke-RestMethod -Uri 'http://localhost:8080/users' -Method Post -Body $json -ContentType 'application/json'
}
```

---

7) Render deployment notes (Docker)
- Ensure `Dockerfile` is at repository root and committed.
- In Render create a Web Service, select Docker, connect to your GitHub repo and chosen branch.
- Add these environment variables in Render settings:
  - `SPRING_DATASOURCE_URL` (JDBC URL)
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
  - `SENDGRID_API_KEY`
  - `APP_MAIL_FROM` (optional)
  - `JASYPT_ENCRYPTOR_PASSWORD` (optional)
- Deploy and monitor logs. The Dockerfile runs Maven during the image build; you do not need to check-in `target/`.

Notes: Render often blocks SMTP ports—use `SENDGRID_API_KEY` for HTTP mail. If `SENDGRID_API_KEY` is empty the app will skip sending emails.

---

8) Using the GitHub Container Registry image (CI)
A GitHub Actions workflow can publish images to GHCR as `ghcr.io/<owner>/demo2:latest`.
- Pull and run (if public):

```bash
docker pull ghcr.io/<owner>/demo2:latest
docker run --rm -p 8080:8080 -e PORT=8080 -e SPRING_DATASOURCE_URL="..." -e SPRING_DATASOURCE_USERNAME="..." -e SPRING_DATASOURCE_PASSWORD="..." ghcr.io/<owner>/demo2:latest
```

If the GHCR package is private, `docker login ghcr.io` with a PAT is required.

---

9) Troubleshooting (common issues)
- "failed to read Dockerfile": confirm `Dockerfile` exists at repo root and is committed.
- JavaMail/SMTP connection timeouts on Render: many PaaS block SMTP; the app uses SendGrid HTTP API to avoid this.
- If the app fails to start due to DB connection: verify `SPRING_DATASOURCE_URL` is a valid JDBC URL and credentials work.
- If dependencies fail to download inside Docker build: run the Maven command locally (`mvn -f demo2/pom.xml clean package -DskipTests`) to reproduce and inspect errors.

---

10) Helpful commands summary
Build jar locally:

```cmd
mvnw.cmd -f demo2/pom.xml clean package -DskipTests
```

Build Docker image:

```cmd
docker build -t demo2:local .
```

Run container:

```cmd
docker run --rm -p 8080:8080 -e PORT=8080 -e SENDGRID_API_KEY="" demo2:local
```

Run tests:

```cmd
mvnw.cmd -f demo2/pom.xml test
```

---

If you'd like, I can also:
- Add a short `render.md` with screenshots and exact Render UI steps (already included as `render.md`),
- Add GitHub Actions or GitLab CI templates to publish images to DockerHub or GHCR (I already added a GHCR workflow), or
- Create a small `.env.example` file showing environment keys to set in Render/locally.

If you want any of those, tell me which and I'll add it.
