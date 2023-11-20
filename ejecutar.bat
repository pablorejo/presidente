@echo off
del /Q bin\*.*
if not exist bin mkdir bin
javac -d bin -sourcepath src src\*.java
java -cp bin App
del /Q bin\*.txt