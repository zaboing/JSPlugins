function HTTPEndpoint(url) {
	this.url = new java.net.URL(url);
}

function HTTPRequest() {
	this.data = null;
	this.contentType = null;
	this.length = null;
	this.method = "GET";
}

HTTPRequest.prototype.setContent = function(data, contentType, length) {
	this.data = data;
	this.contentType = contentType;
	this.length = length;
}

HTTPRequest.prototype.setRequestMethod = function(method) {
	this.method = method;
}

HTTPRequest.prototype.sendTo = function(httpEndpoint, successCallback) {
	var connection = httpEndpoint.url.openConnection();
	connection.setRequestMethod(this.method);
	connection.setUseCaches(false);
	connection.setDoInput(true);
	connection.setDoOutput(true);

	if (this.data !== null) {
		connection.setRequestProperty("Content-Type", this.contentType);
		connection.setRequestProperty("Content-Length", this.contentLength);
		var stream = connection.getOutputStream();
		stream.write(this.data, 0, this.data.length);
		stream.close();
	}
	$.getServer().getScheduler().runTaskAsynchronously($, function() {
		var input = connection.getInputStream();
		var output = new java.io.ByteArrayOutputStream();
		var i;
		while ((i = input.read()) != -1) {
			output.write(i);
		}
		input.close();
		output.close();
		connection.disconnect();
		$.getServer().getScheduler().runTask($, function() {
			successCallback(output.toByteArray());
		});
	});
}