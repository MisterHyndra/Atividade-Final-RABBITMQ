package br.com.facul.rabbitmq.service;

import br.com.facul.rabbitmq.domain.RecipientEmail;
import br.com.facul.rabbitmq.domain.SendJob;
import br.com.facul.rabbitmq.dto.EmailDispatchMessage;
import br.com.facul.rabbitmq.repository.RecipientEmailRepository;
import br.com.facul.rabbitmq.repository.SendJobRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailDispatchService {

	private final RecipientEmailRepository recipientEmailRepository;
	private final SendJobRepository sendJobRepository;
	private final BatchEmailProducer batchEmailProducer;

	public EmailDispatchService(
		RecipientEmailRepository recipientEmailRepository,
		SendJobRepository sendJobRepository,
		BatchEmailProducer batchEmailProducer
	) {
		this.recipientEmailRepository = recipientEmailRepository;
		this.sendJobRepository = sendJobRepository;
		this.batchEmailProducer = batchEmailProducer;
	}

	@Transactional
	public Long scheduleDispatch(String subject, String messageBody) {
		if (subject == null || subject.isBlank()) {
			throw new IllegalArgumentException("Informe o assunto da mensagem.");
		}

		if (messageBody == null || messageBody.isBlank()) {
			throw new IllegalArgumentException("Informe o conteúdo da mensagem.");
		}

		List<RecipientEmail> recipients = recipientEmailRepository.findAllByOrderByEmailAsc();
		if (recipients.isEmpty()) {
			throw new IllegalArgumentException("Cadastre pelo menos um e-mail antes de solicitar o envio.");
		}

		String snapshot = recipients.stream()
			.map(RecipientEmail::getEmail)
			.reduce((left, right) -> left + System.lineSeparator() + right)
			.orElseThrow();

		SendJob job = new SendJob(subject.trim(), messageBody.trim(), snapshot, recipients.size());
		sendJobRepository.save(job);

		batchEmailProducer.publish(new EmailDispatchMessage(job.getId()));
		return job.getId();
	}

	@Transactional(readOnly = true)
	public List<SendJob> listRecentJobs() {
		return sendJobRepository.findTop10ByOrderByRequestedAtDesc();
	}
}