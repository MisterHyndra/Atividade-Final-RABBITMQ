package br.com.facul.rabbitmq.dto;

public record EmailBatchRequest(String subject, String messageBody) {
}