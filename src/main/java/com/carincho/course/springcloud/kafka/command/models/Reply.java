package com.carincho.course.springcloud.kafka.command.models;

public record Reply<T>(ReplyStatus status, String message, T body) {
}