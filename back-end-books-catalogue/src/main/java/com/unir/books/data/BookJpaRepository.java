package com.unir.books.data;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.unir.books.data.model.Book;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface BookJpaRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findByIsbn(String isbn);

    List<Book> findByCategory(String category);

    List<Book> findByRating(int rating);

    List<Book> findByDatePublished(Date datePublished);

}
