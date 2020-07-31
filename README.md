# reMemshell
学习ReBeyond大神的[memshell](https://github.com/rebeyond/memShell)  

基于Java Agent和Javassist的Tomcat无文件webshell  

环境:JDK8u261,Javassist3.22,servlet-api-3.1  
IDEA编译请添加tools.jar改为自己JDK对应的包:$JAVA_HOME$/lib/tools.jar  

或导入项目中并修改pom.xml中lib对应的路径使用maven编译:`mvn assembly:assembly`  

## MANIFEST.MF in reAgent
```
Manifest-Version: 1.0
Agent-Class: net.sorry.agent.reAgent //代理类
Can-Redefine-Classes: true //是否能够被重定义
Can-Retransform-Classes: true //是否能替换,注意下面有个空行
```
