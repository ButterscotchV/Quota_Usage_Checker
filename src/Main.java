
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

public class Main {
	private static JFrame frame;
	private static JLabel text;
	private static JProgressBar bar;
	private static JLabel bText;

	private static final String IP_RANGE_REGEX = ".*(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})-(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3}).*";

	private static final int HEIGHT = 70; // + OsUtil.getOSType().getDisplayOffset()
	private static final int WIDTH = 200;

	private static final int START_DAY = 1; // The day that all data resets. 0
	// is sunday, 6 is saturday

	private static int lastX = Integer.MIN_VALUE;
	private static int lastY = Integer.MIN_VALUE;
	// private static boolean drag = true;
	private static int grappleX = Integer.MIN_VALUE;
	private static int grappleY = Integer.MIN_VALUE;

	public static boolean update = false;

	public static int minutesToUpdate = 1;

	private static int locked = 0;

	private static int[] lockCoord = new int[2];

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		MouseAdapter mouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				doPop(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				lastX = Integer.MIN_VALUE;
				lastY = Integer.MIN_VALUE;
				grappleX = Integer.MIN_VALUE;
				grappleY = Integer.MIN_VALUE;
				if (locked == 1) {
					frame.setLocation(lockCoord[0], frame.getY());
					locked = 0;
					lockCoord[0] = 0;
					lockCoord[1] = 0;
				} else if (locked == 2) {
					frame.setLocation(frame.getX(), lockCoord[1]);
					locked = 0;
					lockCoord[0] = 0;
					lockCoord[1] = 0;
				} else if (locked == 3) {
					frame.setLocation(lockCoord[0], lockCoord[1]);
					locked = 0;
					lockCoord[0] = 0;
					lockCoord[1] = 0;
				}

				doPop(e);
			}

			private void doPop(MouseEvent e) {
				System.out.println(e.isPopupTrigger());
				if (e.isPopupTrigger()) {
					// drag = false;
					PopupMenu menu = new PopupMenu();
					menu.show(e.getComponent(), e.getX(), e.getY());
				} else {
					// drag = true;
				}

				new Settings(isAlwaysOntop(), minutesToUpdate, frame.getLocationOnScreen()).save();
			}

			// Range in which the window locks to screen edges in pixels, locking range (lr)
			private int lr = 22;
			private GraphicsDevice[] screenList = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

