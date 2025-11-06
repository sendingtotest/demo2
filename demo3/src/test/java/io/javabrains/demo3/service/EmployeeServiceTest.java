package io.javabrains.demo3.service;

import io.javabrains.demo3.model.Employee;
import io.javabrains.demo3.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeService service;

    private Employee alice;

    @BeforeEach
    void setup() {
        alice = new Employee(1L, "Alice", "Engineering");
    }

    @Test
    void create_shouldSaveEmployee() {
        when(repository.save(any(Employee.class))).thenReturn(alice);

        Employee saved = service.create(new Employee(null, "Alice", "Engineering"));

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        verify(repository, times(1)).save(any(Employee.class));
    }

    @Test
    void findAll_shouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(alice));

        List<Employee> list = service.findAll();

        assertThat(list).hasSize(1).contains(alice);
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById_shouldReturnOptional() {
        when(repository.findById(1L)).thenReturn(Optional.of(alice));

        Optional<Employee> opt = service.findById(1L);

        assertThat(opt).isPresent().contains(alice);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void update_existing_shouldModifyAndSave() {
        Employee updated = new Employee(null, "Alice Smith", "R&D");
        when(repository.findById(1L)).thenReturn(Optional.of(alice));
        when(repository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee result = service.update(1L, updated);

        assertThat(result.getName()).isEqualTo("Alice Smith");
        assertThat(result.getDepartment()).isEqualTo("R&D");
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Employee.class));
    }

    @Test
    void update_nonExisting_shouldCreateWithId() {
        Employee updated = new Employee(null, "Bob", "Sales");
        when(repository.findById(2L)).thenReturn(Optional.empty());
        when(repository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            e.setId(2L);
            return e;
        });

        Employee result = service.update(2L, updated);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Bob");
        verify(repository, times(1)).findById(2L);
        verify(repository, times(1)).save(any(Employee.class));
    }

    @Test
    void delete_shouldCallRepository() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}

