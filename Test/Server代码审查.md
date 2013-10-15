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
6. AbstractBusinessDevicePoo.java:120, if (!(deviceMap.containsKey(sn) || chatDeviceMap.containsKey(sn)))，后半部分判断条件搞反了。
7. 