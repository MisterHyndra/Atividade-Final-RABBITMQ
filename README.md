# Atividade Final - Sistema de Envio de E-mails com Java e RabbitMQ

## Descricao do projeto

Este projeto foi desenvolvido para a atividade final da disciplina de Mensageria e Streams em Aplicacoes. O sistema realiza o envio assincrono de e-mails em lote utilizando Java com Spring Boot, RabbitMQ, PostgreSQL e uma interface web simples.

A proposta do sistema e permitir o cadastro de destinatarios em banco de dados e, depois, solicitar o envio de uma mensagem para todos os e-mails cadastrados. Esse envio nao acontece diretamente pela requisicao da tela. Em vez disso, a aplicacao publica uma mensagem em uma fila no RabbitMQ, e um consumidor processa essa mensagem de forma assincrona.

## Objetivo da atividade

Aplicar, na pratica, os conceitos de mensageria com RabbitMQ vistos em aula, especialmente:

- queue
- exchange
- binding
- routing key
- producer
- consumer
- RabbitTemplate
- @RabbitListener
- processamento assincrono

## Tecnologias utilizadas

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Spring AMQP
- Spring Mail
- Thymeleaf
- PostgreSQL
- RabbitMQ
- MailHog
- Maven
- HTML e CSS

## Arquitetura utilizada

A arquitetura do projeto foi separada em camadas simples para facilitar manutencao e demonstracao:

- frontend web com Thymeleaf, HTML e CSS;
- controller para receber as requisicoes da tela;
- service para regra de negocio;
- repository para acesso ao banco de dados;
- RabbitMQ para mensageria assincrona;
- PostgreSQL para persistencia;
- MailHog para simulacao de envio de e-mails.

## Fluxo principal do sistema

1. O usuario abre a interface web.
2. Cadastra um ou mais e-mails de destinatarios.
3. Os e-mails sao salvos no banco PostgreSQL.
4. O usuario informa o assunto e a mensagem.
5. A aplicacao cria um job de envio.
6. O producer publica uma mensagem no RabbitMQ.
7. O consumer recebe a mensagem da fila.
8. O consumer envia os e-mails em lote usando o MailHog.
9. O resultado do processamento e salvo no banco.
10. O status do job aparece na interface.

## Requisitos da atividade atendidos

O projeto atende aos requisitos pedidos na atividade:

- backend em Java com Spring Boot;
- integracao com RabbitMQ;
- configuracao de fila;
- configuracao de exchange;
- configuracao de routing key;
- implementacao de producer;
- implementacao de consumer;
- persistencia em banco de dados;
- envio de e-mails em lote;
- interface funcional para interacao com o sistema.

## Estrutura principal de classes

### Configuracao RabbitMQ

Arquivo:
- `src/main/java/br/com/facul/rabbitmq/config/RabbitMQConfig.java`

Responsavel por:
- criar a fila;
- criar a exchange;
- criar o binding;
- definir a routing key;
- configurar o conversor JSON das mensagens.

### Controller

Arquivo:
- `src/main/java/br/com/facul/rabbitmq/controller/WebController.java`

Responsavel por:
- carregar a pagina inicial;
- receber os formularios da interface;
- chamar os services de cadastro e envio;
- retornar mensagens de sucesso ou erro para a tela.

### Cadastro de destinatarios

Arquivo:
- `src/main/java/br/com/facul/rabbitmq/service/RecipientService.java`

Responsavel por:
- validar os e-mails informados;
- remover duplicados;
- salvar os destinatarios no banco;
- listar os destinatarios cadastrados.

### Agendamento do envio

Arquivo:
- `src/main/java/br/com/facul/rabbitmq/service/EmailDispatchService.java`

Responsavel por:
- validar assunto e mensagem;
- buscar os destinatarios cadastrados;
- criar o job de envio;
- salvar o job no banco;
- chamar o producer para publicar a mensagem na fila.

### Producer

Arquivo:
- `src/main/java/br/com/facul/rabbitmq/service/BatchEmailProducer.java`

