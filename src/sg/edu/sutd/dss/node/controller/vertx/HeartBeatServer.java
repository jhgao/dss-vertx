package sg.edu.sutd.dss.node.controller.vertx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.net.NetSocket;
import org.vertx.java.deploy.Verticle;

import sg.edu.sutd.dss.protocol.HeartBeat.StatReport;
import sg.edu.sutd.dss.protocol.HeartBeat.StatReportAck;
import sg.edu.sutd.dss.protocol.HeartBeat.StorageStat;
import sg.edu.sutd.dss.protocol.HeartBeat.UserStoreStat;

import com.google.protobuf.InvalidProtocolBufferException;

public class HeartBeatServer extends Verticle {
	private Logger logger;
	private ConcurrentMap<String, Buffer> snodesStats; // <node name, stat>

	public void start() {
		// log
		logger = container.getLogger();

		// configuration
		JsonObject config = container.getConfig();
		logger.info("[GeartBeatServer.java] config is " + config);
		int port_default = config.getNumber("port_default").intValue();

		// shared status
		snodesStats = vertx.sharedData().getMap("shared.snodes.stats");

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

							// update stats
							snodesStats = vertx.sharedData().getMap(
									"shared.snodes.stats");
							StorageStat ss = rp.getStat();
							ByteArrayOutputStream buf = new ByteArrayOutputStream();
							ss.writeTo(buf);
							snodesStats.put(rp.getSnodeId(),
									new Buffer(buf.toByteArray()));
							Iterator< Map.Entry<String,Buffer>> ite = snodesStats.entrySet().iterator();
							while(ite.hasNext()){
								Map.Entry<String, Buffer> e =ite.next();
								logger.info("live nodes: "+ e.getKey() + StorageStat.parseFrom(
										e.getValue().getBytes()).toString());
							}
							// send ACK
							StatReportAck.Builder ack = StatReportAck
									.newBuilder();
							UserStoreStat.Builder us = UserStoreStat
									.newBuilder();
							for (int i = 0; i < 5; ++i) {
								us.setUserId("userId" + i);
								us.setBlockCount(i);
								ack.addUserStoreStat(us);
							}
							ByteArrayOutputStream delimitedBytes = new ByteArrayOutputStream();
							ack.build().writeDelimitedTo(delimitedBytes);
							socket.write(new Buffer(delimitedBytes
									.toByteArray()));

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
