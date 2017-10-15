/**
 *
 */
package jp.sigre.fbs.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import jp.sigre.fbs.gui.component.ConsolePanel;
import jp.sigre.fbs.selenium.trade.TradeController;
import jp.sigre.fbs.timer.FbsTimerTask;

/**
 * @author sigre
 *
 */
public class FbsGui extends JFrame implements ActionListener{

	private boolean isActive = false;
    JButton button = new JButton();
	Timer timer = new Timer();
	static TradeController trade = new TradeController();

	public FbsGui(String title){
//		setTitle(title);
//		setBounds(100, 100, 200, 160);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//	    JPanel p1 = new JPanel();
//	    p1.setBackground(Color.BLUE);
//
//	    JPanel p2 = new JPanel();
//	    p2.setBackground(Color.ORANGE);
//
//	    Container contentPane = getContentPane();
//	    contentPane.add(p1, BorderLayout.NORTH);
//	    contentPane.add(p2, BorderLayout.SOUTH);
	    setTitle(title);
	    setBounds(100, 100, 500, 500);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    setLayout(new FlowLayout());

	    JPanel p1 = new JPanel();
	    p1.setPreferredSize(new Dimension(500, 50));
	    //p1.setBackground(Color.BLUE);
	    p1.setOpaque(false);

	    JPanel p2 = new JPanel();
	    p2.setPreferredSize(new Dimension(50, 100));
	    p2.setBackground(Color.ORANGE);

	    button.setText("START");
	    button.setPreferredSize(new Dimension(460, 45));

	    button.addActionListener(this);
	    p1.add(button);

	    BevelBorder border = new BevelBorder(BevelBorder.RAISED);
	    p2.setBorder(border);

	    ConsolePanel console = new ConsolePanel(460, 400);

	    Container contentPane = getContentPane();
	    contentPane.add(p1, BorderLayout.NORTH);
	    //contentPane.add(p1);
	    //contentPane.add(p2);
	    contentPane.add(console, BorderLayout.SOUTH);

	}
	/**
	 * @param args
	 */
	public static void main(String args[]){

		FbsGui frame = new FbsGui("fia bit system");

		boolean resultSetup = trade.tradeSetup();

		if (!resultSetup) return;

	    frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (false == isActive) {
			button.setText("STOP");
			timer = new Timer();
			timer.schedule(new FbsTimerTask(trade), 0, 20 * 60 * 1000);
		} else {
			button.setText("START");
			System.out.println("stop....");
			timer.cancel();
		}

		isActive = !isActive;

	}

}