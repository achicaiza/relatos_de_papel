package com.unir.orders.service;

import com.unir.orders.controller.model.OrderRequest;
import com.unir.orders.data.OrderJpaRepository;
import com.unir.orders.data.model.Order;
import com.unir.orders.facade.BooksFacade;
import com.unir.orders.facade.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    //Inyeccion por campo (field injection). Es la menos recomendada.
    private final BooksFacade booksFacade;

    //Inyeccion por campo (field injection). Es la menos recomendada.
    private final OrderJpaRepository repository;

    @Override
    public Order createOrder(OrderRequest request) {

        List<Book> books = request.getBooks().stream().map(booksFacade::getBook).filter(Objects::nonNull).toList();

        if (books.size() != request.getBooks().size()
                || books.stream().anyMatch(book -> !book.getVisibility())
                || books.stream().anyMatch(book -> (book.getStock() <= 0))
        ) {
            return null;
        } else {
            Order order = Order.builder().books(books.stream().map(Book::getId).collect(Collectors.toList())).build();
            repository.save(order);
            return order;
        }
    }

    @Override
    public Order getOrder(String id) {
        return repository.findById(Long.valueOf(id)).orElse(null);
    }

    @Override
    public List<Order> getOrders() {
        List<Order> orders = repository.findAll();
        return orders.isEmpty() ? null : orders;
    }
}
