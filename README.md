HttpService
===========

这是一个常驻的http web服务器，能通过它在android上搭建一个android常驻的web 			service,进而通过web端口来对android内部的文件，以及公开的服务进行操作

该项目依赖于开源轻量级http java web服务器 NanoHTTPD(https://github.com/NanoHttpd/nanohttpd)

功能内容
====
	
	1. 具有基本的文件浏览功能
	2. 能支持web服务插件
	3. 将http Service开启之后，能自动常驻后台，除非主动停止外，系统杀掉之后，能自动启动

期待新功能
====

	1. 支持assets根目录，与sdcard目录等多层目录作为web服务器的跟目录
	2. 支持jar文件导入
