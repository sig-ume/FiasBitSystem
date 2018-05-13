/**
 *
 */
package jp.sigre.fbs.gui.component;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jp.sigre.fbs.gui.FbsGui;
import jp.sigre.fbs.log.LogMessage;

/**
 * @author sigre
 *
 */
public class FbsMainComponentListener implements ComponentListener {

	private FbsGui frame;
	private int pastWidth = -1;
	private int pastHeight = -1;

	public FbsMainComponentListener(FbsGui frame) {
		this.frame = frame;
	}

	LogMessage log = new LogMessage();
	/* (非 Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		Component comp = e.getComponent();
		if (comp==null) return;

		//System.out.println("x=" + comp.getWidth() + ", y=" + comp.getHeight());
		if (comp.getWidth() != pastWidth && comp.getHeight() != pastHeight) {
			frame.changeSize(comp.getWidth(), comp.getHeight());
			pastWidth = comp.getWidth();
			pastHeight = comp.getHeight();
		}
	}

	/* (非 Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	/* (非 Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	/* (非 Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
