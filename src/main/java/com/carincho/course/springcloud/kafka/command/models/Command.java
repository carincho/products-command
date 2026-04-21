package com.carincho.course.springcloud.kafka.command.models;

public record Command<T>(String type, Long id, T body) {
}
