package jmsdemo.messenger;

import java.io.FileReader;

public interface Messenger {

	public interface TextMessageListener {
		void receive(String sender, String text);
	}

	public interface AttachmentMessageListener {
		void receive(String sender, String fileName, String mimeType, byte[] message);
	}

	void sendTextMessage(int address, String message);

	void sendAttachment(int address, String fileName, String mimeType, FileReader fileReader);

	void register(TextMessageListener listener);

	void register(AttachmentMessageListener listener);

}
