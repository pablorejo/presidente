#!/bin/bash

# Verificar si el directorio bin existe y, si no, crearlo
if [ ! -d "bin" ]; then
  mkdir bin
fi

# Borrar archivos existentes en bin
rm -f bin/*

# Compilar archivos fuente en el directorio bin
javac -d bin -sourcepath src src/*.java

# Ejecutar la aplicación desde el directorio bin
java -cp bin App
