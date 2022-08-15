package com.tielan.controler;

import com.dzz.socketclient.constant.Constants;
import com.dzz.socketclient.export.WSClientTCP;
import com.dzz.socketclient.export.WSClientTCPRecv;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 通信服务
 */
@Slf4j
@Component
public class ComServe implements WSClientTCPRecv, Constants {
    @Value("${communication.server.ip}")
    private String comServeIp;

    @Value("${communication.server.port}")
    private int comServePort;

    // 客户端名称(自定义 同一IP下不能重复)
    private static String clientName = "aaa_cli";


    // 客户端对象
    public static WSClientTCP client = new WSClientTCP();


    // 订阅主题回调处理方法
    @Override
    public void OnClientRevTCP(String topic, byte[] bytes) {
        System.out.println(topic);
        // 包头
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        //PACKAGE_HEADER_LENGTH
/*        byte[] headerBytes = new byte[PACKAGE_HEADER_LENGTH];
        buf.readBytes(headerBytes);
        PackageHeader header = PackageHeader.decode(headerBytes);
        int bodyLen = header.getBodyLen();
        byte[] bodyBytes = new byte[bodyLen];
        buf.readBytes(bodyBytes);*/

        if (topic.equals("T3007")) {

        }

        if (topic.equals("T2015")) {

        }

    }

/*
    //发送
    public void sendMsg(String topic, int funcCode, byte[] bodyBytes) {
        //构造包头
        int bodyLength = bodyBytes.length;
        PackageHeader header = new PackageHeader((short) funcCode, bodyLength);

        //组包
        ByteBuf buf = Unpooled.buffer(18 + bodyLength + 1);
        byte[] packageBytes = buf.writeBytes(header.encode()).writeBytes(bodyBytes).array();

        //发送到通服
        client.SendTopicData(topic, packageBytes, null);
    }
*/

    // 初始化客户端
    @PostConstruct
    public void init() {
        // 1.初始化客户端并与通服建立连接
        System.out.println("*********初始化通服客户端**********");
        boolean res = client.InitClient(clientName);
        client.SetRecv(this);

        if (!res) {
            System.out.println("初始化通服客户端失败!");
            return;
        }

        res = client.StartConnect(comServeIp, comServePort);
        if (!res) {
            //由于异步连接,存在延迟,故循环获取客户端连接状态
            //startconnect()内部循环5次能返回true
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                res = client.ClientIsRunOK();
                if (res) break;
            }
        }
        if (!res) {
            System.out.println("通服连接失败!");
            client.ExitClient();
            return;
        }
        System.out.println("通服连接成功! " + comServeIp + ":" + comServePort);


        // 2.订阅主题列表
        String[] topicList = new String[]{"T3007", "T2015"};
        for (String topic : topicList) {
            //false存入订阅列表,UpData_SubTopicList时一起调用
            client.SubscribeTopic(topic, false);
        }
        res = client.UpData_SubTopicList();
        if (!res) {
            System.out.println("订阅主题列表失败!");
        }
    }

    @PreDestroy
    public void disconnect() {
        System.out.println("*********与通服断开连接**********");
        client.ExitClient();
    }


}
