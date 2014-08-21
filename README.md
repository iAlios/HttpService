HttpService
===========

这是一个常驻的http web服务器，能通过它在android上搭建一个android常驻的web 			service,进而通过web端口来对android内部的文件，以及公开的服务进行操作

该项目依赖于开源轻量级http java web服务器 NanoHTTPD(https://github.com/NanoHttpd/nanohttpd)

主要知识点
====

	1. assets目录下的空目录是不会被打包进入到apk中的，所以判断文件和文件夹区别的使用list就可以。
	   如果返回的数组为空(长度为0)，就是文件。
	2. 将所有的web页面都可以放置到wwwroot目录中
	3. 将所有导入的插件都需要放置到wwwlib目录中(即dex文件都需要放置到这里哦)
	4. 主要的wwwroot是在assets目录下，sdcard中为下载或上传文件目录
	5. 主要的控制相关的内容都需要到assets目录下

功能内容
====
	
	1. 具有基本的文件浏览功能
	2. 能支持web服务插件
	3. 将http Service开启之后，能自动常驻后台，除非主动停止外，系统杀掉之后，能自动启动
	4. 支持assets根目录，与sdcard目录等多层目录作为web服务器的跟目录

期待新功能
====

	1. 支持jar文件导入
	2. 支持assets文件加密
