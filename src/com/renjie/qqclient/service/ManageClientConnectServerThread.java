package com.renjie.qqclient.service;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Renjie
 * @version 1.0
 * 该类管理客户端连接到服务器端的线程的类
 * 如果只为通信类的线程就不用管理器了，这里为了后续扩展性，这里特别使用一个ConcurrentHashMap来管理线程，增强其扩展性
 * ConcurrentHashMap线程安全
 */
public class ManageClientConnectServerThread {
    //保存方式为 userId -- Thread
    //我们把多个线程放入一个HashMap集合，key 就是用户id, value 就是线程
    private static ConcurrentHashMap<String, ClientConnectServerThread> hm = new ConcurrentHashMap<>();

    //将某个线程加入到集合
    public static void addClientConnectServerThread(String userId, ClientConnectServerThread clientConnectServerThread) {
        hm.put(userId, clientConnectServerThread);
    }

    //通过userId 可以得到对应线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId) {
        return hm.get(userId);
    }

}
