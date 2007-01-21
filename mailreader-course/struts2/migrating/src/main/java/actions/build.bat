@echo off
del *.class
set CLASSPATH=..\..\lib\xwork-2.0.0.jar;..\..\lib\struts-core-1.3.5.jar;..\..\..\..\..\common\lib\servlet-api.jar
javac *.java
