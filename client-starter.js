load('vertx.js')

var logger = vertx.logger;
logger.info("[starter.js] start storage node ...");

var config = vertx.config;
logger.info("[starter.js] Config is " + JSON.stringify(config));


// Application config

var appConfig = {
    client_conf : {
        "port_default": 5678
    },
	cmd_protoc_test_conf : {
		"snode_ip": "localhost",
		"snode_cmd_port": 5678,
		"snode_data_port": 6789
	}
}

// Start the verticles that make up the app
var deploymentdID = vertx.deployVerticle("ClientNode",appConfig.cmd_protoc_test_conf,1,function(){
		logger.info("[starter.js] ClientNode deployed");
});

