package com.hieutt.blogRESTapi.service;

import com.hieutt.blogRESTapi.dto.CategoryDto;
import com.hieutt.blogRESTapi.entity.Category;

import java.net.URI;
import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto getCategoryById(Long id);

    CategoryDto updateCategory(Long id, CategoryDto categoryDto);

    void deleteCategory(Long id);
}
