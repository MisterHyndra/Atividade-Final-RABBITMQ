package br.com.facul.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	@Value("${app.rabbit.queue}")
	private String queueName;

	@Value("${app.rabbit.exchange}")
	private String exchangeName;

	@Value("${app.rabbit.routing-key}")
	private String routingKey;

	@Bean
	public Queue emailQueue() {
		return new Queue(queueName, true);
	}

	@Bean
	public DirectExchange emailExchange() {
		return new DirectExchange(exchangeName);
	}

	@Bean
	public Binding emailBinding(Queue emailQueue, DirectExchange emailExchange) {
		return BindingBuilder.bind(emailQueue).to(emailExchange).with(routingKey);
	}

	@Bean
	public MessageConverter jacksonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}