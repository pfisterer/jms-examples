package de.farberg.apollo.examples;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.apollo.broker.Broker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.farberg.apollo.examples.util.CommonStuff;
import de.farberg.apollo.factory.ApolloEmbeddedFactory;

public class OpenWire {

	public static void main(String[] args) throws Exception {
		Broker broker = ApolloEmbeddedFactory.start(CommonStuff.setup().openWire(8889).build());
		Logger log = LoggerFactory.getLogger(OpenWire.class);

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("demo1", "demo1", "tcp://localhost:8889");

		// Create a connection
		Connection connection = connectionFactory.createConnection();
		connection.start();
		log.debug("Started Connection...");

		// Create a session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("queue1");

		// Add a message listener to this queue
		MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener(message -> {
			log.info("Received message: {}", message);
		});

		// Produce some messages
		MessageProducer producer = session.createProducer(destination);
		for (int i = 0; i < 10; i++) {
			TextMessage message = session.createTextMessage("Message #" + i);
			log.debug("Sending message #" + i);
			producer.send(message);
			Thread.sleep(100);
		}

		producer.close();
		consumer.close();
		session.close();
		connection.close();

		CommonStuff.teardownAndExit(broker);
	}
}
