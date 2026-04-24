package com.carincho.course.springcloud.kafka.command.handlers;

import com.carincho.course.springcloud.kafka.command.models.Command;
import com.carincho.course.springcloud.kafka.command.models.Reply;
import com.carincho.course.springcloud.kafka.command.models.dto.ProductDto;
import com.carincho.course.springcloud.kafka.command.models.mapper.Mappers;
import com.carincho.course.springcloud.kafka.command.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.function.Consumer;
import java.util.function.Function;

//Es una clase que tiene que ser de configuracion
@Configuration
public class ProductCommandConsumer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProductService service;

    public ProductCommandConsumer(ProductService service) {
        this.service = service;
    }

    @Bean
    //Consumer por que solo recibia ahora recibe y es ambos ahora se usa Function
    //Esto era cuando solo se consumia
//    public Consumer<Command<ProductDto>> handleCommands() {
    //Esto es cuando se recibe y se devuelve algo el Reply lo ponemos mas generico
    public Function<Message<Command<ProductDto>>, Message<Reply<?>>> handleCommands() {


//        return cmd -> {Aqui ya no regresa el objeto comando ahora es un Message
        return msg -> {
            //Ahora el tipe se obtiene del payload de message
            Command<ProductDto> cmd = msg.getPayload();
            String type = cmd.type() == null ? "": cmd.type().toUpperCase();

            Reply<?> reply;
            switch (type) {


                case "CREATE" -> {
                    if (cmd.body() == null) {

                        logger.error("Create empty body");
                        reply =  new Reply<>("ERROR", "CREATE EMPTY BODY", null);
                    }

                   ProductDto productSave =  service.create(cmd.body());

                    logger.info("Create product name={}, price={}", productSave.name(), productSave.price());
                    reply =  new Reply<>("SUCCESS", "CREATE PRODUCT", productSave);

                }
                case "READ" -> {

                    if (cmd.id() == null) {
                        logger.warn("Id is required");
                        reply =  new Reply<>("ERROR", "ID IS REQUIRED", null);
                    }
                    ProductDto dto = service.findById(cmd.id());
                    reply = dto == null ?
                            new Reply<>("ERROR", "PRODUCT NOT FOUND", null):
                            new Reply<>("SUCCESS", "READ PRODUCT", dto);
                    logger.info("Read product by id");

                }

                case "READ_ALL" -> {
                    reply =  new Reply<>("SUCCESS", "READ ALL PRODUCTS", service.findAll());

                    logger.info("Read all products");
                }
                case "UPDATE" -> {
                    if(cmd.body() == null || cmd.id() == null) {
                        logger.warn("Id and body is required");
                        reply =  new Reply<>("ERROR", "ID AND BODY IS REQUIRED", null);
                    }
                    ProductDto dto = service.update(cmd.id(), cmd.body());
                    if(dto != null) {

                       reply =  new Reply<>("SUCCESS", "UPDATE PRODUCT", dto);
                        logger.info("Update product name={}, price={}", dto.name(), dto.price());

                    } else {
                        reply =   new Reply<>("ERROR", "Product not foound", null);
                        logger.info("Product not foound");

                    }

                }
                case "DELETE" -> {

                    if(cmd.id() == null) {
                        logger.warn("Id is required");
                        reply =  new Reply<>("ERROR", "ID IS REQUIRED", null);
                    }

                    boolean result = service.delete(cmd.id());
                    reply = result ? new Reply<>("SUCCESS", "DELETE PRODUCT", "Deleted"):
                    new Reply<>("ERROR", "PRODUCT NOT FOUND", null);

                    logger.info("Delete product");
                }

                default -> {
                    logger.warn("Unknown command type={}", type);
                    reply =  new Reply<>("ERROR", "Unknown command type", null);
                }
            }

            String correlationId = msg.getHeaders().get("correlationId", String.class);
            logger.info("Recibiendo Correlation id={}", correlationId);


            MessageBuilder<Reply<?>>out = MessageBuilder.withPayload(reply);
            if(correlationId != null) {
                out.setHeader("correlationId", correlationId);
            }
            return out.build();

        };
    }
}
