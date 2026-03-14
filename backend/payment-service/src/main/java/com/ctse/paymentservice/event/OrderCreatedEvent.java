package com.ctse.paymentservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private Long orderId;
    private String customerId;
    private Long itemId;
    private Integer quantity;
    private Double totalPrice;
    private String status;
}
