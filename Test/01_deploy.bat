echo on

cd E:\work\git\RenHai\Codes\Server

rem compress jar package
del renhai.war
rem rename renhai.war renhai.war.bak

cd build\classes
"C:\Program Files\7-Zip\7z.exe" a -tzip renhai.jar com\*
copy renhai.jar ..\..\
move renhai.jar ..\..\WebContent\WEB-INF\lib
cd ..\..\
copy lib\hibernate\*.jar WebContent\WEB-INF\lib
copy lib\json\*.jar WebContent\WEB-INF\lib
copy lib\log\*.jar WebContent\WEB-INF\lib
copy lib\juit\*.jar WebContent\WEB-INF\lib
copy src\logback.xml WebContent\WEB-INF\classes
rem copy src\hibernate.cfg.xml WebContent\WEB-INF\classes
copy src\mybatis.xml WebContent\WEB-INF\classes
copy src\setting.json WebContent\

rem create war package
cd WebContent

"C:\Program Files\7-Zip\7z.exe" a -tzip renhai.war * WEB-INFO\* META-INF\*
move renhai.war ..\

cd ..
del WebContent\WEB-INF\lib\*.jar
del E:\work\tomcat\logs\renhai.log

rmdir \\192.168.1.5\本地磁盘（e）\work\tomcat\webapps\renhai /s/q
del \\192.168.1.5\本地磁盘（e）\work\tomcat\webapps\renhai.war
copy renhai.war \\192.168.1.5\本地磁盘（e）\work\tomcat\webapps

rmdir E:\work\tomcat\webapps\renhai /s/q
del E:\work\tomcat\webapps\renhai.war
move renhai.war E:\work\tomcat\webapps

copy renhai.jar E:\work\JMeter\apache-jmeter-2.9\lib\ext\
move renhai.jar E:\work\tomcat\webapps

rem call E:\work\tomcat\bin\shutdown.bat
rem call E:\work\tomcat\bin\startup.bat
rem pause