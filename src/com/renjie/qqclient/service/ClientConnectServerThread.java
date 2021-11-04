package com.renjie.qqclient.service;

import com.renjie.qqclient.utils.Utility;
import com.renjie.qqcommon.Message;
import com.renjie.qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author Renjie
 * @version 1.0
 * 客户端链接到服务端的线程，持续循环读写操作，正常通信的线程
 */
public class ClientConnectServerThread extends Thread {
    //该线程需要持有Socket
    private Socket socket;

    //构造器可以接受一个Socket对象
    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    //
    @Override
    public void run() {
        //因为Thread需要在后台和服务器通信，while持续循环
        while (true) {

            try {
                System.out.println("客户端线程，等待从读取从服务器端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                //如果服务器没有发送Message对象,线程会阻塞在这里
                Message message = (Message) ois.readObject();//主要就是通过Message的类型进行分开操作显示

                //如果是读取到的是 服务端返回的在线用户列表
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    //取出在线列表信息，并显示
                    //规定
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n=======当前在线用户列表========");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户: " + onlineUsers[i]);
                    }

                    //普通的聊天消息或离线消息
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES) ||
                        message.getMesType().equals(MessageType.MESSAGE_OFFLINE_MES)) {
                    //把从服务器转发的消息，显示到控制台即可
                    System.out.println("\n" + message.getSender()
                            + " 对 " + message.getGetter() + " 说: " + message.getContent() + " --[ " + message.getSendTime() + " ]--");

                    //群聊消息，与上面基本一样
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    //显示在客户端的控制台
                    System.out.println("\n" + message.getSender() + " 对大家说: " + message.getContent() + " --[ " + message.getSendTime() + " ]--");

                    //文件消息或离线文件消息
                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES) ||
                        message.getMesType().equals(MessageType.MESSAGE_OFFLINE_FILE)) {

                    //这里设置为：如果发送方有写放文件的位置就放在那个地方，如果是空格就让接收方选定保存位置
                    //这里还有一些异常问题，需要改进 【2021.10.31】

                    String dest = "";

                    if (message.getDest().equals(" ")) {
                        System.out.print("请输入您想存放此文件的位置[格式为 d:\\xx.jpg]: ");
                        dest = Utility.readString(50);
                    } else {
                        dest = message.getDest();
                    }

                    System.out.println("\n" + message.getSender() + " 给 " + message.getGetter()
                            + " 发文件: " + message.getSrc() + " 到我的电脑的目录 " + dest);

                    //取出message的文件字节数组，通过文件输出流写出到磁盘，追加写入
                    FileOutputStream fileOutputStream = new FileOutputStream(dest, true);
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n 保存文件成功~");

                    //重复登录消息
                } else if (message.getMesType().equals(MessageType.MESSAGE_ALREADY_LOGIN)) {
                    System.out.println("--------警告：有人重复登录您的账号，请及时更换密码-------");
                } else {
                    System.out.println("是其他类型的message, 暂时不处理....");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    //为了更方便的得到Socket
    public Socket getSocket() {
        return socket;
    }
}
