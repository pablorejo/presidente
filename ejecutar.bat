@echo off
if not exist bin mkdir bin
javac -d bin -sourcepath src src\*.java
java -cp bin App
if exist bin\*.txt del bin\*.txt