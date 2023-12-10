package com.demo.order.core.events;

import com.demo.order.core.model.OrderStatus;

import lombok.Value;

@Value
public class OrderApprovedEvent {

	private final String orderId;
	private final OrderStatus orderStatus = OrderStatus.APPROVED;
	
}