Responsavel por:
- publicar a mensagem no RabbitMQ usando `RabbitTemplate`.

### Consumer

Arquivo:
- `src/main/java/br/com/facul/rabbitmq/service/BatchEmailConsumer.java`

Responsavel por:
- consumir a mensagem da fila usando `@RabbitListener`;
- processar o envio em lote;
- atualizar o status final do job.

### Envio de e-mail

Arquivo:
- `src/main/java/br/com/facul/rabbitmq/service/EmailSenderService.java`

Responsavel por:
- montar o e-mail;
- enviar para o servidor SMTP de teste.

## Banco de dados

O projeto utiliza PostgreSQL para persistir os dados.

Principais tabelas:

- `recipient_emails`: armazena os e-mails cadastrados;
- `send_jobs`: armazena os jobs de envio e seus status.

Configuracao padrao:

- `DB_HOST=localhost`
- `DB_PORT=5432`
- `DB_USER=postgres`
- `DB_PASSWORD=`
- `DB_NAME=atividade_final_rabbitmq_db`

Exemplo de criacao do banco:

```sql
CREATE DATABASE atividade_final_rabbitmq_db;
```

## RabbitMQ

O projeto utiliza RabbitMQ local via Docker.

Configuracao utilizada:

- host: `localhost`
- porta AMQP: `5673`
- painel web: `http://localhost:15673`
- usuario: `guest`
- senha: `guest`

Elementos configurados:

- queue: `email.lote.queue`
- exchange: `email.lote.exchange`
- routing key: `email.lote.routing-key`

## MailHog

O MailHog foi usado para simular o envio dos e-mails, permitindo demonstrar o resultado sem depender de um servico real.

Acesso:

- SMTP: `localhost:1026`
- painel web: `http://localhost:8026`

## Como executar o projeto

### 1. Subir RabbitMQ e MailHog

Na raiz do workspace, executar:

```bash
docker compose up -d
```

### 2. Entrar na pasta do projeto

```bash
cd atividade-final-rabbitmq
```

### 3. Configurar o banco PostgreSQL

Criar o banco com o nome:

```sql
CREATE DATABASE atividade_final_rabbitmq_db;
```

### 4. Configurar variaveis de ambiente

Exemplo no PowerShell:

```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_USER="postgres"
$env:DB_PASSWORD="neuro123"
$env:DB_NAME="atividade_final_rabbitmq_db"
$env:SERVER_PORT="8081"
```

### 5. Executar a aplicacao

Opcao utilizada no projeto:

```powershell
java -jar target\atividade-final-rabbitmq-0.0.1-SNAPSHOT.jar
```

Se precisar gerar o JAR antes:

```powershell
.\mvnw clean package
```

## Acesso ao sistema

- aplicacao web: `http://localhost:8081`
- RabbitMQ: `http://localhost:15673`
- MailHog: `http://localhost:8026`

## Como testar o sistema

### Teste manual

1. Abrir a interface web.
2. Cadastrar e-mails de destinatarios.
3. Confirmar se os e-mails aparecem na tabela da tela.
4. Confirmar se os registros foram salvos no PostgreSQL.
5. Informar assunto e mensagem.
6. Solicitar o envio em lote.
7. Verificar a mensagem de sucesso com o numero do job.
8. Verificar os logs do producer e do consumer.
9. Verificar o status do job na interface.
10. Verificar os e-mails recebidos no MailHog.

### Teste de compilacao

```powershell
.\mvnw clean package
```

### Teste automatizado

```powershell
.\mvnw test
```

## Evidencias de execucao

As evidências do funcionamento foram apresentadas por meio de um vídeo demonstrativo, no qual foram exibidas e validadas as funcionalidades implementadas no projeto.

- interface web funcionando;
- cadastro de destinatarios;
- persistencia no PostgreSQL;
- fila configurada no RabbitMQ;
- publicacao da mensagem pelo producer;
- consumo da mensagem pelo consumer;
- envio em lote exibido no MailHog;
- status dos jobs na interface e no banco de dados.




## Repositorio

https://github.com/MisterHyndra/Atividade-Final-RABBITMQ
