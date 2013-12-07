@set /p input=请输入设备编号：
set id=%input%
rem set wholeid=线程组 1-%id%
set wholeid=MA-%id%
call 00_LogAnalyzeTool.py renhai.log serverlog-%id%.txt "<%wholeid%>"
call 00_LogAnalyzeTool.py MockApp.txt applog-%id%.txt "<%wholeid%>"


rem call 00_LogAnalyzeTool.py renhai.log serverlog-378A.txt "<45CF7936-3FA1-49B2-937D-D462AB5F378A>"