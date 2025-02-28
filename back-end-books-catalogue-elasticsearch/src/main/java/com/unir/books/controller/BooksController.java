package com.unir.books.controller;

import java.util.List;
import java.util.Map;

import com.unir.books.controller.model.BooksQueryFacetsResponse;
import com.unir.books.controller.model.BooksQueryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unir.books.data.model.Book;
import com.unir.books.controller.model.CreateBookRequest;
import com.unir.books.service.BooksService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BooksController {

	private final BooksService service;

	@GetMapping("/books")
	public ResponseEntity<BooksQueryResponse> getBooks(
			@RequestHeader Map<String, String> headers,
			@RequestParam(required = false) String description, 
			@RequestParam(required = false) String title,
			@RequestParam(required = false) String category,
			@RequestParam(required = false, defaultValue = "false") Boolean aggregate) {

		log.info("headers: {}", headers);
		BooksQueryResponse products = service.getBooks(title, description, category, aggregate);
		return ResponseEntity.ok(products);
	}

	@GetMapping("/books/{bookId}")
	public ResponseEntity<Book> getBook(@PathVariable String bookId) {

		log.info("Request received for book {}", bookId);
		Book book = service.getBook(bookId);

		if (book != null) {
			return ResponseEntity.ok(book);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@DeleteMapping("/books/{bookId}")
	public ResponseEntity<Void> deleteProduct(@PathVariable String bookId) {

		Boolean removed = service.removeBook(bookId);

		if (Boolean.TRUE.equals(removed)) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@PostMapping("/books")
	public ResponseEntity<Book> getProduct(@RequestBody CreateBookRequest request) {

		Book createdProduct = service.createBook(request);

		if (createdProduct != null) {
			return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
		} else {
			return ResponseEntity.badRequest().build();
		}

	}

	@GetMapping("/facets")
	public ResponseEntity<BooksQueryFacetsResponse> getBooks(
			@RequestParam(required = false) List<String> categories,
			@RequestParam(required = false) List<String> authors,
			@RequestParam(required = false) List<String> ratingRanges,
			@RequestParam(required = false) List<String> priceRanges,
			@RequestParam(required = false) String title,
			@RequestParam(required = false) String description,
			@RequestParam(required = false, defaultValue = "0") String page) {

		BooksQueryFacetsResponse response = service.getBookFacets(
				categories,
				authors,
				ratingRanges,
				priceRanges,
				title,
				description,
				page);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/books2")
	public ResponseEntity<List<Book>> getBooks() {

		List<Book> books = service.findAll();
		return ResponseEntity.ok(books);
	}

}
