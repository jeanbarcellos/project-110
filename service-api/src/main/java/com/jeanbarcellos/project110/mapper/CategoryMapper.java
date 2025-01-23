package com.jeanbarcellos.project110.mapper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jeanbarcellos.project110.dto.CategoryRequest;
import com.jeanbarcellos.project110.dto.CategoryResponse;
import com.jeanbarcellos.project110.entity.Category;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final ModelMapper modelMapper;

    public Category toEntity(CategoryRequest request) {
        return modelMapper.map(request, Category.class);
    }

    public CategoryResponse toResponse(Category category) {
        return modelMapper.map(category, CategoryResponse.class);
    }

    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)
                .toList();
    }

    public Category copy(Category destination, CategoryRequest source) {
        this.modelMapper.map(source, destination);
        return destination;
    }

}
