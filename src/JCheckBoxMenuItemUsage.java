import javax.swing.JCheckBoxMenuItem;

public class JCheckBoxMenuItemUsage extends JCheckBoxMenuItem {
	private static final long serialVersionUID = -2132154630213188943L;
	public Usage usage;
	
	public JCheckBoxMenuItemUsage(Usage usage) {
		super(getUsageEntry(usage));
		
		this.usage = usage;
	}
	
	public static String getUsageEntry(Usage usage) {
		return "<html><font color=#" + Main.getUsageColourHex(usage)
		+ ">" + Main.settings.getIPRangeLabel(usage.ipRange) + "</font>"
		+ (Main.mainUsage == usage ? " (Yours)" : "") + " " + usage.getDownloadSpeedString();
	}
	
	public void updateUsage() {
		Usage newUsage = Main.findUsage(usage);
		
		if (newUsage != null)
			usage = newUsage;
	}
	
	public void updateText() {
		updateUsage();
		setText(getUsageEntry(usage));
	}
}
