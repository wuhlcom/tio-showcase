# tio-showcase

 **至理名言：学习[t-io](https://gitee.com/tywo45/t-io)最好的方式是从本项目开始** 

## 项目介绍
用于学习[t-io](https://gitee.com/tywo45/t-io)的一些例子，一共3个例子


- ### helloworld
    - helloworld是入门[t-io](https://gitee.com/tywo45/t-io)最好的方式！而作者也是用心写了一个对生产项目有参考价值的hello tio，而不是仅仅是show hello而hello
    - 服务器端入口程序：org.tio.examples.helloworld.server.HelloServerStarter
    - 客户端入口程序：org.tio.examples.helloworld.client.HelloClientStarter

    - 本例子演示的是一个典型的TCP长连接应用，大体业务简介如下。

        - 分为server和client工程，server和client共用common工程
        - 服务端和客户端的消息协议比较简单，消息头为4个字节，用以表示消息体的长度，消息体为一个字符串的byte[]
        - 服务端先启动，监听6789端口
        - 客户端连接到服务端后，会主动向服务器发送一条消息
        - 服务器收到消息后会回应一条消息
        - 之后，框架层会自动从客户端发心跳到服务器，服务器也会检测心跳有没有超时（这些事都是框架做的，业务层只需要配一个心跳超时参数即可）
        - 框架层会在断链后自动重连（这些事都是框架做的，业务层只需要配一个重连配置对象即可）

- ### showcase
    - showcase工程用于进一步掌握t-io，甚至可以用作你项目的脚手架（@精灵007 同学已经用这个工程完成了3个项目）
    - 这里有一篇博客，可以参考：[ShowCase设计分析](http://www.cnblogs.com/panzi/p/7814062.html)
    - 服务器端入口程序：org.tio.examples.showcase.server.ShowcaseServerStarter
    - 客户端入口程序：org.tio.examples.showcase.client.ShowcaseClientStarter

- ### IM
    - im项目在1.7.0版本前一直都开放的（见：[https://gitee.com/tywo45/t-io/tree/v1.7.0](https://gitee.com/tywo45/t-io/tree/v1.7.0)），考虑到im的复杂性，这会给作者带来一些额外的咨询工作，所以在后面的版本没有放出来，现在重新放出来
    - j-im项目是在本项目的基础上改造而来的，有兴趣的可以看看j-im
    - 服务器端入口程序：org.tio.examples.im.server.ImServerStarter
    - 客户端入口程序：org.tio.examples.im.client.ImClientStarter
    - 当年有用户用这个工程 **轰出每秒收发500万条聊天消息** （当然现在[t-io](https://gitee.com/tywo45/t-io)加了各种流量监控后，简单测试发现性能大约降了一半）
    ![每秒收发500万条聊天消息](https://gitee.com/tywo45/tio-side/raw/master/docs/performance/500%E4%B8%87.png "每秒收发500万条聊天消息")

## 母项目介绍
 **[不仅仅是百万级网络编程框架](https://gitee.com/tywo45/t-io)，母项目才是作者花心血最多的项目！** 