			@Override
			public void mouseDragged(MouseEvent e) {
				// Prints modifier (ex. 4 = Right Click, 8 = Scroll Wheel, 16 = Left Click)
				// System.out.println(e.getModifiers());

				if (e.getModifiers() != 4) {
					int currentX = e.getXOnScreen();
					int currentY = e.getYOnScreen();

					if (lastX == Integer.MIN_VALUE) {
						lastX = currentX;
						lastY = currentY;
					}

					if (grappleX == Integer.MIN_VALUE) {
						grappleX = e.getX() + (e.getComponent() instanceof JFrame ? 0 : e.getComponent().getX());
						grappleY = e.getY() + (e.getComponent() instanceof JFrame ? 0 : e.getComponent().getY());
					}

					int dX = (currentX - lastX);
					int dY = (currentY - lastY);

					int xOff = currentX - dX - grappleX;
					int yOff = currentY - dY - grappleY;

					frame.setLocation(xOff, yOff);

					lastX = currentX;
					lastY = currentY;
				}

				// Code for Edge Locker
				Component c = frame;

				Window myWindow = new Window(frame);
				GraphicsConfiguration config = myWindow.getGraphicsConfiguration();
				GraphicsDevice myScreen = config.getDevice();
				// matches against active display
				for (GraphicsDevice gd : getScreenList()) {
					// this will set the display to a new display if the window
					// is moved
					// to a new display
					if (gd.equals(myScreen)) {
						myScreen = gd;
						break;
					}
				}

				// Actual Screen Dimensions
				int screenWidth = myScreen.getDefaultConfiguration().getBounds().width;
				int screenHeight = myScreen.getDefaultConfiguration().getBounds().height;
				// Component Width/Height
				int cWidth = c.getWidth();
				int cHeight = c.getHeight();
				// setting offsets in case of different screen
				int currentX = myScreen.getDefaultConfiguration().getBounds().x;
				int currentY = myScreen.getDefaultConfiguration().getBounds().y;
				// Top left (tl) corner coordinate
				int[] tl = new int[] { currentX, currentY };
				// Bottom right (br) corner coordinate
				int[] br = new int[] { currentX + screenWidth, currentY + screenHeight };
				// Top left (tl) corner coordinate of component (c)
				int[] tlc = new int[] { c.getX(), c.getY() };
				// Bottom right (br) corner coordinate of component (c)
				int[] brc = new int[] { c.getX() + cWidth, c.getY() + cHeight };

				// System.out.println(tl[0] + " " + tl[1] + " " + br[0] + " " + br[1]);

				if (Math.abs(tlc[0] - tl[0]) <= lr || Math.abs(tlc[1] - tl[1]) <= lr || Math.abs(brc[0] - br[0]) <= lr
						|| Math.abs(brc[1] - br[1]) <= lr) {
					if (Math.abs(tlc[0] - tl[0]) <= lr) {
						lockCoord[0] = tl[0];
						locked = 1;
					}

					if (Math.abs(tlc[1] - tl[1]) <= lr) {
						lockCoord[1] = tl[1];
						if (locked == 1)
							locked = 3;
						else
							locked = 2;
					}

					if (Math.abs(brc[0] - br[0]) <= lr) {
						lockCoord[0] = br[0] - cWidth;
						if (locked == 2)
							locked = 3;
						else
							locked = 1;
					}

					if (Math.abs(brc[1] - br[1]) <= lr) {
						lockCoord[1] = br[1] - cHeight;
						if (locked == 1)
							locked = 3;
						else
							locked = 2;
					}
				} else {
					locked = 0;
					lockCoord[0] = 0;
					lockCoord[1] = 0;
				}

				/*
				 * System.out.println(); System.out.println(lockCoord[0]);
				 * System.out.println(lockCoord[1]);
				 */
			}

