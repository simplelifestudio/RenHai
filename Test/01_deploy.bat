echo on
set CURPATH="E:\work\git\RenHai\Test"
set SOURCEPATH="E:\work\git\RenHai\Codes\Server"
cd %SOURCEPATH%

cd build\classes
rem copy com\* %CURPATH%\renhai
"C:\Program Files\7-Zip\7z.exe" a -tzip renhai.jar com\* META-INF\*
copy renhai.jar ..\..\
move renhai.jar ..\..\WebContent\WEB-INF\lib
cd ..\..\

copy lib\hibernate\*.jar WebContent\WEB-INF\lib
copy lib\json\*.jar WebContent\WEB-INF\lib
copy lib\log\*.jar WebContent\WEB-INF\lib
copy lib\juit\*.jar WebContent\WEB-INF\lib
copy lib\opentok\*.jar WebContent\WEB-INF\lib
copy lib\websocketclient\*.jar WebContent\WEB-INF\lib

copy src\logback.xml WebContent\WEB-INF\classes
copy src\mybatis.xml WebContent\WEB-INF\classes
copy src\setting.json WebContent\

del build\MockApp\*.lib
del build\MockApp\*.xml
copy WebContent\WEB-INF\lib\*.jar build\MockApp
copy WebContent\WEB-INF\classes\*.xml build\MockApp

cd WebContent
"C:\Program Files\7-Zip\7z.exe" a -tzip renhai.war * WEB-INFO\* META-INF\*
move renhai.war ..\

cd ..
del WebContent\WEB-INF\lib\*.jar
del E:\work\tomcat\logs\renhai.log

rem rmdir \\192.168.1.5\本地磁盘（e）\work\tomcat\webapps\renhai /s/q
rem del \\192.168.1.5\本地磁盘（e）\work\tomcat\webapps\renhai.war
rem copy renhai.war \\192.168.1.5\本地磁盘（e）\work\tomcat\webapps

rmdir E:\work\tomcat\webapps\renhai /s/q
del E:\work\tomcat\webapps\renhai.war
move renhai.war E:\work\tomcat\webapps

rem copy renhai.jar E:\work\JMeter\apache-jmeter-2.9\lib\ext\
rem copy renhai.jar \\192.168.1.5\本地磁盘（e）\work\MockApp\
move renhai.jar E:\work\tomcat\webapps

rem call E:\work\tomcat\bin\shutdown.bat
rem call E:\work\tomcat\bin\startup.bat
rem pause