package com.hieutt.blogRESTapi.service.impl;

import com.hieutt.blogRESTapi.dto.CategoryDto;
import com.hieutt.blogRESTapi.dto.CommentDto;
import com.hieutt.blogRESTapi.entity.Category;
import com.hieutt.blogRESTapi.entity.Comment;
import com.hieutt.blogRESTapi.exception.ResourceNotFoundException;
import com.hieutt.blogRESTapi.repository.CategoryRepository;
import com.hieutt.blogRESTapi.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper mapper;


    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map((category -> mapToDto(category)))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = mapToEntity(categoryDto);
        Category newCategory = categoryRepository.save(category);
        return mapToDto(newCategory);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToDto(category);
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        if (Objects.nonNull(categoryDto.getName()) &&
                !"".equalsIgnoreCase(categoryDto.getName())) {
            category.setName(categoryDto.getName());
        }
        Category updatedCategory = categoryRepository.save(category);
        return mapToDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepository.delete(category);
    }

    // convert Entity into DTO
    private CategoryDto mapToDto(Category category) {
        // Mapping using ModelMapper
        CategoryDto categoryDto = mapper.map(category, CategoryDto.class);

//        CommentDto commentDto = new CommentDto();
//        commentDto.setId(comment.getId());
//        commentDto.setBody(comment.getBody());

        return categoryDto;
    }

    // convert DTO into Entity
    private Category mapToEntity(CategoryDto categoryDto) {
        // Mapping using ModelMapper
        Category category = mapper.map(categoryDto, Category.class);

//        Comment comment = new Comment();
//        comment.setBody(commentDto.getBody());

        return category;
    }
}
