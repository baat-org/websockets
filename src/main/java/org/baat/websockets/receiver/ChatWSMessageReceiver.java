package org.baat.websockets.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.baat.core.transfer.chat.ChatMessage;
import org.baat.core.transfer.chat.ReplyMessage;
import org.baat.websockets.handler.UserSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

@Component
public class ChatWSMessageReceiver {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChatWSMessageReceiver.class);

    @Autowired
    UserSessionHandler userSessionHandler;

    @Value("${channel_service_uri}")
    private String channelServiceURI;

    public void receiveMessage(final String rawChatMessage) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ChatMessage chatMessage = objectMapper.readValue(rawChatMessage, ChatMessage.class);

        try {
            final ReplyMessage replyMessage = new ReplyMessage(chatMessage.getSenderUserId(),
                    chatMessage.getRecipientChannelId(), chatMessage.getTextMessage());
            final String rawReplyMessage = objectMapper.writeValueAsString(replyMessage);

            if (chatMessage.getRecipientChannelId() != null) {
                final Set<Long> usersInChannel = getUsersInChannel(chatMessage.getRecipientChannelId());
                if (usersInChannel != null) {
                    for (final Long userId : usersInChannel) {
                        userSessionHandler.sendMessage(userId, rawReplyMessage);
                    }
                }
            } else {
                userSessionHandler.sendMessage(chatMessage.getRecipientUserId(), rawReplyMessage);
            }
        } catch (Exception exception) {
            LOGGER.error("Error sending message: " + exception);
        }
    }

    public Set<Long> getUsersInChannel(final Long channelId) {
        return new RestTemplate().exchange(
                URI.create(channelServiceURI + "/channels/" + channelId + "/users"), HttpMethod.GET, null, new ParameterizedTypeReference<Set<Long>>() {
                }).getBody();
    }
}
