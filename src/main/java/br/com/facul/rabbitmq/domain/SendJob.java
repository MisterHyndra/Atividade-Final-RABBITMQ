package br.com.facul.rabbitmq.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "send_jobs")
public class SendJob {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 180)
	private String subject;

	@Lob
	@Column(nullable = false)
	private String messageBody;

	@Lob
	@Column(nullable = false)
	private String recipientSnapshot;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private SendJobStatus status;

	@Column(nullable = false)
	private Integer recipientCount;

	@Column(nullable = false)
	private Integer successfulCount;

	@Column(nullable = false)
	private Integer failedCount;

	@Column(nullable = false)
	private LocalDateTime requestedAt;

	private LocalDateTime processedAt;

	@Lob
	private String processingDetails;

	protected SendJob() {
	}

	public SendJob(String subject, String messageBody, String recipientSnapshot, Integer recipientCount) {
		this.subject = subject;
		this.messageBody = messageBody;
		this.recipientSnapshot = recipientSnapshot;
		this.recipientCount = recipientCount;
		this.successfulCount = 0;
		this.failedCount = 0;
		this.status = SendJobStatus.QUEUED;
		this.requestedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public String getRecipientSnapshot() {
		return recipientSnapshot;
	}

	public SendJobStatus getStatus() {
		return status;
	}

	public Integer getRecipientCount() {
		return recipientCount;
	}

	public Integer getSuccessfulCount() {
		return successfulCount;
	}

	public Integer getFailedCount() {
		return failedCount;
	}

	public LocalDateTime getRequestedAt() {
		return requestedAt;
	}

	public LocalDateTime getProcessedAt() {
		return processedAt;
	}

	public String getProcessingDetails() {
		return processingDetails;
	}

	public void markProcessing() {
		this.status = SendJobStatus.PROCESSING;
		this.processingDetails = "Consumidor iniciou o processamento da fila.";
	}

	public void markFinished(int success, int failed, String details) {
		this.successfulCount = success;
		this.failedCount = failed;
		this.processedAt = LocalDateTime.now();
		this.processingDetails = details;

		if (success > 0 && failed == 0) {
			this.status = SendJobStatus.COMPLETED;
			return;
		}

		if (success > 0) {
			this.status = SendJobStatus.COMPLETED_WITH_ERRORS;
			return;
		}

		this.status = SendJobStatus.FAILED;
	}
}