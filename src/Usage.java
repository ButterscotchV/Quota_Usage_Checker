
public class Usage {
	public final IPRange ipRange;
	public final long downloadUsed;
	public final long downloadTotal;
	public final long downloadSpeed;
	public final long timestamp;
	public double percentOfTotalSpeed = 0;

	private Usage(IPRange ipRange, long downloadUsed, long downloadTotal, Usage lastUsage) {
		this.ipRange = ipRange;
		this.downloadUsed = downloadUsed;
		this.downloadTotal = downloadTotal;
		this.timestamp = System.currentTimeMillis() / 1000; // Current time in seconds

		if (lastUsage != null)
			this.downloadSpeed = (downloadUsed - lastUsage.downloadUsed) / (timestamp - lastUsage.timestamp);
		else
			this.downloadSpeed = 0;
	}

	public static Usage createUsage(IPRange ipRange, long downloadUsed, long downloadTotal, Usage lastUsage) {
		return new Usage(ipRange, downloadUsed, downloadTotal, lastUsage);
	}

	public static Usage createUsage(String ipRange, long downloadUsed, long downloadTotal, Usage lastUsage) {
		return new Usage(new IPRange(ipRange), downloadUsed, downloadTotal, lastUsage);
	}
	
	public String getDownloadSpeedString() {
		return "<font color=#"
				+ Main.getUsageColourHex(this) + ">(" + Main.percentWithPrecision(downloadSpeed, Main.BYTE_IN_A_MB, 1)
				+ " MB/s)</font>";
	}
}
