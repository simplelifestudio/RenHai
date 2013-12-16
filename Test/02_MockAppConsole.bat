rem +---------------------------------------------------------------------+
rem | Java -jar renhai.jar {mockAppCount} {mockAppInterval}               |
rem |          {websocketLink} {appBehaviorMode}                          |
rem | where:                                                              |
rem |   mockAppCount: number of Mock App                                  |
rem |   mockAppInterval: interval of launching mockApps                   |
rem |   websocketLink: websocket link of server for communication         |
rem |   appBehaviorMode: behaveMode of mockApp, shall be one of:          |
rem |     - SendInvalidJSONCommand                                        |
rem |     - NoAppSyncRequest                                              |
rem |     - NoEnterPoolRequest                                            |
rem |     - NoResponseForSessionBound                                     |
rem |     - NoRequestOfAgreeChat                                          |
rem |     - ConnectLossDuringChatConfirm                                  |
rem |     - RejectChat                                                    |
rem |     - ConnectLossDuringChat                                         |
rem |     - NoRequestOfAgreeChat                                          |
rem |     - NormalAndContinue                                             |
rem |     - NormalAndQuit                                                 |
rem +---------------------------------------------------------------------+

rem call java -XX:NewSize=128m -XX:MaxNewSize=250m -XX:PermSize=128m -XX:MaxPermSize=512m -Xms980m -Xmx980m -jar ./renhai.jar 1000 100 "ws://192.168.1.2/renhai/websocket" "NormalAndContinue" > MockApp.txt
rem java -jar ./renhai.jar 1 100 "ws://192.168.1.2/renhai/websocket" "NormalAndContinue" > MockApp.txt
call java -jar ./renhai.jar 1 100 "ws://192.81.135.31/renhai/websocket" "NormalAndContinue" > MockApp.txt
