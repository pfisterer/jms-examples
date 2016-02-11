package jmsdemo;

import java.util.LinkedList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jmsdemo.util.CommonStuff;

public class Topics {
	public static void main(String[] args) throws Exception {
		// Create a connection factory
		ActiveMQConnectionFactory connectionFactory = CommonStuff.setupAndGetConnectionFactory(args);
		Logger log = LoggerFactory.getLogger(OpenWire.class);

		// Create a connection
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

		// Cleanup resources
		consumers.forEach(consumer -> {
			try {
				consumer.close();
			} catch (Exception e) {
			}
		});
		session.close();
		connection.close();

	}
}
