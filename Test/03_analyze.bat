move E:\work\tomcat\logs\renhai.log .\
rem move  \\192.168.1.5\±æµÿ¥≈≈Ã£®e£©\work\tomcat\logs\renhai.log .\

call 00_LogAnalyzeTool.py renhai.log InterestScheduler.txt "InterestScheduler"
rem call 00_LogAnalyzeTool.py renhai.log RandomScheduler.txt "RandomScheduler"

call 00_LogAnalyzeTool.py MockApp.txt app_error.txt "[ERROR],timeout,[WARN ]"
call 00_LogAnalyzeTool.py renhai.log DBCacheTask.txt "[DBCacheTask]"
call 00_LogAnalyzeTool.py renhai.log DBCache.txt "=============Cache"
call 00_LogAnalyzeTool.py renhai.log AssessAndQuit.txt "<AssessAndQuit>,<AssessAndContinue>"
call 00_LogAnalyzeTool.py renhai.log server_error.txt "[ERROR],timeout,[WARN ]"

rem call 00_LogAnalyzeTool.py renhai.log ThreadPool.txt "Start to send,Start to execute"
rem call 00_LogAnalyzeTool.py renhai.log session.txt "Enter startSession,Enter endSession"
rem call 00_LogAnalyzeTool.py renhai.log dbconnection.txt "Checked out connection,Created connection,Returned connection,Closed connection,Closing JDBC Connection,Waiting as long as"
