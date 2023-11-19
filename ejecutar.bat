@echo off
del bin/*
javac -d bin -sourcepath src src/*.java
java bin/*.java
del bin/*.txt