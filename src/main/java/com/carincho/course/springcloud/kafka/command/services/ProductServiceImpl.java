package com.carincho.course.springcloud.kafka.command.services;

import com.carincho.course.springcloud.kafka.command.entities.Product;
import com.carincho.course.springcloud.kafka.command.models.dto.ProductDto;
import com.carincho.course.springcloud.kafka.command.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private  ProductRepository  productRepository;

    @Override
    @Transactional
    public ProductDto create(ProductDto productDto) {

        Product product = new Product(productDto.name(), productDto.price());

        Product productNew = productRepository.save(product);
        return new ProductDto(productNew.getId(), productNew.getName(), productNew.getPrice());
    }
}
