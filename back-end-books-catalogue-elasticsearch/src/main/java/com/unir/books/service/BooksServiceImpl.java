package com.unir.books.service;

import com.unir.books.controller.model.BooksQueryFacetsResponse;
import com.unir.books.controller.model.BooksQueryResponse;
import com.unir.books.controller.model.CreateBookRequest;
import com.unir.books.data.DataAccessRepository;
import com.unir.books.data.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BooksServiceImpl implements BooksService {

    private final DataAccessRepository repository;

    @Override
    public BooksQueryResponse getBooks(String title, String description, String category, Boolean aggregate) {
        // Ahora por defecto solo devolverá libros visibles
        return repository.getBooks(title, description, category, aggregate);
    }

    @Override
    public List<Book> findAll(){
        return repository.findAll();
    }

    @Override
    public Book getBook(String productId) {
        return repository.findById(productId).orElse(null);
    }

    @Override
    public Boolean removeBook(String productId) {

        Book book = repository.findById(productId).orElse(null);
        if (book != null) {
            repository.delete(book);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    public Book createBook(CreateBookRequest request) {

        if (request != null &&
                StringUtils.hasLength(request.getTitle().trim()) &&
                StringUtils.hasLength(request.getAuthor().trim()) &&
                StringUtils.hasLength(request.getDescription().trim()) &&
                StringUtils.hasLength(request.getCategory().trim()) &&
                StringUtils.hasLength(request.getIsbn().trim()) &&
                request.getVisibility() != null &&
                request.getPrice() != null &&
                request.getStock() != null &&
                request.getRating() != null &&
                request.getDatePublished() != null) {
            Book book = Book.builder().title(request.getTitle()).author(request.getAuthor()).description(request.getDescription())
                    .category(request.getCategory()).visibility(request.getVisibility()).price(request.getPrice())
                    .stock(request.getStock()).datePublished(request.getDatePublished()).rating(request.getRating())
                    .isbn(request.getIsbn()).build();

            return repository.save(book);
        } else {
            return null;
        }
    }

    @Override
    public BooksQueryFacetsResponse getBookFacets(List<String> categories, List<String> authors, List<String> ratingRanges, List<String> priceRanges,
                                                  String title, String description, String page) {
        return repository.findBooks(
                categories,
                authors,
                ratingRanges,
                priceRanges,
                title,
                description,
                page);

    }

}
