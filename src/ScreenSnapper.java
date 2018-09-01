import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

public class ScreenSnapper {
	public enum SnapStatus {
		UNLOCKED,
		X_LOCK,
		Y_LOCK,
		X_AND_Y_LOCK,
	}

	public class SnapPos {
		public int x;
		public int y;

		public SnapStatus snapStatus;

		public SnapPos(int x, int y, SnapStatus snapStatus) {
			this.x = x;
			this.y = y;
			this.snapStatus = snapStatus;
		}
	}

	private GraphicsDevice[] screenList = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	public int snapDist;
	public Frame frame;

	public ScreenSnapper(Frame frame, int snapDist) {
		this.frame = frame;
		this.snapDist = snapDist;
	}

	private GraphicsDevice[] getScreenList() {
		return screenList;
	}

	public SnapPos calculateSnapPos() {
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
		int cWidth = frame.getWidth();
		int cHeight = frame.getHeight();
		// setting offsets in case of different screen
		int currentX = myScreen.getDefaultConfiguration().getBounds().x;
		int currentY = myScreen.getDefaultConfiguration().getBounds().y;
		// Top left (tl) corner coordinate
		int[] tl = new int[] { currentX, currentY };
		// Bottom right (br) corner coordinate
		int[] br = new int[] { currentX + screenWidth, currentY + screenHeight };
		// Top left (tl) corner coordinate of component (c)
		int[] tlc = new int[] { frame.getX(), frame.getY() };
		// Bottom right (br) corner coordinate of component (c)
		int[] brc = new int[] { frame.getX() + cWidth, frame.getY() + cHeight };

		// System.out.println(tl[0] + " " + tl[1] + " " + br[0] + " " + br[1]);
		
		SnapStatus locked = SnapStatus.UNLOCKED;
		int x = 0;
		int y = 0;
		
		if (Math.abs(tlc[0] - tl[0]) <= snapDist || Math.abs(tlc[1] - tl[1]) <= snapDist || Math.abs(brc[0] - br[0]) <= snapDist
				|| Math.abs(brc[1] - br[1]) <= snapDist) {
			if (Math.abs(tlc[0] - tl[0]) <= snapDist) {
				x = tl[0];
				locked = SnapStatus.X_LOCK;
			}

			if (Math.abs(tlc[1] - tl[1]) <= snapDist) {
				y = tl[1];
				if (locked == SnapStatus.X_LOCK)
					locked = SnapStatus.X_AND_Y_LOCK;
				else
					locked = SnapStatus.Y_LOCK;
			}

			if (Math.abs(brc[0] - br[0]) <= snapDist) {
				x = br[0] - cWidth;
				if (locked == SnapStatus.Y_LOCK)
					locked = SnapStatus.X_AND_Y_LOCK;
				else
					locked = SnapStatus.X_LOCK;
			}

			if (Math.abs(brc[1] - br[1]) <= snapDist) {
				y = br[1] - cHeight;
				if (locked == SnapStatus.X_LOCK)
					locked = SnapStatus.X_AND_Y_LOCK;
				else
					locked = SnapStatus.Y_LOCK;
			}
		}
		
		return new SnapPos(x, y, locked);
	}

	public void movetoSnapPos(SnapPos pos) {
		int x = (pos.snapStatus == SnapStatus.X_LOCK || pos.snapStatus == SnapStatus.X_AND_Y_LOCK ? pos.x : frame.getX());
		int y = (pos.snapStatus == SnapStatus.Y_LOCK || pos.snapStatus == SnapStatus.X_AND_Y_LOCK ? pos.y : frame.getY());
		
		frame.setLocation(x, y);
	}

	public void snapToScreen() {
		movetoSnapPos(calculateSnapPos());
	}
}
