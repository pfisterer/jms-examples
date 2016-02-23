package jmsdemo.messenger;

import java.io.FileReader;
import java.util.LinkedList;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessengerImpl implements Messenger, MessageListener {
	private static Logger log = LoggerFactory.getLogger(MessengerImpl.class);
	private LinkedList<TextMessageListener> textListeners = new LinkedList<>();
	private LinkedList<AttachmentMessageListener> binaryListeners = new LinkedList<>();
	private Session session;
	private Topic myId;

	public MessengerImpl(Topic myId, Session session) {
		this.session = session;
		this.myId = myId;

		try {
			MessageConsumer consumer = session.createConsumer(myId);
			consumer.setMessageListener(this);
		} catch (JMSException e) {
			log.error("Error: " + e, e);
		}

	}

	@Override
	public void sendTextMessage(int address, String message) {
		log.info("Sending message {} to {}", message, address);

		try {
			Topic topic = session.createTopic("" + address);
			TextMessage textMessage = session.createTextMessage(message);
			textMessage.setJMSReplyTo(myId);

			MessageProducer producer = session.createProducer(topic);
			producer.send(textMessage);
			producer.close();

		} catch (JMSException e) {
			log.warn("Error: " + e, e);
		}

	}

	@Override
	public void sendAttachment(int address, String fileName, String mimeType, FileReader fileReader) {
		log.info("Sending file {} to {}", fileName, address);
		// TODO
	}

	@Override
	public void register(TextMessageListener listener) {
		textListeners.add(listener);
	}

	@Override
	public void register(AttachmentMessageListener listener) {
		binaryListeners.add(listener);
	}

	@Override
	public void onMessage(Message message) {

		if (message instanceof TextMessage) {
			TextMessage tm = (TextMessage) message;

			try {
				for (TextMessageListener listener : textListeners) {
					listener.receive("" + tm.getJMSReplyTo(), tm.getText());
				}
			} catch (JMSException e) {
				log.error("Error: " + e, e);
			}

		} else if (message instanceof BytesMessage) {
			BytesMessage bm = (BytesMessage) message;

			try {
				for (AttachmentMessageListener listener : binaryListeners) {
					listener.receive(bm.getJMSReplyTo().toString(), "", "", null);
				}
			} catch (JMSException e) {
				log.error("Error: " + e, e);
			}

		} else {
			log.warn("Unknown message type received: " + message);
		}

	}

}
