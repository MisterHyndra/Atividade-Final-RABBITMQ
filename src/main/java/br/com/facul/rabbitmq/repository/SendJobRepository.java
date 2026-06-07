package br.com.facul.rabbitmq.repository;

import br.com.facul.rabbitmq.domain.SendJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SendJobRepository extends JpaRepository<SendJob, Long> {

	List<SendJob> findTop10ByOrderByRequestedAtDesc();
}