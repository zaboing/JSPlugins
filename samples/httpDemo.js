load("jsplugins/modules/HTTP.js");

var endpoint = new HTTPEndpoint("http://httpbin.org/ip");

function onEnable() {
	var request = new HTTPRequest();
	request.sendTo(endpoint, function(data) {
		var response = JSON.parse(new java.lang.String(data));
		$.getLogger().info("Running on ip " + response.origin);
	});
}

function onDisable() {

}

function getName() {
	return "HTTPDemo";
}

function getVersion() {
	return "1.0";
}