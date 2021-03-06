package jmsdemo;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jmsdemo.util.CommonStuff;

public class OpenWire {

	public static void main(String[] args) throws Exception {
		// Create a connection factory
		ActiveMQConnectionFactory connectionFactory = CommonStuff.setupAndGetConnectionFactory(args);
		Logger log = LoggerFactory.getLogger(OpenWire.class);

		// Create a connection
		Connection connection = connectionFactory.createConnection();
		connection.start();
		log.debug("Started Connection...: " + connection.getMetaData().getJMSProviderName());

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
			Thread.sleep(300);
		}

		producer.close();
		consumer.close();
		session.close();
		connection.close();

	}
}
