@echo off
COLOR 0b
title FandC Register Game Server
java -server -Dfile.encoding=UTF-8 %JAVA_OPTS% -cp config;./../libs/* l2jexpress.loginserver.GameServerRegister
@pause
