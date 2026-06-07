package br.com.facul.rabbitmq.repository;

import br.com.facul.rabbitmq.domain.RecipientEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipientEmailRepository extends JpaRepository<RecipientEmail, Long> {

	boolean existsByEmailIgnoreCase(String email);

	List<RecipientEmail> findAllByOrderByEmailAsc();
}