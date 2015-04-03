package de.farberg.apollo.examples.util;

import asia.stampy.client.listener.validate.ClientMessageValidationListener;
import asia.stampy.client.message.connect.ConnectMessage;
import asia.stampy.client.netty.ClientNettyChannelHandler;
import asia.stampy.client.netty.ClientNettyMessageGateway;
import asia.stampy.client.netty.connected.NettyConnectedMessageListener;
import asia.stampy.client.netty.disconnect.NettyDisconnectListenerAndInterceptor;
import asia.stampy.common.gateway.HostPort;
import asia.stampy.common.gateway.SecurityMessageListener;
import asia.stampy.common.heartbeat.HeartbeatContainer;
import asia.stampy.common.message.StampyMessage;
import asia.stampy.common.message.StompMessageType;

public class StompClientStampyFactory {

	public static ClientNettyMessageGateway createClientConnection(String host, int port, String username, String password) {
		// Create a stampy client
		HeartbeatContainer heartbeatContainer = new HeartbeatContainer();

		ClientNettyMessageGateway gateway = new ClientNettyMessageGateway();
		gateway.setHost(host);
		gateway.setPort(port);
		gateway.setHeartbeat(1000);

		ClientNettyChannelHandler channelHandler = new ClientNettyChannelHandler();
		channelHandler.setGateway(gateway);
		channelHandler.setHeartbeatContainer(heartbeatContainer);

		// No security required
		gateway.addMessageListener(new SecurityMessageListener() {

			@Override
			public StompMessageType[] getMessageTypes() {
				return StompMessageType.values();
			}

			@Override
			public boolean isForMessage(StampyMessage<?> message) {
				return false;
			}

			@Override
			public void messageReceived(StampyMessage<?> message, HostPort hostPort) throws Exception {

			}
		});

		gateway.addMessageListener(new ClientMessageValidationListener());

		NettyConnectedMessageListener cml = new NettyConnectedMessageListener();
		cml.setHeartbeatContainer(heartbeatContainer);
		cml.setGateway(gateway);
		gateway.addMessageListener(cml);

		NettyDisconnectListenerAndInterceptor disconnect = new NettyDisconnectListenerAndInterceptor();
		disconnect.setCloseOnDisconnectMessage(false);
		gateway.addMessageListener(disconnect);
		gateway.addOutgoingMessageInterceptor(disconnect);
		disconnect.setGateway(gateway);

		gateway.setHandler(channelHandler);

		try {
			gateway.connect();
			ConnectMessage connectMessage = new ConnectMessage(host);
			connectMessage.getHeader().setLogin(username);
			connectMessage.getHeader().setPasscode(password);
			gateway.broadcastMessage(connectMessage);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return gateway;
	}
}