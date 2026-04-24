package com.carincho.course.springcloud.kafka.command.handlers;

import com.carincho.course.springcloud.kafka.command.models.Command;
import com.carincho.course.springcloud.kafka.command.models.CommandType;
import com.carincho.course.springcloud.kafka.command.models.Reply;
import com.carincho.course.springcloud.kafka.command.models.ReplyStatus;
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
    public Function<Message<Command<ProductDto>>, Message<Reply<Object>>> handleCommands() {
//        return cmd -> {Aqui ya no regresa el objeto comando ahora es un Message
        return msg -> {

            String correlationId = msg.getHeaders().get("correlationId", String.class);
            logger.info("Recibiendo Correlation id={}", correlationId);

            if(correlationId == null || correlationId.isBlank()) {
                return MessageBuilder
                        .withPayload(new Reply<>(ReplyStatus.ERROR, "Missing correlationId", null))
                        .build();

            }
            //Ahora el tipe se obtiene del payload de message
            Command<ProductDto> cmd = msg.getPayload();


            Reply<Object> reply = switch (cmd.type()) {


                case CommandType.CREATE -> {
                    if (cmd.body() == null) {

                        logger.error("Create empty body");
                        yield  new Reply<>(ReplyStatus.ERROR, "CREATE EMPTY BODY", null);
                    }

                   ProductDto productSave =  service.create(cmd.body());

                    logger.info("Create product name={}, price={}", productSave.name(), productSave.price());
                    yield  new Reply<>(ReplyStatus.SUCCESS, "CREATE PRODUCT", productSave);

                }
                case CommandType.READ -> {

                    if (cmd.id() == null) {
                        logger.warn("Id is required");
                        yield  new Reply<>(ReplyStatus.ERROR, "ID IS REQUIRED", null);
                    }
                    ProductDto dto = service.findById(cmd.id());
                    logger.info("Read product by id");
                    yield dto == null ?
                            new Reply<>(ReplyStatus.ERROR, "PRODUCT NOT FOUND", null):
                            new Reply<>(ReplyStatus.SUCCESS, "READ PRODUCT", dto);


                }

                case CommandType.READ_ALL -> {
                    logger.info("Read all products");
                    yield  new Reply<>(ReplyStatus.SUCCESS, "READ ALL PRODUCTS", service.findAll());
                }
                case CommandType.UPDATE -> {
                    if(cmd.body() == null || cmd.id() == null) {
                        logger.warn("Id and body is required");
                        yield  new Reply<>(ReplyStatus.ERROR, "ID AND BODY IS REQUIRED", null);
                    }
                    ProductDto dto = service.update(cmd.id(), cmd.body());
                    if(dto != null) {
                        logger.info("Update product name={}, price={}", dto.name(), dto.price());
                        yield  new Reply<>(ReplyStatus.SUCCESS, "UPDATE PRODUCT", dto);
                    } else {
                        logger.info("Product not foound");
                        yield   new Reply<>(ReplyStatus.ERROR, "Product not foound", null);
                    }

                }
                case CommandType.DELETE -> {

                    if(cmd.id() == null) {
                        logger.warn("Id is required");
                        yield  new Reply<>(ReplyStatus.ERROR, "ID IS REQUIRED", null);
                    }

                    boolean result = service.delete(cmd.id());
                    logger.info("Delete product");
                    yield result ? new Reply<>(ReplyStatus.SUCCESS, "DELETE PRODUCT", "Deleted"):
                    new Reply<>(ReplyStatus.ERROR, "PRODUCT NOT FOUND", null);


                }

                default -> {
                    logger.warn("Unknown command type={}", cmd.type());
                    yield  new Reply<>(ReplyStatus.ERROR, "Unknown command type", null);
                }
            };

            return MessageBuilder.withPayload(reply).
                    setHeader("correlationId", correlationId).
                    build();

        };
    }
}
