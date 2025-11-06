# Demo3 - Employees CRUD (Spring Boot)

A small Spring Boot 3 application demonstrating a simple CRUD REST API for an `Employee` entity (id, name, department), a minimal static frontend, and unit/integration/end-to-end tests.

This project is configured to use an in-memory H2 database for development and testing.

Quick overview
- Java: 17
- Spring Boot: configured in `pom.xml` (spring-boot-starter-parent)
- Build: Maven wrapper (`mvnw.cmd` on Windows)
- DB: H2 (in-memory)

Project structure (relevant files)
- `src/main/java/io/javabrains/demo3/`
  - `Demo3Application.java` - Spring Boot main class
  - `model/Employee.java` - JPA entity
  - `repository/EmployeeRepository.java` - Spring Data JPA repository
  - `service/EmployeeService.java` - Service layer
  - `controller/EmployeeController.java` - REST controller exposing CRUD endpoints
- `src/main/resources/application.properties` - configuration (H2 + logging)
- `src/main/resources/static/index.html` - small single-page frontend (fetch() based)
- `src/test/java/...` - unit (Mockito), controller (MockMvc), and integration (TestRestTemplate) tests

Endpoints
Base path: `/api/employees`
- POST `/api/employees` - create employee (returns 201 with Location header)
  - Body: `{ "name": "Alice", "department": "Engineering" }`
- GET `/api/employees` - list all employees
- GET `/api/employees/{id}` - get employee by id
- PUT `/api/employees/{id}` - update or create with given id
  - Body: `{ "name": "Alice Smith", "department": "R&D" }`
- DELETE `/api/employees/{id}` - delete employee

Frontend
- Open the app and visit `http://localhost:8080/` (the `index.html` in `src/main/resources/static`) to use the built-in web UI for create/list/edit/delete.

H2 console
- URL: http://localhost:8080/h2-console
- JDBC URL (default): `jdbc:h2:mem:demo3db`
- Username: `sa`, password: (empty)

Run locally (Windows cmd.exe)
- Run tests (compile + tests):
```
cd /d C:\SpringCert\module01-examples\demo3
mvnw.cmd -DskipTests=false test
```
- Run the app:
```
cd /d C:\SpringCert\module01-examples\demo3
mvnw.cmd spring-boot:run
```

After the app starts, open `http://localhost:8080/` for the frontend or use curl/Postman against the REST endpoints.

Sample curl commands (Windows cmd.exe - backslashes used for line continuation):
- Create
```
curl -X POST http://localhost:8080/api/employees ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Alice\",\"department\":\"Engineering\"}"
```
- List
```
curl http://localhost:8080/api/employees
```
- Get
```
curl http://localhost:8080/api/employees/1
```
- Update
```
curl -X PUT http://localhost:8080/api/employees/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Alice Smith\",\"department\":\"R&D\"}"
```
- Delete
```
curl -X DELETE http://localhost:8080/api/employees/1
```

Testing
- Unit tests (Mockito) for `EmployeeService` in `src/test/java/.../service/EmployeeServiceTest.java`.
- Controller tests (MockMvc) in `src/test/java/.../controller/EmployeeControllerTest.java`.
- Integration/E2E tests (TestRestTemplate) in `src/test/java/.../integration/EmployeeIntegrationTest.java`.

Notes / Next steps
- Logging is enabled at DEBUG for the application package in `application.properties` to help while developing.
- If you want the frontend to be served from a different location or to add a build step (React/Vue), consider moving `index.html` and assets into a dedicated frontend project.
- To persist data between restarts, replace the in-memory H2 URL in `application.properties` with a file-based H2 or external DB and update credentials.

If you'd like, I can now:
- Run the full test suite and report results, and/or
- Start the Spring Boot app and show a few sample requests and logs.


