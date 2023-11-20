@echo off
del /Q bin\*.*
javac -d bin -sourcepath src src\*.java
java -cp bin App
del /Q bin\*.txt