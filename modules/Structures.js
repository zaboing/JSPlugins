function Structure() {
	this.blocks = new Array();
	this.metadata = new Array();
	this.pivot = { x: 0, y: 0, z: 0 };
}

Structure.prototype.place = function(world, origin, protectBlocks) {
	for (var i = 0; i < this.blocks.length; i++) {
		for (var j = 0; j < this.blocks[i].length; j++) {
			for (var k = 0; k < this.blocks[i][j].length; k++) {
				var type = this.blocks[i][j][k];
				var x = i + origin.x - this.pivot.x;
				var y = j + origin.y - this.pivot.y;
				var z = k + origin.z - this.pivot.z;
				if (type === undefined || type === null || type === "") {
					world.getBlockAt(x, y, z).setType(org.bukkit.Material.AIR);
				} else {
					var block = world.getBlockAt(x, y, z);
					block.setType(org.bukkit.Material.valueOf(type));
					block.setData(this.getMetadata(i, j, k));
					if (protectBlocks) {
						block.setMetadata("protected", new org.bukkit.metadata.FixedMetadataValue($, true));
					}
				}
			}
		}
	}
}

Structure.prototype.read = function(world, min, max, sender, filter) {
	this.pivot = { x: 0, y: 0, z: 0 };
	this.blocks = new Array();
	var ignored = {};
	for (var i = 0; i <= max.x - min.x; i++) {
		this.blocks[i] = new Array();
		for (var j = 0; j <= max.y - min.y; j++) {
			this.blocks[i][j] = new Array();
			for (var k = 0; k <= max.z - min.z; k++) {
				var block = world.getBlockAt(i + min.x, j + min.y, k + min.z);
				var type = block.getType().name();
				if (filter !== undefined && filter.indexOf(type) !== -1) {
					if (!ignored.hasOwnProperty(type)) {
						ignored[type] = 0;
					}
					ignored[type]++;
					type = "";
				}
				this.blocks[i][j][k] = type;
				var data = block.getData();
				if (data !== 0) {
					this.setMetadata(i, j, k, data);
				}
			}
		}
	}
	for (var type in ignored) {
		if (ignored.hasOwnProperty(type)) {
			sender.sendMessage("Ignored " + type + " " + ignored[type] + " times.");
		}
	}
}

Structure.prototype.setMetadata = function(i, j, k, data) {
	if (this.metadata[i] === undefined) {
		this.metadata[i] = new Array();
	}
	if (this.metadata[i][j] === undefined) {
		this.metadata[i][j] = new Array();
	}
	this.metadata[i][j][k] = data;
}

Structure.prototype.getMetadata = function(i, j, k) {
	if (this.metadata[i] == undefined) {
		return 0;
	}
	if (this.metadata[i][j] == undefined) {
		return 0;
	}
	if (this.metadata[i][j][k] == undefined) {
		return 0;
	}
	return this.metadata[i][j][k];
}

Structure.prototype.load = function(data) {
	this.blocks = data.blocks;
	this.pivot = data.pivot;
	this.metadata = data.metadata;
}

function replaceAll(find, replace, str) {
  return str.replace(new RegExp(find, 'g'), replace);
}

Structure.prototype.toString = function() {
	return JSON.stringify({
		blocks: this.blocks,
		pivot: this.pivot,
		metadata: JSON.parse(replaceAll("null", "[]", JSON.stringify(this.metadata)))
	});
}

Structure.prototype.size = function() {
	if (this.blocks.length === 0) {
		return 0;
	}
	return this.blocks.length * this.blocks[0].length * this.blocks[0][0].length;
}