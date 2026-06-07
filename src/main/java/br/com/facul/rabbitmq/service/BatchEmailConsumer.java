package br.com.facul.rabbitmq.service;

import br.com.facul.rabbitmq.domain.SendJob;
import br.com.facul.rabbitmq.dto.EmailDispatchMessage;
import br.com.facul.rabbitmq.repository.SendJobRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BatchEmailConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(BatchEmailConsumer.class);

	private final SendJobRepository sendJobRepository;
	private final EmailSenderService emailSenderService;

	@Value("${app.rabbit.queue}")
	private String queueName;

	public BatchEmailConsumer(SendJobRepository sendJobRepository, EmailSenderService emailSenderService) {
		this.sendJobRepository = sendJobRepository;
		this.emailSenderService = emailSenderService;
	}

	@Transactional
	@RabbitListener(queues = "${app.rabbit.queue}")
	public void consume(EmailDispatchMessage message) {
		SendJob job = sendJobRepository.findById(message.sendJobId())
			.orElseThrow(() -> new IllegalArgumentException("Job não encontrado para processamento."));

		LOGGER.info("Mensagem recebida da fila {} para o job #{}", queueName, job.getId());
		job.markProcessing();
		sendJobRepository.save(job);

		List<String> recipients = Arrays.stream(job.getRecipientSnapshot().split("\\R"))
			.map(String::trim)
			.filter(email -> !email.isBlank())
			.toList();

		int success = 0;
		int failed = 0;
		List<String> details = new ArrayList<>();

		for (String recipient : recipients) {
			try {
				emailSenderService.sendEmail(recipient, job.getSubject(), job.getMessageBody());
				success++;
				details.add("OK -> " + recipient);
				LOGGER.info("E-mail enviado para {} no job #{}", recipient, job.getId());
			} catch (Exception ex) {
				failed++;
				details.add("ERRO -> " + recipient + " | " + ex.getMessage());
				LOGGER.error("Falha ao enviar e-mail para {} no job #{}", recipient, job.getId(), ex);
			}
		}

		job.markFinished(success, failed, String.join(System.lineSeparator(), details));
		sendJobRepository.save(job);
		LOGGER.info(
			"Processamento finalizado para o job #{} | sucesso={} | falha={}",
			job.getId(),
			success,
			failed
		);
	}
}