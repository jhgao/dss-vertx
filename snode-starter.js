load('vertx.js')

var logger = vertx.logger;
logger.info("[starter.js] start storage node ...");

var config = vertx.config;
logger.info("[starter.js] Config is " + JSON.stringify(config));


// Application config

var appConfig = {
    command_server_conf : {
        "port_default": 5678
    },
    block_server_conf : {
        "port_default": 6789 
    }
}

// Start the verticles that make up the app
var deploymentdID = vertx.deployVerticle("StorageNode",appConfig.command_server_conf,4,function(){
		logger.info("[starter.js] StorageNode deployed");
});

//vertx.undeployVerticle(did);
//var blockServerDeployId = vertx.deployWorkerVerticle("StorageNode.java", appConfig.block_server_conf);
