package util;

public class ScheduleEntry {
	public long id;
	public long start;
	public String itemName;

	public ScheduleEntry(long id, long start, String video) {
		super();
		this.id = id;
		this.start = start;
		this.itemName = video;
	}

	@Override
	public String toString() {
		return "ScheduleEntry [id=" + id + ", start=" + start + ", itemName=" + itemName + "]";
	}

}
