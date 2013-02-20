load('vertx.js')

function loginfo(str){
	logger.info("[controller-starter.js] " + str);
}

var logger = vertx.logger;
loginfo("start node ...");

var config = vertx.config;
loginfo("Config is" + JSON.stringify(config));


// Application config

var appConfig = {
	client_server_conf :{
		"port_default" : 6000
	},
    heartbeat_server_conf : {
        "port_default": 5000
    }
}

// Start the verticles that make up the controller
var deploymentdID = vertx.deployVerticle("Controller",appConfig.heartbeat_server_conf,4,function(){
		loginfo("Controller deployed");
});

//vertx.undeployVerticle(did);
//var blockServerDeployId = vertx.deployWorkerVerticle("StorageNode.java", appConfig.block_server_conf);
