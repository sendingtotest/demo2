package io.javabrains.demo3.controller;

import io.javabrains.demo3.model.Employee;
import io.javabrains.demo3.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        logger.info("Request to create employee: name='{}', department='{}'", employee.getName(), employee.getDepartment());
        Employee created = service.create(employee);
        logger.debug("Created employee with id={}", created.getId());
        return ResponseEntity.created(URI.create("/api/employees/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Employee> list() {
        logger.debug("Request to list all employees");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        logger.debug("Request to get employee by id={}", id);
        return service.findById(id)
                .map(emp -> {
                    logger.debug("Found employee id={}", id);
                    return ResponseEntity.ok(emp);
                })
                .orElseGet(() -> {
                    logger.warn("Employee not found id={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        logger.info("Request to update employee id={}", id);
        Employee updated = service.update(id, employee);
        logger.debug("Updated employee id={}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Request to delete employee id={}", id);
        service.delete(id);
        logger.debug("Deleted employee id={}", id);
        return ResponseEntity.noContent().build();
    }
}
