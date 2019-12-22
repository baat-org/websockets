package org.baat.websockets.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.baat.core.transfer.chat.ChatMessage;
import org.baat.core.transfer.chat.ReplyMessage;
import org.baat.websockets.handler.UserSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ChatWSMessageReceiver {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChatWSMessageReceiver.class);

    @Autowired
    UserSessionHandler userSessionHandler;

    public void receiveMessage(final String rawChatMessage) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ChatMessage chatMessage = objectMapper.readValue(rawChatMessage, ChatMessage.class);

        try {
            final ReplyMessage replyMessage = new ReplyMessage(userSessionHandler.findUserForToken(chatMessage.getSenderUserToken()).getId(),
                    chatMessage.getRecipientChannelId(), chatMessage.getTextMessage());
            final String rawReplyMessage = objectMapper.writeValueAsString(replyMessage);

            userSessionHandler.sendMessage(chatMessage.getRecipientUserId(), rawReplyMessage);
        } catch (Exception exception) {
            LOGGER.error("Error sending message: " + exception);
        }
    }
}
