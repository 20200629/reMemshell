# reMemshell
Aprendizaje sobre el [memshell](https://github.com/rebeyond/memShell) del maestro ReBeyond.

Webshell sin archivos para Tomcat basada en Java Agent y Javassist.

Entorno: JDK8u261, Javassist3.22, servlet-api-3.1.
Para compilar en IDEA, por favor agregue `tools.jar` cambiando la ruta al paquete correspondiente de su JDK: `$JAVA_HOME$/lib/tools.jar`.

O importe el proyecto y modifique la ruta de lib en el `pom.xml` para compilar con maven: `mvn assembly:assembly`

## MANIFEST.MF in reAgent
```
Manifest-Version: 1.0
Agent-Class: net.sorry.agent.reAgent //Clase del agente
Can-Redefine-Classes: true //Si se pueden redefinir las clases
Can-Retransform-Classes: true //Si se pueden retransformar, note que hay una línea vacía debajo
```
