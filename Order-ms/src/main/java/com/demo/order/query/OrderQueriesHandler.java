package com.demo.order.query;

import com.demo.order.core.model.OrderSummary;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.demo.order.core.data.OrderEntity;
import com.demo.order.core.data.OrdersRepository;

@Component
public class OrderQueriesHandler {

	OrdersRepository ordersRepository;

	public OrderQueriesHandler(OrdersRepository ordersRepository) {
		this.ordersRepository = ordersRepository;
	}

	@QueryHandler
	public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
		OrderEntity orderEntity = ordersRepository.findByOrderId(findOrderQuery.getOrderId());
		return new OrderSummary(orderEntity.getOrderId(), 
				orderEntity.getOrderStatus(), "");
	}

}
