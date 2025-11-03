# Render deployment quick guide (step-by-step)

This file shows exact Render UI steps to deploy the `demo2` application using the repository-root `Dockerfile` and a managed Postgres database. Where screenshots are helpful I include a placeholder — replace with actual screenshots if you want.

Checklist (what we'll do)
- Create a managed Postgres on Render
- Create a Web Service (Docker) on Render connected to GitHub
- Add required environment variables
- Deploy and verify logs & endpoints
- Optional: use GHCR-published image instead of building on Render

---

1) Create a managed Postgres database on Render
- In Render dashboard click: New → Database → PostgreSQL
- Choose a name (e.g. `demo2-db`) and plan (Free or Paid)
- Click Create database
- When ready open the database page and copy the **Connection Info**. There are two useful values:
  - `Connection String` (URI) e.g. `postgres://user:pass@host:5432/dbname`
  - `JDBC URL` (if available) e.g. `jdbc:postgresql://host:5432/dbname`

Placeholder screenshot: [screenshot: render-create-db.png]

Notes:
- Keep the DB info handy — you'll use it as `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` in the next step.

---

2) Create the Web Service (deploy app)
- In Render dashboard click: New → Web Service
- Connect your Git provider (GitHub) and select the repository and branch (e.g. `main`)
- Under **Environment** choose **Docker**
- Important fields to fill:
  - Name: `demo2` (or your preferred name)
  - Region: pick nearest region
  - Branch: `main` (or your deployment branch)
  - Dockerfile Path: leave as default `Dockerfile` if the file is at repo root; if your Dockerfile is elsewhere set the relative path (example `demo2/Dockerfile`)
  - Build Command: leave blank (Dockerfile controls the build)
  - Start Command: leave blank (Dockerfile ENTRYPOINT handles startup)

Placeholder screenshot: [screenshot: render-create-webservice-1.png]

---

3) Add Environment Variables (Render → Service → Environment)
Add the following variables (click "Environment" → "Add Environment Variable"):
- `SPRING_DATASOURCE_URL` = your JDBC URL, e.g. `jdbc:postgresql://<host>:5432/<dbname>`
- `SPRING_DATASOURCE_USERNAME` = `<db-username>`
- `SPRING_DATASOURCE_PASSWORD` = `<db-password>`
- `SENDGRID_API_KEY` = `SG.xxxxx...` (or leave empty to skip email sending)
- `APP_MAIL_FROM` = `no-reply@yourdomain.com` (optional)
- `JASYPT_ENCRYPTOR_PASSWORD` = `<your-jasypt-pass>` (optional, if using encrypted properties)

Notes and tips:
- Do not commit secrets to source control. Use Render environment variables for secrets.
- If your DB connection is a full URL with username/password embedded, you can set `SPRING_DATASOURCE_URL` to that and omit the separate username/password vars.

Placeholder screenshot: [screenshot: render-env-vars.png]

---

4) Deploy and watch logs
- Click **Create Web Service** (or **Save & Deploy** if editing an existing service)
- Render will start a build. If you used Docker, Render runs `docker build` using the repository `Dockerfile`.
- Open the Build & Deploy logs and monitor these key stages:
  - Docker build (multi-stage): you should see Maven running `mvn -f demo2/pom.xml -DskipTests package`
  - Image push and service startup
  - Application logs (Spring Boot startup)

What to look for in logs:
- Successful Maven package and JAR created
- Spring Boot binding to port (Render sets `$PORT`) — look for `Started Demo2Application` or `Tomcat started` logs
- Hibernate / SQL logs if you enabled `spring.jpa.show-sql=true`

Placeholder screenshot: [screenshot: render-build-logs.png]

---

5) Verify the service endpoint
- After successful deploy Render shows a public URL for your service (e.g., `https://demo2-xxxxx.onrender.com`)
- Open `<service-url>/users` in a browser or use curl / PowerShell to test the API

Simple curl test (POSIX / WSL):
```bash
curl https://<your-service>.onrender.com/users
```
PowerShell (Invoke-RestMethod):
```powershell
Invoke-RestMethod -Uri 'https://<your-service>.onrender.com/users' -Method Get
```

---

6) Common problems & fixes
- Build fails with "failed to read Dockerfile": Confirm the Dockerfile path specified on the Web Service creation page matches where the file is in the repository (use `Dockerfile` for repo root). Also confirm the Dockerfile is committed to the selected branch.
- Maven dependency download failures inside Docker build: reproduce locally with `mvn -f demo2/pom.xml -DskipTests package` and inspect errors; network issues or incorrect dependency coordinates cause failures.
- DB connection errors: verify `SPRING_DATASOURCE_URL` is the full JDBC URL and credentials are correct; ensure you copied the JDBC URL from the Render DB page.
- Email sending timeouts (smtp.gmail.com): Render often blocks outbound SMTP. Use `SENDGRID_API_KEY` (SendGrid HTTP API) — the application uses SendGrid when the key is set.

---

7) Optional: deploy from GHCR pre-built image (pull instead of build)
If you prefer to build images in CI and have Render pull them:
- Build & publish image to GHCR in CI (we included a GitHub Actions workflow that tags `ghcr.io/<owner>/demo2:latest`)
- In Render when creating the service choose **Deploy from Docker image** (instead of connecting a repo). Provide image `ghcr.io/<owner>/demo2:latest`.
- If the GHCR image is private provide registry credentials to Render (username and PAT with `read:packages` scope)

Placeholder screenshot: [screenshot: render-deploy-from-image.png]

---

8) Rolling back, environment changes, and health checks
- Use the Render UI to roll back to a previous deploy (Builds → select previous → Rollback)
- Update environment variables in the UI and click **Redeploy** to apply changes
- Optionally enable a health check in the Render service settings pointing to `/actuator/health` or `/` to make Render restart unhealthy instances

---

9) Checklist recap (done)
- Created managed Postgres and copied JDBC URL
- Created Web Service (Docker) and pointed to repo/branch
- Added environment variables: DB creds, SENDGRID_API_KEY, APP_MAIL_FROM
- Deployed image and verified logs & service URL

---

If you want, I can:
- Fill the placeholders with actual screenshot files if you provide images, or
- Produce PNG screenshots automatically if you give me access to the Render UI (not possible here). 


