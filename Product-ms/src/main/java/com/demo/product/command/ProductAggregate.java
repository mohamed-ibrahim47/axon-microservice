package com.demo.product.command;

import java.math.BigDecimal;

import com.demo.core.commands.CancelProductReservationCommand;
import com.demo.core.commands.ReserveProductCommand;
import com.demo.core.events.ProductReservationCancelledEvent;
import com.demo.core.events.ProductReservedEvent;
import com.demo.product.core.events.ProductCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;



@Aggregate(snapshotTriggerDefinition="productSnapshotTriggerDefinition")
@NoArgsConstructor
public class ProductAggregate {

	@AggregateIdentifier
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;

    @CommandHandler
	public ProductAggregate(CreateProductCommand createProductCommand) {
		// Validate Create Product Command

		if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Price cannot be less or equal than zero");
		}

		if(createProductCommand.getTitle() == null
				|| createProductCommand.getTitle().isBlank()) {
			throw new IllegalArgumentException("Title cannot be empty");
		}

		ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();

		BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

		AggregateLifecycle.apply(productCreatedEvent);
	}

	@CommandHandler
	public void handle(ReserveProductCommand reserveProductCommand) {
		// quantity already up to date
		if(quantity < reserveProductCommand.getQuantity()) {
			throw new IllegalArgumentException("Insufficient number of items in stock");
		}

		ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
				.orderId(reserveProductCommand.getOrderId())
				.productId(reserveProductCommand.getProductId())
				.quantity(reserveProductCommand.getQuantity())
				.userId(reserveProductCommand.getUserId())
				.build();

		AggregateLifecycle.apply(productReservedEvent);

	}

	@CommandHandler
	public void handle(CancelProductReservationCommand cancelProductReservationCommand) {

		ProductReservationCancelledEvent productReservationCancelledEvent =
				ProductReservationCancelledEvent.builder()
				.orderId(cancelProductReservationCommand.getOrderId())
				.productId(cancelProductReservationCommand.getProductId())
				.quantity(cancelProductReservationCommand.getQuantity())
				.reason(cancelProductReservationCommand.getReason())
				.userId(cancelProductReservationCommand.getUserId())
				.build();

		AggregateLifecycle.apply(productReservationCancelledEvent);

	}


	@EventSourcingHandler
	public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
		this.quantity += productReservationCancelledEvent.getQuantity();
	}


	@EventSourcingHandler
	public void on(ProductCreatedEvent productCreatedEvent) {
		this.productId = productCreatedEvent.getProductId();
		this.price = productCreatedEvent.getPrice();
		this.title = productCreatedEvent.getTitle();
		this.quantity = productCreatedEvent.getQuantity();
	}


	@EventSourcingHandler
	public void on(ProductReservedEvent productReservedEvent) {
		this.quantity -= productReservedEvent.getQuantity();
	}



}
