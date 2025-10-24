# 1. Use a imagem base oficial do AWS Lambda para Python 3.9
FROM public.ecr.aws/lambda/python:3.9

# 2. Copie o arquivo de dependências para dentro da imagem
COPY requirements.txt .

# 3. Instale as dependências.
# O '-t .' é a parte mais importante: ele instala as bibliotecas
# na pasta ATUAL ('.') em vez de no site-packages do sistema.
RUN pip install -r requirements.txt -t .

# 4. Copie o seu código Python para dentro da imagem
COPY lambda_function.py .

# 5. O comando final que será executado quando rodarmos o 'build.sh'.
# Ele zera tudo que está na pasta (seu código + as libs instaladas)
# e salva em /dist/lambda.zip. A pasta /dist será "mapeada"
# para a pasta 'dist' no seu computador.
CMD [ "sh", "-c", "zip -r /dist/lambda.zip ." ]