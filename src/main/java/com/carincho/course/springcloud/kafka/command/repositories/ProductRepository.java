package com.carincho.course.springcloud.kafka.command.repositories;

import com.carincho.course.springcloud.kafka.command.entities.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository <Product, Long>{
}
