import yfinance as yf
import psycopg2
import sys
import os # Importar a biblioteca 'os'

# ============================
# CONFIGURAÇÃO DO BANCO (LENDO DAS VARIÁVEIS DE AMBIENTE)
# ============================
DB_CONFIG = {
    "host": os.environ.get('DB_HOST'),
    "port": int(os.environ.get('DB_PORT', 5432)), # 5432 como padrão
    "database": os.environ.get('DB_NAME'),
    "user": os.environ.get('DB_USER'),
    "password": os.environ.get('DB_PASSWORD')
}

# ============================
# LISTAS DE ATIVOS POR PERFIL
# ============================
FIIS = {
    "CONSERVATOR": ["RBRR11.SA", "MCCI11.SA", "BTLG11.SA", "RBRF11.SA", "COCA34.SA", "PETR4.SA", "VALE3.SA", "KLBN3.SA", "KLBN4.SA", "EGIE3.SA", "ITUB4.SA", "BBDC4.SA"],
    "MODERATE": ["RBRF11.SA", "RZTR11.SA", "CPTS11.SA", "LIFE11.SA", "VGIR11.SA", "ITUB4.SA", "BBDC4.SA", "VALE3.SA"],
    "AGRESSIVE": ["MXRF11.SA", "BTHF11.SA", "KNRI11.SA", "HGLG11.SA", "XPML11.SA", "WEGE3.SA", "LREN3.SA", "MGLU3.SA"]
}

NOME_DA_TABELA = "fiis" 

# ============================
# FUNÇÃO HANDLER DO LAMBDA
# ============================
def lambda_handler(event, context):
    """
    Esta é a função que o AWS Lambda irá executar.
    """
    print("Iniciando a execução do Lambda...")
    try:
        atualizar_ativos()
        print("Execução concluída com sucesso.")
        return {
            'statusCode': 200,
            'body': 'Dados atualizados com sucesso!'
        }
    except Exception as e:
        print(f"ERRO FATAL na execução do Lambda: {e}", file=sys.stderr)
        return {
            'statusCode': 500,
            'body': f'Erro ao executar a atualização: {e}'
        }

def atualizar_ativos():
    """
    Função principal.
    """
    conn = None
    try:
        # 1. CONECTAR AO BANCO DE DADOS
        print("Conectando ao banco de dados...")
        
        # Validação se as variáveis de ambiente foram carregadas
        if not all([DB_CONFIG['host'], DB_CONFIG['database'], DB_CONFIG['user'], DB_CONFIG['password']]):
            raise ValueError("Variáveis de ambiente do banco de dados (DB_HOST, DB_NAME, etc.) não estão configuradas.")
            
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("✅ Conexão bem-sucedida.")

        # 2. LIMPAR (APAGAR) OS DADOS ATUAIS DA TABELA
        print(f"\nLimpando a tabela '{NOME_DA_TABELA}'...")
        cursor.execute(f"TRUNCATE TABLE {NOME_DA_TABELA} RESTART IDENTITY;")
        print("✅ Tabela limpa.")

        # 3. BUSCAR DADOS NA API E INSERIR NO BANCO
        print("\nIniciando a busca e inserção de dados atualizados...")
        
        for perfil, tickers in FIIS.items():
            print(f"\n--- Processando Perfil: {perfil} ---")
            for ticker_code in tickers:
                try:
                    print(f"Buscando dados para {ticker_code}...")
                    ticker_info = yf.Ticker(ticker_code).info

                    nome = ticker_info.get('longName', 'Nome não disponível')
                    setor = ticker_info.get('sector', 'Setor não disponível')
                    preco_atual = ticker_info.get('currentPrice') or ticker_info.get('regularMarketPrice')

                    dy_fracao = ticker_info.get('dividendYield')
                    dy = dy_fracao * 100 if dy_fracao is not None else None
                    p_vp = ticker_info.get('priceToBook')
                    
                    if preco_atual is None:
                        print(f"⚠️ AVISO: Preço atual não encontrado para {ticker_code}. Inserção pulada.")
                        continue
                        
                    insert_query = f"""
                        INSERT INTO {NOME_DA_TABELA} (ticker, nome, setor, preco_atual, dy, p_vp, perfil)
                        VALUES (%s, %s, %s, %s, %s, %s, %s);
                    """
                    dados_para_inserir = (ticker_code, nome, setor, preco_atual, dy, p_vp, perfil)
                    
                    cursor.execute(insert_query, dados_para_inserir)
                    print(f"  -> Inserido: {ticker_code} | Preço: {preco_atual:.2f}")

                except Exception as e:
                    print(f"❌ ERRO ao processar o ticker {ticker_code}: {e}", file=sys.stderr)

        # 4. CONFIRMAR (COMMIT) AS ALTERAÇÕES NO BANCO
        conn.commit()
        print("\n✅ Operação finalizada com sucesso! Dados atualizados e salvos no banco.")

    except (psycopg2.Error, ValueError) as e: 
        print(f"\n❌ ERRO CRÍTICO: {e}", file=sys.stderr)
        if conn:
            conn.rollback()
        raise e 
            
    finally:
        # 5. FECHAR A CONEXÃO COM O BANCO
        if conn:
            cursor.close()
            conn.close()
            print("🔌 Conexão com o banco de dados fechada.")