package com.jeanbarcellos.project110.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @JsonIgnore
    private Long id;

    private String name;
    private String description;
    private Double price;
    private Long categoryId;

}