			public GraphicsDevice[] getScreenList() {
				return screenList;
			}
		};

		/*
		 * ComponentAdapter edgeSnapper = new ComponentAdapter() { private boolean
		 * locked = false;
		 * 
		 * // feel free to modify; set based on my own preferences // incorporate as
		 * user option? private int lockingRange = 30; private GraphicsDevice[]
		 * screenList =
		 * GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		 * 
		 * // clamping at 5 seems correct, 0 clamps at -5 beyond screen // boundary
		 * 
		 * @Override public void componentMoved(ComponentEvent evt) { // gets current
		 * display device Window myWindow = new Window((Frame) evt.getComponent());
		 * GraphicsConfiguration config = myWindow.getGraphicsConfiguration();
		 * GraphicsDevice myScreen = config.getDevice(); // matches against active
		 * display for (GraphicsDevice gd : getScreenList()) { // this will set the
		 * display to a new display if the window // is moved // to a new display if
		 * (gd.equals(myScreen)) { myScreen = gd; break; } }
		 * 
		 * // minimising calls to stack int screenWidth =
		 * myScreen.getDefaultConfiguration().getBounds().width; int screenHeight =
		 * myScreen.getDefaultConfiguration().getBounds().height; int compWidth =
		 * evt.getComponent().getWidth(); int compHeight =
		 * evt.getComponent().getHeight(); int nx = evt.getComponent().getX(); int ny =
		 * evt.getComponent().getY(); // setting offsets in case of different screen int
		 * currentX = myScreen.getDefaultConfiguration().getBounds().x; int currentY =
		 * myScreen.getDefaultConfiguration().getBounds().y;
		 * 
		 * // see end of method // OR conditions seem to stabilise movement when close
		 * to screen // edge if (locked || nx == currentX + 5 || ny == currentY + 5 ||
		 * nx == currentX + screenWidth - compWidth - 5 || ny == currentY + screenHeight
		 * - compHeight - 5) return;
		 * 
		 * // left if (nx < (currentX + sd) && nx > (currentX + 5)) { nx = currentX + 5;
		 * }
		 * 
		 * // top if (ny < (currentY + sd) && ny > (currentY + 5)) { ny = currentY + 5;
		 * }
		 * 
		 * // right if (nx > currentX + screenWidth - compWidth - sd && nx < currentX +
		 * screenWidth - compWidth - 5) { nx = currentX + screenWidth - compWidth - 5; }
		 * 
		 * // bottom if (ny > currentY + screenHeight - compHeight - sd && ny < currentY
		 * + screenHeight - compHeight - 5) { ny = currentY + screenHeight - compHeight
		 * - 5; }
		 * 
		 * // make sure we don't get into a recursive loop when the // set location
		 * generates more events locked = true; evt.getComponent().setLocation(nx, ny);
		 * locked = false; }
		 * 
		 * public GraphicsDevice[] getScreenList() { return screenList; } };
		 */

		frame = new JFrame("Internet Usage");
		frame.addMouseMotionListener(mouseListener);
		frame.addMouseListener(mouseListener);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null); // Starts window in centre of screen
		frame.setLayout(null);
		frame.setUndecorated(true);
		// frame.addComponentListener(edgeSnapper);

		Settings settings = Settings.load();
		if (settings != null) {
			setAlwaysOntop(settings.isAlwaysOntop);
			minutesToUpdate = settings.minutesToUpdate;
			frame.setLocation(settings.frameLocation);
		}

		text = new JLabel();
		text.addMouseMotionListener(mouseListener);
		text.addMouseListener(mouseListener);
		frame.add(text);

		bText = new JLabel();
		bText.addMouseMotionListener(mouseListener);
		bText.addMouseListener(mouseListener);
		frame.add(bText);

		bar = new JProgressBar();
		bar.setBounds(16, 16, WIDTH - 32, 20);
		bar.setToolTipText("Auto updates every " + minutesToUpdate + " minute(s)");
		bar.addMouseMotionListener(mouseListener);
		bar.addMouseListener(mouseListener);
		frame.add(bar);

		frame.setVisible(true);

		while (true) {
			update = false;
			updateText("Updating Internet Usage");

			try {
				URL url = new URL("http://192.168.1.2/");
				InputStream is = url.openStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				lastQuotas.clear();
				lastQuotas = new ArrayList<Usage>(quotas);
				quotas.clear();

				long used = -1;
				long max = -1;
				IPAddress ip = null;
				IPRange range = null;
				String line;
				while ((line = br.readLine()) != null) {
					if (ip != null) { // finds max and used

						if (line.matches(IP_RANGE_REGEX)) {
							// System.out.println(line);

							if (line.split("\\[").length == 4) {
								String ipRange = line.split("\\[")[2].split("\\]")[0].replaceAll("\"", "").trim();
								range = new IPRange(ipRange);
								// System.out.println(ipRange);
							}

							if (line.startsWith("quotaUsed[") && line.split("\\[").length == 4) {
								String uss = line.split(",")[1];
								uss = uss.substring(1, uss.length());
								used = Long.parseLong(uss);

							} else if (line.startsWith("quotaLimits[") && line.split("\\[").length == 4) {
								String maxx = line.split(",")[1];
								maxx = maxx.substring(1, maxx.length());
								max = Long.parseLong(maxx);
							}

							// System.out.println(line);

							if (used != -1 && max != -1 && range != null) {
								// System.out.println(range);

								Usage usage = Usage.createUsage(range, used, max, findUsage(range, lastQuotas));
								quotas.add(usage);

								if (range.isIPWithinRange(ip)) {
									mainUsage = usage;
								}

								used = -1;
								max = -1;
								range = null;
							}
						}

						// Finds the IP
					} else if (line.contains("var connectedIp = ")) {
						String[] split1 = line.split("\"");
						ip = new IPAddress(split1[1]);

						System.out.println("Connected IP: " + ip);
					}
				}

				// Calculate total speed
				long totalSpeed = 0;
				for (Usage usage : quotas) {
					totalSpeed += usage.downloadSpeed;
				}

				// If speed is over 0, calculate percentages
				if (totalSpeed > 0) {
					for (Usage usage : quotas) {
						usage.percentOfTotalSpeed = percentWithPrecision(usage.downloadSpeed, totalSpeed, 100);
					}
				}

				// Set the current usage from the previous one, if there is no previous one, use
				// your own
				curUsage = findUsage(curUsage);
				curUsage = (curUsage == null ? mainUsage : curUsage);

				updatePercentage();

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!hasWarnedStopped && mainUsage.downloadUsed >= mainUsage.downloadTotal) {
				hasWarnedStopped = true;

				new Popup("You have reached your maximum", "data usage of "
						+ percentWithPrecision(mainUsage.downloadTotal, BYTE_IN_A_GIG, 1) + "G" + " for this week.");
			}

			if (!hasWarnedOver && !hasWarnedStopped) {
				int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
				int daysSinceStartDay = today - START_DAY;

				if (today - 1 < START_DAY) {
					daysSinceStartDay += 7;
				}

				long designedUsage = (mainUsage.downloadTotal / 7) * daysSinceStartDay;

				if (mainUsage.downloadUsed > designedUsage) {
					hasWarnedOver = true;

					String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

					new Popup("You have exceeded your data usage", "designed for up to this day. (Only",
							percentWithPrecision(designedUsage, BYTE_IN_A_GIG, 1) + "G should be used by "
									+ days[today - 1] + ")",
							"You have used " + percentWithPrecision(mainUsage.downloadUsed, BYTE_IN_A_GIG, 1) + "G");
				}
			}

			int secondsToWait = 60 * minutesToUpdate; // Normal: 60 * 5
			int secondsPassed = 0;

			while (secondsToWait > secondsPassed && !update) {
				try {
					bar.setToolTipText("Auto updates every " + minutesToUpdate + " minute(s)");

					/*
					 * In-case it loses focus I've had this happen many times and I'm not sure
					 * what's causing it But this might fix it
					 * 
					 * if(isAlwaysOntop()) { setAlwaysOntop(false); setAlwaysOntop(true); }
					 * 
					 * This minimizes fullscreen windows, don't use this
					 */

					Thread.sleep(1000);
					secondsPassed++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static boolean hasWarnedOver = false;
	private static boolean hasWarnedStopped = false;

	public static final long BYTE_IN_A_GIG = 1024 * 1024 * 1024;
	public static final long BYTE_IN_A_MB = 1024 * 1024;
	public static final long BYTE_IN_A_KB = 1024;
	private static final double PRECISION = 100;

	private static final Color LOW_USAGE = new Color(67, 181, 129);
	private static final Color MEDIUM_USAGE = new Color(250, 166, 26);
	private static final Color HIGH_USAGE = new Color(240, 71, 71);

	private static double percentage = 0;
	public static Usage mainUsage = null;
	public static Usage curUsage = null;
	public static ArrayList<Usage> quotas = new ArrayList<Usage>();
	public static ArrayList<Usage> lastQuotas = new ArrayList<Usage>();

	public static Usage findUsage(Usage usage) {
		if (usage != null)
			return findUsage(usage.ipRange, quotas);
		else
			return null;
	}

	public static Usage findUsage(String ipRange) {
		if (ipRange != null)
			return findUsage(new IPRange(ipRange), quotas);
		else
			return null;
	}

	public static Usage findUsage(Usage usage, ArrayList<Usage> quotas) {
		if (usage != null)
			return findUsage(usage.ipRange, quotas);
		else
			return null;
	}

	public static Usage findUsage(String ipRange, ArrayList<Usage> quotas) {
		if (ipRange != null)
			return findUsage(new IPRange(ipRange), quotas);
		else
			return null;
	}

	public static Usage findUsage(IPRange ipRange, ArrayList<Usage> quotas) {
		for (Usage quotaUsage : quotas) {
			if (quotaUsage.ipRange.equals(ipRange)) {
				return quotaUsage;
			}
		}

		return null;
	}

	public static double percentWithPrecision(long numerator, long denominator, int percentModifier) {
		return Math.round(((double) numerator / (double) denominator) * percentModifier * PRECISION) / PRECISION;
	}

	public static void updatePercentage() {
		percentage = percentWithPrecision(curUsage.downloadUsed, curUsage.downloadTotal, 100);

		System.out.println("Used Data: " + curUsage.downloadUsed);
		System.out.println("Maximum Data: " + curUsage.downloadTotal);
		System.out.println("Percent Used: " + percentage);
		System.out.println("Speed: " + curUsage.downloadSpeed);
		System.out.println("Percent of Total Speed: " + curUsage.percentOfTotalSpeed);

		Color from = curUsage.percentOfTotalSpeed < 50 ? LOW_USAGE : MEDIUM_USAGE;
		Color to = curUsage.percentOfTotalSpeed < 50 ? MEDIUM_USAGE : HIGH_USAGE;

		float percentFromTo = (float) ((curUsage.percentOfTotalSpeed < 50 ? curUsage.percentOfTotalSpeed
				: curUsage.percentOfTotalSpeed - 50) * 2);

		String hexColour = colorToHex(colourFromPercentage(from, to, percentFromTo));

		updateBar((int) percentage);
		updateText("<html>Used: " + percentWithPrecision(curUsage.downloadUsed, BYTE_IN_A_GIG, 1) + "/"
				+ percentWithPrecision(curUsage.downloadTotal, BYTE_IN_A_GIG, 1) + "G <font color=#" + hexColour + ">("
				+ percentWithPrecision(curUsage.downloadSpeed, BYTE_IN_A_MB, 1) + " mb/s)</font>");
	}

	public static Color colourFromPercentage(Color to, Color from, float percent) {
		return colourFromDecimal(to, from, percent / 100);
	}

	public static Color colourFromDecimal(Color to, Color from, float decimalPercent) {
		float inverse_blending = 1 - decimalPercent;

		float red = from.getRed() * decimalPercent + to.getRed() * inverse_blending;
		float green = from.getGreen() * decimalPercent + to.getGreen() * inverse_blending;
		float blue = from.getBlue() * decimalPercent + to.getBlue() * inverse_blending;

		return new Color(red / 255, green / 255, blue / 255);
	}

	public static String colorToHex(Color color) {
		String rgb = Integer.toHexString(color.getRGB());
		return rgb.substring(2, rgb.length());
	}

	public static void updateBar(int percent) {
		bar.setValue(percent);

		putTextCentered(WIDTH, percent + "%", bText, 15);
	}

	public static void updateText(String newText) {
		putTextCentered(WIDTH, newText, text, 40);
	}

	public static void putTextCentered(int windowWidth, String text, JLabel label, int yPos) {
		label.setText(text);
		int width = label.getGraphics().getFontMetrics().stringWidth(text.replaceAll("<[^<>]*>", ""));
		label.setBounds((windowWidth / 2) - (width / 2), yPos, 300, 20);
	}

	public static boolean isAlwaysOntop() {
		return frame.isAlwaysOnTop();
	}

	public static boolean isAlwaysOntopSupported() {
		return frame.isAlwaysOnTopSupported();
	}

	public static void setAlwaysOntop(boolean alwaysOnTop) {
		if (isAlwaysOntopSupported()) {
			frame.setAlwaysOnTop(alwaysOnTop);
		}
	}
}
