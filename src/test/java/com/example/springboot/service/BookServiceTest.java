package com.example.springboot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.springboot.dto.book.BookDto;
import com.example.springboot.dto.book.BookDtoWithoutCategoryIds;
import com.example.springboot.dto.book.BookSearchParameters;
import com.example.springboot.dto.book.CreateBookRequestDto;
import com.example.springboot.exception.EntityNotFoundException;
import com.example.springboot.mapper.BookMapper;
import com.example.springboot.model.Book;
import com.example.springboot.model.Category;
import com.example.springboot.repository.book.BookRepository;
import com.example.springboot.repository.book.BookSpecificationBuilder;
import com.example.springboot.repository.category.CategoryRepository;
import com.example.springboot.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookSpecificationBuilder specificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Verify saving book to DB")
    void save_ValidCreateBookRequestDto_ReturnsBookDto() {
        CreateBookRequestDto createBookRequestDto = createBookRequestDto();
        Book book = createBook();
        BookDto expected = createBookDto();

        when(bookMapper.toEntity(createBookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.save(createBookRequestDto);

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toEntity(createBookRequestDto);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify the correct list of booksDto was returned")
    void findAll_ValidPageable_ReturnsAllBooks() {
        Book book1 = createBook();
        Book book2 = createBook();
        book2.setId(2L);

        BookDto bookDto1 = createBookDto();
        BookDto bookDto2 = createBookDto();
        bookDto2.setId(2L);

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book1, book2);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book1)).thenReturn(bookDto1);
        when(bookMapper.toDto(book2)).thenReturn(bookDto2);

        List<BookDto> expected = List.of(bookDto1, bookDto2);
        List<BookDto> actual = bookService.findAll(pageable);

        assertEquals(expected.size(), actual.size());

        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(2)).toDto(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify the correct bookDto was returned by valid bookId")
    void findById_WithValidBookId_ShouldReturnValidBookDto() {
        Book book = createBook();
        BookDto expected = createBookDto();

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.findById(book.getId());
        assertEquals(expected, actual);

        verify(bookRepository, times(1)).findById(book.getId());
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify the exception was thrown when bookId was incorrect")
    void findById_WithInvalidBookId_ShouldThrowEntityException() {
        Long bookId = -10L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.findById(bookId)
        );

        String expected = "Can't find book by id " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);

        verify(bookRepository).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Verify the bookDto was updated correctly "
            + "by valid bookId and bookRequestDto")
    void updateById_WithValidBookId_ReturnsBookDto() {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        bookRequestDto.setAuthor("Orwell");
        bookRequestDto.setTitle("1984");
        Book book = createBook();
        BookDto expected = createBookDto();
        expected.setAuthor("Orwell");
        expected.setTitle("1984");

        when(bookMapper.toEntity(bookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.updateById(bookRequestDto, book.getId());

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify method delete was invoked")
    void deleteById() {
        Long bookId = 1L;
        bookService.deleteById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @DisplayName("Verify the search method is correct")
    @Test
    void search() {
        BookDto bookDto2 = createBookDto();
        bookDto2.setId(2L);
        bookDto2.setTitle("1984");
        bookDto2.setAuthor("Orwell");
        String[] titles = {"Harry Potter", "1984"};
        String[] authors = {"Rouling", "Orwell"};
        Book book1 = createBook();
        Book book2 = createBook();
        book2.setId(2L);
        book2.setTitle("1984");
        book2.setAuthor("Orwell");
        List<Book> books = List.of(book1, book2);
        BookDto bookDto1 = createBookDto();
        List<BookDto> expected = List.of(bookDto1, bookDto2);
        Pageable pageable = PageRequest.of(0, 5);
        Specification<Book> specification = mock(Specification.class);
        BookSearchParameters bookSearchParameters = new BookSearchParameters(titles, authors);
        when(specificationBuilder.build(bookSearchParameters)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(new PageImpl<>(books));
        when(bookMapper.toDto(any())).thenReturn(expected.get(0));

        bookService.search(bookSearchParameters, pageable);

        verify(specificationBuilder, times(1)).build(bookSearchParameters);
        verify(bookRepository, times(1)).findAll(specification, pageable);
        verify(bookMapper, times(2)).toDto(any());
        verifyNoMoreInteractions(specificationBuilder, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify the correct list of booksDto was returned with such category")
    void findAllByCategoryId_ValidCategoryId_ReturnsAllBooksWithSuchCategory() {
        Book book1 = createBook();
        Book book2 = createBook();
        book2.setId(2L);

        BookDtoWithoutCategoryIds bookDto1 = createBookDtoWithoutCategoryIds();
        BookDtoWithoutCategoryIds bookDto2 = createBookDtoWithoutCategoryIds();
        bookDto2.setId(2L);

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book1, book2);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        Long categoryId = 1L;

        when(bookRepository.findAllByCategoryId(categoryId, pageable)).thenReturn(books);
        when(bookMapper.toDtoWithoutCategories(book1)).thenReturn(bookDto1);
        when(bookMapper.toDtoWithoutCategories(book2)).thenReturn(bookDto2);

        List<BookDtoWithoutCategoryIds> expected = List.of(bookDto1, bookDto2);
        List<BookDtoWithoutCategoryIds> actual = bookService
                .findAllByCategoryId(categoryId, pageable);

        assertEquals(expected.size(), actual.size());

        verify(bookRepository, times(1))
                .findAllByCategoryId(categoryId, pageable);
        verify(bookMapper, times(2)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Harry Potter");
        book.setAuthor("Rouling");
        book.setIsbn("12345678");
        book.setDescription("Story about magic world");
        book.setPrice(BigDecimal.valueOf(100));
        book.setCategories(Set.of(createCategory()));
        return book;
    }

    private Category createCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");
        return category;
    }

    private BookDto createBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Harry Potter");
        bookDto.setAuthor("Rouling");
        bookDto.setIsbn("12345678");
        bookDto.setDescription("Story about magic world");
        bookDto.setPrice(BigDecimal.valueOf(100));
        bookDto.setCategories(Set.of(createCategory()));
        return bookDto;
    }

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setTitle("Harry Potter");
        bookRequestDto.setAuthor("Rouling");
        bookRequestDto.setIsbn("12345678");
        bookRequestDto.setDescription("Story about magic world");
        bookRequestDto.setPrice(BigDecimal.valueOf(100));
        bookRequestDto.setCategories(Set.of(createCategory()));
        return bookRequestDto;
    }

    private BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds() {
        BookDtoWithoutCategoryIds bookDto = new BookDtoWithoutCategoryIds();
        bookDto.setId(1L);
        bookDto.setTitle("Harry Potter");
        bookDto.setAuthor("Rouling");
        bookDto.setIsbn("12345678");
        bookDto.setDescription("Story about magic world");
        bookDto.setPrice(BigDecimal.valueOf(100));
        return bookDto;
    }
}
