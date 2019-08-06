package org.baat.websockets.receiver;

import org.baat.core.transfer.chat.ChatWSMessage;
import org.baat.core.transfer.chat.ReplyMessage;
import org.baat.websockets.handler.UserSessionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ChatWSMessageReceiver {

	@Autowired
	UserSessionHandler userSessionHandler;

	public void receiveMessage(final String rawChatWSMessage) throws IOException {
		final ObjectMapper objectMapper = new ObjectMapper();
		final ChatWSMessage chatWSMessage = objectMapper.readValue(rawChatWSMessage, ChatWSMessage.class);

		final ReplyMessage replyMessage = new ReplyMessage(chatWSMessage.getSenderUserId(), chatWSMessage.getRecipientChannelId(), chatWSMessage.getTextMessage());
		final String rawReplyMessage = objectMapper.writeValueAsString(replyMessage);

		userSessionHandler.sendMessage(chatWSMessage.getRecipientUserTokens(), rawReplyMessage);
	}
}
