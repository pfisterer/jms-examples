package de.farberg.apollo.examples.util;

import org.apache.activemq.apollo.broker.Broker;
import org.slf4j.bridge.SLF4JBridgeHandler;

import de.farberg.apollo.factory.ApolloConfigurationBuilder;
import de.uniluebeck.itm.util.logging.LogLevel;
import de.uniluebeck.itm.util.logging.Logging;

public class CommonStuff {

	public static ApolloConfigurationBuilder setup() throws Exception {
		// Set up logging
		SLF4JBridgeHandler.removeHandlersForRootLogger(); // (since SLF4J 1.6.5)
		SLF4JBridgeHandler.install();
		Logging.setLoggingDefaults(LogLevel.DEBUG);

		return new ApolloConfigurationBuilder().queueNonPersistentNoExpiration("queue1").externalAccess()
				.authenticatedHeader("authenticated-user-name").jaasAuthentication("src/main/resources/demo.jaas")
				.alwaysTrueAuthorization();

	}

	public static void teardownAndExit(Broker broker) {
		broker.stop(new Runnable() {

			@Override
			public void run() {
				System.exit(0);
			}
		});
	}
}
