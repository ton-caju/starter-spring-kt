# Starter - Arquitetura Hexagonal

Projeto Spring Boot com Kotlin implementando arquitetura hexagonal (Ports & Adapters) com deploy independente de drivers.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-blue)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-9.2.1-blue)](https://gradle.org/)

## 📊 Status do Projeto

- **44 arquivos Kotlin** (~2.160 linhas de código)
- **6 módulos** (1 domain + 3 driven adapters + 2 driver adapters)
- **Testes:** Domain, REST Server, Event Consumer
- **Documentação:** Swagger/OpenAPI integrada
- **Qualidade:** Spotless + JaCoCo + SonarQube

## 🏗️ Arquitetura

```
starter/
├── domain/                     # Lógica de negócio pura (sem dependências externas)
│   ├── model/                 # User
│   ├── port/
│   │   ├── driven/           # UserRepository, EventPublisher
│   │   └── driver/           # UserManagement
│   ├── usecase/              # CreateUser, GetUser, UpdateUser, DeleteUser
│   ├── service/              # UserManagementImpl
│   └── exception/            # Domain exceptions
│
├── driven/                    # Adapters de saída (implementam ports do domain)
│   ├── persistence/          # JPA + PostgreSQL
│   ├── event-producer/       # Kafka Producer
│   └── rest-client/          # WebClient (HTTP não-bloqueante)
│
└── driver/                    # Adapters de entrada (aplicações Spring Boot independentes)
    ├── rest-server/          # API REST (porta 8080)
    │   ├── controller/       # Endpoints REST
    │   ├── dto/              # Request/Response DTOs
    │   ├── config/           # Configuração Spring
    │   ├── exception/        # Global exception handler
    │   └── documentation/    # Swagger annotations
    │
    └── event-consumer/       # Kafka Consumer (porta 8081)
        ├── listener/         # @KafkaListener
        └── config/           # Kafka configuration
```

### 🎯 Princípios Arquiteturais

1. **Domain-Driven Design** - Domain define regras de negócio sem dependências externas
2. **Ports & Adapters** - Interfaces (ports) separam domain de infraestrutura
3. **Inversão de Dependência** - Domain define ports, adapters os implementam
4. **Deploy Independente** - Cada driver é uma aplicação Spring Boot autônoma
5. **Testabilidade** - Domain testável isoladamente sem Spring/DB/Kafka

## 🚀 Quick Start

### Pré-requisitos
```bash
java -version  # Java 21+
docker --version
```

### 1. Iniciar dependências (PostgreSQL + Kafka)
```bash
make docker-up
# ou
docker-compose up -d
```

### 2. Executar as aplicações

#### Opção 1: Usando Makefile ⭐ (recomendado)
```bash
# REST API (porta 8080)
make run

# Event Consumer (porta 8081)
make run-consumer

# Ambos simultaneamente
make run-all

# Parar todas as aplicações
make stop-all
```

#### Opção 2: Usando Gradle
```bash
# REST API
./gradle/gradlew :driver:rest-server:bootRun

# Event Consumer
./gradle/gradlew :driver:event-consumer:bootRun
```

### 3. Testar a API

```bash
# Criar usuário
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "phone": "+5511999999999",
    "birthday": "1990-01-15"
  }'

# Listar todos os usuários
curl http://localhost:8080/api/users

# Buscar por ID
curl http://localhost:8080/api/users/{id}

# Atualizar usuário
curl -X PUT http://localhost:8080/api/users/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva Atualizado",
    "email": "joao.novo@example.com",
    "phone": "+5511988888888",
    "birthday": "1990-01-15"
  }'

# Deletar usuário
curl -X DELETE http://localhost:8080/api/users/{id}
```

### 4. Acessar documentação da API

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 📋 Makefile - Comandos Disponíveis

Execute `make help` para ver todos os comandos disponíveis.

### Build e Testes
```bash
make build              # Compilar o projeto
make build-fast         # Compilar sem testes
make test               # Executar todos os testes
make test-unit          # Testes unitários apenas
make test-integration   # Testes de integração apenas
make clean              # Limpar build artifacts
make clean-build        # Limpar e compilar
```

### Execução
```bash
make run                # Executar REST server (8080)
make run-consumer       # Executar event consumer (8081)
make run-all            # Executar ambos simultaneamente
make stop-all           # Parar todas as aplicações
make run-dev            # REST server com live reload
make run-consumer-dev   # Event consumer com live reload
```

### Docker
```bash
make docker-up          # Iniciar PostgreSQL + Kafka
make docker-down        # Parar todos os containers
make docker-logs        # Ver logs em tempo real
make docker-clean       # Remover containers e volumes
make docker-postgres    # Apenas PostgreSQL
make docker-kafka       # Apenas Kafka + Zookeeper
make docker-restart     # Reiniciar containers
```

### Qualidade de Código
```bash
make format             # Formatar código (Spotless)
make format-check       # Verificar formatação
make lint               # Verificar estilo de código
make coverage           # Gerar relatório de cobertura
make coverage-verify    # Verificar cobertura mínima (70%)
make coverage-report    # Gerar e abrir relatório HTML
make sonar              # Análise SonarQube local
```

### Workflows Completos
```bash
make dev-setup          # Configurar ambiente completo
make dev-clean          # Limpar ambiente
make pre-commit         # Formatar + testar (antes de commit)
make check              # Format check + tests
make verify             # Format check + tests + coverage
make ci                 # Pipeline CI completo
```

### Utilitários
```bash
make help               # Mostrar todos os comandos
make info               # Informações do projeto
make dependencies       # Árvore de dependências
make tasks              # Listar todas as tasks Gradle
```

## 🎯 Domain - Casos de Uso

### Use Cases Implementados

1. **CreateUserUseCase**
   - Valida dados do usuário
   - Verifica email duplicado
   - Salva no repositório
   - Publica evento `USER_CREATED`

2. **GetUserUseCase**
   - Busca usuário por ID
   - Lista todos os usuários
   - Lança exceção se não encontrado

3. **UpdateUserUseCase**
   - Valida novos dados
   - Atualiza usuário existente
   - Publica evento `USER_UPDATED`

4. **DeleteUserUseCase**
   - Remove usuário do repositório
   - Publica evento `USER_DELETED`

### Entidade de Domínio

```kotlin
data class User(
    val id: String,
    val name: String,       // Não pode ser vazio
    val email: String,      // Deve ser válido e único
    val phone: String,      // Formato internacional
    val birthday: LocalDate // Data de nascimento
)
```

### Ports (Interfaces)

**Driven Ports** (implementados pelos adapters de saída):
- `UserRepository` - Persistência de usuários
- `EventPublisher` - Publicação de eventos

**Driver Ports** (chamados pelos adapters de entrada):
- `UserManagement` - Facade para todos os use cases

## 🔌 Driven Adapters (Infraestrutura)

### Persistence (JPA + PostgreSQL)
- **Implementa**: `UserRepository`
- **Tecnologia**: Spring Data JPA + PostgreSQL 16
- **Mapeamento**: `UserEntity` ↔ `User` (domain)
- **Configuração**: `application.yml` no rest-server

### Event Producer (Kafka)
- **Implementa**: `EventPublisher`
- **Tecnologia**: Spring Kafka
- **Topic**: `user-events`
- **Eventos**: `USER_CREATED`, `USER_UPDATED`, `USER_DELETED`
- **Formato**: JSON

### Rest Client (WebClient)
- **Propósito**: Integração com APIs externas
- **Tecnologia**: Spring WebFlux WebClient
- **Modo**: Não-bloqueante (reativo)

## 🚪 Driver Adapters (Aplicações)

### Rest Server (porta 8080)

**Endpoints REST:**
```
POST   /api/users      - Criar usuário
GET    /api/users      - Listar todos
GET    /api/users/{id} - Buscar por ID
PUT    /api/users/{id} - Atualizar
DELETE /api/users/{id} - Deletar
```

**Recursos:**
- ✅ Validação de entrada (@Valid)
- ✅ Global Exception Handler
- ✅ Swagger/OpenAPI documentation
- ✅ DTOs para request/response
- ✅ Tratamento de erros padronizado

**Documentação:**
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs

### Event Consumer (porta 8081)

**Funcionalidades:**
- Consome eventos do tópico `user-events`
- Processa eventos assincronamente
- Loga informações dos eventos
- Exemplo de processamento: notificações, cache, analytics

**Consumer Group**: `user-consumer-group`

## 📦 Build e Deploy

### Build Local

```bash
# Build completo (todos os módulos)
./gradle/gradlew clean build

# Build de um driver específico
./gradle/gradlew :driver:rest-server:bootJar
./gradle/gradlew :driver:event-consumer:bootJar

# Build rápido (sem testes)
./gradle/gradlew build -x test
```

### JARs Gerados

```
driver/rest-server/build/libs/rest-server-0.0.1-SNAPSHOT.jar
driver/event-consumer/build/libs/event-consumer-0.0.1-SNAPSHOT.jar
```

### Executar JARs

```bash
# REST Server
java -jar driver/rest-server/build/libs/rest-server-0.0.1-SNAPSHOT.jar

# Event Consumer
java -jar driver/event-consumer/build/libs/event-consumer-0.0.1-SNAPSHOT.jar
```

### Deploy Independente

Cada driver pode ser:
- Deployado separadamente em diferentes servidores
- Escalado horizontalmente de forma independente
- Ter configurações específicas por ambiente
- Ser versionado e atualizado independentemente

## 🧪 Testes e Qualidade

### Executar Testes

```bash
# Todos os testes
./gradle/gradlew test

# Com relatório de cobertura
./gradle/gradlew test jacocoTestReport

# Verificar cobertura mínima (70%)
./gradle/gradlew jacocoTestCoverageVerification

# Testes com logs detalhados
./gradle/gradlew test --info
```

### Estrutura de Testes

- **Domain**: Testes unitários dos use cases (MockK)
- **REST Server**: Testes de integração (Testcontainers)
- **Event Consumer**: Testes de integração (Testcontainers)

**Frameworks de Teste:**
- JUnit 5 (Jupiter)
- Kotest (assertions)
- MockK (mocking)
- SpringMockK (Spring + MockK)
- Testcontainers (PostgreSQL, Kafka)

### Spotless - Formatação de Código

```bash
# Verificar formatação
./gradle/gradlew spotlessCheck

# Aplicar formatação automaticamente
./gradle/gradlew spotlessApply
```

**Configuração:**
- **Kotlin**: ktfmt (kotlinlang style)
- **Java**: Google Java Format
- **Indentação**: 4 espaços
- **Arquivos**: `*.kt`, `*.java`, `*.gradle.kts`

### JaCoCo - Cobertura de Código

**Configuração:**
- Cobertura mínima exigida: **70%**
- Relatório XML: `build/reports/jacoco/test/jacocoTestReport.xml`
- Relatório HTML: `build/reports/jacoco/test/html/index.html`

**Exclusões de cobertura:**
- `*Application.kt` - Classes principais
- `*Config.kt` - Configurações Spring
- DTOs, Entities, Models - Classes de dados

### SonarQube - Análise de Qualidade

#### Análise Local
```bash
# 1. Iniciar SonarQube
docker run -d -p 9000:9000 sonarqube:latest

# 2. Executar análise
./gradle/gradlew clean test jacocoTestReport sonar
```

**Quality Gate:**
- ✅ Sem bugs críticos ou bloqueadores
- ✅ Sem vulnerabilidades críticas
- ✅ Cobertura ≥ 70%
- ✅ Duplicação ≤ 3%
- ✅ Code smells controlados

## ⚙️ Configuração

### PostgreSQL

```yaml
# driver/rest-server/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### Kafka

**Producer (REST Server):**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

**Consumer (Event Consumer):**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: user-consumer-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
```

### Variáveis de Ambiente

```bash
# PostgreSQL
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=userdb
export DB_USER=postgres
export DB_PASSWORD=postgres

# Kafka
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export KAFKA_TOPIC=user-events
export KAFKA_CONSUMER_GROUP=user-consumer-group

# Application
export SERVER_PORT=8080          # REST Server
export CONSUMER_PORT=8081        # Event Consumer
```

## 🛠️ Stack Tecnológico

### Core
- **Linguagem**: Kotlin 2.2.21
- **Framework**: Spring Boot 4.0.1
- **Build**: Gradle 9.2.1
- **Java**: OpenJDK 21.0.8

### Banco de Dados
- **PostgreSQL**: 16-alpine
- **JPA/Hibernate**: Spring Data JPA

### Mensageria
- **Kafka**: Confluent Platform 7.5.0
- **Zookeeper**: 7.5.0 (via Confluent)
- **Spring Kafka**: Incluído no Spring Boot 4.0.1

### Testes
- **JUnit**: 5 (Jupiter)
- **Kotest**: 5.9.1
- **MockK**: 1.13.13
- **SpringMockK**: 4.0.2
- **Testcontainers**: 1.19.8

### Qualidade
- **Spotless**: 8.1.0 (ktfmt + Google Java Format)
- **JaCoCo**: 0.8.12
- **SonarQube**: Plugin 7.2.1.6560

### Documentação
- **SpringDoc OpenAPI**: 2.8.14
- **Swagger UI**: Incluído no SpringDoc

### HTTP Client
- **Spring WebFlux**: WebClient (não-bloqueante)

## 📖 Estrutura de Diretórios

```
starter/
├── .gradle/                    # Cache do Gradle
├── build/                      # Artefatos de build
├── gradle/                     # Gradle wrapper ⚠️
│   ├── gradlew                # Executável Unix
│   ├── gradlew.bat            # Executável Windows
│   └── wrapper/               # Configuração wrapper
├── scripts/
│   └── sonar-analysis.sh      # Script SonarQube
├── domain/
│   ├── src/
│   │   ├── main/kotlin/br/com/caju/domain/
│   │   │   ├── model/         # User
│   │   │   ├── port/          # Interfaces
│   │   │   ├── usecase/       # Use cases
│   │   │   ├── service/       # UserManagementImpl
│   │   │   └── exception/     # DomainException
│   │   └── test/kotlin/       # Testes unitários
│   └── build.gradle.kts
├── driven/
│   ├── persistence/
│   │   ├── src/main/kotlin/   # JPA implementation
│   │   └── build.gradle.kts
│   ├── event-producer/
│   │   ├── src/main/kotlin/   # Kafka producer
│   │   └── build.gradle.kts
│   └── rest-client/
│       ├── src/main/kotlin/   # WebClient
│       └── build.gradle.kts
├── driver/
│   ├── rest-server/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── kotlin/    # Controllers, DTOs, Config
│   │   │   │   └── resources/ # application.yml
│   │   │   └── test/kotlin/   # Integration tests
│   │   └── build.gradle.kts
│   └── event-consumer/
│       ├── src/
│       │   ├── main/
│       │   │   ├── kotlin/    # Listeners, Config
│       │   │   └── resources/ # application.yml
│       │   └── test/kotlin/   # Integration tests
│       └── build.gradle.kts
├── build.gradle.kts            # Build principal
├── settings.gradle.kts         # Módulos
├── gradle.properties           # Propriedades Gradle
├── docker-compose.yml          # PostgreSQL + Kafka
├── sonar-project.properties    # Config SonarQube
├── Makefile                    # Comandos úteis
└── README.md                   # Este arquivo
```

## 🎯 Vantagens da Arquitetura

### 1. Testabilidade
- Domain testável sem Spring, DB ou Kafka
- Mocking simples via interfaces (ports)
- Testes rápidos e confiáveis

### 2. Flexibilidade Tecnológica
- Trocar PostgreSQL → MongoDB: reimplementar persistence
- Trocar Kafka → RabbitMQ: reimplementar event-producer
- Domain permanece intacto

### 3. Deploy Independente
- REST Server e Event Consumer escaláveis separadamente
- Atualizações independentes
- Configurações específicas por driver

### 4. Manutenibilidade
- Separação clara de responsabilidades
- Código organizado em módulos
- Fácil localização de funcionalidades

### 5. Proteção das Regras de Negócio
- Lógica de negócio isolada no domain
- Sem vazamento de detalhes técnicos
- Frameworks não invadem o domain

## 📝 Regras de Dependência

```
┌─────────────────────────────────┐
│          DOMAIN                 │
│  (regras de negócio)            │
│  - Sem dependências externas    │
│  - Define as interfaces (ports) │
└────────────▲─────────▲──────────┘
             │         │
      ┌──────┘         └────────┐
      │                         │
┌─────┴────────┐      ┌─────────┴──────┐
│   DRIVEN     │      │    DRIVER      │
│  (adapters)  │      │   (adapters)   │
│              │      │                │
│ - persistence│      │ - rest-server  │
│ - events     │      │ - event-consumer│
│ - rest-client│      │                │
└──────────────┘      └────────────────┘
```

**Regras:**
1. **Domain** não depende de ninguém (apenas Kotlin stdlib)
2. **Driven Adapters** dependem apenas do Domain
3. **Driver Adapters** dependem do Domain + Driven Adapters necessários
4. **Fluxo**: sempre em direção ao Domain (inversão de dependências)

## 🔍 Comandos de Verificação

### PostgreSQL
```bash
# Conectar ao banco
docker exec -it userdb-postgres psql -U postgres -d userdb

# Dentro do psql:
\dt                    # Listar tabelas
SELECT * FROM users;   # Ver dados
\q                     # Sair
```

### Kafka
```bash
# Listar tópicos
docker exec -it kafka kafka-topics \
  --bootstrap-server localhost:9092 --list

# Consumir mensagens
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic user-events \
  --from-beginning
```

### Logs das Aplicações
```bash
# Logs em tempo real do Docker
docker-compose logs -f

# Logs específicos
docker logs -f userdb-postgres
docker logs -f kafka
```

## 🐛 Troubleshooting

### Porta já em uso
```bash
# Descobrir processo na porta 8080
lsof -i :8080

# Matar processo
kill -9 <PID>

# Ou usar outro comando do Makefile
make stop-all
```

### Kafka não conecta
```bash
# Verificar status
docker ps | grep kafka

# Ver logs
docker logs kafka

# Reiniciar
make docker-restart
```

### Build falha
```bash
# Limpar cache
./gradle/gradlew clean --refresh-dependencies

# Limpar completamente
rm -rf .gradle build */build
./gradle/gradlew clean build

# Ou usar Makefile
make clean-build
```

### Testes falhando
```bash
# Verificar se Docker está rodando
docker ps

# Limpar e testar
make clean test

# Testes com mais informações
make test-verbose
```

### Formatação incorreta
```bash
# Aplicar formatação automaticamente
make format

# Ou
./gradle/gradlew spotlessApply
```

## 🚀 Workflow de Desenvolvimento

### Setup Inicial
```bash
# 1. Clonar repositório
git clone <repo-url>
cd starter

# 2. Configurar ambiente
make dev-setup

# 3. Verificar se tudo está OK
make verify
```

### Desenvolvimento Diário
```bash
# 1. Atualizar código
git pull

# 2. Iniciar dependências
make docker-up

# 3. Executar aplicação
make run

# Em outro terminal
make run-consumer

# 4. Desenvolver...

# 5. Antes de commit
make pre-commit

# 6. Commit
git add .
git commit -m "feat: nova funcionalidade"
git push
```

### Adicionando Nova Funcionalidade
```bash
# 1. Criar branch
git checkout -b feature/nova-funcionalidade

# 2. Implementar no domain (use cases, ports)
# 3. Implementar adapters (se necessário)
# 4. Adicionar testes

# 5. Verificar qualidade
make check
make coverage

# 6. Commit e push
make pre-commit
git push origin feature/nova-funcionalidade

# 7. Criar Pull Request
```

## 📚 Próximos Passos Sugeridos

### Funcionalidades
- [ ] Adicionar paginação na listagem de usuários
- [ ] Implementar busca por nome/email
- [ ] Adicionar campos adicionais (endereço, avatar)
- [ ] Implementar soft delete

### Segurança
- [ ] Autenticação JWT
- [ ] Autorização por roles
- [ ] Rate limiting
- [ ] Validação de CORS

### Performance
- [ ] Implementar cache Redis
- [ ] Otimizar queries JPA
- [ ] Adicionar índices no banco
- [ ] Connection pooling

### Observabilidade
- [ ] Métricas (Micrometer + Prometheus)
- [ ] Logs estruturados (ELK Stack)
- [ ] Tracing distribuído (Jaeger/Zipkin)
- [ ] Health checks

### DevOps
- [ ] Dockerfiles para cada driver
- [ ] Docker Compose para produção
- [ ] Pipeline CI/CD (GitHub Actions)
- [ ] Deploy Kubernetes
- [ ] Helm charts

### Arquitetura
- [ ] Implementar CQRS
- [ ] Event Sourcing
- [ ] Saga pattern para transações distribuídas
- [ ] API Gateway

## 📄 Licença

Este é um projeto de exemplo/starter para demonstrar arquitetura hexagonal com Spring Boot e Kotlin.

---

**Desenvolvido com:**
- ☕ Java 21
- 🎯 Kotlin 2.2.21
- 🍃 Spring Boot 4.0.1
- 🐘 PostgreSQL 16
- 🎺 Kafka 7.5.0
- 🐘 Gradle 9.2.1

**Arquitetura:** Hexagonal (Ports & Adapters) + Domain-Driven Design
