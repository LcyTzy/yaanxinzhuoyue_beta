package com.zhantu.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/chat/{userId}")
public class ChatWebSocket {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessions.put(userId, session);
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        sessions.remove(userId);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        try {
            Map<String, Object> msg = mapper.readValue(message, Map.class);
            String to = (String) msg.get("to");
            Session targetSession = sessions.get(to);
            if (targetSession != null && targetSession.isOpen()) {
                msg.put("from", userId);
                targetSession.getBasicRemote().sendText(mapper.writeValueAsString(msg));
            }
            Session adminSession = sessions.get("admin");
            if (adminSession != null && adminSession.isOpen() && !"admin".equals(userId)) {
                msg.put("from", userId);
                adminSession.getBasicRemote().sendText(mapper.writeValueAsString(msg));
            }
        } catch (IOException e) {
            // ignore
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        // ignore
    }
}