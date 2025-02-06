package com.unir.books.data;

import com.unir.books.data.model.Book;
import com.unir.books.data.utils.Consts;
import com.unir.books.data.utils.SearchCriteria;
import com.unir.books.data.utils.SearchOperation;
import com.unir.books.data.utils.SearchStatement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final BookJpaRepository repository;

    public List<Book> getBooks() {
        return repository.findAll();
    }

    public Book getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Book save(Book book) {
        return repository.save(book);
    }

    public void delete(Book book) {
        repository.delete(book);
    }

    public List<Book> search(String title, String author, String category,
                             String isbn, Boolean visible, Integer rating, Date publicationDate) {
        SearchCriteria<Book> spec = new SearchCriteria<>();

        if (StringUtils.isNotBlank(title)) {
            spec.add(new SearchStatement(Consts.TITLE, title, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(author)) {
            spec.add(new SearchStatement(Consts.AUTHOR, author, SearchOperation.EQUAL));
        }

        if (StringUtils.isNotBlank(category)) {
            spec.add(new SearchStatement(Consts.CATEGORY, category, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(isbn)) {
            spec.add(new SearchStatement(Consts.ISBN, isbn, SearchOperation.MATCH));
        }

        if (rating != null) {
            spec.add(new SearchStatement(Consts.RATING, rating, SearchOperation.MATCH));
        }

        if (publicationDate != null) {
            spec.add(new SearchStatement(Consts.DATE_PUBLISHED, publicationDate, SearchOperation.MATCH));
        }


        if (visible != null) {
            spec.add(new SearchStatement(Consts.VISIBILITY, visible, SearchOperation.EQUAL));
        }

        return repository.findAll(spec);
    }

}
