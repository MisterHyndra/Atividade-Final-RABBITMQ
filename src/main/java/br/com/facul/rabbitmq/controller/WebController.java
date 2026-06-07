package br.com.facul.rabbitmq.controller;

import br.com.facul.rabbitmq.dto.EmailBatchRequest;
import br.com.facul.rabbitmq.dto.RecipientBatchRequest;
import br.com.facul.rabbitmq.service.EmailDispatchService;
import br.com.facul.rabbitmq.service.RecipientImportResult;
import br.com.facul.rabbitmq.service.RecipientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

	private final RecipientService recipientService;
	private final EmailDispatchService emailDispatchService;

	@Value("${app.rabbit.queue}")
	private String queueName;

	@Value("${app.rabbit.exchange}")
	private String exchangeName;

	@Value("${app.rabbit.routing-key}")
	private String routingKey;

	public WebController(RecipientService recipientService, EmailDispatchService emailDispatchService) {
		this.recipientService = recipientService;
		this.emailDispatchService = emailDispatchService;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("recipientForm", new RecipientBatchRequest(""));
		model.addAttribute("emailForm", new EmailBatchRequest("", ""));
		model.addAttribute("recipients", recipientService.listAll());
		model.addAttribute("jobs", emailDispatchService.listRecentJobs());
		model.addAttribute("recipientCount", recipientService.countRecipients());
		model.addAttribute("queueName", queueName);
		model.addAttribute("exchangeName", exchangeName);
		model.addAttribute("routingKey", routingKey);
		return "index";
	}

	@PostMapping("/recipients")
	public String saveRecipients(@ModelAttribute RecipientBatchRequest request, RedirectAttributes redirectAttributes) {
		try {
			RecipientImportResult result = recipientService.addRecipients(request.emailsText());
			redirectAttributes.addFlashAttribute("successMessage", result.toHumanMessage());
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/";
	}

	@PostMapping("/dispatch")
	public String requestDispatch(@ModelAttribute EmailBatchRequest request, RedirectAttributes redirectAttributes) {
		try {
			Long jobId = emailDispatchService.scheduleDispatch(request.subject(), request.messageBody());
			redirectAttributes.addFlashAttribute(
				"successMessage",
				"Solicitação enviada para a fila com sucesso. Job gerado: #" + jobId
			);
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
		}
		return "redirect:/";
	}
}