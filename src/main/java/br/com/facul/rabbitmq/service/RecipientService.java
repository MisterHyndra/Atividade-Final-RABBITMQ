package br.com.facul.rabbitmq.service;

import br.com.facul.rabbitmq.domain.RecipientEmail;
import br.com.facul.rabbitmq.repository.RecipientEmailRepository;
import jakarta.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipientService {

	private final RecipientEmailRepository recipientEmailRepository;

	public RecipientService(RecipientEmailRepository recipientEmailRepository) {
		this.recipientEmailRepository = recipientEmailRepository;
	}

	@Transactional
	public RecipientImportResult addRecipients(String rawEmails) {
		if (rawEmails == null || rawEmails.isBlank()) {
			throw new IllegalArgumentException("Informe ao menos um e-mail para cadastro.");
		}

		int added = 0;
		int duplicates = 0;
		int invalid = 0;

		for (String candidate : Arrays.stream(rawEmails.split("(?:,|;|\\R|\\s)+"))
			.map(String::trim)
			.filter(text -> !text.isBlank())
			.map(String::toLowerCase)
			.distinct()
			.toList()) {
			if (!isValidEmail(candidate)) {
				invalid++;
				continue;
			}

			if (recipientEmailRepository.existsByEmailIgnoreCase(candidate)) {
				duplicates++;
				continue;
			}

			recipientEmailRepository.save(new RecipientEmail(candidate));
			added++;
		}

		return new RecipientImportResult(added, duplicates, invalid);
	}

	@Transactional(readOnly = true)
	public List<RecipientEmail> listAll() {
		return recipientEmailRepository.findAllByOrderByEmailAsc();
	}

	@Transactional(readOnly = true)
	public long countRecipients() {
		return recipientEmailRepository.count();
	}

	private boolean isValidEmail(String email) {
		try {
			InternetAddress internetAddress = new InternetAddress(email);
			internetAddress.validate();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}