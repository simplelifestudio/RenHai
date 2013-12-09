move E:\work\tomcat\logs\renhai.log .\
move \\192.168.1.5\本地磁盘（e）\work\MockApp\MockApp.txt .\
move \\192.168.1.5\本地磁盘（e）\work\MockApp\Monitor.txt .\
rem move  \\192.168.1.5\本地磁盘（e）\work\tomcat\logs\renhai.log .\

rem call 00_LogAnalyzeTool.py renhai.log InterestScheduler.txt "InterestScheduler"
rem call 00_LogAnalyzeTool.py renhai.log RandomScheduler.txt "RandomScheduler"

call 00_LogAnalyzeTool.py MockApp.txt app_error.txt "[ERROR],timeout,[WARN ]"
rem call 00_LogAnalyzeTool.py renhai.log match.txt "===================="
rem call 00_LogAnalyzeTool.py renhai.log online.txt "\"online\":"
rem call 00_LogAnalyzeTool.py renhai.log MatchStart.txt "<MatchStart>,Enter startSession"
rem call 00_LogAnalyzeTool.py renhai.log WaitMatch.txt "waiting for match:"
rem call 00_LogAnalyzeTool.py renhai.log WebRTC.txt "WebRTC session"
rem call 00_LogAnalyzeTool.py renhai.log DBCacheTask.txt "[DBCacheTask]"
rem call 00_LogAnalyzeTool.py renhai.log DBCache.txt "=============Cache"
call 00_LogAnalyzeTool.py renhai.log Assess.txt "<AssessAndQuit>,<AssessAndContinue>"
call 00_LogAnalyzeTool.py renhai.log server_error.txt "[ERROR],timeout,[WARN ]"

rem call 00_LogAnalyzeTool.py renhai.log ThreadPool.txt "Start to send,Start to execute"
rem call 00_LogAnalyzeTool.py renhai.log session.txt "Enter startSession,Enter endSession"
rem call 00_LogAnalyzeTool.py renhai.log dbconnection.txt "Checked out connection,Created connection,Returned connection,Closed connection,Closing JDBC Connection,Waiting as long as"
