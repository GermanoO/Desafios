#!/bin/bash

echo "Qual o Path?"
read DESAFIO_DIR;

echo "Onde deseja salvar o backup?"
read DIR_BACKUP

echo "Criando Diretorio:"
Sleep 2

	mkdir $DESAFIO_DIR
	chmod 0777 $DESAFIO_DIR
	echo "Diretório $DESAFIO_DIR criado"


for i in {1..100};
do
	echo "Arquivo criado" > $DESAFIO_DIR/teste$i.txt
done

echo "foram criadois $i arquivos"

sleep 2

cd $DESAFIO_DIR

zip -r $DESAFIO_DIR/backup.zip ./*

sleep 2

if [ $? -eq 0 ]; then
	echo "Diretorio Compactado com Sucesso"
	
else
	echo "Diretorio não compactado"
fi

echo "Movendo o arquivo de backup para a pasta $DIR_BACKUP "

sleep 2

mv backup.zip $DIR_BACKUP

echo "Arquivo backup.zip foi movido para a pasta $DIR_BACKUP corretamente."
