package com.carincho.course.springcloud.kafka.command.handlers;

import com.carincho.course.springcloud.kafka.command.models.Command;
import com.carincho.course.springcloud.kafka.command.models.dto.ProductDto;
import com.carincho.course.springcloud.kafka.command.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

//Es una clase que tiene que ser de configuracion
@Configuration
public class ProductCommandConsumer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProductService service;

    public ProductCommandConsumer(ProductService service) {
        this.service = service;
    }

    @Bean
    public Consumer<Command<ProductDto>> handleCommands() {

        return cmd -> {
            String type = cmd.type() == null ? "": cmd.type().toUpperCase();
            switch (type) {

                case "CREATE" -> {
                    if (cmd.body() == null) {

                        logger.error("Create empty body");
                        return;
                    }

                    ProductDto productDto = cmd.body();
                    service.create(productDto);

                    logger.info("Create product name={}, price={}", productDto.name(), productDto.price());

                }
                case "UPDATE" -> {
                    logger.info("Update product name={}, price={}", null, null);
                }
                case "DELETE" -> {
                    logger.info("Delete product name={}, price={}");
                }
                case "READ_ALL" -> {
                    logger.info("Read all products");
                }
                case "READ_ONE" -> {
                    logger.info("Read product by id");

                }
                default -> logger.warn("Unknown command type={}", type);

            }
        };
    }
}
