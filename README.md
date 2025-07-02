# 🪙 Software-Testing: Simulador de Criaturas e Guardiões

## 📜 Descrição do Projeto

O **Software-Testing** é uma aplicação que simula um ecossistema dinâmico onde criaturas saltitantes competem por recursos, se fundem em clusters mais fortes e interagem com um guardião poderoso.

O objetivo principal é **aplicar e exercitar um conjunto completo de práticas de teste de software**, incluindo:

* ✅ **Testes de Unidade e Domínio**
* ✅ **Testes de Fronteira**
* ✅ **Testes Estruturais (Cobertura de Linha, Branch, MC/DC)**
* ✅ **Testes de Mutação com PITest**
* ✅ **Testes Baseados em Propriedades e uso de Dublês de Teste (Mocks/Stubs)**

O projeto é composto por:

* 🎯 **Backend:** Desenvolvido em **Java 17** com **Spring Boot**, expondo uma API REST completa.
* 🎨 **Frontend:** Desenvolvido em **React + Next.js** com **TailwindCSS**, permitindo uma visualização interativa da simulação.

---

## 🚀 Regras da Simulação

A simulação envolve **criaturas**, **clusters** e um **guardião**.

* Existem entre **1 e 10 criaturas** iniciais.
* **Usuários** podem se cadastrar para salvar suas pontuações de simulações bem-sucedidas.

### 🧬 Entidades

#### Criaturas
* Começam com 🪙 **1.000.000 de moedas de ouro** (`gi`).
* Possuem uma 📍 **posição inicial no horizonte** (`xi`), um número de ponto flutuante.

#### Guardião
* Uma criatura especial com ID `n+1`.
* Começa com 🪙 **0 moedas de ouro**.
* E com o **X == 5_000000.0**.
* Sua missão é eliminar os clusters e proteger o horizonte.

#### Clusters
* São formados quando duas ou mais criaturas (ou clusters) ocupam a mesma posição.
* O ouro de um cluster é a **soma do ouro** de todas as entidades que o formaram.
* Atuam como uma única entidade poderosa.

### 🔄 A Cada Iteração

1.  **Movimento:** Todas as criaturas, clusters e o guardião calculam uma nova posição:
    ```
    xi ← xi + r * gi
    ```
    * Onde `r` é um valor aleatório no intervalo `[-1, 1]`.

2.  **Interações de Colisão:**
    * **Criatura + Criatura/Cluster → Fusão:** Se entidades colidem, elas se fundem em um único cluster, somando seus ouros.
    * **Guardião + Cluster → Absorção:** Se o guardião colide com um cluster, o cluster é **eliminado**, e todo o seu ouro é transferido para o guardião.

3.  **Roubo do Vizinho:** Após o movimento e as colisões, cada criatura/cluster restante rouba **metade do ouro** da entidade mais próxima.

### ⚰️ Eliminação

* Se o ouro de uma criatura ou cluster chegar a **0 ou menos**, a entidade é **eliminada** da simulação.

### 🏁 Condições de Término

A simulação termina quando resta apenas o guardião e no máximo uma outra entidade. O resultado é então decidido:

* 🏆 **Vitória:** A simulação é bem-sucedida se:
    * Resta apenas o guardião no horizonte.
    * OU, restam o guardião e uma criatura, e o **ouro do guardião é maior** que o ouro da criatura.

* 💔 **Derrota:** A simulação falha se:
    * Restam o guardião e uma criatura, mas o **ouro da criatura é maior ou igual** ao ouro do guardião.

---

## 🔥 Funcionalidades

* 🔢 Suporte de **1 a 10 criaturas** iniciais.
* 뭉 **Fusão de Criaturas:** Entidades colidindo formam clusters mais fortes.
* 🛡️ **Guardião do Horizonte:** Uma entidade especial com mecânicas únicas de absorção.
* 👤 **Gestão de Usuários:** Cadastro, login e pontuação individual.
* 📈 **Painel de Estatísticas:** Acompanhamento de pontuações, médias e quantidade de simulações.
* 📊 Relatórios em tempo real de posições (`xi`), ouro (`gi`) e estado das entidades.
* 🌐 Interface gráfica interativa e reativa.
* 📡 API REST para controle total da simulação e gestão de usuários.
* 🧪 **Suíte de Testes Abrangente:**
    * Testes de domínio, fronteira e estruturais.
    * **Cobertura MC/DC de 100%.**
    * Testes de mutação para garantir a robustez da suíte.

---

