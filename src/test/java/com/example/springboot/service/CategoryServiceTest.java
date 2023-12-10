package com.example.springboot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.springboot.dto.category.CategoryDto;
import com.example.springboot.dto.category.CreateCategoryRequestDto;
import com.example.springboot.exception.EntityNotFoundException;
import com.example.springboot.mapper.CategoryMapper;
import com.example.springboot.model.Category;
import com.example.springboot.repository.category.CategoryRepository;
import com.example.springboot.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
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

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Verify the correct list of categoriesDto was returned")
    void findAll_ValidPageable_ReturnsAllCategories() {
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Non-fiction");
        CategoryDto categoryDto1 = createCategoryDto();
        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto1.setId(2L);
        categoryDto2.setName("Non-fiction");
        Category category1 = createCategory();

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category1)).thenReturn(categoryDto1);
        when(categoryMapper.toDto(category2)).thenReturn(categoryDto2);
        List<CategoryDto> expected = List.of(categoryDto1, categoryDto2);
        List<CategoryDto> actual = categoryService.findAll(pageable);

        assertEquals(expected, actual);

        verify(categoryRepository, times(1)).findAll(pageable);
        verify(categoryMapper, times(2)).toDto(any());
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify the correct categoryDto was returned by valid bookId")
    void findById_WithValidCategoryId_ShouldReturnValidCategoryDto() {
        Category category = createCategory();
        CategoryDto expected = createCategoryDto();

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.getById(category.getId());

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(category.getId());
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify the exception was thrown when categoryId was incorrect")
    void findById_WithInvalidCategoryId_ShouldReturnValidCategoryDto() {
        Long categoryId = -10L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(categoryId)
        );

        String expected = "Can't find category by id " + categoryId;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Verify saving category to DB")
    void save_ValidCreateCategoryRequestDto_ReturnsCategoryDto() {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        Category category = createCategory();
        CategoryDto expected = createCategoryDto();

        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toEntity(categoryRequestDto)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.save(categoryRequestDto);

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toEntity(categoryRequestDto);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify the categoryDto was updated correctly "
            + "by valid categoryId and categoryRequestDto")
    void update_ValidCategoryIdAndCategoryRequestDto_ShouldReturnCategoryDto() {
        CreateCategoryRequestDto requestDto = createCategoryRequestDto();
        requestDto.setName("Non-fiction");
        Category category = createCategory();
        CategoryDto expected = createCategoryDto();
        expected.setName("Non-fiction");
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        Long categoryId = category.getId();
        CategoryDto actual = categoryService.update(categoryId, requestDto);

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toEntity(requestDto);
        verify(categoryMapper, times(1)).toDto(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify method delete was invoked")
    void deleteById_ValidCategoryId_Success() {
        Long categoryId = 1L;

        categoryService.deleteById(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }

    private CategoryDto createCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Fiction");
        return categoryDto;
    }

    private Category createCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");
        return category;
    }

    private CreateCategoryRequestDto createCategoryRequestDto() {
        CreateCategoryRequestDto categoryRequestDto = new CreateCategoryRequestDto();
        categoryRequestDto.setName("Fiction");
        return categoryRequestDto;
    }
}
