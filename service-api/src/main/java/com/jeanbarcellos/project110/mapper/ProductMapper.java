package com.jeanbarcellos.project110.mapper;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.jeanbarcellos.project110.dto.ProductRequest;
import com.jeanbarcellos.project110.dto.ProductResponse;
import com.jeanbarcellos.project110.entity.Product;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ModelMapper modelMapper;

    public Product toEntity(ProductRequest request) {
        return modelMapper.map(request, Product.class);
    }

    public ProductResponse toResponse(Product entity) {
        return modelMapper.map(entity, ProductResponse.class);
    }

    public List<ProductResponse> toResponseList(List<Product> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public Product copy(Product entity, ProductRequest source) {
        this.modelMapper.map(source, entity);

        // entity.setName(source.getName());
        // entity.setDescription(source.getDescription());
        // entity.setPrice(source.getPrice());

        return entity;
    }

}
