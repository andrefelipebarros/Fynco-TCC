# 1. Use a imagem base oficial do AWS Lambda para Python 3.10
FROM public.ecr.aws/lambda/python:3.10

# 2. Instala o 'zip'
RUN yum install -y zip

# 3. Cria um diretório de trabalho temporário
WORKDIR /build

# 4. Copia o arquivo de dependências para o /build
COPY requirements.txt .

# 5. Cria o diretório do pacote final (um local limpo)
RUN mkdir /pkg

# 6. Instala as dependências DENTRO do diretório /pkg
RUN pip install -r requirements.txt -t /pkg

# 7. Copia o seu código Python DENTRO do diretório /pkg
COPY lambda_function.py /pkg

# 8. Anula o "Entrypoint" padrão da imagem base.
ENTRYPOINT [""]

# 9. O comando final 
#    - 'cd /pkg' : Entra na pasta limpa /pkg
#    - 'zip -r ...' : Zipa o CONTEÚDO dessa pasta
CMD [ "sh", "-c", "cd /pkg && zip -9 -r /dist/lambda.zip ." ]