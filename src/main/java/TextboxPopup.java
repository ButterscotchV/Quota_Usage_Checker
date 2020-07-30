import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TextboxPopup {
    JFrame frame;
    JTextField txtBox;
    JButton but;

    public static final int WIDTH = 300;
    public static final int HEIGHT = 124;
    public static final int WIDTH_SPACING = 20;
    public static final int TEXT_SPACING = 17;

    public static final int OS_COMPONENT_DECORATIONS = (OsUtil.getOSType() == EnumOS.WINDOWS ? 2 : 0);
    public static final int OS_TXTBOX_DECORATIONS = (OsUtil.getOSType() == EnumOS.WINDOWS ? 4 : 0) + OS_COMPONENT_DECORATIONS;

    public TextboxPopup(IPRange range) {
        frame = new JFrame("Rename range \"" + Main.settings.getIPRangeLabel(range) + "\"");
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        frame.setVisible(true);

        txtBox = new JTextField(Main.settings.getIPRangeLabel(range));
        frame.add(txtBox);
        txtBox.setBounds(WIDTH_SPACING, TEXT_SPACING, WIDTH - (WIDTH_SPACING * 2) - OS_TXTBOX_DECORATIONS, 28);
        txtBox.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                but.setEnabled(isNameOkay(txtBox.getText()));
            }

            @Override
            public void keyPressed(KeyEvent e) {
                but.setEnabled(isNameOkay(txtBox.getText()));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                but.setEnabled(isNameOkay(txtBox.getText()));

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    acceptButton(range, txtBox.getText());
                }
            }
        });

        but = new JButton("Accept");
        frame.add(but);
        but.setEnabled(isNameOkay(txtBox.getText()));
        but.setBounds((WIDTH / 2) - (80 / 2) - (OS_COMPONENT_DECORATIONS / 2), TEXT_SPACING * 3, 80, 28);
        but.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                acceptButton(range, txtBox.getText());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    public void acceptButton(IPRange range, String name) {
        if (accept(range, name)) {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public boolean accept(IPRange range, String name) {
        if (isNameOkay(name)) {
            Main.settings.addIPRangeLabel(range, name);

            return true;
        }

        return false;
    }

    public boolean isNameOkay(String name) {
        return name != null && !name.isEmpty();
    }
}
