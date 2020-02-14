
### 主要内容包括
|   :blue_heart:|:blue_heart:|:blue_heart: |:blue_heart:|:blue_heart:|:blue_heart:|
| --------   | :----:   | :----: |:----: |:----: |:----: |
| jvm参数类型:alien:|jinfo & jps(参数和进程查看):alien:|jstat(类加载、垃圾收集、JIT 编译):alien: |jmap+MAT(内存溢出):alien:|jstack(线程、死循环、死锁):alien:|JVisualVM(本地和远程可视化监控:alien:|
|使用 BTrace进行拦截调试:alien:|Tomcat 性能监控与调优:alien:| Nginx 性能监控与调优:alien:|JVM 层 GC 调优:alien:|JAVA代码层调优:alien: |:alien:|
#### 1.JVM的参数类型
```
标准参数（各版本中保持稳定）
-help
-server -client
-version -showversion
-cp -classpath
 ```
```
X 参数（非标准化参数）
-Xint：解释执行
-Xcomp：第一次使用就编译成本地代码
-Xmixed：混合模式，JVM 自己决定是否编译成本地代码
```
示例：
```
java -version（默认是混合模式）
Java HotSpot(TM) 64-Bit Server VM (build 25.40-b25, mixed mode)
java -Xint -version
Java HotSpot(TM) 64-Bit Server VM (build 25.40-b25, interpreted mode)
```
 ```
XX 参数（非标准化参数）
主要用于 JVM调优和 debug
Boolean类型（+-）
格式：-XX:[+-]<name>表示启用或禁用 name 属性
如：-XX:+UseConcMarkSweepGC（启用cms垃圾收集器）
-XX:+UseG1GC（启用G1垃圾收集器）
非Boolean类型（key-value）（带=的）
格式：-XX:<name>=<value>表示 name 属性的值是 value
如：-XX:MaxGCPauseMillis=500（GC最大停用时间）
-xx:GCTimeRatio=19
-Xmx -Xms属于 XX 参数
-Xms 等价于-XX:InitialHeapSize（初始化堆大小）
-Xmx 等价于-XX:MaxHeapSize    (最大堆大小)
-xss 等价于-XX:ThreadStackSize（线程堆栈）
查看
jinfo -flag MaxHeapSize <pid>（查看最大内存）
-XX:+PrintFlagsInitial
-XX:+PrintFlagsFinal
-XX:+UnlockExperimentalVMOptions 解锁实验参数
-XX:+UnlockDiagnosticVMOptions 解锁诊断参数
-XX:+PrintCommandLineFlags 打印命令行参数
输出结果中=表示默认值，:=表示被用户或 JVM 修改后的值
示例：java -XX:+PrintFlagsFinal -version
```

#### 2.jinfo & jps(参数和进程查看)
```
pid 可通过类似 ps -ef|grep tomcat或 jps来进行查看
jps
[详情参考 jps官方文档](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jps.html)
-l
jinfo
jinfo -flag MaxHeapSize <pid>
jinfo -flags <pid>

jstat
[详情参考 jps官方文档](https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jstat.html#BEHBBBDJ)
jstat 使用示例
```
###### 3.jstat(类加载、垃圾收集、JIT 编译)
```
类加载
每隔1000ms 即1秒，共输出10次
jstat -class <pid> 1000 10
loaded 加载类的个数
[root@localhost java]# jps
4167 Jps
3370 Bootstrap
[root@localhost java]# jstat -class 3370
Loaded  Bytes  Unloaded  Bytes     Time
  5990 12028.7        0     0.0      15.50
```
```
垃圾收集
[root@localhost java]# jstat -gc 3370
Warning: Unresolved Symbol: sun.gc.metaspace.capacity substituted NaN
Warning: Unresolved Symbol: sun.gc.metaspace.used substituted NaN
Warning: Unresolved Symbol: sun.gc.compressedclassspace.capacity substituted NaN
Warning: Unresolved Symbol: sun.gc.compressedclassspace.used substituted NaN
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
1600.0 1600.0  0.0   1600.0 13184.0   7779.2   32696.0    22669.8     -      -      -      -       117    1.056   5      0.386    1.441
-gc, -gcutil, -gccause, -gcnew, -gcold
jstat -gc <pid> 1000 10
以下大小的单位均为 KB
S0C, S1C, S0U, S1U: S0和 S1的总量和使用量
EC, EU: Eden区总量与使用量
OC, OU: Old区总量与使用量
MC, MU: Metacspace区(jdk1.8前为 PermGen)总量与使用量
CCSC, CCSU: 压缩类区总量与使用量
YGC, YGCT: YoungGC 的次数与时间
FGC, FGCT: FullGC 的次数与时间
GCT: 总的 GC 时间
```
```
JIT 编译
-compiler, -printcompilation
[root@localhost java]# jstat -compiler 3370
Compiled Failed Invalid   Time   FailedType FailedMethod
     833      0       0    26.04          0
    花费26.04s编译了833个方法
```

#### 4.jmap+MAT(内存溢出)

内存溢出演示：
[生成springboot初始化代码](https://start.spring.io/)
[最终代码](https://start.spring.io/)
```
为快速产生内存溢出，右击 Run As>Run Configurations, Arguments 标签VM arguments 中填入
-Xmx32M -Xms32M
访问 http://localhost:8080/heap
Exception in thread "http-nio-8080-ClientPoller-0" java.lang.OutOfMemoryError: GC overhead limit exceeded
    at java.util.HashMap$KeySet.iterator(HashMap.java:916)
    at java.util.HashSet.iterator(HashSet.java:172)
    at java.util.Collections$UnmodifiableCollection$1.<init>(Collections.java:1039)
Exception in thread "http-nio-8080-exec-1" java.lang.OutOfMemoryError: GC overhead limit exceeded
-XX:MetaspaceSize=32M -XX:MaxMetaspaceSize=32M（同时在 pom.xml 中加入 asm 的依赖）
访问 http://localhost:8080/nonheap
Exception in thread "main" java.lang.OutOfMemoryError: Metaspace
Exception in thread "ContainerBackgroundProcessor[StandardEngine[Tomcat]]" java.lang.OutOfMemoryError: Metaspace
内存溢出自动导出
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./
右击 Run As>Run Configurations, Arguments 标签VM arguments 中填入
-Xmx32M -Xms32M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./
可以看到自动在当前目录中生成了一个java_pid660.hprof文件
java.lang.OutOfMemoryError: GC overhead limit exceeded
Dumping heap to ./java_pid660.hprof ...
另一种导出溢出也更推荐的方式是jmap
option: -heap, -clstats, -dump:<dump-options>, -F
jmap -dump:format=b,file=heap.hprof <pid>
C:\Users\Mr Chen>jps -l
10476 sun.tools.jps.Jps
6744
14980 com.imooc.monitor_tuning.MonitorTuningApplication
C:\Users\Mr Chen>cd desktop
C:\Users\Mr Chen\Desktop>jmap -dump:format=b,file=heap.hprof 14980
Dumping heap to C:\Users\Mr Chen\Desktop\heap.hprof ...
Heap dump file created
 ```
jmap 导出溢出文件
[MAT下载地址](http://www.eclipse.org/mat/)
找开上述导出的内存溢出文件即可进行分析，如下图的溢出源头分析：
Memory Analyzer 内存溢出分析
#### 5.jstack(线程、死循环、死锁)
##### 1.线程状态
```
jstack： 打印jvm内部所有线程
C:\Users\Mr Chen\Desktop>jps -l
15260 com.imooc.monitor_tuning.MonitorTuningApplication
10836 sun.tools.jps.Jps
6744
C:\Users\Mr Chen\Desktop>jstack 15260> 15260.txt
```
可查看其中包含java.lang.Thread.State: WAITING (parking)，JAVA 线程包含的状态有：
NEW：线程尚未启动
RUNNABLE：线程正在 JVM 中执行
BLOCKED：线程在等待监控锁(monitor lock)
WAITING：线程在等待另一个线程进行特定操作（时间不确定）
TIMED_WAITING：线程等待另一个线程进行限时操作
TERMINATED：线程已退出
##### 2.死循环导致cpu飙高
```
monitor_tuning中新增CpuController.java
直接 maven clean、 maven install
此时会生成一个monitor_tuning-0.0.1-SNAPSHOT.jar的 jar包，为避免本地的 CPU 消耗过多导致死机，建议上传上传到虚拟机进行测试
nohup java -jar monitor_tuning-0.0.1-SNAPSHOT.jar &
访问 http://xx.xx.xx.xx:8080/loop(端口8080在application.properties文件中定义)
top -p <pid> -H可以查看线程及 CPU 消耗情况
top 命令打出线程 CPU 消耗

```
使用 jstack <pid>可以导出追踪文件，文件中 PID 在 jstack 中显示的对应 nid 为十六进制(命令行可执行 print '%x' <pid>可以进行转化，如4008对应的十六进制为1007)
```
[root@localhost java]# jstack 4008 > 4008.txt
[root@localhost java]# printf "%x" 4103
1007[root@localhost java]# jstack 4103 > 4103.txt
```
```
"http-nio-8080-exec-7" #22 daemon prio=5 os_prio=0 tid=0x00007f6378be7000 nid=0x1026 runnable [0x00007f63556d1000]
   java.lang.Thread.State: RUNNABLE
    at java.lang.String.indexOf(String.java:1769)
    at java.lang.String.indexOf(String.java:1718)
    at com.imooc.monitor_tuning.chapter2.CpuController.getPartneridsFromJson(CpuController.java:73)
```
**说明getPartneridsFromJson这里导致的cpu飙升**

##### 3.死锁

访问http://xx.xx.xx.xx:8080/deadlock(如上jstack <pid>导出追踪记录会发现如下这样的记录)
```
[root@localhost java]# ps -ef |grep  java
root       4357   3145 23 02:00 pts/0    00:01:22 java -jar monitor_tuning-0.0.1-SNAPSHOT.jar
root       4474   3707  0 02:06 pts/3    00:00:00 grep --color=auto java
[root@localhost java]# jstack 4357 > 4357.txt

```
将文件拉到最后会发现
```
"Thread-5":
    at com.imooc.monitor_tuning.chapter2.CpuController.lambda$deadlock$1(CpuController.java:41)
    - waiting to lock <0x00000000f6b383e0> (a java.lang.Object)
    - locked <0x00000000f6b383f0> (a java.lang.Object)
    at com.imooc.monitor_tuning.chapter2.CpuController$$Lambda$337/678439847.run(Unknown Source)
    at java.lang.Thread.run(Thread.java:748)
"Thread-4":
    at com.imooc.monitor_tuning.chapter2.CpuController.lambda$deadlock$0(CpuController.java:33)
    - waiting to lock <0x00000000f6b383f0> (a java.lang.Object)
    - locked <0x00000000f6b383e0> (a java.lang.Object)
    at com.imooc.monitor_tuning.chapter2.CpuController$$Lambda$336/498593401.run(Unknown Source)
    at java.lang.Thread.run(Thread.java:748)
Found 1 deadlock.
```
**线程4在等待线程5，同时线程5也在等待线程4，此时死锁**

#### 6.JVisualVM(本地和远程可视化监控)
##### 1.监控本地java进程
详情参考官方文档
Mac命令行直接输入jvisualvm命令，Windows 找到对应的 exe 文件双击即可打开
插件安装Tools>Plugins>Settings根据自身版本(java -version)更新插件中心地址，各版本查询地址：
[jvisualvm插件](https://visualvm.github.io/pluginscenters.html)
建议安装：Visual GC, BTrace Workbench
##### 2.监控远程java进程
以上是本地的JAVA进程监控，还可以进行远程的监控，在上图左侧导航的 Applications 下的 Remote 处右击Add Remote Host...，输入主机 IP 即可添加，在 IP 上右击会发现有两种连接 JAVA 进程进行监控的方式:JMX, jstatd
```
vi bin/catalina.sh(以192.168.73.0为例)
:/JAVA_OPTS 按两个n 添加
```
```
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9004 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.net.preferIPv4Stack=true -Djava.rmi.server.hostname=192.168.73.0"
```
**报错**
异常情况：VisualVM 无法使用 service:jmx:rmi:///jndi/rmi:///jmxrmi 连接到
原因：
除了JMX server指定的监听端口号外，JMXserver还会监听一到两个随机端口号，
可以通过命grep <pid> 来查看当前java进程需要监听的随机端口号
```
[root@localhost bin]# jps
4216 Jps
4031 Bootstrap
[root@localhost bin]# lsof -i|grep java | grep 4031
java      4031    root   19u  IPv4  32304      0t0  TCP *:33707 (LISTEN)
java      4031    root   20u  IPv4  32310      0t0  TCP *:9004 (LISTEN)
java      4031    root   21u  IPv4  32322      0t0  TCP *:50022 (LISTEN)
java      4031    root   53u  IPv4  32323      0t0  TCP *:webcache (LISTEN)
java      4031    root   57u  IPv4  32325      0t0  TCP *:8009 (LISTEN)
java      4031    root   69u  IPv4  32338      0t0  TCP localhost:mxi (LISTEN)
将监听的端口去掉防火墙当然9004端口也是需要去掉的
[root@localhost bin]# iptables -I INPUT -p tcp --dport 33707 -j ACCEPT
[root@localhost bin]# iptables -I INPUT -p tcp --dport 50022 -j ACCEPT
[root@localhost bin]# iptables -I INPUT -p tcp --dport 8009 -j ACCEPT

```

启动tomcat，以 JMX 为例，在 IP 上右击点击Add JMX Connection...，输入 192.168.73.0:9004
Add JMX Connection
以上为 Tomcat，其它 JAVA 进程也是类似的，如：
```
nohup java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9005 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.net.preferIPv4Stack=true -Djava.rmi.server.hostname=192.168.73.0 -jar monitor_tuning-0.0.1-SNAPSHOT.jar &
```
#### 6.使用 BTrace 进行拦截调试
[下载BTrace ](https://github.com/btraceio/btrace/releases/tag/v1.3.11.1)
可以动态地向目标应用程序的字节码注入追踪代码，使用的技术有 JavaCompilerApi, JVMTI, Agent, Instrumentation+ASM
使用方法：JVisualVM中添加 BTrace 插件
方法二：btrace <pid> <trace_script>
monitor_tuning中新增包org.alanhou.monitor_tuning.chapter4
安装BTrace 要记得配置环境变量，以 windos 为例
```
BTRACE_HOME  D:\Program Files\btrace-bin-1.3.11.1
path 添加 %BTRACE_HOME%\bin
添加btrace相关依赖btrace-agent, btrace-boot, btrace-client
```
访问：http://localhost:8080/ch4/arg1?name=belong
```
d:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>jps -l
3932 com.imooc.monitor_tuning.MonitorTuningApplication
1108 sun.tools.jps.Jps
7852 org/netbeans/Main
15456

d:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>btrace 3932 PrintArgSimple.java
[belong, ]
com.imooc.monitor_tuning.chapter4.Ch4Controller,arg1

```
##### 1.拦截方法
普通方法：@OnMethod( clazz="", method="")，如上例(PrintArgSimple.java)
构造函数：@OnMethod( clazz="", method="<init> ")
访问 http://localhost:8080/ch4/constructor?id=1&name=belong
```
d:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>jps -l
3296 sun.tools.jps.Jps
7852 org/netbeans/Main
15592 com.imooc.monitor_tuning.MonitorTuningApplication
15456

d:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>btrace 15592 PrintConstructor.java
com.imooc.monitor_tuning.chapter2.User,<init>
[1, belong, ]
```
##### 2.拦截同名函数：用参数区分（PrintSame.java）
如下例中虽然方法名相同，但分别有一个和两个参数
```
@RequestMapping("/same1")
public String same(@RequestParam("name")String name) {
    // 访问地址:  http://localhost:8080/ch4/same1?name=Java
    return "Hello, "+name;
}

@RequestMapping("/same2")
public String same(@RequestParam("name")String name, @RequestParam("id")int id) {
    // 访问地址:  http://localhost:8080/ch4/same2?name=Java&id=1
    return "Hello, "+name+", "+id;
}

import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.ProbeClassName;
import com.sun.btrace.annotations.ProbeMethodName;

@BTrace
public class PrintSame {

    @OnMethod(
            clazz="com.imooc.monitor_tuning.chapter4.Ch4Controller",
            method="same"
    )
    public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn, String  name,int id) {
        BTraceUtils.println(pcn+","+pmn + "," + name+ "," + id);
        BTraceUtils.println();
    }
}
```
##### 3.拦截时机
Kind.ENTRY: 入口，默认值（上述例子均为这种情况）
Kind.RETURN: 返回（PrintReturn.java）
```
d:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>btrace 16268 PrintReturn.java
com.imooc.monitor_tuning.chapter4.Ch4Controller,arg1,hello,belong
```
Kind.THROW: 异常（PrintOnThrow.java）
```
d:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>jps -l
12836 sun.tools.jps.Jps
16268 com.imooc.monitor_tuning.MonitorTuningApplication
7852 org/netbeans/Main
15456

d:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>btrace 16268 PrintOnThrow.java
java.lang.ArithmeticException: / by zero
        com.imooc.monitor_tuning.chapter4.Ch4Controller.exception(Ch4Controller.java:41)
        sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        java.lang.reflect.Method.invoke(Method.java:498)
        org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:209)
        org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:136)
        org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:102)
```
打印指定行号是否执行
```
d:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>btrace 16268 PrintLine.java
com.imooc.monitor_tuning.chapter4.Ch4Controller,exception,41
```
##### 3.拦截 this、入参、返回值
this：@self
入参：可以用 AnyType，也可以用真实类型，同名的用真实的
返回：@Return
获取对象的值
简单类型：直接获取
复杂类型：反射，类名+属性名（PrintArgComplex.java）
```
D:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>jps -l
2896 sun.tools.jps.Jps
10568 com.imooc.monitor_tuning.MonitorTuningApplication
7004

D:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>btrace -cp "D:\workspace\work-app\project\o435au\target\classes" 10568 PrintArgComplex.java
{id=1, name=belong, }
belong
com.imooc.monitor_tuning.chapter4.Ch4Controller,arg2
```
###### 1.拦截函数中还可以使用正则表达式，如method="/.*/"匹配指定类下的所有方法(PrintRegex.java)
```
import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.ProbeClassName;
import com.sun.btrace.annotations.ProbeMethodName;

@BTrace
public class PrintRegex {

    @OnMethod(
            clazz="com.imooc.monitor_tuning.chapter4.Ch4Controller",
            method="/.*/"   )
    public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn) {
        BTraceUtils.println(pcn+","+pmn);
        BTraceUtils.println();
    }
}
```
```
D:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>btrace 10568 PrintRegex.java
com.imooc.monitor_tuning.chapter4.Ch4Controller,arg2
```
###### 2.打印环境变量(PrintJinfo.java)和 jinfo -help一样
```
D:\workspace\work-app\project\o435au\src\main\java\com\imooc\monitor_tuning\chapter4>btrace 10568 PrintJinfo.java
System Properties:
java.vm.version = 25.131-b11
sun.jnu.encoding = GBK
java.vendor.url = http://java.oracle.com/
java.vm.info = mixed mode
java.awt.headless = true
user.dir = D:\workspace\work-app\project\o435au
sun.cpu.isalist = amd64
java.awt.graphicsenv = sun.awt.Win32GraphicsEnvironment
sun.os.patch.level =
catalina.useNaming = false
user.home = C:\Users\Mr Chen
java.io.tmpdir = C:\Users\MRCHEN~1\AppData\Local\Temp\
java.awt.printerjob = sun.awt.windows.WPrinterJob
java.version = 1.8.0_131
file.encoding.pkg = sun.io
java.vendor.url.bug = http://bugreport.sun.com/bugreport/
file.encoding = UTF-8
```
注意事项
默认只能本地运行
生产环境下可以使用，但是被修改的字节码不会被还原
#### 7.Tomcat 性能监控与调优
##### 1.Tomcat 远程 Debug
[JDWP](https://www.ibm.com/developerworks/cn/java/j-lo-jpda3/)
bin/startup.sh 修改最后一行(添加 jpda)
```
exec "$PRGDIR"/"$EXECUTABLE" jpda start "$@"

```
bin/catalina.sh 为便于远程调试进行如下修改
JPDA_ADDRESS="localhost:8000"
 修改为
```
  if [ -z "$JPDA_ADDRESS" ]; then
    JPDA_ADDRESS="54321"
  fi
  if [ -z "$JPDA_SUSPEND" ]; then
    JPDA_SUSPEND="n"

```
查看日志发现已经启动起来
```
tail -f ./logs/catalina.out
```
再查看54321端口是否被监听
```
[root@localhost apache-tomcat-8.5.34]# netstat -nap | grep 54321
tcp        0      0 0.0.0.0:54321           0.0.0.0:*               LISTEN      3314/java
```
打开54321端口防火墙
```
[root@localhost apache-tomcat-8.5.34]#  iptables -I INPUT -p tcp --dport 54321 -j ACCEPT
```
```
若发现54321端口启动存在问题可尝试bin/catalina.sh jpda start
本地添加包org.alanhou.monitor_tuning.chapter5，修改打包方式为 war，并重写configure，进入monitor_tuning文件夹，执行mvn clean package 进行打包，target 目录下默认生成的包名为monitor_tuning-0.0.1-SNAPSHOT.war，为便于访问修改为monitor_tuning.war再上传到服务器的webapps目录下
http://192.168.0.5:8080/monitor_tuning/ch5/hello
使用 Eclipse 远程调试，右击 Debug As > Debug Configurations... > Remote Java Application > 右击 New 新建
```
##### 2.tomcat-manager 监控
```
1.conf/tomcat-users.xml添加用户
  <role rolename="tomcat"/>
  <role rolename="manager-status"/>
  <role rolename="manager-gui"/>
  <user username="tomcat" password="123456" roles="tomcat,manager-gui,manager-status"/>
2.新建conf/Catalina/localhost/manager.xml配置允许的远程连接
<?xml version="1.0" encoding="UTF-8"?>
<Context privileged="true" antiResourceLocking="false"
        docBase="$(catalina.home)/webapps/manager">
  <Valve className="org.apache.catalina.valves.RemoteAddrValve"
        allow="127.0.0.1" />
</Context>
远程连接将allow="127.0.0.1"修改为allow="^.*$"，浏览器中输入http://127.0.0.1:8080/manage或对应的 IP，用户名密码为tomcat-users.xml中所设置的
3.重启 Tomcat 服务
```
##### 3.psi-probe 监控
[psi-probe下载地址](https://github.com/psi-probe/psi-probe)
```
下载后进入psi-probe-master目录，执行：
mvn clean package -Dmaven.test.skip
将 web/target/probe.war放到 Tomcat 的 webapps 目录下，同样需要conf/tomcat-users.xml和conf/Catalina/localhost/manager.xml中的配置（可保持不变），启动 Tomcat 服务
浏览器中输入http://127.0.0.1:8080/probe或对应的 IP，用户名密码为tomcat-users.xml中所设置的
```

##### 4.Tomcat 调优
###### 1.线程优化（webapps/docs/config/http.html）：
```
maxConnections： 最大链接数现在nio 1w，之前apr8192
acceptCount   ：最大排队数，超过最大连接数后的队列 默认100
maxThreads  ： 工作线程，最大并发
minSpareThreads ：最小空闲线程，不能设置太小
```
###### 2.配置优化（webapps/docs/config/host.html）：
```
autoDeploy： 周期性检查是否有新应用部署，默认true，生产千万不能true
enableLookups（http.html）：默认false，千万不能true
reloadable（context.html）：默认false，千万不要true
protocol="org.apache.coyote.http11.Http11AprProtocol"   8.5以后nio
apr用于大并发  比较合适
Session 优化：
如果是 JSP, 可以禁用原生 Session=false
session放在redis
```
###### 3.补充：APR 配置
```
yum install -y apr-devel openssl-devel
cd tomcat/bin
tar -zxvf tomcat-native.tar.gz
cd tomcat-native-1.2.17-src/native/
./configure --with-apr=/usr/bin/apr-1-config --with-java-home=/usr/lib/jvm/java-1.8.0 --with-ssl=yes
make && make install

vi tomcat/bin/setenv.sh
export CATALINA_OPTS=”$CATALINA_OPTS -Djava.library.path=/usr/local/apr/lib”
#vi tomcat/conf/server.xml
<Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
 修改为
<Connector port="8080" protocol="org.apache.coyote.http11.Http11AprProtocol"
               connectionTimeout="20000"
               redirectPort="8443" />


<Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />
```

##### 5.Nginx 性能监控与调优
###### 1.Nginx 安装
添加 yum 源（/etc/yum.repos.d/nginx.repo）
```
[root@localhost Desktop]# vi /etc/yum.repos.d/nginx.repo
[nginx]
name=nginx repo
baseurl=http://nginx.org/packages/centos/7/x86_64/     #7为当前centos的版本号
gpgcheck=0
enabled=1
[root@localhost Desktop]# yum install -y nginx
```
关闭80端口防火墙，打开nginx
```
[root@localhost nginx]# iptables -I INPUT -p tcp --dport 80 -j ACCEPT
[root@localhost nginx]# nginx
[root@localhost nginx]# ps -ef | grep nginx
root      12178      1  0 01:25 ?        00:00:00 nginx: master process nginx
nginx     12180  12178  0 01:25 ?        00:00:00 nginx: worker process
root      12192  11673  0 01:25 pts/0    00:00:00 grep --color=auto nginx

```
开启
```
[root@localhost nginx]# nginx
```
重启
```
[root@localhost nginx]# nginx -s reload
```
停止
```
[root@localhost nginx]# nginx -s stop
```
查看编译参数
```
nginx -v
```
默认配置文件
```
/etc/nginx/nginx.conf
注意：配置反向代理要关闭selinux，setenforce 0
```
取消注释
```
 cat default.conf | grep -v "#'
```
###### 2.ngx_http_stub_status 监控连接信息
```
[root@localhost Desktop]# cd /etc/nginx
[root@localhost nginx]# cd conf.d/
[root@localhost conf.d]# ll
total 4
-rw-r--r--. 1 root root 1093 Nov  6 05:54 default.conf
[root@localhost conf.d]# vi default.conf
添加
location = /nginx_status {
    stub_status on;
    access_log off;
    allow 127.0.0.1;
    deny all;
}
重启
[root@localhost conf.d]# nginx -s reload
查看连接信息
[root@localhost conf.d]# wget http://127.0.0.1/nginx_status
--2018-11-12 19:46:04--  http://127.0.0.1/nginx_status
Connecting to 127.0.0.1:80... connected.
HTTP request sent, awaiting response... 200 OK
Length: 97 [text/plain]
Saving to: ‘nginx_status’

100%[======================================>] 97          --.-K/s   in 0s

2018-11-12 19:46:04 (5.45 MB/s) - ‘nginx_status’ saved [97/97]

[root@localhost conf.d]# cat nginx_status
Active connections: 1     当前活动连接数 包含正在等待的连接  并发数
server accepts handled requests
 7 7 7
Reading: 0当前正在发生请求  Writing: 1 nginx相应写回客户端的连接数
Waiting: 0    当前空闲的连接数
```
###### 2.ngxtop
[ngxtop](https://github.com/lebinh/ngxtop)
```
安装 python-pip
yum install epel-release
yum install python-pip
 安装 ngxtop
pip install ngxtop
指定配置文件，查询具体访问：ngxtop -c /etc/nginx/nginx.conf
查询状态是200：ngxtop -c /etc/nginx/nginx.conf -i 'status == 200'
查询访问最多 ip：ngxtop -c /etc/nginx/nginx.conf -g remote_addr
ngxtop查询访问最多 ip
```
###### 3.nginx-rrd 图形化监控
```
nginx-rrd 依赖于前面的ngx_http_stub_status
安装 php
yum install php php-gd php-soap php-mbstring php-xmlrpc php-dom php-fpm -y
Ngnix融合 php-fpm(/etc/php-fpm.d/www.conf)

修改
[root@localhost nginx]# vi /etc/php-fpm.d/www.conf
user = nginx
; RPM: Keep a group allowed to write in log dir.
group = nginx
启动 php-fpm
[root@localhost nginx]# systemctl start php-fpm
[root@localhost nginx]# netstat -nat | grep 9000
tcp        0      0 127.0.0.1:9000          0.0.0.0:*               LISTEN
 修改 Nginx 配置文件
  [root@localhost nginx]# vi /etc/nginx/conf.d/default.conf
  加入
location ~ .php$ {
    root           /usr/share/nginx/html;
    fastcgi_pass   127.0.0.1:9000;
    fastcgi_index  index.php;
    fastcgi_param  SCRIPT_FILENAME  $document_root/$fastcgi_script_name;
    include        fastcgi_params;
}
```
新建php文件查看php是否安装成功
```
[root@localhost nginx]# cd /usr/share/nginx/html
[root@localhost html]# vi index.php
<?php phpinfo(); ?>
关闭防火墙
systemctl stop firewalld
访问 http://192.168.81.128/index.php 检测配置是否成功
 ```
安装 rddtool 相关依赖
```
[root@localhost html]# yum install perl rrdtool perl-libwww-perl libwww-perl perl-rrdtool -y
移动文件到当前文件
[root@localhost tmp]# mv /usr/share/nginx/html/nginx-rrd-0.1.4.tgz .
```
 安装 nginx-rdd
 ```
wget http://soft.vpser.net/status/nginx-rrd/nginx-rrd-0.1.4.tgz
tar zxvf nginx-rrd-0.1.4.tgz
cd nginx-rrd-0.1.4
cp usr/sbin/* /usr/sbin     # 复制主程序文件到 /usr/sbin 下
cp etc/nginx-rrd.conf /etc  # 复制配置文件到 /etc 下
cp html/index.php /usr/share/nginx/html/
```
```
 修改配置(vi /etc/nginx-rrd.conf)
RRD_DIR="/usr/share/nginx/html/nginx-rrd";
WWW_DIR="/usr/share/nginx/html";
添加定时任务
[root@localhost nginx-rrd-0.1.4]# crontab -e
*  * * * * /bin/sh /usr/sbin/nginx-collect
*/1 * * * * /bin/sh /usr/sbin/nginx-graph
 查看定时任务执行情况
tail -f /var/log/cron
ab 压测（未安装 yum -y install httpd-tools）
ab -n 10000 -c 10 http://127.0.0.1/index.html
访问 http://192.168.81.128/index.php 即可得到如下这种图形化界面：
```
##### 4.Nginx 优化
###### 4.配置线程数和并发数
```
增加工作线程数和并发连接数
[root@localhost /]# vi /etc/nginx/nginx.conf
worker_processes  4; # 一般CPU 是几核就设置为几 也可以设置成auto
events {
    worker_connections  10240; # 每个进程打开的最大连接数，包含了 Nginx 与客户端和 Nginx 与 upstream 之间的连接
    multi_accept on; # 可以一次建立多个连接
    use epoll;   #epoll这种网络模型
}
查看nginx 语法是否正确
[root@localhost /]# nginx -t
nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
启用长连接
[root@localhost /]# vi /etc/nginx/nginx.conf
配置反向代理
upstream server_pool{
    server localhost:8080 weight=1 max_fails=2 fail_timeout=30s;
    server localhost:8081 weight=1 max_fails=2 fail_timeout=30s;
    keepalive 300; # 300个长连接 提高效率
}
配置反向代理服务
location / {
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_pass http://server_pool;  #所有请求都代理给server_pool
}
配置压缩
gzip on;
gzip_http_version 1.1;
gzip_disable "MSIE [1-6].(?!.*SV1)";
gzip_proxied any;
gzip_types text/plain text/css application/javascript application/x-javascript application/json application/xml application/vnd.ms-fontobject application/x-font-ttf application/svg+xml application/x-icon;
gzip_vary on;
gzip_static on;
操作系统优化
 配置文件/etc/sysctl.conf
sysctl -w net.ipv4.tcp_syncookies=1 # 防止一个套接字在有过多试图连接到时引起过载
sysctl -w net.core.somaxconn=1024 # 默认128，操作系统连接队列
sysctl -w net.ipv4.tcp_fin_timeout=10 # timewait 的超时时间
sysctl -w net.ipv4.tcp_tw_reuse=1 # os 直接使用 timewait的连接
sysctl -w net.ipv4.tcp_tw_recycle=0 # 回收禁用
 /etc/security/limits.conf
           hard    nofile            204800
            soft    nofile             204800
            soft    core             unlimited
             soft    stack             204800
其它优化
sendfile    on; # 减少文件在应用和内核之间拷贝
tcp_nopush  on; # 当数据包达到一定大小再发送
tcp_nodelay off; # 有数据随时发送
```
##### 6.JVM层GC调优
[jvm虚拟机规范](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/toc.html)
###### 1.运行时数据区：
程序计数器 PC Register
虚拟机栈 JVM Stacks
堆 Heap（java虚拟机所管理内存中最大的一块，存放对象实例）
方法区 Method Area（线程共享的内存区域，存储类信息，常量，静态常量，编译器编译后的代码）
 常量池 Run-Time Constant Pool
本地方法栈 Native Method Stacks

###### 2.常用参数：
```
-Xms 最小堆内存 -Xmx最大堆内存
-XX:NewSize 新生代大小 -XX:MaxNewSize最大新生代大小
-XX:NewRatio new和old的比例 -XX:SurvivorRatio survivor和edn的比例
-XX:MetaspaceSize -XX:MaxMetaspaceSize   一般调大
-XX:+UseCompressedClassPointers 是否启用压缩类指针
-XX:CompressedClassSpaceSize
-XX:InitialCodeCacheSize
-XX:ReservedCodeCacheSize
 ```
###### 3.垃圾回收算法
java自动垃圾回收
思想：枚举根节点，做可达性分析
根节点：类加载器、Thread、虚拟机栈的本地变量表、static 成员、常量引用、本地方法栈的变量
算法：标记清除、复制、标记整理、分带垃圾回收
jvm垃圾回收yong区用复制算法old区用标记清除或者标记整理
对象分配：对象优先在 Eden 区分配、大对象直接进入Old 区(-XX:PretenureSizeThreshold)、长期存活对象进入 Old 区(-XX:MaxTenuringThreshold, -XX:+PrintTenuringDistribution, -XX:TargetSurvivorRatio
###### 4.垃圾收集器
 常见配置示例(bin/catalina.sh)
 ```
PARALLEL_OPTION="-XX:+UseParallelGC -XX:+UseParallelOldGC -XX:MaxGCPauseMillis=200 -XX:GCTimeRatio=99"
CMS_OPTION="-XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=5"
G1_OPTION="-XX:+UseG1GC -XX:+UseStringDeduplication -XX:StringDeduplicationAgeThreshold=3 -XX:+UseCompressedClassPointers -XX:MaxGCPauseMillis=200"

JAVA_OPTS="$JAVA_OPTS $CMS_OPTION -Xms128M -Xmx128M -XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=128M -XX:+UseCompressedClassPointers"
串行收集器 Serial:：Serial, Serial Old (-XX:+UseSerialGC -XX:+UseSerialOldGC)
并行收集器 Parallel（吞吐量优先, Server 模式默认收集器）：Parallel Scavenge, Parallel Old (-XX:+UseParallelGC, -XX:+UseParallelOldGC)

-XX:ParallelGCThreads=<N> 多少个 GC 线程（CPU> 8 N=5/8; CPU<8 N=CPU）
Parallel Collector Ergonomics:
-XX:MaxGCPauseMillis=<N>
-XX:GCTimeRatio=<N>
-Xmx<N>
动态内存调整
-XX:YoungGenerationSizeIncrement=<Y>
-XX:TenuredGenerationSizeIncrement=<T>
-XX:AdaptiveSizeDecrementScaleFactor=<D>

并发收集器 Concurent（停顿时间优先）：CMS (-XX:+UseConcMarkSweepGC -XX:+UseParNewGC), G1(-XX:UseG1GC)
停顿时间：垃圾收集器做垃圾回收中断应用执行的时间。
-xx:MANGCPauseMillis
吞吐量：花在垃圾收集的时间和花在应用时间的占比。-xx:GCTimeRatui=<n>,垃圾收集时间占：1/1+n 吞吐量最大的时候停顿时间最小最好
```
CMS
```
并发收集，低停顿，低延迟，老年代收集器
1. CMS initial mark: 初始标记 Root, STW
2. CMS concurrent mark：并发标记
3. CMS-concurrent-preclean：并发预清理
4. CMS remark：重新标记，STW
5. CMS concurrent sweep：并发清除
6. CMS-concurrent-reset：并发重置
缺点：CPU 敏感、产生浮动垃圾和空间碎片
相关参数：
-XX:ConcGCThreads：并发的 GC 线程数
-XX:+UseCMSCompactAtFullCollection：FullGC 之后做压缩
-XX:CMSFullGCsBeforeCompaction：多少次 FullGC之后压缩一次
-XX:CMSInitiatingOccupancyFraction：触发 FullGC
-XX:+UseCMSInitiatingOccupancyOnly：是否动态可调
-XX:+CMSScavengeBeforeRemark：FullGC之前先做 YGC
-XX:+CMSClassUnloadingEnable：启用回收Perm 区
 ```
iCMS
适用于单核或者双核
G1 Collector(JDK 7开始，推荐使用）新生代和老生代收集器
G1的几个概念
Region
SATB：Snapshot-At-The-Beginning，它是通过 Root Tracing 得到的，GC 开始时候存活对象的快照。
RSet：记录了其他 Region中的对象引用本 Region 中对象的关系，属于 points-into 结构(谁引用了我的对象)
YoungGC
新对象进入 Eden 区
存活对象拷贝到Survivor 区
存活时间达到年龄阈值时，对象晋升到 Old 区
MixedGC
不是 FullGC，回收所有的 Young和所有的 Old
global concurrent marking
1. Initial marking phase: 标记 GC Root, STW
2. Root region scanning phase：标记存活 Region
3. Concurrent marking phase：标记存活的对象
4. Remark phase：重新标记，STW
5. Cleanup phase：部分 STW

MixedGC时机
InitiatingHeapOccupancyPercent
G1HeapWastePercent
G1MixedGCLiveThresholdPercent   Old区的region被回收时候存活对象占比
G1MixedGCCountTarget  一次global concurrent marking 之后最多执行mixed gc 的次数
G1OldGCSetRegionThresholdPercent    一次mixed GC中能被选入cset的最多old区的region数量
-XX:+UseG1GC 开启 G1
-XX:G1HeapRegionSize=n， Region 的大小，1-32M，最多2048个
-XX:MaxGCPauseMillis=200 最大停顿时间
-XX:G1NewSizePercent、-XX:G1MaxNewSizePercent
-XX:G1ReservePercent=10 保留防止 to space溢出
-XX:ParallelGCThreads=n SWT线程数并行
-XX:ConcGCThreads=n 并发线程数=1/4*并行

最佳实践
年轻代大小：避免使用-Xmn, -XX:NewRatio 等显式 Young 区大小，会覆盖暂停时间目标
暂停时间目标：暂停时间不要太严苛，其吞吐量目标是90%的应用程序时间和10%的垃圾回收时间，太严苛会直接影响到吞吐量

需要切换到 G1的情况：
1. 50%以上的堆被存活对象占用
2. 对象分配和晋升的速度变化非常大
3. 垃圾回收时间特别长，超过了1秒

查看方法：jinfo -flag xxx <pid>

并行：多条垃圾收集线程并行工作，但用户线程处于等待状态
并发：用户线程与垃圾收集线程同时执行（或交替执行）

如何选择垃圾收集器
```
优先调整堆的大小让服务器自己来选择
如果内存小于100m，使用串行收集器
如果是单核，并且没有停顿时间的要求，串行或者jvm自己选
如果允许停顿时间超过1s，选择并行或者jvm自己选
如果响应时间最重要，并且不能超过1s，使用并发收集器
```

###### 5.可视化 GC 日志分析工具
打印日志相关参数：
```
-XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -Xloggc:$CATALINA_HOME/logs/gc.log -XX:+PrintHeapAtGC -XX:+PrintTenuringDistribution
例（默认为 ParallelGC, 其它的添加-XX:+UseConcMarkSweepGC或-XX:+UseG1GC即可）：
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -Xloggc:$CATALINA_HOME/logs/gc.log"
CMS日志格式
G1日志格式
[在线工具gceasy](http://gceasy.io/)
访问 GCeasy 官网导入日志即可获取可视化分析及优先建议
```
GCViewer
```
mvn clean package -Dmaven.test.skip 生成 jar包，双击执行，导入日志即可进入图形化分析页面
 ```
Tomcat 的 GC 调优实战
ParallelGC调优
```
设置 Metaspace 大小 -XX:MetaspaceSize=64M -XX:MaxMetaspaceSize=64M
添加吞吐量和停顿时间参数 -XX:GCTimeRatio=99 -XX:MaxGCPauseMillis=100
修改动态扩容增量 -XX:YoungGenerationSizeIncrement=30
```
G1 GC 最佳实践
```
-XX:InitiatingHeapOccupancyPercent: Use to change the marking threshold.
-XX:G1MixedGCLiveThresholdPercent and -XX:G1HeapWastePercent: Use to change the mixed garbage collection decisions.
-XX:G1MixedGCCountTarget and -XX:G1OldCSetRegionThresholdPercent: Use to adjust the CSet for old regions.
```
[jvm内存调优官方文档](https://docs.oracle.com/javase/9/gctuning/introduction-garbage-collection-tuning.htm#JSGCT-GUID-326EB4CF-8C8C-4267-8355-21AB04F0D304)
##### 7.JAVA代码层调优
最终代码：monitor_tuning
 JVM字节码指令与 javap
javap <options> <classes>
```
cd monitor_tuning/target/classes/org/alanhou/monitor_tuning/chapter8/
javap -verbose Test1.class > Test1.txt 即可保存字节码文件
```
[常量池](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.4)
[字段描述符](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3.2)
[方法描述符](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3.3)
[字节码指令](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html)
i++与++i，字符串拼接+原理
javap -verbose SelfAdd.class > SelfAdd.txt
通过对 f1()和 f2()的字节码，我们得出结论 i++和++i 的执行效果完全相同
```
  public static void f1();
    descriptor: ()V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=0
         0: iconst_0
         1: istore_0
         2: goto          15
         5: getstatic     #25                 // Field java/lang/System.out:Ljava/io/PrintStream;
         8: iload_0
         9: invokevirtual #31                 // Method java/io/PrintStream.println:(I)V
        12: iinc          0, 1
        15: iload_0
        16: bipush        10
        18: if_icmplt     5
        21: return
 ```
其他代码优先方法
字符串拼接+
```
javap -verbose StringAdd.class >StringAdd.txt
通过字节码可以看出+拼接符效率要低于 append
for循环每次要new一个stringbuffer
```
Try-Finally
javap -verbose TryFinally.class >TryFinally.txt
Constant variable(final)
javap -verbose StringConstant.class >StringConstant.txt
jvm是基于栈的架构
常用代码优化方法
1. 尽量重用对象，不要循环创建对象，比如:for 循环字符串拼接(不在 for中使用+拼接，先new 一个StringBuilder再在 for 里 append)
2. 容器类初始化的地时候指定长度（扩容比较耗时）
```
List<String> collection = new ArrayLIst<String>(5);
Map<String, String> map = new HashMap<String, String>(32);
```
3. **ArrayList（底层数组）随机遍历快，LinkedList（底层双向链表）添加删除快只需移动一个指针，hashmap底层数组+链表**
4. 集合遍历尽量减少重复计算
5. 使用 Entry 遍历 Map
```
for(Map.Entry<String,String>entry:map.entrySet()){
    String key=entry.getKey();
    String value=entry.getValue();
}
```
6. 大数组复制使用System.arraycopy
7. 尽量使用基本类型而不是包装类型
Integer底层使用缓存，因为没有1000没有缓存，所以要new
8. 不要手动调用 System.gc()
9. 及时消除过期对象的引用，防止内存泄漏
10. 尽量使用局部变量，减小变量的作用域
11. 尽量使用非同步的容器ArraryList vs. Vector（建议使用ArraryList）手动加锁
12. 尽量减小同步作用范围, synchronized 方法 vs. 代码块（建议synchronized 方法）
13. 用ThreadLocal 缓存线程不安全的对象，SimpleDateFormat
14. 尽量使用延迟加载
15. 尽量减少使用反射，必须用加缓存
16. 尽量使用连接池、线程池、对象池、缓存
17. 及时释放资源， I/O 流、Socket、数据库连接
18. 慎用异常，不要用抛异常来表示正常的业务逻辑
19. String 操作尽量少用正则表达式
20. 日志输出注意使用不同的级别
21. 日志中参数拼接使用占位符
log.info("orderId:" + orderId); 不推荐
log.info("orderId:{}", orderId); 推荐（没有字符串拼接）
 
 < END >

程序员NBA
一个有故事的程序员

微信扫描二维码，关注我的公众号
 ![image](https://mmbiz.qpic.cn/mmbiz_jpg/d7tnZXLg9m8vyCxEmcVwcdoaFfaEJAHRYH32r3a4G3Pgg854j1qW1wBulzII2J9ntcKCW1eM52pHd1HD5ZcYicQ/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)
 
或者加我微信1414200300 一起讨论java技术
课程链接：https://coding.imooc.com/class/241.html
