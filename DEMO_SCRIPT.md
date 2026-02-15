# Script do demo (passo a passo)

Este roteiro foi pensado para uma demonstração em sala, em ~10–20 minutos, repetível.

> **Dica:** Os exemplos usam `jq` para formatar JSON. Se você não tiver `jq`, pode remover o `| jq` e ler o JSON “cru”.


## 0) Subir os serviços

Na raiz do repositório:

```bash
docker compose up --build
```

Espere ver no log:
- `product-service` ouvindo em `8080` (host `8081`)
- `client-service` ouvindo em `8080` (host `8080`)

## 1) Baseline (tudo saudável)

1. Confira que o product-service está “saudável”:

```bash
curl -s http://localhost:8081/admin/config | jq
```

Esperado: `failPercent=0` e `delayMs=0`.

2. Chame o client-service (sem falhas):

```bash
curl -s http://localhost:8080/catalog/1 | jq
```

Esperado: `source: "product-service"`.

## 2) Simular falhas (abrir o Circuit Breaker por taxa de erro)

1. Configure 80% de falha no downstream:

```bash
curl -s -X POST "http://localhost:8081/admin/config?failPercent=80&delayMs=0" | jq
```

2. Dispare várias chamadas pelo client-service:

```bash
for i in {1..20}; do curl -s http://localhost:8080/catalog/1 | jq -r '.source' ; done
```

O que observar:
- No começo, mistura de `product-service` e `fallback`.
- Depois, predominância de `fallback` **instantâneo**: o Circuit Breaker tende a entrar em **OPEN** e “curto-circuitar” as chamadas.

## 3) Simular lentidão (abrir o Circuit Breaker por chamadas lentas)

1. Zere falhas e adicione delay acima do threshold configurado (ex.: 900ms):

```bash
curl -s -X POST "http://localhost:8081/admin/config?failPercent=0&delayMs=900" | jq
```

2. Use o batch para coletar chamadas suficientes rapidamente:

```bash
curl -s "http://localhost:8080/catalog/batch/1?n=15" | jq '.[].source'
```

O que observar:
- Mesmo com respostas bem sucedidas, elas contam como **slow calls** (chamadas lentas) para o Circuit Breaker.
- Após a janela mínima, ele pode abrir por **slowCallRateThreshold**.

## 4) Recuperação automática (HALF-OPEN → CLOSED)

1. “Cure” o downstream:

```bash
curl -s -X POST "http://localhost:8081/admin/config?failPercent=0&delayMs=0" | jq
```

2. Aguarde o tempo de OPEN (por padrão, ~5s) e chame novamente:

```bash
sleep 6
curl -s http://localhost:8080/catalog/1 | jq
```

O que observar:
- O Circuit Breaker tenta algumas chamadas em **HALF-OPEN**.
- Se elas passam, volta a **CLOSED** e o `source` volta a ser `product-service`.

## 5) (Opcional) Ver métricas no Actuator

A forma exata do nome das métricas pode variar por versão, mas vale explorar:

```bash
curl -s http://localhost:8080/actuator/metrics | jq '.names[]' | grep -i circuit
```

Depois, consulte uma métrica específica retornada na lista.

---

### Dicas rápidas de fala
- "O objetivo não é esconder o erro — é falhar rápido e preservar recursos."
- "Circuit Breaker é especialmente útil para evitar *cascading failures* em sistemas distribuídos."
