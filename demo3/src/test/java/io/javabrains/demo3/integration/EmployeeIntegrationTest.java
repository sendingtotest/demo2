package io.javabrains.demo3.integration;

import io.javabrains.demo3.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/employees";
    }

    @Test
    void e2e_crud_flow() {
        // create
        Employee toCreate = new Employee(null, "E2E User", "QA");
        ResponseEntity<Employee> created = restTemplate.postForEntity(baseUrl, toCreate, Employee.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        Long id = created.getBody().getId();

        // get
        Employee got = restTemplate.getForObject(baseUrl + "/" + id, Employee.class);
        assertThat(got).isNotNull();
        assertThat(got.getName()).isEqualTo("E2E User");

        // update
        Employee update = new Employee(null, "E2E User Updated", "QA2");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> entity = new HttpEntity<>(update, headers);
        ResponseEntity<Employee> updated = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.PUT, entity, Employee.class);
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updated.getBody()).isNotNull();
        assertThat(updated.getBody().getName()).isEqualTo("E2E User Updated");

        // delete
        restTemplate.delete(baseUrl + "/" + id);

        ResponseEntity<Employee> afterDelete = restTemplate.getForEntity(baseUrl + "/" + id, Employee.class);
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
