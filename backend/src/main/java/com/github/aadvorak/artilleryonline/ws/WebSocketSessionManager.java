package com.github.aadvorak.artilleryonline.ws;

import com.github.aadvorak.artilleryonline.entity.User;
import com.github.aadvorak.artilleryonline.error.exception.AuthenticationAppException;
import com.github.aadvorak.artilleryonline.security.ArtilleryOnlineUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WebSocketSessionManager extends WebSocketHandlerDecorator {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public WebSocketSessionManager(WebSocketHandler delegate, MeterRegistry registry) {
        super(delegate);
        Gauge.builder("artilleryonline.users.online", sessions::size)
                .register(registry);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var user = getUserFromSession(session);
        var oldSession = sessions.put(user.getId(), session);
        if (oldSession == null) {
            log.info("New WebSocket Session has been established for user {}", user.getNickname());
        } else {
            if (oldSession.isOpen()) {
                try {
                    oldSession.close(CloseStatus.NORMAL);
                } catch (IOException ignored) {
                }
            }
            log.info("Replaced WebSocket Session for user {}", user.getNickname());
        }
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        var user = getUserFromSession(session);
        sessions.remove(user.getId());
        log.info("Closed WebSocket Session for user {}", user.getNickname());
        super.afterConnectionClosed(session, closeStatus);
    }

    private User getUserFromSession(WebSocketSession session) {
        var principal = session.getPrincipal();
        if (principal instanceof UsernamePasswordAuthenticationToken authenticationToken) {
            if (authenticationToken.getPrincipal() instanceof ArtilleryOnlineUserDetails userDetails) {
                return userDetails.getUser();
            }
        }
        throw new AuthenticationAppException();
    }
}
