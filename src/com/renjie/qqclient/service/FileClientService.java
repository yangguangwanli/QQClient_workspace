package com.renjie.qqclient.service;

import com.renjie.qqcommon.Message;
import com.renjie.qqcommon.MessageType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * @author Renjie
 * @version 1.0
 * 该类/对象完成 文件传输服务
 */
public class FileClientService {
    /**
     *
     * @param src 源文件
     * @param dest 把该文件传输到对方的哪个目录
     * @param senderId 发送用户id
     * @param getterId 接收用户id
     */

    //整个过程是：先从本地读入数据到程序中 -- > 把字节数组保存在message中 --> 给管道写入一个输出流
    public void sendFileToOne(String src, String dest, String senderId, String getterId) {

        //读取src文件  -->  message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);//设置文件类型 文件传输类型
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);
        message.setSendTime(new Date().toString());//发送时间设置到message对象

        //需要将文件读取
        FileInputStream fileInputStream = null;
        //源文件多大就给多大的存储字节数组空间，文件对象的length方法默认是long型，new byte[]里面默认是int型，这里强制转换
        byte[] fileBytes = new byte[(int)new File(src).length()];

        try {

            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);//将src文件读入到程序的字节数组
            //将文件对应的字节数组设置message
            message.setFileBytes(fileBytes);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //提示信息
        System.out.println("\n" + senderId + " 给 " + getterId + " 发送文件: " + src
                + " 到对方的电脑的目录 " + dest + " [ " + message.getSendTime() + " ]");
        //发送
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
