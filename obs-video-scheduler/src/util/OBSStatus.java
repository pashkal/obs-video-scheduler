package util;

public class OBSStatus {
	private static boolean status = false;
	
	public static void set(boolean value) {
		status = value;
	}
	
	public static boolean get() {
		return status;
	}
}
