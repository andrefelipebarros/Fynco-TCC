@echo off
echo "Limpando build antigo..."
REM Remove a pasta 'dist' antiga, se existir
if exist dist (
    rmdir /s /q dist
)
REM Cria a pasta 'dist' limpa
mkdir dist

REM 1. Constrói a imagem Docker e dá a ela um nome ("build-fynco-lambda")
echo "Construindo imagem Docker..."
docker build -t build-fynco-lambda .

REM 2. Executa a imagem
REM --rm = apaga o container depois de executar
REM -v "%cd%/dist:/dist" = mapeia a pasta 'dist' do seu PC
REM                          para a pasta '/dist' dentro do container.
echo "Executando container para gerar o .zip..."
docker run --rm -v "%cd%/dist:/dist" build-fynco-lambda

echo "------------------------------------------------"
echo "Build concluido!"
echo "Seu pacote esta pronto em: dist/lambda.zip"
echo "------------------------------------------------"