## 🗺️ Arquitetura do Projeto

### 🏗️ Backend

* **Java 17** e **Spring Boot**.
* Arquitetura Hexagonal (Portas e Adaptadores).
* API REST com recursos para:
    * Controle do ciclo de vida da simulação (iniciar, iterar, resetar).
    * Gestão de Usuários (CRUD).
    * Consulta de estatísticas globais e por usuário.

### 🎨 Frontend

* **React + Next.js** e **TailwindCSS**.
* Interface que permite:
    * Visualizar todas as entidades na simulação.
    * Acompanhar ouro e posições em tempo real.
    * Executar iterações manual ou automaticamente.
    * Registrar-se e fazer login.
    * Visualizar o placar de estatísticas.

---

## 🧰 Dependências

### ✅ Backend

* **Spring Boot** (`web`, `validation`, `data-jpa`, `devtools`, `test`)
* **Spring Security** (para gestão de usuários)
* **Lombok**
* **MapStruct** (para mapeamento de DTOs)
* **JaCoCo** (cobertura de código)
* **PITest** (teste de mutação)
* **AssertJ**

### ✅ Frontend

* **React** & **Next.js**
* **TypeScript**
* **TailwindCSS**

---

## ⚙️ Requisitos para Executar

* **Java 17+** (Adoptium Temurin é recomendado)
* **Maven 3.9+**
* **Node.js 18+**
* **npm** (geralmente acompanha o Node.js)

### Verificar instalações:

```bash
java -version
mvn -v
node -v
npm -v
````

-----

## 🚀 Execução do Projeto

### 1️⃣ Clonar o repositório

```bash
git clone [https://github.com/Brenokly/Software-Testing.git](https://github.com/Brenokly/Software-Testing.git)
cd Software-Testing
```

### 2️⃣ Instalar dependências do frontend

```bash
cd frontend
npm install
```

### 3️⃣ Executar backend + frontend juntos

A partir do diretório raiz (`Software-Testing`):

```bash
mvn spring-boot:run
```

Em outro terminal, a partir da pasta `frontend`:

```bash
npm run dev
```

Acesse:

  * **Frontend** → [http://localhost:3000](https://www.google.com/search?q=http://localhost:3000)
  * **Backend API** → `http://localhost:8080`

-----

## 🔗 API - Endpoints

Base URL → `http://localhost:8080/api/simulacao`

| Método | Endpoint     | Descrição                                    | Corpo da Requisição (se houver)     |
| :----- | :----------- | :------------------------------------------- | :---------------------------------- |
| POST   | `/iniciar`   | Inicia simulação com `numeroDeCriaturas`       | `{ "numeroDeCriaturas": <int> }`      |
| POST   | `/iterar`    | Executa a próxima iteração da simulação ativa | `{ DTO do Horizonte atual }`          |

*Observação: Endpoints adicionais para gestão de usuários (`/api/usuarios`) e estatísticas (`/api/estatisticas`) também foram implementados.*

-----

## 🧪 Executando a Suíte de Testes

Todos os comandos devem ser executados a partir da pasta `backend`.

### ✔️ Testes Unitários e de Integração

Este comando roda todos os testes (unidade e integração) e valida as regras de cobertura do JaCoCo.

```bash
mvn clean verify
```

### ✔️ Testes de Mutação (PITest)

Este comando executa a suíte completa e, em seguida, roda a análise de mutação.

```bash
mvn clean verify pitest:mutationCoverage
```

Acesse o relatório gerado em:
`backend/target/pit-reports/YYYYMMDDHHMM/index.html`

| Status   | Significado                                            |
| :------- | :----------------------------------------------------- |
| 🟩 **Killed** | Perfeito\! Seus testes detectaram e "mataram" a mutação.   |
| 🔴 **Survived** | Atenção\! A mutação sobreviveu. Seus testes precisam ser melhorados. |

-----

## ▶️ Exemplos de Uso via API

1️⃣ **Iniciar simulação com 5 criaturas**

```http
POST /api/simulacao/iniciar
Content-Type: application/json

{
  "numeroDeCriaturas": 5
}
```

2️⃣ **Iterar simulação**

```http
POST /api/simulacao/iterar
Content-Type: application/json

// Body contém o estado atual do 'Horizonte' retornado pela chamada anterior
{
  "entities": [...],
  "guardiao": {...},
  "status": "RUNNING"
}
```

-----

## 🏅 Autor

  * **Breno Kly** – [GitHub](https://github.com/Brenokly)

<!-- end list -->

```
```
