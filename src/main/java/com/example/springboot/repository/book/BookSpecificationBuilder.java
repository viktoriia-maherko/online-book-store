package com.example.springboot.repository.book;

import com.example.springboot.dto.book.BookSearchParameters;
import com.example.springboot.model.Book;
import com.example.springboot.repository.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final BookSpecificationProviderManager bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);
        String[] authors = searchParameters.authors();
        if (authors != null && authors.length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider("author")
                    .getSpecification(authors));
        }
        String[] titles = searchParameters.titles();
        if (titles != null && titles.length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider("title")
                    .getSpecification(titles));
        }
        return spec;
    }
}
