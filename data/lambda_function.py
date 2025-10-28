import os
import sys
import yfinance as yf
import psycopg2
from psycopg2 import sql
from psycopg2 import errors

# ============================
# CONFIGURAÇÃO DO BANCO (VARIÁVEIS DE AMBIENTE)
# ============================
DB_CONFIG = {
    "host": os.environ.get('DB_HOST'),
    "port": int(os.environ.get('DB_PORT', 5432)),
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


def lambda_handler(event, context):
    print("Iniciando execução do Lambda...")
    try:
        atualizar_ativos()
        print("Execução concluída com sucesso.")
        return {"statusCode": 200, "body": "Dados atualizados com sucesso!"}
    except Exception as e:
        print(f"ERRO FATAL na execução do Lambda: {e}", file=sys.stderr)
        return {"statusCode": 500, "body": f"Erro ao executar a atualização: {e}"}


def atualizar_ativos():
    conn = None
    cursor = None
    try:
        # validação de variáveis de ambiente
        if not all([DB_CONFIG['host'], DB_CONFIG['database'], DB_CONFIG['user'], DB_CONFIG['password']]):
            raise ValueError("Variáveis de ambiente do banco não estão todas configuradas (DB_HOST, DB_NAME, DB_USER, DB_PASSWORD).")

        print("Conectando ao banco de dados...")
        conn = psycopg2.connect(**DB_CONFIG)
        cursor = conn.cursor()
        print("✅ Conexão bem-sucedida.")

        # 1) Ler preços atuais (preço 'anterior' para quando re-inserirmos) — caso a tabela exista
        precos_antigos = {}
        try:
            select_q = sql.SQL("SELECT ticker, preco_atual FROM {}").format(sql.Identifier(NOME_DA_TABELA))
            cursor.execute(select_q)
            rows = cursor.fetchall()
            for ticker, preco in rows:
                precos_antigos[ticker] = preco
            print(f"✅ {len(precos_antigos)} preços anteriores carregados da tabela.")
        except Exception as e:
            # Se a tabela não existir ou houver problema no SELECT, apenas logamos e seguimos com precos_antigos vazio
            print(f"⚠️ Não foi possível ler preços anteriores (tabela pode não existir): {e}")
            conn.rollback()

        # 2) Certificar-se de que a coluna preco_anterior existe — se não existir, tenta adicionar
        try:
            if not coluna_existe(cursor, NOME_DA_TABELA, "preco_anterior"):
                print("Coluna 'preco_anterior' não encontrada — tentando adicionar automaticamente.")
                alter = sql.SQL("ALTER TABLE {} ADD COLUMN preco_anterior numeric;").format(sql.Identifier(NOME_DA_TABELA))
                try:
                    cursor.execute(alter)
                    conn.commit()
                    print("✅ Coluna 'preco_anterior' adicionada com sucesso.")
                except Exception as alter_err:
                    print(f"❌ Falha ao adicionar coluna 'preco_anterior': {alter_err} — iremos prosseguir sem alterar (inserção pode falhar se tentar inserir a coluna).")
                    conn.rollback()
            else:
                print("Coluna 'preco_anterior' já existe.")
        except Exception as e:
            # Problema ao checar a tabela (possivelmente tabela não existe) — seguir em frente
            print(f"⚠️ Erro ao verificar/alterar colunas: {e}")
            conn.rollback()

        # 3) Limpar a tabela
        try:
            print(f"\nLimpando a tabela '{NOME_DA_TABELA}'...")
            truncate_q = sql.SQL("TRUNCATE TABLE {} RESTART IDENTITY;").format(sql.Identifier(NOME_DA_TABELA))
            cursor.execute(truncate_q)
            print("✅ Tabela truncada.")
        except Exception as e:
            # Se a tabela não existir, criaremos uma tabela mínima (opcional) para evitar falha — logamos e criamos a tabela
            print(f"⚠️ TRUNCATE falhou (a tabela pode não existir): {e}")
            conn.rollback()
            criar_tabela_minima(cursor, NOME_DA_TABELA)
            conn.commit()
            print("✅ Tabela criada (minimamente) para continuar o processo.")

        # 4) Buscar dados via yfinance e inserir com preco_anterior vindo do dicionário precos_antigos
        print("\nIniciando busca e inserção de dados atualizados...")
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
                        print(f"⚠️ Preço atual não encontrado para {ticker_code}. Pulando.")
                        continue

                    preco_anterior = precos_antigos.get(ticker_code)  # None se não existia antes

                    # Query que tenta inserir incluindo preco_anterior
                    insert_q = sql.SQL("""
                        INSERT INTO {} (ticker, nome, setor, preco_atual, dy, p_vp, perfil, preco_anterior)
                        VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                    """).format(sql.Identifier(NOME_DA_TABELA))

                    dados = (ticker_code, nome, setor, preco_atual, dy, p_vp, perfil, preco_anterior)

                    try:
                        cursor.execute(insert_q, dados)
                    except Exception as ins_err:
                        # Se falhar por coluna inexistente (ou outro motivo), tenta fallback sem preco_anterior
                        print(f"⚠️ Inserção com 'preco_anterior' falhou: {ins_err} — tentando inserir sem a coluna.")
                        conn.rollback()
                        insert_fallback_q = sql.SQL("""
                            INSERT INTO {} (ticker, nome, setor, preco_atual, dy, p_vp, perfil)
                            VALUES (%s, %s, %s, %s, %s, %s, %s)
                        """).format(sql.Identifier(NOME_DA_TABELA))
                        cursor.execute(insert_fallback_q, (ticker_code, nome, setor, preco_atual, dy, p_vp, perfil))

                    preco_anterior_str = f"{preco_anterior:.2f}" if preco_anterior is not None else "N/A"
                    print(f"  -> Inserido: {ticker_code} | Anterior: {preco_anterior_str} | Novo: {preco_atual:.2f}")

                except Exception as e:
                    print(f"❌ ERRO ao processar {ticker_code}: {e}", file=sys.stderr)
                    conn.rollback()

        # 5) Commit final
        conn.commit()
        print("\n✅ Operação finalizada com sucesso! Dados atualizados e salvos no banco.")

    except (psycopg2.Error, ValueError) as e:
        print(f"\n❌ ERRO CRÍTICO: {e}", file=sys.stderr)
        if conn:
            conn.rollback()
        raise e

    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()
            print("🔌 Conexão com o banco de dados fechada.")


def coluna_existe(cursor, table_name, column_name):
    """
    Retorna True se a coluna existir na tabela (information_schema).
    """
    check_q = """
        SELECT 1 FROM information_schema.columns
        WHERE table_name = %s AND column_name = %s;
    """
    cursor.execute(check_q, (table_name, column_name))
    return cursor.fetchone() is not None


def criar_tabela_minima(cursor, table_name):
    """
    Cria uma tabela mínima caso a tabela não exista.
    Ajuste os tipos conforme sua modelagem real (aqui usei numeric para preços).
    """
    create_q = sql.SQL("""
        CREATE TABLE IF NOT EXISTS {} (
            id serial PRIMARY KEY,
            ticker text UNIQUE,
            nome text,
            setor text,
            preco_atual numeric,
            dy numeric,
            p_vp numeric,
            perfil text,
            preco_anterior numeric
        );
    """).format(sql.Identifier(table_name))
    cursor.execute(create_q)
    print(f"✅ Criada tabela mínima '{table_name}'.")
