package com.unir.orders.service;

import com.unir.orders.data.model.Order;
import com.unir.orders.controller.model.OrderRequest;
import java.util.List;

public interface OrdersService {
	
	Order createOrder(OrderRequest request);

	Order getOrder(String id);

	List<Order> getOrders();

}
