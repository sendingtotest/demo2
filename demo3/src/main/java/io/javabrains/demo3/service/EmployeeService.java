package io.javabrains.demo3.service;

import io.javabrains.demo3.model.Employee;
import io.javabrains.demo3.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee create(Employee employee) {
        logger.debug("Saving new employee: name='{}', department='{}'", employee.getName(), employee.getDepartment());
        Employee saved = repository.save(employee);
        logger.info("Employee saved with id={}", saved.getId());
        return saved;
    }

    public List<Employee> findAll() {
        logger.debug("Retrieving all employees");
        return repository.findAll();
    }

    public Optional<Employee> findById(Long id) {
        logger.debug("Searching employee by id={}", id);
        return repository.findById(id);
    }

    public Employee update(Long id, Employee updated) {
        logger.debug("Updating employee id={}", id);
        return repository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDepartment(updated.getDepartment());
            Employee saved = repository.save(existing);
            logger.info("Updated employee id={}", saved.getId());
            return saved;
        }).orElseGet(() -> {
            updated.setId(id);
            Employee saved = repository.save(updated);
            logger.info("Created employee during update with id={}", saved.getId());
            return saved;
        });
    }

    public void delete(Long id) {
        logger.debug("Deleting employee id={}", id);
        repository.deleteById(id);
        logger.info("Deleted employee id={}", id);
    }
}
