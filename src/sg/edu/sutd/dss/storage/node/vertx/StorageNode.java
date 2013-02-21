package sg.edu.sutd.dss.storage.node.vertx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.net.NetSocket;
import org.vertx.java.deploy.Verticle;

import sg.edu.sutd.dss.protocol.SnodeProtoc.StoreAck;
import sg.edu.sutd.dss.protocol.SnodeProtoc.StoreRequest;

import com.google.protobuf.InvalidProtocolBufferException;

public class StorageNode extends Verticle {
	public void start() {
		// log
		final Logger logger = container.getLogger();

		// configuration
		JsonObject config = container.getConfig();
		logger.info("[StorageNode.java] config is " + config);
		int port_default = config.getNumber("port_default").intValue();

		// server
		NetServer server = vertx.createNetServer();

		server.connectHandler(new Handler<NetSocket>() {
			public void handle(final NetSocket socket) {
				// Pump.createPump(socket, socket).start();
				logger.info("some one connected");
				socket.dataHandler(new Handler<Buffer>() {
					public void handle(Buffer buffer) {
						// test request command
						try {
							StoreRequest req = StoreRequest.parseDelimitedFrom(
									new ByteArrayInputStream(buffer
											.getBytes()));
							logger.info("got: "
									+ req.toString());
							// write back ACK
							StoreAck.Builder ack = StoreAck.newBuilder();
							ack.setReqSn(req.getReqSn());
							ack.setIsApproved(true);

							ByteArrayOutputStream delimitedBytes = new ByteArrayOutputStream();
							ack.build().writeDelimitedTo(delimitedBytes);
							socket.write(new Buffer(delimitedBytes
									.toByteArray()));
						} catch (InvalidProtocolBufferException e) {
							// TODO Auto-generated catch block
							logger.error(e);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
						}
					}
				});
			}
		});

		server.listen(port_default);
	}

}
