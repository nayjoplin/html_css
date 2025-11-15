# Envio Rápido API

API em Node.js para cadastro de envios, com validação de CEP, cálculo de frete e notificação em fila.

## Tecnologias

- Node.js + Express
- MongoDB (com Mongoose)
- RabbitMQ (com amqplib)
- Axios para clientes HTTP
- Jest e Supertest para testes
- Swagger para documentação

## Pré-requisitos

- [Node.js](https://nodejs.org/) (v18 ou superior)
- [Docker](https://www.docker.com/get-started) (para rodar MongoDB e RabbitMQ)

## Como rodar

1.  **Clone o repositório**

2.  **Instale as dependências**
    ```bash
    npm install
    ```

3.  **Configure as variáveis de ambiente**

    Copie o arquivo `.env.example` para um novo arquivo chamado `.env` e ajuste as variáveis conforme necessário.

    ```bash
    cp .env.example .env
    ```

4.  **Inicie os serviços de dependência**

    Suba instâncias do MongoDB e RabbitMQ usando Docker:
    ```bash
    # Iniciar MongoDB
    docker run -d --name mongo -p 27017:27017 mongo

    # Iniciar RabbitMQ
    docker run -d --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
    ```

5.  **Execute os testes** (Opcional)
    ```bash
    npm test
    ```

6.  **Inicie a aplicação**
    ```bash
    npm start
    ```

A API estará disponível em `http://localhost:3000`.

## Configuração

As seguintes variáveis de ambiente devem ser configuradas no arquivo `.env`:

| Variável        | Descrição                                                              | Padrão no Exemplo                          |
| --------------- | ---------------------------------------------------------------------- | ------------------------------------------ |
| `MONGO_URI`     | String de conexão para o banco de dados MongoDB.                       | `mongodb://localhost:27017/envio_rapido`   |
| `RABBITMQ_URL`  | URL para conexão com o servidor RabbitMQ.                                | `amqp://localhost`                         |
| `PORT`          | Porta onde a API será executada.                                       | `3000`                                     |
| `FRETE_API_URL` | URL da API externa para cálculo de frete.                                | `http://localhost:3000/api/envios/frete-mock` |
| `JWT_SECRET`    | Chave secreta para gerar tokens de autenticação. **Altere para um valor seguro.** | `troque-esta-chave`                        |


## Documentação da API

A documentação completa dos endpoints está disponível via Swagger em:
**`http://localhost:3000/api-docs`**

## Endpoints Principais

### `POST /api/envios`

Cria um novo envio. O CEP de origem e destino são validados, o frete é calculado e o envio é salvo no banco.

**Exemplo de corpo da requisição:**
```json
{
  "remetente": {
    "nome": "Empresa Exemplo",
    "cep": "01001000"
  },
  "destinatario": {
    "nome": "Cliente Final",
    "cep": "87020025"
  },
  "pacote": {
    "peso_kg": 1.5,
    "altura_cm": 10,
    "largura_cm": 20,
    "comprimento_cm": 30
  }
}
```

**Exemplo de resposta (sucesso `201 Created`):**
```json
{
  "_id": "637d0b7b3e1b9b1b1b1b1b1b",
  "remetente": {
    "nome": "Empresa Exemplo",
    "cep": "01001000",
    "logradouro": "Praça da Sé",
    "bairro": "Sé",
    "cidade": "São Paulo",
    "uf": "SP"
  },
  "destinatario": {
    "nome": "Cliente Final",
    "cep": "87020025",
    "logradouro": "Avenida Advogado Horácio Raccanello Filho",
    "bairro": "Zona 07",
    "cidade": "Maringá",
    "uf": "PR"
  },
  "pacote": {
    "peso_kg": 1.5,
    "altura_cm": 10,
    "largura_cm": 20,
    "comprimento_cm": 30
  },
  "valor_frete": 25.50,
  "status": "CRIADO",
  "createdAt": "2022-11-22T18:00:00.000Z"
}
```

### Outros Endpoints

-   `GET /api/envios/{id}` – Consulta um envio específico.
-   `PUT /api/envios/{id}` – Atualiza os dados de um envio.
-   `PATCH /api/envios/{id}/endereco` – Atualiza apenas o endereço de um envio.
-   `DELETE /api/envios/{id}` – Remove um envio.
