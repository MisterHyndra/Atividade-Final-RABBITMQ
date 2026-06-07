package br.com.facul.rabbitmq.service;

public record RecipientImportResult(int added, int duplicates, int invalid) {

	public String toHumanMessage() {
		return "Importação concluída: " + added + " novo(s), " + duplicates + " duplicado(s) e " + invalid + " inválido(s).";
	}
}