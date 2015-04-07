package de.farberg.apollo.examples;

import java.util.LinkedList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.apollo.broker.Broker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.farberg.apollo.examples.util.CommonStuff;
import de.farberg.apollo.factory.ApolloEmbeddedFactory;

public class Topics {
	public static void main(String[] args) throws Exception {
		Broker broker = ApolloEmbeddedFactory.start(CommonStuff.setup().openWire(8889).build());
		Logger log = LoggerFactory.getLogger(OpenWire.class);

		// Create a connection
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("demo1", "demo1", "tcp://localhost:8889");
		Connection connection = connectionFactory.createConnection();
		connection.start();

		// Create a session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create a topic
		Topic demoTopic = session.createTopic("demotopic");

		// Register some consumers
		List<MessageConsumer> consumers = new LinkedList<>();

		{
			MessageConsumer consumer = session.createConsumer(demoTopic);
			consumers.add(consumer);
			consumer.setMessageListener(message -> {
				try {
					log.info("#1 Received: {}", ((TextMessage) message).getText());
				} catch (Exception e) {
					log.warn("Exception: {}", e);
				}
			});
		}
		{
			MessageConsumer consumer = session.createConsumer(demoTopic);
			consumers.add(consumer);
			consumer.setMessageListener(message -> {
				try {
					log.info("#2 Received: {}", ((TextMessage) message).getText());
				} catch (Exception e) {
					log.warn("Exception: {}", e);
				}
			});
		}

		// Produce some messages
		MessageProducer producer = session.createProducer(demoTopic);
		for (int i = 0; i < 10; i++) {
			TextMessage message = session.createTextMessage("Message #" + i);
			log.debug("Sending message #" + i);
			producer.send(message);
			Thread.sleep(100);
		}

		consumers.forEach(consumer -> {
			try {
				consumer.close();
			} catch (Exception e) {
			}
		});
		session.close();
		connection.close();

		CommonStuff.teardownAndExit(broker);
	}
}
