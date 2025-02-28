package com.unir.books.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookRequest {

	private String title;
	private String author;
	private String category;
	private String description;
	private Boolean visibility;
	private Float rating;
	private String isbn;
	private Integer stock;
	private Date datePublished;
	private Double price;
}
