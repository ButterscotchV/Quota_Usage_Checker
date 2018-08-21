
public class Usage {
	IPRange ipRange;
	long downloadUsed;
	long downloadTotal;
	
	private Usage(IPRange ipRange, long downloadUsed, long downloadTotal) {
		this.ipRange = ipRange;
		this.downloadUsed = downloadUsed;
		this.downloadTotal = downloadTotal;
	}
	
	public static Usage createUsage(IPRange ipRange, long downloadUsed, long downloadTotal) {
		return new Usage(ipRange, downloadUsed, downloadTotal);
	}
	
	public static Usage createUsage(String ipRange, long downloadUsed, long downloadTotal) {
		return new Usage(new IPRange(ipRange), downloadUsed, downloadTotal);
	}
}
