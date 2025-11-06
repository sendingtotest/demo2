package io.javabrains.demo4;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javabrains.demo4.controller.OrderController;
import io.javabrains.demo4.model.Order;
import io.javabrains.demo4.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(OrderControllerTest.TestConfig.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository; // this will be the Mockito mock from TestConfig

    @Autowired
    private ObjectMapper objectMapper;

    static class TestConfig {
        @Bean
        public OrderRepository orderRepository() {
            return Mockito.mock(OrderRepository.class);
        }
    }

    @Test
    void testGetAll() throws Exception {
        Order o1 = new Order(1L, "Order1", "Dept1");
        Order o2 = new Order(2L, "Order2", "Dept2");
        Mockito.when(orderRepository.findAll()).thenReturn(Arrays.asList(o1, o2));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderid").value(1))
                .andExpect(jsonPath("$[1].ordername").value("Order2"));
    }

    @Test
    void testCreateAndGetById() throws Exception {
        Order toCreate = new Order(null, "New", "Dept");
        Order created = new Order(5L, "New", "Dept");
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(created);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderid").value(5));

        Mockito.when(orderRepository.findById(5L)).thenReturn(Optional.of(created));
        mockMvc.perform(get("/api/orders/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordername").value("New"));
    }

    @Test
    void testUpdateAndDelete() throws Exception {
        Order existing = new Order(10L, "Old", "DeptA");
        Mockito.when(orderRepository.findById(10L)).thenReturn(Optional.of(existing));
        Mockito.when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order updated = new Order(null, "Updated", "DeptB");
        mockMvc.perform(put("/api/orders/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ordername").value("Updated"));

        mockMvc.perform(delete("/api/orders/10"))
                .andExpect(status().isNoContent());
    }
}
