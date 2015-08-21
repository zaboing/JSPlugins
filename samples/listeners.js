/*
 *	A simple event demo
 */

function onEnable() {
	$.on("PlayerLoginEvent", playerLogin);
	$.on("PlayerMoveEvent", playerMove);
}

function onDisable() {

}

function getName() {
	return "Event Demo";
}

function getVersion() {
	return "0.1.0";
}

function playerLogin(event) {
	$.getLogger().info("Welcome, " + event.getPlayer().getName() + "!");
}

function playerMove(event) {
	event.getPlayer().sendMessage("Stop moving!");
}