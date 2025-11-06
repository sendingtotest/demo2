package io.javabrains.demo3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javabrains.demo3.model.Employee;
import io.javabrains.demo3.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeeControllerTest {

    private MockMvc mockMvc;
    private EmployeeService service;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        service = Mockito.mock(EmployeeService.class);
        EmployeeController controller = new EmployeeController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void list_shouldReturnEmployees() throws Exception {
        when(service.findAll()).thenReturn(List.of(new Employee(1L, "Alice", "Engineering")));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getById_found() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(new Employee(1L, "Alice", "Engineering")));

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        Employee toCreate = new Employee(null, "Bob", "Sales");
        Employee created = new Employee(2L, "Bob", "Sales");
        when(service.create(any(Employee.class))).thenReturn(created);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/employees/2"))
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        Employee updated = new Employee(1L, "Alice Smith", "R&D");
        when(service.update(eq(1L), any(Employee.class))).thenReturn(updated);

        mockMvc.perform(put("/api/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Smith"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }
}
