package com.tielan.controler;

import cn.hutool.core.date.DateUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;


class PackageHeader {

    private char[] Head = {'D', 'C', 'H', 'J'};  //帧标识符
    private short Source = 22;  // 源地址代码
    private short Destination = 33; //目的地址代码
    private short InfoCode; //信息类别码
    private int Length;  //信息长度
    private int TimeMask;  //信息交换时标


    PackageHeader(short funcCode, int bodyLength) {

        this.InfoCode = funcCode;
        this.Length = bodyLength;
        this.TimeMask = Integer.valueOf(DateUtil.format(new Date(), "HHmmssSSS"));

    }

    private static byte[] reverseByteArr(byte[] bytes) {

        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("bytes字节数组为空");
        }
        byte[] b = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            b[bytes.length - i - 1] = bytes[i];
        }
        return b;
    }

    byte[] encode() {    //编码

        byte[] result;

        //帧标识符转换

        byte[] temp1 = new byte[8];
        ByteBuf buf = Unpooled.buffer(22);
        ByteBuffer.wrap(temp1).order(ByteOrder.BIG_ENDIAN).asCharBuffer().put(Head);

        // 源地址代码转换

        byte[] b = new byte[2];
        int tem1 = this.Source;
        for (int i = 0; i < b.length; i++) {
            b[i] = Integer.valueOf(tem1 & 0xff).byteValue();// 将最低位保存在最低位
            tem1 = tem1 >> 8;// 向右移8位
        }
        byte[] temp2 = reverseByteArr(b);
        //目的地址转换

        int tem2 = this.Destination;
        byte[] b2 = new byte[2];
        for (int i = 0; i < b2.length; i++) {
            b2[i] = Integer.valueOf(tem2 & 0xff).byteValue();// 将最低位保存在最低位
            tem2 = tem2 >> 8;// 向右移8位
        }
        byte[] temp3 = reverseByteArr(b2);

        //信息类别码

        byte[] temp4 = new byte[2];
        byte[] b3 = new byte[2];
        int tem3 = this.InfoCode;
        for (int i = 0; i < b3.length; i++) {
            b3[i] = Integer.valueOf(tem3 & 0xff).byteValue();// 将最低位保存在最低位
            tem3 = tem3 >> 8;// 向右移8位
        }
        temp4 = reverseByteArr(b3);

        //信息长度

        byte[] a1 = new byte[4];
        int tem4 = this.Length;
        for (int i = 0; i < a1.length; i++) {
            a1[i] = Integer.valueOf(tem4 & 0xff).byteValue();
            tem4 = tem4 >> 8;
        }
        byte[] temp5 = reverseByteArr(a1);

        //信息交换时标


        byte[] a2 = new byte[4];
        int tem5 = this.TimeMask;
        for (int i = 0; i < a2.length; i++) {
            a2[i] = Integer.valueOf(tem5 & 0xff).byteValue();
            tem5 = tem5 >> 8;
        }
        byte[] temp6 = reverseByteArr(a2);
        result = buf.writeBytes(temp1).writeBytes(temp2).writeBytes(temp3).writeBytes(temp4).writeBytes(temp5).writeBytes(temp6).array();
        return result;
    }

    static PackageHeader decode(byte[] bytes) {  //解码
        PackageHeader a = new PackageHeader((short) 1, 2);
        if (bytes == null) {
            return null;
        }
        int i = 0;
        int bodyLength = bytes.length;

        // 解析帧标识符

        byte[] temp1 = new byte[8];

        for (; i < 8; i++) {
            temp1[i] = bytes[i];
        }

        ByteBuffer.wrap(temp1).order(ByteOrder.BIG_ENDIAN).asCharBuffer().get(a.Head);

        //解析源地址代码
        byte[] temp2 = new byte[2];
        for (int num = 0; num < 2; num++) {
            temp2[num] = bytes[i++];
        }
        temp2 = reverseByteArr(temp2);
        short s = 0;
        short s0 = (short) (temp2[0] & 0xff);
        short s1 = (short) (temp2[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        a.Source = s;

        //解析目的地址代码

        byte[] temp3 = new byte[2];
        for (int num = 0; num < 2; num++) {
            temp3[num] = bytes[i++];
        }
        temp3 = reverseByteArr(temp3);
        short s2 = 0;
        short s21 = (short) (temp3[1] & 0xff);
        short s20 = (short) (temp3[0] & 0xff);
        s21 <<= 8;
        s2 = (short) (s20 | s21);
        a.Destination = s2;

        //解析信息类别码

        byte[] temp4 = new byte[2];
        temp4[0] = bytes[i++];
        temp4[1] = bytes[i++];
        temp4 = reverseByteArr(temp4);
        short s3 = 0;
        short s30 = (short) (temp4[0] & 0xff);
        short s31 = (short) (temp4[1] & 0xff);
        s31 <<= 8;
        s3 = (short) (s30 | s31);
        a.InfoCode = s3;

        //解析信息长度

        byte[] temp5 = new byte[4];
        for (int num = 0; num < 4; num++) {
            temp5[num] = bytes[i++];
        }
        temp5 = reverseByteArr(temp5);
        int s4 = 0;
        int s40 = temp5[0] & 0xff;
        int s41 = temp5[1] & 0xff;
        int s42 = temp5[2] & 0xff;
        int s43 = temp5[3] & 0xff;
        s43 <<= 24;
        s42 <<= 16;
        s41 <<= 8;
        s4 = s40 | s41 | s42 | s43;
        a.Length = s4;

        //解析信息交换时标

        byte[] temp6 = new byte[4];
        for (int num = 0; num < 4; num++) {
            temp6[num] = bytes[i++];
        }
        temp6 = reverseByteArr(temp6);
        int s51 = temp6[1] & 0xff;
        int s5 = 0;
        int s53 = temp6[3] & 0xff;
        int s50 = temp6[0] & 0xff;
        int s52 = temp6[2] & 0xff;
        s53 <<= 24;
        s52 <<= 16;
        s51 <<= 8;
        s5 = s50 | s51 | s52 | s53;
        a.TimeMask = s5;

        return a;

    }

    char[] ASD() {
        return this.Head;
    }

    short ASD1() {
        return this.Source;
    }

    short ASD2() {
        return this.Destination;
    }

    short ASD3() {
        return this.InfoCode;
    }

    int ASD4() {
        return this.Length;
    }

    int ASD5() {
        return this.TimeMask;
    }


}
