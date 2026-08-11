package com.example.badminton_team.controller;

import com.example.badminton_team.dao.ChatMessageDao;
import com.example.badminton_team.entity.ChatMessage;
import com.example.badminton_team.util.JwtUtil;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/chat/{token}")
public class ChatEndpoint {

    private static final Map<Integer, Session> onlineUsers = new ConcurrentHashMap<>();
    private static ChatMessageDao chatMessageDao;
    private static JwtUtil jwtUtil;   // 新增：静态注入 JwtUtil

    /** 由 Spring 启动时注入 ChatMessageDao */
    public static void setChatMessageDao(ChatMessageDao dao) {
        chatMessageDao = dao;
    }

    /** 由 Spring 启动时注入 JwtUtil */
    public static void setJwtUtil(JwtUtil util) {
        jwtUtil = util;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        try {
            Integer uid = jwtUtil.getIdFromToken(token);   // 使用注入的实例方法
            if (uid != null) {
                onlineUsers.put(uid, session);
            }
        } catch (Exception ignored) {}
    }

    @OnClose
    public void onClose(Session session, @PathParam("token") String token) {
        try {
            Integer uid = jwtUtil.getIdFromToken(token);
            if (uid != null) {
                onlineUsers.remove(uid);
            }
        } catch (Exception ignored) {}
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("token") String token) {
        try {
            // 1. 解析发送者 uid
            Integer senderUid = jwtUtil.getIdFromToken(token);
            if (senderUid == null) {
                System.out.println("无效 token，消息被忽略");
                return;
            }

            // 2. 解析 JSON 消息体
            com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSON.parseObject(message);
            Integer receiverUid = obj.getInteger("receiverUid");
            String content = obj.getString("content");

            if (receiverUid == null || content == null) {
                System.out.println("消息格式错误");
                return;
            }

            // 3. 保存到数据库
            ChatMessage msg = new ChatMessage();
            msg.setSenderUid(senderUid);
            msg.setReceiverUid(receiverUid);
            msg.setContent(content);
            msg.setCreateTime(new Date());
            chatMessageDao.insert(msg);              // 此时 msg.id 已被 MyBatis 回填

            // 4. 序列化为 JSON 字符串（用于推送）
            String msgJson = com.alibaba.fastjson.JSON.toJSONString(msg);

            // 5. 推送给接收方（如果在线）
            Session targetSession = onlineUsers.get(receiverUid);
            if (targetSession != null && targetSession.isOpen()) {
                targetSession.getBasicRemote().sendText(msgJson);
            }

            // ✅ 6. 推送给发送方自己（这样自己也能实时看到消息，不需要刷新）
            if (session.isOpen()) {
                session.getBasicRemote().sendText(msgJson);
            }

            System.out.println("消息已处理：sender=" + senderUid + ", receiver=" + receiverUid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}