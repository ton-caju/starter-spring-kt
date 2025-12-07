# Arquitetura Hexagonal - Detalhamento

## Visão Geral

Este projeto implementa a arquitetura hexagonal (também conhecida como Ports & Adapters), proposta por Alistair Cockburn. O objetivo é criar um sistema desacoplado, testável e independente de frameworks.

## Camadas da Arquitetura

### 1. Domain (Núcleo)

O domínio é o coração da aplicação e contém toda a lógica de negócio. Ele é **completamente independente** de frameworks, bibliotecas externas e detalhes de infraestrutura.

```
domain/
├── model/
│   └── User.kt                    # Entidade de domínio com validações
├── port/
│   ├── driven/
│   │   ├── UserRepository.kt      # Port para persistência
│   │   └── EventPublisher.kt      # Port para eventos
│   └── driver/
│       └── UserManagement.kt      # Port para casos de uso
└── usecase/
    ├── CreateUserUseCase.kt       # Lógica de criação
    ├── GetUserUseCase.kt          # Lógica de consulta
    ├── UpdateUserUseCase.kt       # Lógica de atualização
    └── DeleteUserUseCase.kt       # Lógica de deleção
```

**Características:**
- Sem anotações do Spring
- Sem dependências externas (apenas Kotlin stdlib)
- Classes e interfaces puramente Kotlin
- Validações de negócio na entidade

### 2. Driven Adapters (Lado Direito do Hexágono)

Implementam as interfaces (ports) definidas pelo domínio. São responsáveis pela comunicação com sistemas externos.

#### Persistence (driven/persistence)
```kotlin
// Implementa UserRepository
UserRepositoryAdapter -> JpaUserRepository -> PostgreSQL
```

**Responsabilidades:**
- Implementar `UserRepository`
- Mapear entre `User` (domínio) e `UserEntity` (JPA)
- Gerenciar transações e persistência

**Dependências:**
- `domain` (para interfaces e modelo)
- Spring Data JPA
- PostgreSQL Driver

#### Event Producer (driven/event-producer)
```kotlin
// Implementa EventPublisher
KafkaEventPublisher -> KafkaTemplate -> Kafka
```

**Responsabilidades:**
- Implementar `EventPublisher`
- Serializar eventos para JSON
- Publicar no tópico Kafka

**Dependências:**
- `domain` (para interfaces e eventos)
- Spring Kafka
- Jackson (serialização)

#### Rest Client (driven/rest-client)
```kotlin
// Exemplo de integração externa
ExternalServiceClient -> WebClient -> API Externa
```

**Responsabilidades:**
- Chamar APIs externas de forma não-bloqueante
- Tratar erros e timeouts
- Exemplo de integração com serviços externos

**Dependências:**
- Spring WebFlux (WebClient)

### 3. Driver Adapters (Lado Esquerdo do Hexágono)

São os pontos de entrada da aplicação. Cada driver é uma **aplicação Spring Boot independente** que pode ser deployada separadamente.

#### Rest Server (driver/rest-server)
```kotlin
// Driver REST API
RestController -> UseCases -> Domain
```

**Responsabilidades:**
- Expor API REST
- Validar requests (Bean Validation)
- Converter entre DTOs e modelo de domínio
- Orquestrar use cases

**Estrutura:**
```
driver/rest-server/
├── RestServerApplication.kt       # @SpringBootApplication
├── controller/
│   └── UserController.kt          # REST endpoints
├── dto/
│   ├── UserRequest.kt             # Input DTO
│   └── UserResponse.kt            # Output DTO
├── config/
│   └── UseCaseConfig.kt           # Beans dos use cases
└── resources/
    └── application.yml            # Configurações
```

**Dependências:**
- `domain`
- `driven:persistence`
- `driven:event-producer`
- Spring Web
- Spring Data JPA

**Porta:** 8080

#### Event Consumer (driver/event-consumer)
```kotlin
// Driver Kafka Consumer
@KafkaListener -> UseCases -> Domain
```

**Responsabilidades:**
- Consumir eventos do Kafka
- Processar eventos assincronamente
- Executar ações baseadas nos eventos

**Estrutura:**
```
driver/event-consumer/
├── EventConsumerApplication.kt    # @SpringBootApplication
├── listener/
│   └── UserEventListener.kt       # @KafkaListener
├── config/
│   └── KafkaConsumerConfig.kt     # Configuração Kafka
└── resources/
    └── application.yml            # Configurações
```

**Dependências:**
- `domain`
- `driven:persistence` (opcional)
- Spring Kafka

**Porta:** 8081

## Fluxo de Dados

### Criação de Usuário (via REST)

