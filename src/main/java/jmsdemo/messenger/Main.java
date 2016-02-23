package jmsdemo.messenger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jmsdemo.OpenWire;
import jmsdemo.util.CommonStuff;

public class Main {
	private static final String regularExpression = "^(\\d+) ([MmdD]) (.+)$";
	private static final Pattern pattern = Pattern.compile(regularExpression);

	private static final Supplier<String> dummyLineSupplier = new Supplier<String>() {
		String[] zeilen = { "1 M Hallo Welt", "2 M Andere Nachricht" };
		int current = -1;

		@Override
		public String get() {
			if (++current >= zeilen.length)
				return null;
			return zeilen[current];
		}
	};
	private static final Supplier<String> sysinlineSupplier = new Supplier<String>() {
		Scanner sysIn = new Scanner(System.in);

		@Override
		public String get() {
			return sysIn.nextLine();
		}
	};

	public static void main(String[] args) throws FileNotFoundException, JMSException {
		Logger log = LoggerFactory.getLogger(OpenWire.class);
		String myAddress = "2";

		// Create a connection factory
		ActiveMQConnectionFactory connectionFactory = CommonStuff.setupAndGetConnectionFactory(args);

		// Create a connection
		Connection connection = connectionFactory.createConnection();
		connection.start();

		// Create a session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Messenger wa = new MessengerImpl(session.createTopic(myAddress), session);
		// Messenger wa = new MessengerDummyImpl();

		wa.register((String sender, String text) -> {
			System.out.println(sender + ": " + text);
		});

		wa.register((String sender, String fileName, String mimeType, byte[] message) -> {
			System.out.println(sender + ": File " + fileName + " (" + mimeType + ")");
		});

		Supplier<String> lineSupplier = sysinlineSupplier;
		// Supplier<String> lineSupplier = dummyLineSupplier;

		for (String line = lineSupplier.get(); line != null; line = lineSupplier.get()) {
			Matcher matcher = pattern.matcher(line);

			if (matcher.matches()) {
				int address = Integer.parseInt(matcher.group(1));
				char operation = matcher.group(2).charAt(0);
				String message = matcher.group(3);

				if (operation == 'M') {
					wa.sendTextMessage(address, message);
				} else if (operation == 'D') {
					wa.sendAttachment(address, "attachment.bin", "application/unknown", new FileReader(message));
				} else {
					log.warn("Line '{}' does not match format {}", line, regularExpression);
				}

			} else {
				System.out.println("Zeile nicht ok");
			}

		}

	}
}
