
public enum EnumOS {
	// TODO change Linux to 0 when exporting
	LINUX(0), SOLARIS(0), WINDOWS(30), OSX(0), UNKNOWN(0);

	private int displayOffset;

	private EnumOS(int osName) {
		displayOffset = osName;
	}

	public int getDisplayOffset() {
		return displayOffset;
	}
}
