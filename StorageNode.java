import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.net.*;
import org.vertx.java.core.streams.Pump;
import org.vertx.java.deploy.Verticle;

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
				//Pump.createPump(socket, socket).start();
				socket.dataHandler(new Handler<Buffer>(){
					public void handle(Buffer buffer){
						logger.info("got " + buffer.length() + " bytes");
					}
				});
			}
		});
		
		server.listen(port_default);
	}

}
