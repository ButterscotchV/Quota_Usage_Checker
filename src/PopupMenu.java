import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class PopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 5589015536038692108L;

	public PopupMenu() {
		JMenuItem update = new JMenuItem("Update");
		update.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Main.update = true;
			}
		});
		add(update);

		JMenuItem close = new JMenuItem("Close");
		close.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				System.exit(0);
			}
		});
		add(close);

		if (Main.isAlwaysOntopSupported()) {
			JCheckBoxMenuItem displayOver = new JCheckBoxMenuItem("Always On Top");
			displayOver.setSelected(Main.isAlwaysOntop());
			displayOver.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					Main.setAlwaysOntop(!Main.isAlwaysOntop());

					Main.settings.isAlwaysOntop = Main.isAlwaysOntop();
					Main.settings.save();
				}
			});
			add(displayOver);
		}

		addSeparator();

		JCheckBoxMenuItem oneMinute = new JCheckBoxMenuItem("Update every minute");
		oneMinute.setSelected(Main.settings.minutesToUpdate == 1);
		oneMinute.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Main.settings.minutesToUpdate = 1;
			}
		});
		add(oneMinute);

		JCheckBoxMenuItem fiveMinute = new JCheckBoxMenuItem("Update every 5 minutes");
		fiveMinute.setSelected(Main.settings.minutesToUpdate == 5);
		fiveMinute.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Main.settings.minutesToUpdate = 5;
			}
		});
		add(fiveMinute);

		addSeparator();

		JMenuItem displayOver = new JMenuItem("Rename Range");
		displayOver.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				new TextboxPopup(Main.curUsage.ipRange);
			}
		});
		add(displayOver);

		addSeparator();

		add(new JLabel("Available Ranges")); // Header

		ArrayList<Usage> sortedQuotas = new ArrayList<Usage>(Main.quotas);
		sortedQuotas.sort(new Comparator<Usage>() {

			@Override
			public int compare(Usage o1, Usage o2) {
				return (o1.ipRange.end.equals(o2.ipRange.start) ? 0
						: (o1.ipRange.end.lessThan(o2.ipRange.start) ? -1 : 1));
			}

		});

		for (Usage usage : sortedQuotas) {

			JCheckBoxMenuItem usageOption = new JCheckBoxMenuItem("<html><font color=#" + Main.getUsageColourHex(usage)
					+ ">" + Main.settings.getIPRangeLabel(usage.ipRange) + "</font>"
					+ (Main.mainUsage == usage ? " (Yours)" : ""));
			usageOption.setSelected(Main.curUsage == usage);
			usageOption.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					Main.curUsage = usage;
					Main.updatePercentage();
				}
			});
			add(usageOption);
		}
	}
}
