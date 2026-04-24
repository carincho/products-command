package com.carincho.course.springcloud.kafka.command.services;

import com.carincho.course.springcloud.kafka.command.models.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> findAll();
    ProductDto findById(Long id);
    ProductDto create(ProductDto productDto);
    ProductDto update(Long id, ProductDto productDto);
    boolean delete(Long id);

}
