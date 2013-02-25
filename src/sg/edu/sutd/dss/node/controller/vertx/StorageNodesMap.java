package sg.edu.sutd.dss.node.controller.vertx;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.deploy.Verticle;

public class StorageNodesMap extends Verticle {
	
	private ConcurrentMap<String, Buffer> snodesStats;	//<node name, stat>
	private Logger logger;

	@Override
	public void start() throws Exception {
		// log
		logger = container.getLogger();

		// configuration
		JsonObject config = container.getConfig();
		logger.info("[StorageNodesMap.java] config is " + config);
		
		//known storage nodes
		snodesStats = vertx.sharedData().getMap("shared.snodes.stats");
		Set<String> nodesName = snodesStats.keySet();
		
		while(nodesName.iterator().hasNext()){
			String nn = nodesName.iterator().next();
			logger.info("live nodes:"+ nn + snodesStats.get(nn).toString());
		}
	}

}
