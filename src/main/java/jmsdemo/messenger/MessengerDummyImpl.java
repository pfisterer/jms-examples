package jmsdemo.messenger;

import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessengerDummyImpl implements Messenger {
	private static Logger log = LoggerFactory.getLogger(MessengerDummyImpl.class);

	@Override
	public void sendTextMessage(int address, String message) {
		log.info("Sending message {} to {}", message, address);
	}

	@Override
	public void sendAttachment(int address, String fileName, String mimeType, FileReader fileReader) {
		log.info("Sending file {} to {}", fileName, address);
	}

	@Override
	public void register(TextMessageListener listener) {
	}

	@Override
	public void register(AttachmentMessageListener listener) {
	}

}
