# Exemplos de API

## Pré-requisitos

1. Iniciar as dependências:
```bash
docker-compose up -d
```

2. Iniciar o Rest Server:
```bash
./gradlew :driver:rest-server:bootRun
```

3. Iniciar o Event Consumer (opcional - para ver os eventos sendo consumidos):
```bash
./gradlew :driver:event-consumer:bootRun
```

## Criar Usuário

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

Resposta esperada:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "João Silva",
  "email": "joao@example.com",
  "phone": "+5511999999999",
  "birthday": "1990-01-15"
}
```

## Listar Todos os Usuários

```bash
curl http://localhost:8080/api/users
```

## Buscar Usuário por ID

```bash
# Substitua {id} pelo ID retornado na criação
curl http://localhost:8080/api/users/{id}
```

## Atualizar Usuário

```bash
# Substitua {id} pelo ID do usuário
curl -X PUT http://localhost:8080/api/users/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva Atualizado",
    "email": "joao.novo@example.com",
    "phone": "+5511988888888",
    "birthday": "1990-01-15"
  }'
```

## Deletar Usuário

```bash
# Substitua {id} pelo ID do usuário
curl -X DELETE http://localhost:8080/api/users/{id}
```

## Testando os Eventos Kafka

Quando você criar, atualizar ou deletar um usuário através da API REST, o evento correspondente será publicado no Kafka e você poderá ver os logs no console do Event Consumer:

```
Received event: {"userId":"123e4567-e89b-12d3-a456-426614174000","name":"João Silva","email":"joao@example.com","eventType":"USER_CREATED"}
Processing USER_CREATED event - UserId: 123e4567-e89b-12d3-a456-426614174000, Name: João Silva, Email: joao@example.com
Event published successfully: USER_CREATED to topic: user-events
```

## Testando Validações

### Nome vazio (deve falhar)

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "email": "test@example.com",
    "phone": "+5511999999999",
    "birthday": "1990-01-15"
  }'
```

### Email inválido (deve falhar)

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "invalid-email",
    "phone": "+5511999999999",
    "birthday": "1990-01-15"
  }'
```

### Email duplicado (deve falhar)

```bash
# Primeiro crie um usuário
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "User 1",
    "email": "duplicate@example.com",
    "phone": "+5511999999999",
    "birthday": "1990-01-15"
  }'

# Tente criar outro com o mesmo email
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "User 2",
    "email": "duplicate@example.com",
    "phone": "+5511988888888",
    "birthday": "1991-02-20"
  }'
```

## Verificar Dados no PostgreSQL

```bash
docker exec -it userdb-postgres psql -U postgres -d userdb

# Dentro do psql:
SELECT * FROM users;
```

## Parar as Dependências

```bash
docker-compose down
```

## Limpar Dados (incluindo volumes)

```bash
docker-compose down -v
```
