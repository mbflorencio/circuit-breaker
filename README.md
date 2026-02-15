# Circuit Breaker Demo (Java + Spring Boot + Resilience4j) — 2 apps + Docker

Este repositório contém **2 aplicações Spring Boot** (Maven) para demonstrar o padrão **Circuit Breaker** em aula, rodando via **Docker Compose**.

- **product-service** (instável/“downstream”): expõe um endpoint de produto e permite injetar **falhas** e **latência**.
- **client-service** (cliente/“upstream”): chama o product-service e aplica **Circuit Breaker + fallback** via **Resilience4j**.

## Pré-requisitos
- Docker + Docker Compose

> Se você quiser rodar sem Docker, precisa de Java 17+ e Maven 3.9+.

## Subindo tudo com Docker

Na raiz do projeto:

```bash
docker compose up --build
```

- client-service: http://localhost:8080
- product-service: http://localhost:8081

## Endpoints principais

### product-service
- `GET  /products/{id}` → retorna um JSON do produto (pode falhar/lentificar)
- `POST /admin/config?failPercent=80&delayMs=900` → configura instabilidade
- `GET  /admin/config` → mostra a configuração atual

### client-service
- `GET  /catalog/{id}` → chama o product-service com Circuit Breaker e fallback
- `GET  /catalog/batch/{id}?n=20` → faz N chamadas (bom para disparar o Circuit Breaker rapidamente)

### Actuator (observabilidade)
- client-service: `GET /actuator/health`, `GET /actuator/metrics`
- product-service: `GET /actuator/health`

## Demonstração (passo a passo)
Veja o arquivo [DEMO_SCRIPT.md](./DEMO_SCRIPT.md).
