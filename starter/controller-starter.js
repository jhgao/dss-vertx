load('vertx.js')

function loginfo(str){
	logger.info("[controller-starter.js] " + str);
}

var logger = vertx.logger;
loginfo("start node ...");

var config = vertx.config;
loginfo("Config is" + JSON.stringify(config));


// Application config

var ControllerConfig = {
	client_server_conf :{
		"port_default" : 6000
	},
    	heartbeat_server_conf : {
		"port_default": 5000
	},
	storage_nodes_map_conf : {
		"type" : "tree"
	},
	backend_server_conf : {
		"port_default" : 8800,
		"addr_default" : "localhost"
	}
}

// sotrage nodes map sg.edu.sutd.dss.node.controller.vertx
var deploymentdID = vertx.deployVerticle("sg.edu.sutd.dss.node.controller.vertx.StorageNodesMap",ControllerConfig.storage_nodes_map_conf,1,function(){
		loginfo("Storeage-nodes-map deployed");
});

// Start the verticles that make up the controller
var deploymentdID = vertx.deployVerticle("sg.edu.sutd.dss.node.controller.vertx.HeartBeatServer",ControllerConfig.heartbeat_server_conf,4,function(){
		loginfo("Heart-beat-server deployed");
});

// sotrage nodes map sg.edu.sutd.dss.node.controller.vertx

var deploymentdID = vertx.deployVerticle("sg.edu.sutd.dss.node.controller.vertx.BackendServer",ControllerConfig.backend_server_conf,1,function(){
		loginfo("Backend-server deployed");
});

//vertx.undeployVerticle(did);
//var blockServerDeployId = vertx.deployWorkerVerticle("StorageNode.java", ControllerConfig.block_server_conf);
