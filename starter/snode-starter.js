load('vertx.js')

function loginfo(str){
	logger.info("[snode-starter.js] " + str);
}

var logger = vertx.logger;
loginfo("start storage node ...");

var config = vertx.config;
loginfo("Config is " + JSON.stringify(config));


// Application config

var appConfig = {
    command_server_conf : {
        "port_default": 5678
    },
    block_server_conf : {
        "port_default": 6789 
    },
	heart_beater_conf: {
	"controller_addr" : "localhost",
	"controller_port" : 5000
	}
}

// Start the verticles that make up the app
var deployID_snode = vertx.deployVerticle("sg.edu.sutd.dss.node.storage.vertx.StorageNode",appConfig.command_server_conf,4,function(){
		loginfo("StorageNode deployed");
});

var deployID_heartbeater = vertx.deployVerticle("sg.edu.sutd.dss.node.storage.vertx.HeartBeater",appConfig.heart_beater_conf,1,function(){
		loginfo("HeartBeater deployed");
});

//vertx.undeployVerticle(did);
//var blockServerDeployId = vertx.deployWorkerVerticle("StorageNode.java", appConfig.block_server_conf);
