package sg.edu.sutd.dss.node.controller.vertx;

import java.util.concurrent.ConcurrentMap;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.deploy.Verticle;

public class BackendServer extends Verticle {
	private Logger logger;
	private ConcurrentMap<String, Buffer> snodesStats; // <node name, stat>

	int port_default;
	String addr_default;

	Handler<HttpServerRequest> httpServerRequestHandler;

	@Override
	public void start() throws Exception {
		// log
		logger = container.getLogger();

		// configuration
		JsonObject config = container.getConfig();
		logger.info("[BackendServer.java] config is " + config);
		port_default = config.getNumber("port_default").intValue();
		addr_default = config.getString("addr_default");

		// shared status
		snodesStats = vertx.sharedData().getMap("shared.snodes.stats");

		// initialize handlers
		httpServerRequestHandler = new Handler<HttpServerRequest>() {
			public void handle(HttpServerRequest request) {
				logger.info("A backend request has arrived on the server!");
			}
		};

		// startup server
		HttpServer server = vertx.createHttpServer();
		server.requestHandler(httpServerRequestHandler);
		server.listen(port_default);
	}
}
