# 🪙 **Software-Testing: Simulador de Criaturas Saltitantes**

## 📜 **Descrição do Projeto**

O **Software-Testing** é uma aplicação que simula uma corrida de criaturas saltitantes, cada uma movendo-se aleatoriamente no horizonte e roubando moedas das criaturas vizinhas. O projeto tem como principal objetivo de aplicar e exercitar práticas robustas de teste de software, incluindo:

    Testes de Domínio

    Testes de Fronteira

    Testes de Cobertura (Linha, Branch, MC/DC)

    Testes de Mutação

O projeto possui:

* **Backend**: Desenvolvido em **Java** utilizando **Spring Boot**.
* **Frontend**: Desenvolvido em **React com Next.js**, permitindo visualização interativa da simulação.

---

## 🚀 **Regras da Simulação**

* Existem entre **2 e 10 criaturas** numeradas de `0` a `n-1`.
* Cada criatura começa com:

  * **1.000.000 moedas de ouro** (`gi`).
  * Uma posição inicial no horizonte (`xi`), que é um número de ponto flutuante.
* A cada iteração:

  1. A criatura calcula um novo lugar no horizonte:

     ```
     xi ← xi + r * gi
     ```

     Onde `r` é um número aleatório no intervalo `[-1, 1]`.
  2. A criatura então **rouba metade das moedas da criatura mais próxima** em um dos lados.
* Se uma criatura tiver menos de **1.0 moeda**, ela é:

  * **Eliminada da simulação**.
  * Movida da lista de criaturas ativas para a lista de inativas.
* A simulação **termina** quando:

  * Alguma criatura atinge uma posição `xi >= 10.000.000`.
  * Ou resta apenas **1 criatura ativa**.

---

## 🔥 **Funcionalidades**

* Execução de simulações com qualquer quantidade de criaturas (2 a 10).
* Relatórios em tempo real de:

  * Posições no horizonte.
  * Quantidade de ouro de cada criatura.
  * Estado (ativa ou eliminada).
* Interface gráfica para visualização da corrida.
* API REST para controle da simulação.
* Suíte de testes abrangente:

  * **Testes de domínio.**
  * **Testes de fronteira.**
  * **Testes estruturais com cobertura MC/DC de 100%.**
  * **Testes de mutação com quase 100% das mutações mortas.**

---

## 🗺️ **Arquitetura do Projeto**

### 🏗️ **Backend**

* **Java 17**
* **Spring Boot**
* API REST que permite:

  * Iniciar uma nova simulação.
  * Iterar a simulação passo a passo.
  * Resetar a simulação.
  * Obter o status atual (posição, ouro e estado das criaturas).
  * Obter a criatura atual.
  * Finalizar a simualação.

### 🎨 **Frontend**

* **React + Next.js**
* Interface gráfica que:

  * Exibe as criaturas na simulação.
  * Mostra visualmente sua posição no horizonte.
  * Permite avançar a simulação, resetar e visualizar a criatura vencedora.
  * Iterações automáticas.
  * Visualizar as moedas e o X de todas as criaturas.
  * Visualizar criaturas ativas e inativas.

---

## 🧰 **Dependências**

### ✅ **Backend**

* **pitest**
* **lombok**
* **spring validation**
* **spring web**
* **spring devtools**
* **spring test**
* **AssertJ (Já vem no spring teste)**
  
### ✅ **Frontend**

* **React**
* **Next.js**
* **TailwindCSS**

---

## 2. Requisitos para Rodar

Para executar o projeto completo (backend + frontend) e rodar os testes, é necessário:

* **Java 17**
* **Maven 3.9.9**
* **Node.js 18 ou superior**
* **npm** (acompanha o Node.js)

### Verifique se está instalado:

```bash
java -version
mvn -v
node -v
npm -v
```

Se não estiver, instale por aqui:

