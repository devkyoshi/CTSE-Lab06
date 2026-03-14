package com.ctse.orderservice.service;

import com.ctse.orderservice.event.OrderCreatedEvent;
import com.ctse.orderservice.model.Order;
import com.ctse.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order save(Order order) {
        Order savedOrder = orderRepository.save(order);
        
        // Publish event to Kafka
        OrderCreatedEvent event = new OrderCreatedEvent(
            savedOrder.getId(),
            savedOrder.getCustomerId(),
            savedOrder.getItemId(),
            savedOrder.getQuantity(),
            savedOrder.getTotalPrice(),
            savedOrder.getStatus()
        );
        kafkaTemplate.send("order-created-events", event);
        
        return savedOrder;
    }

    public Optional<Order> updateStatus(Long id, String status) {
        return orderRepository.findById(id).map(existing -> {
            existing.setStatus(status);
            return orderRepository.save(existing);
        });
    }

    public Optional<Order> update(Long id, Order updatedOrder) {
        return orderRepository.findById(id).map(existing -> {
            existing.setItemId(updatedOrder.getItemId());
            existing.setCustomerId(updatedOrder.getCustomerId());
            existing.setQuantity(updatedOrder.getQuantity());
            existing.setTotalPrice(updatedOrder.getTotalPrice());
            existing.setStatus(updatedOrder.getStatus());
            return orderRepository.save(existing);
        });
    }

    public boolean deleteById(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
