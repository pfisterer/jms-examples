package jmsdemo.util;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class CommonStuff {

	public static ActiveMQConnectionFactory setupAndGetConnectionFactory(String[] args) {
		// Logging configuration
		SLF4JBridgeHandler.removeHandlersForRootLogger(); // (since SLF4J 1.6.5)
		SLF4JBridgeHandler.install();

		// Parse command line params
		if (args.length != 3) {
			System.err.println("Provide <username> <password> <server> as parameters (e.g., demo1 demo1 tcp://127.0.0.1:61616)");
			System.exit(1);
		}

		String user = args[0];
		String password = args[1];
		String server = args[2];

		// Get a connection
		return new ActiveMQConnectionFactory(user, password, server);

	}

}
