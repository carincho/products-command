package com.carincho.course.springcloud.kafka.command.handlers;

import com.carincho.course.springcloud.kafka.command.models.Command;
import com.carincho.course.springcloud.kafka.command.models.dto.ProductDto;
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

    @Bean
    public Consumer<Command<ProductDto>> handleCommands() {

        return cmd -> {
            logger.info("Comando recibido y consumido con exito: type{}, body={}", cmd.type(), cmd.body());
        };
    }
}
