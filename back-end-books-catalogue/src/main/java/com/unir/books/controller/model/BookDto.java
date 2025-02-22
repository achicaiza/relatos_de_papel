package com.unir.books.controller.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BookDto {

    private String title;
    private String author;
    private String category;
    private Float rating;
    private String isbn;
    private Integer stock;
    private Date datePublished;
    private Boolean visibility;
    private BigDecimal price;

}
