# 🪙 **Software-Testing: Simulador de Criaturas Saltitantes**

## 📜 **Descrição do Projeto**

O **Software-Testing** é uma aplicação que simula uma corrida de criaturas saltitantes, cada uma movendo-se aleatoriamente no horizonte e roubando moedas das criaturas vizinhas. O projeto tem como foco o desenvolvimento de um simulador robusto aliado a uma suíte de testes completa, com cobertura MC/DC de 100%, garantindo alta confiabilidade no comportamento do sistema.

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

### 🎨 **Frontend**

* **React + Next.js**
* Interface gráfica que:

  * Exibe as criaturas na simulação.
  * Mostra visualmente sua posição no horizonte.
  * Permite avançar a simulação, resetar e visualizar a criatura vencedora.

---

## 🧰 **Dependências**

### ✅ **Backend**

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.pitest</groupId>
        <artifactId>pitest-junit5-plugin</artifactId>
        <version>1.2.0</version>
    </dependency>
</dependencies>
```

### ✅ **Frontend**

* **React**
* **Next.js**
* **TailwindCSS** (opcional, se utilizado no seu projeto)

---

## 🏃‍♂️ **Como Executar o Projeto**

### 🔧 **Backend (Java)**

1. Clone o repositório:

```bash
git clone https://github.com/Brenokly/Software-Testing.git
cd Software-Testing
```

2. Rode a aplicação:

* No terminal da IDE (IntelliJ, VS Code, Eclipse) ou:

```bash
./mvnw spring-boot:run
```

3. A API estará disponível em:

```
http://localhost:8080
```

### 📱 **Frontend (React + Next.js)**

1. Acesse a pasta do frontend (`/frontend` ou a pasta onde você criou o front):

```bash
cd frontend
```

2. Instale as dependências:

```bash
npm install
```

3. Rode a aplicação:

```bash
npm run dev
```

4. Acesse no navegador:

```
http://localhost:3000
```

⚠️ **Observação:** Certifique-se de que o backend esteja rodando em `localhost:8080`, pois o frontend se conecta a essa API.

---

## 🔗 **Endpoints da API**

| Método | Rota                  | Descrição                         |
| ------ | --------------------- | --------------------------------- |
| `POST` | `/simulation/start`   | Inicia uma nova simulação         |
| `POST` | `/simulation/iterate` | Executa uma iteração              |
| `POST` | `/simulation/reset`   | Reseta a simulação                |
| `GET`  | `/simulation/status`  | Retorna status atual da simulação |

---

## 🧪 **Testes**

* **Cobertura:** 100% de cobertura MC/DC.
* **Tipos de testes aplicados:**

  * **Domínio:** Valida regras específicas da simulação.
  * **Fronteira:** Testa limites, como mínima e máxima quantidade de criaturas.
  * **Estruturais:** Valida os fluxos de controle interno, como eliminação de criaturas, movimentação e término da simulação.

**Ferramentas:**

* **JUnit 5**
* **AssertJ**
* **JaCoCo** (análise de cobertura)
* **PITest** (teste de mutação)

---

## 📊 **Exemplos de Execução**

1. **Iniciar simulação com 5 criaturas**

   * Chamada: `POST /simulation/start` com body:

```json
{
  "creatures": 5
}
```

2. **Iterar simulação**

   * Chamada: `POST /simulation/iterate`
3. **Obter status atual**

   * Chamada: `GET /simulation/status`
4. **Resetar simulação**

   * Chamada: `POST /simulation/reset`

---

## 🎯 **Possíveis Melhorias Futuras**

* Sistema de logs para análise detalhada de cada iteração.
* Exportação dos resultados da simulação (JSON, CSV).
* Ranking das criaturas em simulações anteriores.
* Animações mais elaboradas no frontend.

---

## 🏅 **Autor**

* **Breno Kly** – [GitHub](https://github.com/Brenokly)

---

Se quiser, posso gerar um arquivo `README.md` com esse conteúdo formatado profissionalmente para subir no seu GitHub. Quer? 🔥
