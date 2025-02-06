package com.unir.books.service;

import com.unir.books.controller.model.BookDto;
import com.unir.books.controller.model.CreateBookRequest;
import com.unir.books.data.model.Book;

import java.util.Date;
import java.util.List;

public interface BooksService {

    List<Book> getBooks(String title, String author, String category,
                        String isbn, Boolean visible, Integer rating, Date publicationDate);

    Book getBook(String bookId);

    Boolean removeBook(String bookId);

    Book createBook(CreateBookRequest request);

    Book updateBook(String bookId, String updateRequest);

    Book updateBook(String bookId, BookDto updateRequest);

}
