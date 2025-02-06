package com.unir.books.data.model;

import com.unir.books.controller.model.BookDto;
import com.unir.books.data.utils.Consts;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = Consts.TITLE)
    private String title;

    @Column(name = Consts.AUTHOR)
    private String author;

    @Column(name = Consts.CATEGORY)
    private String category;

    @Column(name = Consts.RATING)
    private Float rating;

    @Column(name = Consts.ISBN)
    private String isbn;

    @Column(name = Consts.STOCK)
    private Integer stock;

    @Column(name = Consts.DATE_PUBLISHED)
    private Date datePublished;

    @Column(name = Consts.VISIBILITY)
    private Boolean visibility;

    public void update(BookDto bookDto) {
        this.title = bookDto.getTitle();
        this.author = bookDto.getAuthor();
        this.category = bookDto.getCategory();
        this.rating = bookDto.getRating();
        this.isbn = bookDto.getIsbn();
        this.stock = bookDto.getStock();
        this.datePublished = bookDto.getDatePublished();
        this.visibility = bookDto.getVisibility();
    }

}
