set JAVA_HOME=C:\Progra~2\Java\jdk1.7.0_79
set MAVEN_HOME=C:\Bin\apache-maven-3.3.3

cd /d %~dp0
call "%MAVEN_HOME%\bin\mvn.cmd" install -DskipTests=true -Dmaven.javadoc.skip=true -B -V

pause
