import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetClient;
import org.vertx.java.core.net.NetSocket;
import org.vertx.java.deploy.Verticle;

import com.google.protobuf.InvalidProtocolBufferException;

import sg.edu.sutd.dss.protocol.cmd.CmdProtocol.Cmd;

public class HeartBeater extends Verticle {
	private Logger logger;	//log
	private String conAddr;
	private int conPort;
	
	@Override
	public void start() throws Exception {
		// logger
		logger = container.getLogger();

		// configuration
		JsonObject config = container.getConfig();
		conAddr = config.getString("controller_addr");
		conPort = config.getNumber("controller_port").intValue();
		
		logger.info("[HeartBeater.java] config is " + config);

		// periodically send heart beat
		long timerID = vertx.setPeriodic(5000, new Handler<Long>() {
			public void handle(Long timerID) {
				doHeartBeat();
			}
		});
	}

	private void doHeartBeat() {
		// send
		NetClient hbskt = vertx.createNetClient();

		hbskt.exceptionHandler(new Handler<Exception>() {
			public void handle(Exception ex) {
				logger.error("(do Heart Beat)" + ex + currentTimeString());
			}
		});

		hbskt.connect(conPort, conAddr, new Handler<NetSocket>() {
			public void handle(NetSocket socket) {
				// add handler
				socket.dataHandler(new Handler<Buffer>() {
					public void handle(Buffer buffer) {
						// test in ACK
						try {
							logger.info("heart beat ACK: "
									+ Cmd.parseFrom(buffer.getBytes())
											.toString());
						} catch (InvalidProtocolBufferException e) {
							// TODO Auto-generated catch block
							logger.error(e);
						}
					}
				});

				try {
					Cmd.Builder hb = Cmd.newBuilder();
					hb.setId(5);
					hb.setName("HEART_BEAT");
					hb.setType(Cmd.CmdType.HEARTBEAT);
					hb.setDbgString("test Heart Beat from snode");

					ByteArrayOutputStream delimitedBytes = new ByteArrayOutputStream();
					hb.build().writeDelimitedTo(delimitedBytes);
					socket.write(new Buffer(delimitedBytes.toByteArray()));
					logger.info("Heart Beat sent. " + currentTimeString());
				} catch (IOException e) {
					logger.info("faild send Heart Beat");
					e.printStackTrace();
				}
			}
		});
	}
	
	private String currentTimeString(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cal.getTime());
	}
}
