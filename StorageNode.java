import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.net.*;
import org.vertx.java.deploy.Verticle;

import com.google.protobuf.*;

import sg.edu.sutd.dss.protocol.cmd.CmdProtocol.Cmd;

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
//				Pump.createPump(socket, socket).start();
				socket.dataHandler(new Handler<Buffer>(){
					public void handle(Buffer buffer){
					//test in cmd
						try {
							logger.info("got: " + Cmd.parseFrom(buffer.getBytes()).toString());
							//write back ACK
							Cmd.Builder ack = Cmd.newBuilder();
							ack.setId(1);
							ack.setName("I am a Snode");
							ack.setType(Cmd.CmdType.CONTROL);
							ack.setDbgString("test ACK from snode");
							socket.write(new Buffer(ack.build().toByteArray()));
						} catch (InvalidProtocolBufferException e) {
							// TODO Auto-generated catch block
							logger.error(e);
						}						
					}
				});
			}
		});
		
		server.listen(port_default);
	}

}
