package com.carincho.course.springcloud.kafka.command.services;

import com.carincho.course.springcloud.kafka.command.entities.Product;
import com.carincho.course.springcloud.kafka.command.models.dto.ProductDto;
import com.carincho.course.springcloud.kafka.command.models.mapper.Mappers;
import com.carincho.course.springcloud.kafka.command.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private  ProductRepository  productRepository;

    @Override
    @Transactional
    public ProductDto create(ProductDto productDto) {

        return  Mappers.toDto(productRepository.save(Mappers.toEntity(productDto)));
    }

    @Override
    @Transactional
    public ProductDto update(Long id, ProductDto productDto) {

        Optional<Product> entityOpt = productRepository.findById(id);

        if(entityOpt.isPresent()){
            Product entity = entityOpt.orElse(null);
            entity.setName(productDto.name());
            entity.setPrice(productDto.price());
            return Mappers.toDto(productRepository.save(entity));

        }

        return null;

    }

    @Override
    @Transactional
    public boolean delete(Long id) {

        boolean result = productRepository.existsById(id);
        if(result){
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {
        return productRepository.findById(id).map(Mappers::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return  productRepository.findAll().stream()
                .map(Mappers::toDto)
                .toList();
    }

}
