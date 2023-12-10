package com.demo.product.core.errorhandling;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;

public class ProductsServiceEventsErrorHandler implements ListenerInvocationErrorHandler {
	// to propagate exception raised in event handler to roll back trx
	@Override
	public void onError(Exception exception, EventMessage<?> event, EventMessageHandler eventHandler) throws Exception {
		throw exception;
	}

}
