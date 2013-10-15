#<center>RenHai Server Code Review Comments</center>

##20131014
1. OnlineDevicePool.java:220, connection.bind(deviceWrapper);这句和DevieWrapper()构造器中的一句重复了。
2. OnlineDevicePool.java:389, public void IdentifyBannedDevice(IDeviceWrapper device)方法名首字母应小写。
3. WebSocketConnection.java:231，synchronized(controller)的意义是什么？
4. WebSocketConnection.java:244, connectionOwner.onTimeOut(this);需要传入WebSocketConnection对象么？
5. RenHaiWebSocketServlet.java:50，需设置线程名字，替代Tomcat自己的Serlvet线程名（[http-bio-80-exec-1]）

##20131015
1. AbstractDevicePool.java:23, deviceMap没有使用ConcurrentHashMap，在多设备同时进入Pool时可能存在异常么？
2. AbstractBusinessDevicePool.java:32，chatDeviceMap同上。
3. AbstractDevicePool.java:32/34，重复定义函数，它们已经在父类AbstractPool中定义过了。
4. AbstractDevicePool.java:36，此函数定义可以移动到父类中去。
5. AbstractBusinessDevicePool.java:73/81, public String onDeviceEnter(IDeviceWrapper device)存在多设备同时进入的问题隐患。
6. OnlineDevicePool.java:55, Thread.currentThread().setName("InactiveCheck");修改线程名字的操作最好放到Timer的构造器中，这样只需一次即可，不需要每次调度Timer时都执行一次。
7. OnlineDevicePool.java:54/57, private class InactiveCheckTask extends TimerTask，Session hibernateSesion = HibernateSessionFactory.getSession();此处Session是否没有使用过？
8. OnlineDevicePool.java:73/76，private class BannedCheckTask extends TimerTask同上。另外建议继承于一个公共的Timer，可以使用doBeforeRun()/doAfterRun()来统一进行相关资源（比如Hibernate Session）的加载与释放，这样就不需要每个实际的业务Timer都反复写这些重复代码。
9. OnlineDevicePool.java:92/95，private class StatSaveTask extends TimerTask，同上。
10. OnlineDevicePool.java:116，HibernateSessionFactory.getSession();这句代码放在这里的意义是什么？
11. OnlineDevicePool.java:310，缺少DeviceWrapper的null判断
12. BusinessSession.java:59，private List<IDeviceWrapper> deviceList = new ArrayList<IDeviceWrapper>(); 这个类中多次使用synchronized访问这个成员变量，因此需要考虑换成线程安全的集合类。解决方法1：Collections.synchronizedList(deviceList)；解决方法2：CopyOnWriteArrayList类。需要进一步分析两个解决方法之间的差异和优劣。参考：http://blog.csdn.net/wind5shy/article/details/5396887。最后，需要看看Java并发容器种类全介绍：http://blog.sina.com.cn/s/blog_56fd58ab0100pl7g.html，可能对兴趣设备池的调度实现有帮助。
13. BusinessSessionPool.java:31，private List<IBusinessSession> sessionList = new ArrayList<IBusinessSession>();此ArrayList在访问时同样存在多线程安全隐患，比如public IBusinessSession getBusinessSession()这里。
