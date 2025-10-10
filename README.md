# Fynco-TCC
Esse repositório tem como prioridade o Trabalho de Conclusão de Curso (TCC).

Projeto de backend para o TCC "Fynco" — aplicação desenvolvida em Java 17, Spring Boot, com autenticação OAuth2 e banco de dados PostgreSQL.

---

## Sumário

- [Visão geral](#visão-geral)  
- [Tecnologias](#tecnologias)  
- [Estrutura do repositório](#estrutura-do-repositório)  
- [Pré-requisitos](#pré-requisitos)  
- [Configuração do banco de dados](#configuração-do-banco-de-dados)  
- [Configuração de ambiente](#configuração-de-ambiente)  
- [Construção e execução](#construção-e-execução)  
- [Endpoints (exemplos)](#endpoints-exemplos)  
- [Testes](#testes)  
- [Uso com Docker / docker-compose](#uso-com-docker--docker-compose)  
- [Contribuições](#contribuições)  
- [Licença](#licença)  

---

## Visão geral

Este repositório contém o backend da aplicação Fynco, parte do Trabalho de Conclusão de Curso (TCC). A API é implementada em **Java 17** usando **Spring Boot**, com autenticação por **OAuth2** e persistência em **PostgreSQL**.  

A pasta **`api/`** contém todo o código-fonte da aplicação backend.

---

## Tecnologias

- Java 17  
- Spring Boot  
- Maven  
- OAuth2 (fluxos de autorização / autenticação)  
- PostgreSQL  
- Docker / Docker Compose (opcional)  

---

## Estrutura do repositório

Uma visão simplificada da estrutura esperada:

```

Fynco-TCC/
├── api/
│   ├── src/
│   ├── pom.xml
│   └── ... (configurações Spring, controladores, serviços etc.)
├── data/ (opcional: scripts SQL, migrações)
├── .github/ (workflows, ações)
└── README.md

````

A pasta `api/` é o núcleo da aplicação backend.

---

## Pré-requisitos

Antes de rodar o projeto localmente, você vai precisar:

- Java 17 instalado  
- Maven instalado  
- PostgreSQL rodando (local ou remoto)  
- Git  

---

## Configuração do banco de dados

Crie um banco e usuário no PostgreSQL para o projeto. Exemplo:

```sql
CREATE DATABASE fynco_db;
CREATE USER fynco_user WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE fynco_db TO fynco_user;
````

Ajuste os nomes conforme seu uso local.

---

## Configuração de ambiente

Você precisará definir algumas propriedades no **application.properties** ou **application.yml**, ou via variáveis de ambiente. Exemplo de variáveis que podem existir:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fynco_db
spring.datasource.username=fynco_user
spring.datasource.password=sua_senha

# Configurações de OAuth2
oauth2.client-id=SEU_CLIENT_ID
oauth2.client-secret=SEU_CLIENT_SECRET
oauth2.issuer-uri=https://seu-servidor-oauth

# Perfil ativo
spring.profiles.active=dev
```

Se usar variáveis de ambiente:

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/fynco_db"
export SPRING_DATASOURCE_USERNAME="fynco_user"
export SPRING_DATASOURCE_PASSWORD="sua_senha"
export OAUTH2_CLIENT_ID="SEU_CLIENT_ID"
export OAUTH2_CLIENT_SECRET="SEU_CLIENT_SECRET"
export OAUTH2_ISSUER_URI="https://seu-servidor-oauth"
export SPRING_PROFILES_ACTIVE="dev"
```

---

## Construção e execução

No terminal:

```bash
cd api
mvn clean package
mvn spring-boot:run
```

Ou, direto:

```bash
cd api
mvn spring-boot:run
```

Por padrão, a aplicação ficará acessível em `http://localhost:8080` (ou outra porta, conforme sua configuração).

---

## Endpoints (exemplos)

Não encontrei ainda uma documentação completa dos endpoints, então aqui vão exemplos genéricos:

* **/oauth2/authorize** — endpoint de autorização
* **/oauth2/token** — emissão de tokens
* **/api/...** — endpoints protegidos da API da aplicação

Recomendo integrar Swagger / OpenAPI para gerar documentação automática da API.

---

## Testes

Para rodar os testes:

```bash
cd api
mvn test
```

É interessante incluir testes de integração usando um banco PostgreSQL (ou usar ferramentas como Testcontainers para isolar o ambiente de teste).

---

## Uso com Docker / docker-compose (opcional)

Você pode definir um `docker-compose.yml` para orquestrar um container PostgreSQL + a aplicação:

```yaml
version: "3.8"
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: fynco_db
      POSTGRES_USER: fynco_user
      POSTGRES_PASSWORD: sua_senha
    ports:
      - "5432:5432"

  app:
    build:
      context: ./api
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/fynco_db
      SPRING_DATASOURCE_USERNAME: fynco_user
      SPRING_DATASOURCE_PASSWORD: sua_senha
    ports:
      - "8080:8080"
```

Com esse setup, basta `docker-compose up --build` na raiz do repositório.

---

## Contribuições

Se quiser colaborar:

1. Abra uma *issue* descrevendo a ideia ou bug.
2. Crie uma branch (`feature/nome-da-feature`, `fix/descrição`).
3. Faça suas alterações com commits claros.
4. Abra um Pull Request e aguarde revisão.

---

## Licença

Este projeto está licenciado sob a **MIT License**.