```
1. Cliente HTTP
   ↓
2. UserController (rest-server)
   ↓
3. CreateUserUseCase (domain)
   ↓
4. UserRepository.save() [port]
   ↓
5. UserRepositoryAdapter (persistence)
   ↓
6. PostgreSQL
   ↓
7. CreateUserUseCase (domain)
   ↓
8. EventPublisher.publish() [port]
   ↓
9. KafkaEventPublisher (event-producer)
   ↓
10. Kafka Topic: user-events
    ↓
11. UserEventListener (event-consumer)
    ↓
12. Processa evento (ex: enviar email, atualizar cache)
```

## Inversão de Dependências

```
┌─────────────────────────────────────┐
│           DOMAIN                    │
│  ┌──────────────────────────────┐   │
│  │      Use Cases               │   │
│  └──────────────────────────────┘   │
│              ↑                       │
│  ┌──────────┴────────────┐          │
│  │  Ports (Interfaces)   │          │
│  └───────────────────────┘          │
└─────────────────────────────────────┘
            ↑          ↑
            │          │
    ┌───────┘          └──────────┐
    │                              │
┌───┴────┐                  ┌──────┴─────┐
│ Driven │                  │   Driver   │
│Adapters│                  │  Adapters  │
└────────┘                  └────────────┘
```

**Princípio:** O domínio define as interfaces (ports), e os adapters implementam essas interfaces. O domínio **nunca** depende dos adapters.

## Vantagens desta Arquitetura

1. **Testabilidade**: Domain pode ser testado sem Spring, banco de dados ou Kafka
2. **Flexibilidade**: Fácil trocar PostgreSQL por MongoDB, ou Kafka por RabbitMQ
3. **Deploy Independente**: Cada driver pode escalar e deployar separadamente
4. **Manutenibilidade**: Separação clara de responsabilidades
5. **Regras de Negócio Protegidas**: Lógica de negócio isolada de detalhes técnicos

## Quando Usar Esta Arquitetura

✅ **Use quando:**
- Aplicação tem lógica de negócio complexa
- Requisitos de escalabilidade diferentes por componente
- Necessidade de múltiplos pontos de entrada (REST, Events, CLI, etc)
- Time precisa trabalhar em paralelo em diferentes adapters
- Alta rotatividade de tecnologias

❌ **Evite quando:**
- Aplicação muito simples (CRUD básico)
- Time pequeno com pouca experiência
- Prazo muito curto
- Não há expectativa de mudanças tecnológicas

## Regras de Dependência

1. **Domain** não depende de nada (exceto Kotlin stdlib)
2. **Driven Adapters** dependem apenas do Domain
3. **Driver Adapters** dependem do Domain e dos Driven Adapters necessários
4. **Fluxo de dependência**: sempre em direção ao Domain

## Build e Deploy

### Build de todos os módulos
```bash
./gradlew clean build
```

### Build de um driver específico
```bash
./gradlew :driver:rest-server:bootJar
./gradlew :driver:event-consumer:bootJar
```

### Executar um driver específico
```bash
./gradlew :driver:rest-server:bootRun
./gradlew :driver:event-consumer:bootRun
```

### Estrutura de JARs gerados

```
driver/rest-server/build/libs/
└── rest-server-0.0.1-SNAPSHOT.jar    # JAR executável

driver/event-consumer/build/libs/
└── event-consumer-0.0.1-SNAPSHOT.jar # JAR executável
```

Cada JAR contém:
- O código do driver
- Todas as dependências (domain + driven adapters)
- Configuração própria (application.yml)

## Pipeline de Deploy (Exemplo)

```yaml
# CI/CD Pipeline
stages:
  - build
  - test
  - deploy-rest-server
  - deploy-event-consumer

# Build comum
build:
  - ./gradlew :domain:build
  - ./gradlew :driven:persistence:build
  - ./gradlew :driven:event-producer:build

# Deploy independente
deploy-rest-server:
  - ./gradlew :driver:rest-server:bootJar
  - docker build -t rest-server:latest driver/rest-server
  - kubectl apply -f k8s/rest-server.yml

deploy-event-consumer:
  - ./gradlew :driver:event-consumer:bootJar
  - docker build -t event-consumer:latest driver/event-consumer
  - kubectl apply -f k8s/event-consumer.yml
```

## Próximos Passos

1. Adicionar testes unitários no domain
2. Adicionar testes de integração nos adapters
3. Implementar tratamento de erros customizado
4. Adicionar observabilidade (metrics, logs, traces)
5. Criar Dockerfiles para cada driver
6. Adicionar mais use cases conforme necessário
