package util;

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

	@Override
	public int compareTo(Item o) {
		return this.name.compareTo(o.name);
	}

}
