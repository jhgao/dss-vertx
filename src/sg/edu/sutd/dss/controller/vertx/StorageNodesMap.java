package sg.edu.sutd.dss.controller.vertx;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.deploy.Verticle;

public class StorageNodesMap extends Verticle {

	@Override
	public void start() throws Exception {
		// log
		final Logger logger = container.getLogger();

		// configuration
		JsonObject config = container.getConfig();
		logger.info("[StorageNodesMap.java] config is " + config);
	}

}
