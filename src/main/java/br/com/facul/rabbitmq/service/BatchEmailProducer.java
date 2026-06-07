package br.com.facul.rabbitmq.service;

import br.com.facul.rabbitmq.dto.EmailDispatchMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BatchEmailProducer {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchEmailProducer.class);

	private final RabbitTemplate rabbitTemplate;

	@Value("${app.rabbit.exchange}")
	private String exchangeName;

	@Value("${app.rabbit.routing-key}")
	private String routingKey;

	public BatchEmailProducer(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void publish(EmailDispatchMessage message) {
		rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
		LOGGER.info("Mensagem publicada na exchange {} com routing key {} para o job #{}", exchangeName, routingKey, message.sendJobId());
	}
}