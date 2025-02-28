package com.unir.books.data;

import com.unir.books.controller.model.AggregationDetails;
import com.unir.books.controller.model.BooksQueryFacetsResponse;
import com.unir.books.controller.model.BooksQueryResponse;
import com.unir.books.data.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DataAccessRepository {

    @Value("${server.fullAddress}")
    private String serverFullAddress;

    // Esta clase (y bean) es la unica que usan directamente los servicios para
    // acceder a los datos.
    private final BookRepository bookRepository;
    private final ElasticsearchOperations elasticClient;

    private final String[] descriptionSearchFields = {"description", "description._2gram", "description._3gram"};

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Boolean delete(Book book) {
        bookRepository.delete(book);
        return Boolean.TRUE;
    }

    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @SneakyThrows
    public BooksQueryResponse getBooks(String title, String description, String category, Boolean aggregate) {

        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(category)) {
            querySpec.must(QueryBuilders.termQuery("category", category));
        }

        if (!StringUtils.isEmpty(title)) {
            querySpec.must(QueryBuilders.matchQuery("title", title));
        }

        if (!StringUtils.isEmpty(description)) {
            querySpec.must(QueryBuilders.multiMatchQuery(description, descriptionSearchFields).type("bool_prefix"));
        }

        // Si no he recibido ningún parámetro, busco todos los elementos.
        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        // Filtro implícito para asegurar que solo se muestran libros visibles
        querySpec.must(QueryBuilders.termQuery("visibility", true));

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        if (aggregate) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("Category Aggregation").field("category").size(1000));
            nativeSearchQueryBuilder.withMaxResults(0);
        }

        Query query = nativeSearchQueryBuilder.build();
        SearchHits<Book> result = elasticClient.search(query, Book.class);

        List<AggregationDetails> responseAggs = new LinkedList<>();

        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();
            ParsedStringTerms categoryAgg = (ParsedStringTerms) aggs.get("Category Aggregation");

            String queryParams = getQueryParams(title, description, category);
            categoryAgg.getBuckets()
                    .forEach(bucket -> responseAggs.add(
                            new AggregationDetails(
                                    bucket.getKey().toString(),
                                    (int) bucket.getDocCount())));
        }
        return new BooksQueryResponse(result.getSearchHits().stream().map(SearchHit::getContent).toList(), responseAggs);
    }

    /**
     * Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
     *
     * @param title       - titulo del libro
     * @param description - descripcion del libro
     * @param category    - categoria del libro
     * @return
     */
    private String getQueryParams(String title, String description, String category) {
        String queryParams = (StringUtils.isEmpty(title) ? "" : "&title=" + title)
                + (StringUtils.isEmpty(description) ? "" : "&description=" + description)
                + (StringUtils.isEmpty(category) ? "" : "&category=" + category);
        return queryParams.endsWith("&") ? queryParams.substring(0, queryParams.length() - 1) : queryParams;
    }


    @SneakyThrows
    public BooksQueryFacetsResponse findBooks(
            List<String> categories,
            List<String> authors,
            List<String> ratingRanges,
            List<String> priceRanges,
            String title,
            String description,
            String page) {

        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        // Filtro por categoría
        if (categories != null && !categories.isEmpty()) {
            categories.forEach(
                    category -> querySpec.must(QueryBuilders.termQuery("category", category))
            );
        }

        // Filtro por autor
        if (authors != null && !authors.isEmpty()) {
            authors.forEach(
                    author -> querySpec.must(QueryBuilders.termQuery("author", author))
            );
        }

        // Filtro por título
        if (!StringUtils.isEmpty(title)) {
            querySpec.must(QueryBuilders.matchQuery("title", title));
        }

        // Filtro por descripción con búsqueda difusa
        if (!StringUtils.isEmpty(description)) {
            querySpec.must(QueryBuilders.multiMatchQuery(description, descriptionSearchFields)
                    .type(MultiMatchQueryBuilder.Type.BOOL_PREFIX));
        }

        // Filtro por rango de calificación
        if (ratingRanges != null && !ratingRanges.isEmpty()) {
            ratingRanges.forEach(
                    rating -> {
                        String[] range = rating.contains("-") ? rating.split("-") : new String[]{};
                        if (range.length == 2) {
                            if ("".equals(range[0])) {
                                querySpec.must(QueryBuilders.rangeQuery("rating").to(range[1]).includeUpper(false));
                            } else {
                                querySpec.must(QueryBuilders.rangeQuery("rating").from(range[0]).to(range[1]).includeUpper(false));
                            }
                        } else if (range.length == 1) {
                            querySpec.must(QueryBuilders.rangeQuery("rating").from(range[0]));
                        }
                    }
            );
        }

        // Filtro por rango de precios
        if (priceRanges != null && !priceRanges.isEmpty()) {
            priceRanges.forEach(
                    price -> {
                        String[] range = price.contains("-") ? price.split("-") : new String[]{};

                        if (range.length == 2) {
                            if ("".equals(range[0])) {
                                querySpec.must(QueryBuilders.rangeQuery("price").to(range[1]).includeUpper(false));
                            } else {
                                querySpec.must(QueryBuilders.rangeQuery("price").from(range[0]).to(range[1]).includeUpper(false));
                            }
                        } else if (range.length == 1) {
                            querySpec.must(QueryBuilders.rangeQuery("price").from(range[0]));
                        }
                    }
            );
        }

        // Filtro por visibilidad (solo libros activos)
        querySpec.must(QueryBuilders.termQuery("visibility", true));

        // Si no hay filtros aplicados, mostrar todos los libros
        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        // Construcción de la consulta
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        // Agregaciones por términos (categoría, autor)
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .terms("categories").field("category").size(10000));

        // Agregaciones por rango (calificación, precio)
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .range("ratingRanges").field("rating")
                .addUnboundedTo("-3", 3.0)
                .addRange("3-4", 3.0, 4.0)
                .addUnboundedFrom("4-", 4.0));

        nativeSearchQueryBuilder.addAggregation(AggregationBuilders
                .range("priceRanges").field("price")
                .addUnboundedTo("-20", 20.0)
                .addRange("20-50", 20.0, 50.0)
                .addUnboundedFrom("50-", 50.0));

        // Paginación (5 resultados por página)
        nativeSearchQueryBuilder.withMaxResults(100);
        int pageInt = Integer.parseInt(page);
        if (pageInt >= 0) {
            nativeSearchQueryBuilder.withPageable(PageRequest.of(pageInt, 5));
        }

        // Ejecución de la consulta
        Query query = nativeSearchQueryBuilder.build();
        SearchHits<Book> result = elasticClient.search(query, Book.class);

        return new BooksQueryFacetsResponse(getResponseBooks(result), getResponseAggregations(result));
    }

    /**
     * Convierte los resultados de la búsqueda en una lista de libros.
     */
    private List<Book> getResponseBooks(SearchHits<Book> result) {
        return result.getSearchHits().stream().map(SearchHit::getContent).toList();
    }

    /**
     * Convierte las agregaciones de la búsqueda en un mapa de detalles de agregaciones.
     */
    private Map<String, List<AggregationDetails>> getResponseAggregations(SearchHits<Book> result) {

        Map<String, List<AggregationDetails>> responseAggregations = new HashMap<>();

        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();

            aggs.forEach((key, value) -> {
                responseAggregations.putIfAbsent(key, new LinkedList<>());

                if (value instanceof ParsedStringTerms parsedStringTerms) {
                    parsedStringTerms.getBuckets().forEach(bucket -> {
                        responseAggregations.get(key).add(new AggregationDetails(bucket.getKey().toString(), (int) bucket.getDocCount()));
                    });
                }

                if (value instanceof ParsedRange parsedRange) {
                    parsedRange.getBuckets().forEach(bucket -> {
                        responseAggregations.get(key).add(new AggregationDetails(bucket.getKeyAsString(), (int) bucket.getDocCount()));
                    });
                }
            });
        }
        return responseAggregations;
    }
}
