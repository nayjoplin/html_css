# Backend Java - Envio Rápido

Projeto Spring Boot que implementa os endpoints principais da API (parte Java do backend).

Requisitos:
- Java 17 instalado
- Maven
- Docker (recomendado para rodar dependências em desenvolvimento)

Como rodar localmente:

Com Maven:

```cmd
cd backend-java
mvn package
java -jar target/envio-rapido-0.0.1-SNAPSHOT.jar
```

Com Docker Compose (recomendado):

```cmd
cd backend-java
docker compose up --build
```

Variáveis de ambiente (opcionais):

- `MONGO_URI` — string de conexão com MongoDB (padrão `mongodb://localhost:27017/envio_rapido`)
- `RABBITMQ_URL` — URL do RabbitMQ (padrão `amqp://localhost`)
- `PORT` — porta da aplicação (padrão `3000`)

Testes:

```cmd
cd backend-java
mvn test
```

Documentação:

O OpenAPI/Swagger fica disponível em `/api-docs` quando a aplicação estiver rodando.

Observações:

- Este diretório contém apenas a parte Java do backend. Há também um serviço em Node.js na raiz do repositório usado para exemplos.
- Mantenha o JDK em 17 ao rodar os testes locais para garantir compatibilidade com as dependências de teste.
