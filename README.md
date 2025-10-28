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
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot">
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
  <img src="https://img.shields.io/badge/MapStruct-FF69B4?style=for-the-badge&logo=mapstruct&logoColor=white" alt="MapStruct">
</p>

*(Outras bibliotecas incluem `web-push` para Notificações Push, entre outras gerenciadas pelo Maven.)*

## ✨ Funcionalidades

- [✅] Autenticação e Autorização de usuários via OAuth2
- [✅] CRUD de Perfil de Usuário
- [✅] Listagem de Fundos Imobiliários (FIIs)
- [✅] Inscrição para Notificações Push
- [⏳] [Funcionalidade em desenvolvimento, Geração de relatórios
- [❌] [Funcionalidade planejada, Alertas de dividendos via e-mail

## 🏁 Como Executar

Siga os passos abaixo para executar o projeto localmente:

### Pré-requisitos

- Java JDK 17 ou superior
- Apache Maven
- Seu SGBD, PostgreSQL, ou nenhum se estiver usando H2

### 1. Clonar o Repositório

```bash
git clone [https://github.com/andrefelipebarros/Fynco-TCC.git](https://github.com/andrefelipebarros/Fynco-TCC.git)
cd Fynco-TCC
````

### 2\. Configurar Variáveis de Ambiente

Se seu projeto usar um arquivo `application.properties` ou `application.yml` que precise de senhas ou chaves de API, configure-as.

[**Exemplo:** Se você usa um banco de dados externo]

```properties
# Em src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fynco_db
spring.datasource.username=[SEU_USUARIO]
spring.datasource.password=[SUA_SENHA]

# Configurações do OAuth2 (ex: Google)
spring.security.oauth2.client.registration.google.client-id=[SEU_CLIENT_ID]
spring.security.oauth2.client.registration.google.client-secret=[SEU_CLIENT_SECRET]
```

### 3\. Executar a Aplicação

Você pode executar a aplicação usando o wrapper do Maven:

```bash
# Para Windows
./mvnw spring-boot:run

# Para Linux/Mac
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080` (ou a porta que você configurou).

## ⚡ Endpoints da API

Aqui está uma descrição dos principais endpoints da aplicação, baseados nos controllers do projeto:

| Método | Endpoint | Controller | Descrição | Acesso |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/fiis` | `FiiController` | Lista todos os Fundos Imobiliários (FIIs). | Privado |
| `POST` | `/api/user/profile` | `UserController` | Salva ou atualiza o perfil do usuário autenticado. | Privado |
| `POST` | `/subscribe` | `NotificationController` | Inscreve o cliente (navegador) para receber notificações push. | Privado |
| `POST` | `/send-notification` | `NotificationController` | [Admin/Debug] Envia uma notificação para todos os clientes inscritos. | Privado |

*(**Nota:** Os endpoints de autenticação, como `/login/oauth2/code/google`, são gerenciados pelo Spring Security e não estão listados aqui.)*

## 📄 Licença

Este projeto está sob a licença, MIT LICENSE(Temporário). Veja o arquivo [LICENSE](https://www.google.com/search?q=LICENSE) para mais detalhes.
