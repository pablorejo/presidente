@echo off
if not exist bin mkdir bin
if not exist redesNeuronales mkdir redesNeuronales
javac -d bin -sourcepath src src\*.java
java -cp bin main
if exist bin\*.txt del bin\*.txt