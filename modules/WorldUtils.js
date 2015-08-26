function getWorld(environment) {
	var worlds = $.getServer().getWorlds();
	for (var i = 0; i < worlds.size(); i++) {
		if (worlds[i].getEnvironment() == environment) {
			return worlds[i];
		}
	}
	return null;
}

function getSurface() {
	return getWorld(org.bukkit.World.Environment.NORMAL);
}

function getNether() {
	return getWorld(org.bukkit.World.Environment.NETHER);
}

function getEnd() {
	return getWorld(org.bukkit.World.Environment.THE_END);
}