import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Popup {
	JFrame frame;
	JLabel[] text;

	public static final int WIDTH = 300;
	public static final int HEIGHT = 100;
	public static final int TEXT_SPACING = 20;

	public Popup(String... labels) {
		frame = new JFrame("WARNING");
		frame.setSize(WIDTH, 32 + 26 + (TEXT_SPACING * labels.length) + OsUtil.getOSType().getDisplayOffset());
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setLayout(null);

		text = new JLabel[labels.length];

		frame.setVisible(true);

		for (int i = 0; i < labels.length; i++) {
			text[i] = new JLabel(labels[i]);
			frame.add(text[i]);
			Main.putTextCentered(WIDTH, labels[i], text[i], 10 + (TEXT_SPACING * i));
		}

		JButton but = new JButton("Okay");
		frame.add(but);
		but.setBounds((WIDTH / 2) - (80 / 2), 20 + (TEXT_SPACING * labels.length), 80, 28);
		but.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
	}
}
