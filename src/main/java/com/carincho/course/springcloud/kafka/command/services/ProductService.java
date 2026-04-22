package com.carincho.course.springcloud.kafka.command.services;

import com.carincho.course.springcloud.kafka.command.models.dto.ProductDto;

public interface ProductService {

    ProductDto create(ProductDto productDto);
    /*ProductDto update(ProductDto productDto);
    ProductDto findById(Long id);
    ProductDto findByName(String name);*/
}
