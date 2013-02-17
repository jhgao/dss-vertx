import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.net.*;
import org.vertx.java.deploy.Verticle;

import sg.edu.sutd.dss.protocol.cmd.CmdProtocol.Cmd;

public class ClientNode extends Verticle {
	public void start() {
		// log
		final Logger logger = container.getLogger();

		// configuration
		JsonObject config = container.getConfig();
		logger.info("[ClientNode.java] config is " + config);
		String snode_ip = config.getString("snode_ip");
		int snode_cmd_port = config.getNumber("snode_cmd_port").intValue();
		int snode_data_port = config.getNumber("snode_data_port").intValue();

		// command client
		NetClient cmdclt = vertx.createNetClient();

		cmdclt.exceptionHandler(new Handler<Exception>() {
			public void handle(Exception ex) {
				logger.error("Failed to connect", ex);
			}
		});

		cmdclt.connect(snode_cmd_port, snode_ip, new Handler<NetSocket>() {
			public void handle(NetSocket socket) {
				logger.info("Cmd Connected");
				// test out cmd
				Cmd.Builder cmd = Cmd.newBuilder();
				cmd.setId(0);
				cmd.setName("TEST_CMD");
				cmd.setType(Cmd.CmdType.CONTROL);
				cmd.setDbgString("TEST dbg string");

				socket.write(new Buffer(cmd.build().toByteArray()));

			}
		});

	}

}