* Java: [https://adoptium.net/](https://adoptium.net/)
* Maven: [https://maven.apache.org/install.html](https://maven.apache.org/install.html)
* Node.js + npm: [https://nodejs.org/](https://nodejs.org/)

---

## 3. Como Executar o Projeto

### 3.1 Clonar o repositório

```bash
git clone https://github.com/Brenokly/Software-Testing.git
cd Software-Testing
```

### 3.2 Instalar dependências do frontend (Entre na pasta frontend e rode o comando)

```bash
npm install
```

### 3.3 Executar o projeto backend + frontend juntos (Execute o comando a baixo partindo da raiz do projeto ##Software-Testing)

```bash
npm start
```

Acesse:

* Frontend: [http://localhost:3000](http://localhost:3000)
* Backend: [http://localhost:8080/api/simulacao](http://localhost:8080/api/simulacao)

---

## 4. 🔗 **Endpoints da API**

Base: `http://localhost:8080/api/simulacao`

| Método | Endpoint          | Descrição                                        | Request Body / Response                                                                 |
| ------ | ----------------- | ------------------------------------------------ | --------------------------------------------------------------------------------------- |
| POST   | `/iniciar`        | Inicia a simulação com quantidade de criaturas   | `{ "quantidade": <int> }` <br> Retorna status da primeira iteração (IterationStatusDTO) |
| POST   | `/iterar`         | Executa uma iteração da simulação                | Retorna status atualizado (IterationStatusDTO)                                          |
| POST   | `/resetar`        | Reseta a simulação para estado inicial           | Retorna status inicial (IterationStatusDTO)                                             |
| GET    | `/status`         | Obtém o status atual da simulação                | Retorna status atual (IterationStatusDTO)                                               |
| GET    | `/criatura-atual` | Obtém o ID da criatura atual que será processada | Retorna inteiro (ID da criatura atual)                                                  |
| GET    | `/finalizar`      | Finaliza a simulação, forçando término           | Retorna status final (IterationStatusDTO)                                               |

---

## 5. Suite de Testes — Completa e Abrangente ✅

A suite de testes contempla:

### ✔️ Testes de Domínio

* Verificam o comportamento com entradas válidas dentro dos intervalos esperados.
* Exemplo: Simular com 5 criaturas, verificar interações padrão.

### ✔️ Testes de Fronteira

* Avaliam os limites inferior e superior dos dados.
* Exemplo: Simular com 2 (mínimo) e 10 (máximo) criaturas, além de testar valores inválidos como 1 ou 11.

### ✔️ Cobertura Total

* **Cobertura de Linha**: 100%
* **Cobertura de Branch**: 100%
* **Cobertura MC/DC**: 100%

Garantido por meio da suíte de testes aplicada nos modelos (`model`) e no serviço (`service`), que são os principais responsáveis pela lógica do negócio.

### ✔️ Teste de Mutação

* Garante que os testes não apenas executam código, mas realmente **detectam falhas**, simulando mutações no código.

---

## 8. ✔️ Gerar Teste de Mutação — Passo a Passo

O projeto usa o **PITest**, ferramenta padrão de mercado para teste de mutação em Java.

### 🔧 Executar teste de mutação:

1. No terminal, dentro da pasta do projeto:

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

### 🧠 Interpretação dos resultados:

* Após rodar, acesse o relatório gerado em:

```
/target/pit-reports/YYYYMMDDHHMM/index.html
```

* Abra o arquivo `index.html` no navegador.

### 📊 O que você verá no relatório:

* **Mutations Killed (Mortas)**: 🟩 — Testes pegaram a mutação.
* **Survived (Sobreviventes)**: 🔴 — Alguma mutação passou sem ser pega (indica problema na suite).
* **No Coverage**: ⚪ — Código não coberto (não acontece neste projeto).
* **Timeouts / Run Errors**: ⚠️ — Problemas na execução.

**Ferramentas:**

* **JUnit 5**
* **AssertJ**
* **PITest** (teste de mutação)

---

## 📊 **Exemplos de Execução**

1. **Iniciar simulação com 5 criaturas**

   * Chamada: `POST /simulation/iniciar` com body:

```json
{
  "creatures": 5
}
```

2. **Iterar simulação**

   * Chamada: `POST /simulation/iterar`
3. **Obter status atual**

   * Chamada: `GET /simulation/status`
4. **Resetar simulação**

   * Chamada: `POST /simulation/resetar`

---

## 🏅 **Autor**

* **Breno Kly** – [GitHub](https://github.com/Brenokly)

---
