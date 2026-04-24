package com.carincho.course.springcloud.kafka.command.models.mapper;

import com.carincho.course.springcloud.kafka.command.entities.Product;
import com.carincho.course.springcloud.kafka.command.models.dto.ProductDto;

public final class Mappers {

    private  Mappers() {
    }


    static public ProductDto toDto(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }

    static public Product toEntity(ProductDto productDto) {
       Product entity = new Product(productDto.name(), productDto.price());
       entity.setId(productDto.id());
       return entity;
    }
}
