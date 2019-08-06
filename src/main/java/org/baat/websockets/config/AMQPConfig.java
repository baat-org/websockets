package org.baat.websockets.config;

import org.baat.websockets.receiver.ChatWSMessageReceiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class AMQPConfig {
	private final static String CHAT_WS_EXCHANGE_NAME = "chat-ws-message-exchange";
	private final static String CHAT_WS_QUEUE_NAME = UUID.randomUUID().toString();

	@Bean
	public Queue chatWSQueue() {
		return new Queue(CHAT_WS_QUEUE_NAME, false, false, true);
	}

	@Bean
	public FanoutExchange chatWSExchange() {
		return new FanoutExchange(CHAT_WS_EXCHANGE_NAME);
	}

	@Bean
	public Binding binding(final Queue chatWSQueue, final FanoutExchange chatWSExchange) {
		return BindingBuilder.bind(chatWSQueue).to(chatWSExchange);
	}

	@Bean
	public SimpleMessageListenerContainer chatWSMessageListenerContainer(final ConnectionFactory connectionFactory,
																		 final MessageListenerAdapter listenerAdapter) {
		final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(CHAT_WS_QUEUE_NAME);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public MessageListenerAdapter chatWSMessageListenerAdapter(final ChatWSMessageReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
}
