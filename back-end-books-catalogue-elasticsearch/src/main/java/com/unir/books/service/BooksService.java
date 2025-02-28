package com.unir.books.service;

import com.unir.books.controller.model.BooksQueryFacetsResponse;
import com.unir.books.controller.model.BooksQueryResponse;
import com.unir.books.controller.model.CreateBookRequest;
import com.unir.books.data.model.Book;

import java.util.List;

public interface BooksService {

    BooksQueryResponse getBooks(String title, String description, String category, Boolean aggregate);

    Book getBook(String bookId);

    Boolean removeBook(String bookId);

    Book createBook(CreateBookRequest request);

    BooksQueryFacetsResponse getBookFacets(List<String> categories,
                                           List<String> authors,
                                           List<String> ratingRanges,
                                           List<String> priceRanges,
                                           String title,
                                           String description,
                                           String page);

    List<Book> findAll();

}
