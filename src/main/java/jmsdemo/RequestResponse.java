package jmsdemo;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jmsdemo.util.CommonStuff;

public class RequestResponse {
	public static void main(String[] args) throws Exception {
		// Create a connection factory
		ActiveMQConnectionFactory connectionFactory = CommonStuff.setupAndGetConnectionFactory(args);
		Logger log = LoggerFactory.getLogger(OpenWire.class);

		// Startup
		Connection connection = connectionFactory.createConnection();
		connection.start();

		// Create a session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("queue1");

		// Message listener to receive requests
		MessageConsumer consumer = session.createConsumer(destination);
		consumer.setMessageListener(message -> {

			try {
				log.info("Creating response for message with reply-to ({}): {}", message.getJMSReplyTo(), message);

				MessageProducer producer = session.createProducer(message.getJMSReplyTo());
				TextMessage responseMessage = session.createTextMessage("Response Message");

				log.info("Sending response message: {}", responseMessage);
				producer.send(responseMessage);
				producer.close();

			} catch (JMSException e) {
				log.warn("Error in generating response: " + e, e);
			}

		});

		// Message listener to receive requests
		Destination temporaryDestination = session.createTemporaryQueue();
		Object lock = new Object();

		MessageConsumer responseConsumer = session.createConsumer(temporaryDestination);
		responseConsumer.setMessageListener(responseMessage -> {
			log.debug("Received response: {}", responseMessage);
			synchronized (lock) {
				lock.notifyAll();
			}
		});

		// Send a request with the temporary destination as reply-to field
		Destination destination2 = session.createQueue("queue1");

		MessageProducer producer = session.createProducer(destination2);
		TextMessage message = session.createTextMessage("Request Message");
		message.setJMSReplyTo(temporaryDestination);
		log.info("Sending request message: {}", message);
		producer.send(message);
		producer.close();

		synchronized (lock) {
			lock.wait(10000);
		}

		consumer.close();
		responseConsumer.close();
		session.close();
		connection.close();
	}
}
