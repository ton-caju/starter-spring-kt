.PHONY: help build clean test run docker-up docker-down docker-logs format format-check coverage sonar install jar bootjar spotless-apply spotless-check

# Default target
.DEFAULT_GOAL := help

## Help target
help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

## Build targets
build: ## Build the project
	./gradle/gradlew build

build-fast: ## Build the project without tests
	./gradle/gradlew build -x test

clean: ## Clean build artifacts
	./gradle/gradlew clean

clean-build: ## Clean and build the project
	./gradle/gradlew clean build

jar: ## Build jar file
	./gradle/gradlew jar

bootjar: ## Build executable Spring Boot jar
	./gradle/gradlew bootJar

install: ## Install dependencies and build project
	./gradle/gradlew build --refresh-dependencies

## Test targets
test: ## Run all tests
	./gradle/gradlew test

test-unit: ## Run unit tests
	./gradle/gradlew test --tests '*Test'

test-integration: ## Run integration tests
	./gradle/gradlew test --tests '*IntegrationTest'

test-verbose: ## Run tests with detailed output
	./gradle/gradlew test --info

## Run targets
run: ## Run the REST server application
	./gradle/gradlew :driver:rest-server:bootRun

run-consumer: ## Run the event consumer application
	./gradle/gradlew :driver:event-consumer:bootRun

run-all: ## Run both REST server and event consumer
	@echo "Starting REST server and Event Consumer..."
	@./gradle/gradlew :driver:rest-server:bootRun > /dev/null 2>&1 & echo $$! > /tmp/rest-server.pid
	@./gradle/gradlew :driver:event-consumer:bootRun > /dev/null 2>&1 & echo $$! > /tmp/event-consumer.pid
	@echo "REST server started (PID: $$(cat /tmp/rest-server.pid))"
	@echo "Event consumer started (PID: $$(cat /tmp/event-consumer.pid))"
	@echo "Use 'make stop-all' to stop both applications"

stop-all: ## Stop all running applications
	@if [ -f /tmp/rest-server.pid ]; then kill $$(cat /tmp/rest-server.pid) 2>/dev/null && rm /tmp/rest-server.pid && echo "REST server stopped"; fi
	@if [ -f /tmp/event-consumer.pid ]; then kill $$(cat /tmp/event-consumer.pid) 2>/dev/null && rm /tmp/event-consumer.pid && echo "Event consumer stopped"; fi

run-dev: ## Run REST server in development mode with live reload
	./gradle/gradlew :driver:rest-server:bootRun --continuous

run-consumer-dev: ## Run event consumer in development mode with live reload
	./gradle/gradlew :driver:event-consumer:bootRun --continuous

## Docker targets
docker-up: ## Start all docker containers
	docker-compose up -d

docker-down: ## Stop all docker containers
	docker-compose down

docker-restart: ## Restart all docker containers
	docker-compose restart

docker-logs: ## Show docker logs
	docker-compose logs -f

docker-clean: ## Stop and remove all containers, networks and volumes
	docker-compose down -v

docker-postgres: ## Start only PostgreSQL
	docker-compose up -d postgres

docker-kafka: ## Start Kafka and Zookeeper
	docker-compose up -d zookeeper kafka

## Code quality targets
format: ## Format code using Spotless
	./gradle/gradlew spotlessApply

format-check: ## Check code formatting
	./gradle/gradlew spotlessCheck

spotless-apply: ## Apply Spotless formatting
	./gradle/gradlew spotlessApply

spotless-check: ## Check Spotless formatting
	./gradle/gradlew spotlessCheck

lint: ## Check code style
	./gradle/gradlew spotlessCheck

## Coverage targets
coverage: ## Generate test coverage report
	./gradle/gradlew test jacocoTestReport

coverage-verify: ## Verify coverage meets minimum threshold
	./gradle/gradlew jacocoTestCoverageVerification

coverage-report: ## Generate and open coverage report
	./gradle/gradlew test jacocoTestReport && open build/reports/jacoco/test/html/index.html

sonar: ## Run SonarQube analysis (requires local SonarQube)
	./gradle/gradlew sonar

## Dependencies targets
dependencies: ## Show dependency tree
	./gradle/gradlew dependencies

dependencies-update: ## Check for dependency updates
	./gradle/gradlew dependencyUpdates

## Development workflow targets
dev-setup: docker-up ## Setup development environment
	@echo "Development environment ready!"
	@echo "PostgreSQL: localhost:5432 (user: postgres, password: postgres, db: userdb)"
	@echo "Kafka: localhost:9092"

dev-clean: docker-down clean ## Clean development environment

dev-restart: docker-restart clean build run ## Restart development environment

check: format-check test ## Run all checks (format + tests)

verify: format-check test coverage-verify ## Run all verifications

ci: clean build test coverage coverage-verify ## Run CI pipeline

pre-commit: format test ## Run before committing
	@echo "✓ Code formatted and tests passed!"

## Info targets
info: ## Show project information
	@echo "Project: starter"
	@echo "Version: 0.0.1-SNAPSHOT"
	@echo "Modules:"
	@echo "  - domain"
	@echo "  - driven:persistence"
	@echo "  - driven:event-producer"
	@echo "  - driven:rest-client"
	@echo "  - driver:rest-server"
	@echo "  - driver:event-consumer"

tasks: ## Show all available Gradle tasks
	./gradle/gradlew tasks --all
