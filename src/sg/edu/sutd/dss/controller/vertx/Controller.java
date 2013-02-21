package sg.edu.sutd.dss.controller.vertx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.net.NetSocket;
import org.vertx.java.deploy.Verticle;

import sg.edu.sutd.dss.protocol.HeartBeat.StatReport;
import sg.edu.sutd.dss.protocol.HeartBeat.StatReportAck;
import sg.edu.sutd.dss.protocol.HeartBeat.UserStoreStat;

import com.google.protobuf.InvalidProtocolBufferException;

public class Controller extends Verticle {
	public void start() {
		// log
		final Logger logger = container.getLogger();

		// configuration
		JsonObject config = container.getConfig();
		logger.info("[Controller.java] config is " + config);
		int port_default = config.getNumber("port_default").intValue();

		// server
		NetServer server = vertx.createNetServer();

		server.connectHandler(new Handler<NetSocket>() {
			public void handle(final NetSocket socket) {
				// Pump.createPump(socket, socket).start();
				socket.dataHandler(new Handler<Buffer>() {
					public void handle(Buffer buffer) {
						// test heart beat
						try {
							StatReport rp = StatReport
									.parseDelimitedFrom(new ByteArrayInputStream(
											buffer.getBytes()));
							// storage node status
							logger.info("[>>Heart Beat]" + currentTimeString());
							logger.info(rp.toString());
							
							StatReportAck.Builder ack = StatReportAck.newBuilder();
							UserStoreStat.Builder us = UserStoreStat.newBuilder();
							for( int i = 0; i < 5; ++i){
								us.setUserId("userId"+i);
								us.setBlockCount(i);
								ack.addUserStoreStat(us);
							}
							ByteArrayOutputStream delimitedBytes = new ByteArrayOutputStream();
							ack.build().writeDelimitedTo(delimitedBytes);
							socket.write(new Buffer(delimitedBytes.toByteArray()));
							
						} catch (InvalidProtocolBufferException e) {
							logger.error("Invalid StatReport.");
							logger.error(e);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});

		server.listen(port_default);
	}

	private String currentTimeString() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cal.getTime());
	}
}
