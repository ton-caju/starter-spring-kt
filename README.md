# Starter - Arquitetura Hexagonal

Este projeto implementa uma arquitetura hexagonal (Ports & Adapters) usando Spring Boot, Kotlin e Gradle.

## Estrutura do Projeto

```
starter/
├── domain/                          # Núcleo do negócio (sem dependências externas)
│   ├── model/                       # Entidades de domínio
│   ├── port/
│   │   ├── driven/                  # Interfaces para adapters driven
│   │   └── driver/                  # Interfaces para adapters driver
│   └── usecase/                     # Casos de uso (lógica de negócio)
│
├── driven/                          # Adapters driven (infraestrutura)
│   ├── persistence/                 # JPA + PostgreSQL
│   ├── event-producer/              # Kafka Producer
│   └── rest-client/                 # WebClient (HTTP não-bloqueante)
│
└── driver/                          # Adapters driver (aplicações independentes)
    ├── rest-server/                 # REST API (porta 8080)
    └── event-consumer/              # Kafka Consumer (porta 8081)
```

## Módulos

### Domain
- **Responsabilidade**: Contém a lógica de negócio pura
- **Entidades**: User (name, email, phone, birthday)
- **Use Cases**: CreateUser, GetUser, UpdateUser, DeleteUser
- **Ports**: Interfaces para repositório e publicação de eventos

### Driven Adapters

#### Persistence
- Implementa o port `UserRepository` usando JPA
- Banco de dados: PostgreSQL
- Mapeamento entre entidades de domínio e JPA

#### Event Producer
- Implementa o port `EventPublisher` usando Kafka
- Publica eventos: USER_CREATED, USER_UPDATED, USER_DELETED
- Topic: `user-events`

#### Rest Client
- Cliente HTTP não-bloqueante usando WebClient
- Exemplo de integração com serviços externos

### Driver Adapters

#### Rest Server
- Aplicação Spring Boot independente
- Expõe API REST para gerenciamento de usuários
- Endpoints:
  - POST   `/api/users` - Criar usuário
  - GET    `/api/users/{id}` - Buscar usuário por ID
  - GET    `/api/users` - Listar todos usuários
  - PUT    `/api/users/{id}` - Atualizar usuário
  - DELETE `/api/users/{id}` - Deletar usuário

#### Event Consumer
- Aplicação Spring Boot independente
- Consome eventos do Kafka (topic: `user-events`)
- Processa eventos de criação, atualização e deleção de usuários

## Requisitos

- Java 21
- PostgreSQL
- Kafka

## Como Executar

### 1. Iniciar dependências (PostgreSQL e Kafka)

```bash
# PostgreSQL
docker run --name postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=userdb -p 5432:5432 -d postgres

# Kafka (usando docker-compose é recomendado)
docker run --name kafka -p 9092:9092 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 -d wurstmeister/kafka
```

### 2. Build do projeto

```bash
./gradlew clean build
```

### 3. Executar Rest Server

```bash
./gradlew :driver:rest-server:bootRun
```

A API estará disponível em: http://localhost:8080

### 4. Executar Event Consumer

```bash
./gradlew :driver:event-consumer:bootRun
```

O consumer estará rodando na porta 8081.

## Exemplo de Uso

### Criar um usuário

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "phone": "+5511999999999",
    "birthday": "1990-01-15"
  }'
```

### Listar usuários

```bash
curl http://localhost:8080/api/users
```

## Deploy Independente

Cada driver pode ser deployado independentemente:

```bash
# Build do JAR do rest-server
./gradlew :driver:rest-server:bootJar

# Build do JAR do event-consumer
./gradlew :driver:event-consumer:bootJar
```

Os JARs estarão em:
- `driver/rest-server/build/libs/rest-server-0.0.1-SNAPSHOT.jar`
- `driver/event-consumer/build/libs/event-consumer-0.0.1-SNAPSHOT.jar`

## Configuração

Cada driver tem seu próprio arquivo `application.yml` em `src/main/resources/`:

- **rest-server**: Porta 8080, configurações de JPA e Kafka
- **event-consumer**: Porta 8081, configurações de Kafka consumer

## Princípios da Arquitetura Hexagonal

1. **Domain** é independente de frameworks e bibliotecas externas
2. **Ports** definem contratos entre domain e adapters
3. **Driven Adapters** implementam interfaces definidas pelo domain (inversão de dependência)
4. **Driver Adapters** são aplicações que orquestram os use cases
5. Cada driver pode ser deployado independentemente

## Tecnologias

- Kotlin 2.2.21
- Spring Boot 4.0.0
- Spring Data JPA
- Spring Kafka
- PostgreSQL
- WebClient (Spring WebFlux)
- Gradle 9.2.1
