package io.javabrains.demo4.controller;

import io.javabrains.demo4.model.Order;
import io.javabrains.demo4.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<Order> getAll() {
        List<Order> all = orderRepository.findAll();
        logger.debug("Fetched {} orders", all.size());
        return all;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        logger.debug("Fetching order by id={}", id);
        Optional<Order> o = orderRepository.findById(id);
        if (o.isPresent()) {
            logger.info("Order {} found", id);
            return ResponseEntity.ok(o.get());
        } else {
            logger.warn("Order {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        logger.info("Creating order with name='{}', department='{}'", order.getOrdername(), order.getOrderDepartment());
        // ensure id is null so a new entity is created
        order.setOrderid(null);
        Order saved = orderRepository.save(order);
        logger.info("Created order with id={}", saved.getOrderid());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order order) {
        logger.info("Updating order id={}", id);
        return orderRepository.findById(id).map(existing -> {
            existing.setOrdername(order.getOrdername());
            existing.setOrderDepartment(order.getOrderDepartment());
            Order saved = orderRepository.save(existing);
            logger.info("Updated order id={}", saved.getOrderid());
            return ResponseEntity.ok(saved);
        }).orElseGet(() -> {
            logger.warn("Order {} not found for update", id);
            return ResponseEntity.notFound().build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting order id={}", id);
        return orderRepository.findById(id).map(existing -> {
            orderRepository.deleteById(id);
            logger.info("Deleted order id={}", id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> {
            logger.warn("Order {} not found for delete", id);
            return ResponseEntity.notFound().build();
        });
    }
}
