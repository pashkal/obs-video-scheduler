package util;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

public class Item implements Comparable<Item> {
	public String name;
	public long duration;
	public boolean isVideo;

	public Item(String name, long duration, boolean isVideo) {
		super();
		this.name = name;
		this.duration = duration;
		this.isVideo = isVideo;
	}
	
	public static Item fromJsonObject(JsonObject o) {
		return new Item(o.getString("name"), o.getInt("duration"), o.getBoolean("isVideo"));
	}

	@Override
	public int compareTo(Item o) {
		return this.name.compareTo(o.name);
	}
	
	public JsonObject toJsonObject() {
		return Json.createObjectBuilder()
			.add("name", name)
			.add("duration", duration)
			.add("isVideo", isVideo)
			.build();
	}
}
