# Fynco-TCC: Worker de Atualização de Ativos (AWS Lambda)

<p align="left">
  <img src="https://img.shields.io/badge/Python-3.10-3776AB?style=for-the-badge&logo=python&logoColor=white" alt="Python 3.10">
  <img src="https://img.shields.io/badge/AWS_Lambda-FF9900?style=for-the-badge&logo=aws-lambda&logoColor=white" alt="AWS Lambda">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
</p>

Este diretório contém o código-fonte e os arquivos de build para uma função AWS Lambda. O objetivo desta função é buscar dados atualizados do mercado financeiro (Ações e FIIs) e popular/atualizar um banco de dados PostgreSQL.

O processo utiliza Docker para criar um pacote `.zip` limpo e portável, contendo a função e todas as suas dependências Python, pronto para ser implantado na AWS.

## 🗂️ Componentes do Projeto

Aqui está uma descrição de cada arquivo no diretório `data/`:

  * **`lambda_function.py`**: O script Python principal que contém toda a lógica da função Lambda.
  * **`requirements.txt`**: A lista de bibliotecas Python necessárias para a função (ex: `yfinance`, `psycopg2`).
  * **`Dockerfile`**: Um arquivo de "receita" para o Docker. Ele define um ambiente de build que instala as dependências e o código em um local limpo.
  * **`build.bat`**: Um script de build para Windows (Batch) que automatiza todo o processo. Ele usa o `Dockerfile` para construir o pacote e extrai o `.zip` final.

## ⚙️ Funcionalidades do Script (`lambda_function.py`)

O script principal executa as seguintes ações:

1.  **Conexão Segura**: Conecta-se a um banco de dados PostgreSQL usando credenciais fornecidas por Variáveis de Ambiente (ENV VARS) da AWS Lambda.
2.  **Leitura de Preços Antigos**: Antes de apagar os dados, ele lê a tabela `fiis` e armazena os preços atuais em memória.
3.  **Atualização de Schema (Robusto)**: Verifica se a coluna `preco_anterior` existe. Se não existir, ele tenta executr um `ALTER TABLE` para adicioná-la.
4.  **Limpeza de Dados**: Executa um `TRUNCATE` na tabela `fiis` para limpá-la antes de inserir os dados novos e atualizados.
5.  **Criação de Tabela (Robusto)**: Se o `TRUNCATE` falhar (provavelmente porque a tabela não existe), o script executa um `CREATE TABLE` para garantir que ela exista.
6.  **Busca na API (yfinance)**: Itera sobre as listas de ativos (`CONSERVATOR`, `MODERATE`, `AGRESSIVE`), busca os dados de cada ticker usando a biblioteca `yfinance`.
7.  **Inserção de Dados**: Insere os novos dados no banco, incluindo:
      * `ticker`, `nome`, `setor`, `perfil`
      * `preco_atual` (o preço recém-buscado)
      * `dy` (Dividend Yield)
      * `p_vp` (Preço / Valor Patrimonial)
      * `preco_anterior` (o preço que foi lido no passo 2)

## 📦 Processo de Build (Como funciona)

O `build.bat` e o `Dockerfile` trabalham juntos para criar o pacote `lambda.zip`.

### 1\. `Dockerfile`

O Dockerfile usa uma imagem oficial do AWS Lambda (`python:3.10`) e segue estes passos:

1.  Instala o utilitário `zip` (necessário para criar o pacote).
2.  Cria um diretório `/pkg` limpo.
3.  Instala todas as bibliotecas do `requirements.txt` **DENTRO** do diretório `/pkg`.
4.  Copia o seu código (`lambda_function.py`) para **DENTRO** do diretório `/pkg`.
5.  O comando final (`CMD`) é zipar todo o conteúdo da pasta `/pkg` e salvar o resultado em `/dist/lambda.zip`.

### 2\. `build.bat`

O script `build.bat` orquestra esse processo na sua máquina local:

1.  **Limpa**: Remove o diretório `dist/` antigo, se existir.
2.  **Cria**: Cria um novo diretório `dist/` vazio.
3.  **Constrói (Build)**: Executa `docker build` para criar uma imagem local chamada `build-fynco-lambda` usando o `Dockerfile`.
4.  **Executa (Run)**: Executa um contêiner temporário (`--rm`) a partir da imagem recém-criada.
5.  **Extrai o .zip**: O passo mais importante é `-v "%cd%/dist:/dist"`. Isso "mapeia" a pasta `dist/` do seu PC para a pasta `/dist/` de dentro do contêiner.
      * Quando o `Dockerfile` executa o `CMD` e salva o arquivo em `/dist/lambda.zip` (dentro do contêiner), ele está, na verdade, salvando o arquivo diretamente na pasta `dist/` do seu computador.

## 🚀 Como Usar (Build e Deploy)

### Pré-requisitos

  * Docker Desktop instalado e em execução.

### 1\. Gerando o Pacote `.zip`

1.  Abra um terminal (Prompt de Comando, PowerShell, etc.) no diretório `data/`.

2.  Execute o script de build:

    ```bash
    .\build.bat
    ```
3.  Ao final do processo, você encontrará o pacote pronto para deploy em `data/dist/lambda.zip`.

### 2\. Conteúdo do `requirements.txt`

Para o build funcionar, o arquivo `requirements.txt` deve conter:

```
yfinance
psycopg2-binary
```

### 3\. Deploy na AWS Lambda

1.  Vá até o console da AWS Lambda e crie uma nova função (ou atualize uma existente).
2.  Escolha a Runtime "Python 3.10".
3.  Faça o upload do arquivo `dist/lambda.zip`.
4.  No painel "Configuração" \> "Geral" \> "Editar", configure o **Manipulador (Handler)** para:
    `lambda_function.lambda_handler`

### 4\. Configuração (Variáveis de Ambiente)

Esta função **requer** as seguintes variáveis de ambiente configuradas na AWS Lambda para se conectar ao banco de dados:

  * `DB_HOST`: O endereço do seu banco (ex: `meu-banco.rds.amazonaws.com`)
  * `DB_PORT`: A porta (ex: `5432`)
  * `DB_NAME`: O nome do banco de dados (ex: `fynco`)
  * `DB_USER`: O nome de usuário do banco.
  * `DB_PASSWORD`: A senha do usuário.
