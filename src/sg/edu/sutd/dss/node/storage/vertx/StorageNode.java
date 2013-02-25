package sg.edu.sutd.dss.node.storage.vertx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.SimpleHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.net.NetServer;
import org.vertx.java.core.net.NetSocket;
import org.vertx.java.core.streams.Pump;
import org.vertx.java.core.streams.ReadStream;
import org.vertx.java.core.streams.WriteStream;
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

		// server : wait for client storage request
		NetServer server = vertx.createNetServer();

		server.connectHandler(new Handler<NetSocket>() {
			public void handle(final NetSocket socket) {
				socket.dataHandler(new Handler<Buffer>() {
					public void handle(Buffer buffer) {
						// test request command
						try {
							final StoreRequest req = StoreRequest
									.parseDelimitedFrom(new ByteArrayInputStream(
											buffer.getBytes()));
							logger.info("got: " + req.toString());
							//TODO test request
							
							//try receive to cache file
							final String fp =  "./FileCache" + req.getEncBlockDesc().getPath();
							final String fn = req.getEncBlockDesc().getEncBlockId();
							final String fpn = fp + "/" + fn;
							//mkdir
							vertx.fileSystem().mkdir(fp, true, new AsyncResultHandler<Void>() {
							    public void handle(AsyncResult ar) {
							        if (ar.exception == null) {                
							            logger.info("Directory created ok");                
							        } else {
							            logger.error("Failed to mkdir", ar.exception);
							        }
							    }
							});
							
							//open and save file
							vertx.fileSystem().createFile(fpn, new AsyncResultHandler<Void>() {

										@Override
										public void handle(
												AsyncResult<Void> arg0) {

											vertx.fileSystem().open(fpn, new AsyncResultHandler<AsyncFile>() {
											    public void handle(AsyncResult<AsyncFile> ar) {
											        if (ar.exception == null) {    
											            AsyncFile asyncFile = ar.result;
											            WriteStream ws = (WriteStream) asyncFile.getWriteStream();
											            ReadStream rs = socket;
											            final Pump p = Pump.createPump(rs, ws);
											            p.start();
											            rs.endHandler(new SimpleHandler() {
											                public void handle() {
											                	p.stop();
											                	logger.info("In stream from client end. ");
											                }
											              }); 

														try {
															// write back ACK: approved
															StoreAck.Builder ack = StoreAck.newBuilder();
															ack.setReqSn(req.getReqSn());
															ack.setIsApproved(true);

															ByteArrayOutputStream delimitedBytes = new ByteArrayOutputStream();
															ack.build().writeDelimitedTo(delimitedBytes);
															socket.write(new Buffer(delimitedBytes
																	.toByteArray()));
														} catch (IOException e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
											        } else {
											            logger.error("Failed to open file", ar.exception);
											        }
											    }
											});
											
										}
							});				
							
						} catch (InvalidProtocolBufferException e) {
							// TODO Auto-generated catch block
							logger.error(e);
						} catch (IOException e) {
							logger.error(e);
						}
					}
				});
			}
		});

		server.listen(port_default);
	}

}
