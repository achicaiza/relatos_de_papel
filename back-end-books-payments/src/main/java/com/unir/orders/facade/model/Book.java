package com.unir.orders.facade.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {
    private Long id;
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
