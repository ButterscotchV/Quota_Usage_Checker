
public class IPRange {
	IPAddress start;
	IPAddress end;

	public IPRange(IPAddress start, IPAddress end) {
		this.start = start;
		this.end = end;
	}

	public IPRange(String start, String end) {
		this.start = new IPAddress(start);
		this.end = new IPAddress(end);
	}

	public IPRange(int[] start, int[] end) {
		this.start = new IPAddress(start);
		this.end = new IPAddress(end);
	}

	public IPRange(String range) {
		if (range != null && range.contains("-")) {
			String[] splitRange = range.split("-");

			this.start = new IPAddress(splitRange[0]);
			this.end = new IPAddress(splitRange[1]);
		}
	}

	public boolean isIPWithinRange(IPAddress ipAddress) {
		for (int i = 0; i < ipAddress.numbers.length; i++) {
			if (ipAddress.numbers[i] < start.numbers[i] || ipAddress.numbers[i] > end.numbers[i]) {
				return false;
			}
		}

		return true;
	}

	public boolean isIPWithinRange(String ipAddress) {
		return isIPWithinRange(new IPAddress(ipAddress));
	}

	public boolean isIPWithinRange(int[] ipAddress) {
		return isIPWithinRange(new IPAddress(ipAddress));
	}

	@Override
	public String toString() {
		return start.toString() + "-" + end.toString();
	}

	public boolean equals(IPRange ipRange) {
		return ipRange.start.equals(start) && ipRange.end.equals(end);
	}
}
