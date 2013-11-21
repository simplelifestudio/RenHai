set id=106
call 00_LogAnalyzeTool.py renhai.log serverlog-%id%.txt "<线程组 1-%id%>"
call 00_LogAnalyzeTool.py MockApp.txt applog-%id%.txt "<线程组 1-%id%>"


rem call 00_LogAnalyzeTool.py renhai.log serverlog-378A.txt "<45CF7936-3FA1-49B2-937D-D462AB5F378A>"