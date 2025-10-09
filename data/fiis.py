 import yfinance as yf
import psycopg2
import sys

# ============================
# CONFIGURAÇÃO DO BANCO
# ============================
DB_CONFIG = {
    "host": "host.name",
    "port": 36578,
    "database": "mysql",
    "user": "test",
    "password": "bvkjbvisdfbipsfins"
}

# ============================
# LISTAS DE ATIVOS POR PERFIL
# ============================
FIIS = {
    "CONSERVATOR": ["RBRR11.SA", "MCCI11.SA", "BTLG11.SA", "RBRF11.SA", "COCA34.SA", "PETR4.SA", "VALE3.SA", "KLBN3.SA", "KLBN4.SA", "EGIE3.SA", "ITUB4.SA", "BBDC4.SA"],
    "MODERATE": ["RBRF11.SA", "RZTR11.SA", "CPTS11.SA", "LIFE11.SA", "VGIR11.SA", "ITUB4.SA", "BBDC4.SA", "VALE3.SA"],
    "AGRESSIVE": ["MXRF11.SA", "BTHF11.SA", "KNRI11.SA", "HGLG11.SA", "XPML11.SA", "WEGE3.SA", "LREN3.SA", "MGLU3.SA"]
}

# ===================================================================
# <<< IMPORTANTE: Alterar aqui para o nome exato da sua tabela >>>
# ===================================================================
NOME_DA_TABELA = "fiis" 
# Exemplo: NOME_DA_TABELA = "ativos"


def atualizar_ativos():
    """
    Função principal para conectar ao banco, limpar a tabela,
    buscar dados atualizados dos ativos e inseri-los novamente.
    """
    conn = None
    try:
        # 1. CONECTAR AO BANCO DE DADOS
        print("Conectando ao banco de dados...")
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("✅ Conexão bem-sucedida.")

        # 2. LIMPAR (APAGAR) OS DADOS ATUAIS DA TABELA
        # O comando TRUNCATE é eficiente para limpar a tabela inteira.
        # RESTART IDENTITY reinicia a contagem do ID (se for uma coluna serial).
        print(f"\nLimpando a tabela '{NOME_DA_TABELA}'...")
        cursor.execute(f"TRUNCATE TABLE {NOME_DA_TABELA} RESTART IDENTITY;")
        print("✅ Tabela limpa.")

        # 3. BUSCAR DADOS NA API E INSERIR NO BANCO
        print("\nIniciando a busca e inserção de dados atualizados...")
        
        # Itera sobre cada perfil e a lista de tickers correspondente
        for perfil, tickers in FIIS.items():
            print(f"\n--- Processando Perfil: {perfil} ---")
            for ticker_code in tickers:
                try:
                    print(f"Buscando dados para {ticker_code}...")
                    
                    # Pega as informações do ticker com a biblioteca yfinance
                    ticker_info = yf.Ticker(ticker_code).info

                    # Extrai os dados necessários do dicionário retornado pela API.
                    # Usamos .get() para evitar erros caso uma informação não esteja disponível.
                    nome = ticker_info.get('longName', 'Nome não disponível')
                    setor = ticker_info.get('sector', 'Setor não disponível')
                    preco_atual = ticker_info.get('currentPrice') or ticker_info.get('regularMarketPrice')

                    # O Dividend Yield vem como uma fração (ex: 0.117). Multiplicamos por 100 para ter a porcentagem.
                    dy_fracao = ticker_info.get('dividendYield')
                    dy = dy_fracao * 100 if dy_fracao is not None else None

                    p_vp = ticker_info.get('priceToBook')
                    
                    # Validação: Se não encontrar o preço, pula a inserção deste ticker
                    if preco_atual is None:
                        print(f"⚠️ AVISO: Preço atual não encontrado para {ticker_code}. Inserção pulada.")
                        continue
                        
                    # Monta a query de inserção para evitar SQL Injection
                    insert_query = f"""
                        INSERT INTO {NOME_DA_TABELA} (ticker, nome, setor, preco_atual, dy, p_vp, perfil)
                        VALUES (%s, %s, %s, %s, %s, %s, %s);
                    """
                    dados_para_inserir = (ticker_code, nome, setor, preco_atual, dy, p_vp, perfil)
                    
                    # Executa a inserção
                    cursor.execute(insert_query, dados_para_inserir)
                    print(f"  -> Inserido: {ticker_code} | Preço: {preco_atual:.2f}")

                except Exception as e:
                    # Captura qualquer erro que ocorra para um ticker específico e continua o processo
                    print(f"❌ ERRO ao processar o ticker {ticker_code}: {e}", file=sys.stderr)

        # 4. CONFIRMAR (COMMIT) AS ALTERAÇÕES NO BANCO
        conn.commit()
        print("\n✅ Operação finalizada com sucesso! Dados atualizados e salvos no banco.")

    except psycopg2.Error as e:
        print(f"\n❌ ERRO CRÍTICO de banco de dados: {e}", file=sys.stderr)
        if conn:
            # Reverte todas as alterações em caso de erro na operação
            conn.rollback()
            
    finally:
        # 5. FECHAR A CONEXÃO COM O BANCO
        if conn:
            cursor.close()
            conn.close()
            print("🔌 Conexão com o banco de dados fechada.")

# ============================
# EXECUTAR O SCRIPT
# ============================
if __name__ == "__main__":
    atualizar_ativos()
