package com.unir.books.data.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "books", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Keyword, name = "author")
    private String author;

    @Field(type = FieldType.Keyword, name = "category")
    private String category;

    @Field(type = FieldType.Search_As_You_Type, name = "description")
    private String description;

    @Field(type = FieldType.Float, name = "rating")
    private Float rating;

    @Field(type = FieldType.Keyword, name = "isbn")
    private String isbn;

    @Field(type = FieldType.Integer, name = "stock")
    private Integer stock;

    @Field(type = FieldType.Date, name = "datePublished",
            format = DateFormat.date_time)
    private Date datePublished;

    @Field(type = FieldType.Boolean, name = "visibility")
    private Boolean visibility;

    @Field(type = FieldType.Double, name = "price")
    private Double price;

}
