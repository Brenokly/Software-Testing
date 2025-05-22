# 🪙 **Software-Testing: Simulador de Criaturas Saltitantes**

## 📜 **Descrição do Projeto**

O **Software-Testing** é uma aplicação que simula uma corrida de criaturas saltitantes, onde cada criatura se move aleatoriamente no horizonte e rouba moedas das criaturas vizinhas.

O objetivo principal é **aplicar e exercitar práticas robustas de teste de software**, incluindo:

* ✅ **Testes de Domínio**
* ✅ **Testes de Fronteira**
* ✅ **Testes de Cobertura (Linha, Branch, MC/DC)**
* ✅ **Testes de Mutação**

O projeto é composto por:

* 🎯 **Backend:** Desenvolvido em **Java 17** com **Spring Boot**.
* 🎨 **Frontend:** Desenvolvido em **React + Next.js**, permitindo uma visualização interativa da simulação.

---

## 🚀 **Regras da Simulação**

* Existem entre **2 e 10 criaturas**, numeradas de `0` a `n-1`.
* Cada criatura começa com:

  * 🪙 **1.000.000 moedas de ouro** (`gi`).
  * 📍 Uma **posição inicial no horizonte** (`xi`), número decimal (ponto flutuante).

### 🔄 A cada iteração:

1. A criatura calcula um novo ponto no horizonte:

   ```
   xi ← xi + r * gi
   ```

   * Onde `r` é um valor aleatório no intervalo `[-1, 1]`.
2. A criatura **rouba metade das moedas da criatura mais próxima** em **um dos lados**.

### ⚰️ Eliminação:

* Se uma criatura tiver menos de **1.0 moeda**, ela:

  * É **eliminada da simulação**.
  * Move-se da lista de criaturas **ativas** para **inativas**.

### 🏁 Condições de término:

* A simulação encerra quando:

  * Alguma criatura atinge uma posição `xi >= 10.000.000`.
  * Ou resta apenas **uma criatura ativa**.

---

## 🔥 **Funcionalidades**

* 🔢 Suporte de **2 a 10 criaturas**.
* 📊 Relatórios em tempo real de:

  * Posições (`xi`)
  * Quantidade de ouro (`gi`)
  * Estado (**ativa** ou **eliminada**)
* 🌐 Interface gráfica interativa.
* 📡 API REST para controle total da simulação.
* 🧪 **Suíte de testes completa:**

  * Testes de domínio.
  * Testes de fronteira.
  * **Cobertura MC/DC de 100%.**
  * Testes de mutação com quase todas as mutações mortas.

---

## 🗺️ **Arquitetura do Projeto**

### 🏗️ **Backend**

* **Java 17**
* **Spring Boot**
* API REST com os seguintes recursos:

  * Iniciar simulação.
  * Iterar simulação.
  * Resetar simulação.
  * Obter status atual.
  * Obter criatura atual.
  * Finalizar a simulação.

### 🎨 **Frontend**

* **React + Next.js**
* Interface que permite:

  * Visualizar criaturas na simulação.
  * Acompanhar a posição no horizonte.
  * Ver moedas e status (ativo/inativo).
  * Executar iterações manual ou automaticamente.
  * Resetar a simulação.
  * Verificar a criatura vencedora.

---

## 🧰 **Dependências**

### ✅ **Backend**

* **Spring Boot** (`web`, `validation`, `devtools`, `test`)
* **Lombok**
* **PITest** (teste de mutação)
* **AssertJ** (via Spring Test)

### ✅ **Frontend**

* **React**
* **Next.js**
* **TailwindCSS**

---

## ⚙️ **Requisitos para Executar**

* **Java 17**
* **Maven 3.9.9**
* **Node.js 18+**
* **npm** (acompanha o Node.js)

### Verificar instalações:

```bash
java -version
mvn -v
node -v
npm -v
```

### Instalação dos ambientes:

* [Java](https://adoptium.net/)
* [Maven](https://maven.apache.org/install.html)
* [Node.js + npm](https://nodejs.org/)

---

## 🚀 **Execução do Projeto**

### 1️⃣ Clonar o repositório

```bash
git clone https://github.com/Brenokly/Software-Testing.git
cd Software-Testing
```

### 2️⃣ Instalar dependências do frontend

```bash
cd frontend
npm install
```

### 3️⃣ Executar backend + frontend juntos

(No diretório raiz do projeto)

```bash
npm start
```

Acesse:

* Frontend → [http://localhost:3000](http://localhost:3000)
* Backend → [http://localhost:8080/api/simulacao](http://localhost:8080/api/simulacao)

---

## 🔗 **API - Endpoints**

Base URL → `http://localhost:8080/api/simulacao`

| Método | Endpoint          | Descrição                                      | Request / Response                               |
| ------ | ----------------- | ---------------------------------------------- | ------------------------------------------------ |
| POST   | `/iniciar`        | Inicia simulação com `quantidade` de criaturas | `{ "quantidade": <int> }` → `IterationStatusDTO` |
| POST   | `/iterar`         | Executa uma iteração                           | → `IterationStatusDTO`                           |
| POST   | `/resetar`        | Reseta a simulação                             | → `IterationStatusDTO`                           |
| GET    | `/status`         | Consulta status atual da simulação             | → `IterationStatusDTO`                           |
| GET    | `/criatura-atual` | Retorna o ID da criatura atual                 | → `int`                                          |
| GET    | `/finalizar`      | Finaliza a simulação manualmente               | → `IterationStatusDTO`                           |

---

## 🧪 **Suíte de Testes — Abrangente e Completa ✅**

### ✔️ **Testes de Domínio**

* Avaliam comportamento com entradas dentro dos limites esperados.

### ✔️ **Testes de Fronteira**

* Avaliam limites:

  * Mínimo → 2 criaturas.
  * Máximo → 10 criaturas.
  * E também entradas inválidas (ex.: 1 ou 11 criaturas).

### ✔️ **Cobertura de Código**

* **Linha:** 100%
* **Branch:** 100%
* **MC/DC:** 100%

### ✔️ **Testes de Mutação**

* **PITest** garante que os testes detectem alterações sutis e potenciais erros.

---

## 🧠 **Executar Teste de Mutação**

1️⃣ No terminal, na raiz do projeto:

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

2️⃣ Acesse o relatório:

```
/target/pit-reports/YYYYMMDDHHMM/index.html
```

### 🔍 Interpretação:

| Status | Significado                                                 |
| ------ | ----------------------------------------------------------- |
| 🟩     | **Killed** — Testes capturaram a mutação                    |
| 🔴     | **Survived** — Mutação sobreviveu (possível falha na suite) |
| ⚪      | **No Coverage** — Código não coberto (não ocorre aqui)      |
| ⚠️     | **Timeout / Run Error** — Erro na execução                  |

---

## ▶️ **Exemplos de Uso via API**

1️⃣ **Iniciar simulação com 5 criaturas**

```http
POST /api/simulacao/iniciar
Body:
{
  "quantidade": 5
}
```

2️⃣ **Iterar simulação**

```http
POST /api/simulacao/iterar
```

3️⃣ **Obter status**

```http
GET /api/simulacao/status
```

4️⃣ **Resetar simulação**

```http
POST /api/simulacao/resetar
```

---

## 🏅 **Autor**

* **Breno Kly** – [GitHub](https://github.com/Brenokly)

---
