# Fynco-TCC

![Badge de Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)

API backend para o projeto Fynco, um aplicativo de auxílio a investimentos no mercado financeiro brasileiro (com foco em Fundos Imobiliários), desenvolvido como Trabalho de Conclusão de Curso.

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Funcionalidades](#funcionalidades)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Licença](#licença)

## 📖 Sobre o Projeto

O Fynco é um sistema projetado para centralizar e analisar investimentos em FIIs, fornecendo recomendações e alertas personalizados para auxiliar o usuário em suas decisões financeiras. Este repositório contém a API REST (backend) que serve como o cérebro da aplicação, gerenciando usuários, dados de investimentos e notificações.

## 🚀 Tecnologias Utilizadas

Este projeto foi desenvolvido utilizando as seguintes tecnologias:

### Backend & Frameworks
<p align="left">
  <img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17+">
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.6-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security">
  <img src="https://img.shields.io/badge/Hibernate-573626?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate (JPA)">
  <img src="https://img.shields.io/badge/OAuth2-24292E?style=for-the-badge&logo=oauth&logoColor=white" alt="OAuth2">
</p>

### Banco de Dados
<p align="left">
  <img src="https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/H2_Database-464646?style=for-the-badge&logo=h2&logoColor=white" alt="H2 Database">
</p>

### Build & Ferramentas
<p align="left">
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/Lombok-AF0E23?style=for-the-badge&logo=lombok&logoColor=white" alt="Lombok">
  <img src="https://img.shields.io/badge/SendGrid-000000?style=for-the-badge&logo=sendgrid&logoColor=white" alt="SendGrid">
</p>

*(Outras bibliotecas incluem `web-push` para Notificações Push e `Java Mail Sender` gerenciadas pelo Maven.)*

## ✨ Funcionalidades

- [✅] Autenticação e Autorização de usuários via OAuth2 (Google)
- [✅] CRUD de Perfil de Usuário e Questionário de Investidor
- [✅] Listagem de Fundos Imobiliários (FIIs) por perfil
- [✅] Histórico de FIIs (Busca por Ticker ou ID)
- [✅] Inscrição para Notificações Push (Web Push)
- [✅] Envio de E-mails (SMTP/SendGrid)
- [⏳] [Funcionalidade em desenvolvimento] Geração de relatórios
- [❌] [Funcionalidade planejada] Alertas de dividendos via e-mail

## 🏁 Como Executar

Siga os passos abaixo para executar o projeto localmente:

### Pré-requisitos

- Java JDK 17 ou superior
- Apache Maven
- Seu SGBD PostgreSQL (ou H2 para testes)

### 1. Clonar o Repositório

```bash
git clone [https://github.com/andrefelipebarros/Fynco-TCC.git](https://github.com/andrefelipebarros/Fynco-TCC.git)
cd Fynco-TCC
````

### 2\. Configurar Variáveis de Ambiente

Este projeto utiliza variáveis de ambiente para credenciais sensíveis. Você deve configurá-las no seu sistema operacional ou na sua IDE antes de rodar:

**Banco de Dados:**

  - `PGHOST`: Host do banco (ex: localhost)
  - `PGPORT`: Porta do banco (ex: 5432)
  - `PGDATABASE`: Nome do banco de dados
  - `PGUSER`: Usuário do PostgreSQL
  - `PGPASSWORD`: Senha do PostgreSQL

**Autenticação Google (OAuth2):**

  - `G_CLIENT_ID`: Seu Client ID do Google Cloud
  - `G_CLIENT_PASSWORD`: Seu Client Secret

**Notificações e E-mail:**

  - `VAPID_PUBLIC_KEY`: Chave pública para Web Push
  - `VAPID_PRIVATE_KEY`: Chave privada para Web Push
  - `MAIL_SMTP_USERNAME`: E-mail remetente (Gmail)
  - `MAIL_SMTP_PASSWORD`: Senha de aplicativo do e-mail
  - `SENDGRID_API_KEY`: API Key do SendGrid

### 3\. Executar a Aplicação

Você pode executar a aplicação usando o wrapper do Maven:

```bash
# Para Windows
./mvnw spring-boot:run

# Para Linux/Mac
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## ⚡ Endpoints da API

Aqui está uma descrição dos principais endpoints da aplicação, baseados nos controllers do projeto:

| Método | Endpoint | Controller | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/user/basic` | `BasicUserInfoController` | Retorna nome e perfil do usuário logado. |
| `GET` | `/api/questionnaire/me` | `UserController` | Retorna o status completo do cadastro do usuário. |
| `POST` | `/api/questionnaire/submit` | `UserController` | Envia o questionário para definir o perfil (Params: name, profile). |
| `GET` | `/api/fiis` | `FiiController` | Lista todos os Fundos Imobiliários (FIIs). |
| `GET` | `/api/fiis/perfil/{perfil}` | `FiiController` | Lista FIIs filtrados pelo perfil (ex: MODERATE). |
| `GET` | `/api/fiis/{id}/history` | `FiiController` | Busca histórico do FII por ID ou Ticker. |
| `POST` | `/subscribe` | `NotificationController` | Inscreve o navegador para receber notificações push. |
| `POST` | `/send-notification` | `NotificationController` | [Admin] Dispara notificação de teste para todos. |

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](https://www.google.com/search?q=LICENSE) para mais detalhes.
