package org.baat.websockets.handler;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserSessionHandler extends AbstractWebSocketHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserSessionHandler.class);

    private final static Map<String, Set<WebSocketSession>> SESSIONS_BY_USER_TOKENS = new ConcurrentHashMap<>();
    private final String userServiceURI;

    public UserSessionHandler(final String userServiceURI) {
        this.userServiceURI = userServiceURI;
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
        final String userToken = message.getPayload();

        if (!validUserToken(userToken)) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("A valid user token must be passed"));
            return;
        }

        addSession(userToken, session);
    }

    @Override
    protected void handleBinaryMessage(final WebSocketSession session, final BinaryMessage message) throws Exception {
        session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Message sending through WS not allowed"));
    }

    @Override
    protected void handlePongMessage(final WebSocketSession session, final PongMessage message) throws Exception {
        // do nothing
    }

    @Override
    public void handleTransportError(final WebSocketSession session, final Throwable exception) throws Exception {
        removeSession(session);
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
        removeSession(session);
    }

    public void sendMessage(final Set<String> userTokens, final String message) throws IOException {
        for (final String userToken : userTokens) {
            final Set<WebSocketSession> sessions = SESSIONS_BY_USER_TOKENS.get(userToken);
            if (sessions != null) {
                for (final WebSocketSession session : sessions) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    private boolean validUserToken(final String userToken) {
        try {
            return BooleanUtils.isTrue(new RestTemplate().getForObject(
                    URI.create(userServiceURI + "/validateUserToken/" + userToken), Boolean.class));
        } catch (Exception exception) {
            LOGGER.error("Error validating user token {}", userToken, exception);
            return false;
        }
    }

    private void addSession(final String userToken, final WebSocketSession session) {
        SESSIONS_BY_USER_TOKENS.computeIfAbsent(userToken, k -> new HashSet<>()).add(session);
    }

    private void removeSession(final WebSocketSession session) {
        for (final Map.Entry<String, Set<WebSocketSession>> sessionByUserToken : SESSIONS_BY_USER_TOKENS.entrySet()) {
            final String userToken = sessionByUserToken.getKey();
            final Set<WebSocketSession> sessions = sessionByUserToken.getValue();

            sessions.remove(session);

            if (sessions.isEmpty()) {
                SESSIONS_BY_USER_TOKENS.remove(userToken);
            }
        }
    }
}