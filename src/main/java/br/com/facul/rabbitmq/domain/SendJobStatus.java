package br.com.facul.rabbitmq.domain;

public enum SendJobStatus {
	QUEUED,
	PROCESSING,
	COMPLETED,
	COMPLETED_WITH_ERRORS,
	FAILED
}