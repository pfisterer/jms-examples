package de.farberg.apollo.examples;

import java.util.Map.Entry;

import org.apache.activemq.apollo.broker.Broker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asia.stampy.client.message.send.SendMessage;
import asia.stampy.client.message.subscribe.SubscribeMessage;
import asia.stampy.client.netty.ClientNettyMessageGateway;
import asia.stampy.common.gateway.HostPort;
import asia.stampy.common.gateway.StampyMessageListener;
import asia.stampy.common.message.StampyMessage;
import asia.stampy.common.message.StompMessageType;
import asia.stampy.server.message.message.MessageMessage;
import de.farberg.apollo.examples.util.CommonStuff;
import de.farberg.apollo.examples.util.StompClientStampyFactory;
import de.farberg.apollo.factory.ApolloEmbeddedFactory;

public class StampyStomp {

	public static void main(String[] args) throws Exception {
		Broker broker = ApolloEmbeddedFactory.start(CommonStuff.setup().stomp(8889).build());
		Logger log = LoggerFactory.getLogger(StampyStomp.class);

		// Create a listener
		ClientNettyMessageGateway listener = StompClientStampyFactory.createClientConnection("localhost", 8889, "demo", "demo");
		listener.broadcastMessage(new SubscribeMessage("/queue/queue1", "sub-0"));

		listener.addMessageListener(new StampyMessageListener() {

			@Override
			public void messageReceived(StampyMessage<?> genericMessage, HostPort hostPort) throws Exception {
				MessageMessage message = (MessageMessage) genericMessage;

				log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
				for (Entry<String, String> entry : message.getHeader().getHeaders().entrySet()) {
					log.info("Message header: key({}) = value({})", entry.getKey(), entry.getValue());
				}
				log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			}

			@Override
			public boolean isForMessage(StampyMessage<?> message) {
				return true;
			}

			@Override
			public StompMessageType[] getMessageTypes() {
				return new StompMessageType[] { StompMessageType.MESSAGE };
			}
		});

		// Create a local producer
		ClientNettyMessageGateway producer = StompClientStampyFactory.createClientConnection("localhost", 8889, "demo", "demo");

		SendMessage message = new SendMessage();
		message.getHeader().setDestination("/queue/queue1");
		message.setBody("hello world");
		message.getHeader().addHeader("ttl", "5000");
		producer.sendMessage(message, producer.getConnectedHostPorts().iterator().next());

		// Shutdown
		Thread.sleep(2000);
		listener.shutdown();
		producer.shutdown();
		CommonStuff.teardownAndExit(broker);
	}
}
