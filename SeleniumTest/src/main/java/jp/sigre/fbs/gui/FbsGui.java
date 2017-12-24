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
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import jp.sigre.fbs.controller.DataController;
import jp.sigre.fbs.gui.component.ConsistActionListener;
import jp.sigre.fbs.gui.component.ConsolePanel;
import jp.sigre.fbs.selenium.trade.TradeController;
import jp.sigre.fbs.timer.FbsTimerTask;

/**
 * @author sigre
 *
 */
public class FbsGui extends JFrame implements ActionListener{

	private boolean isActive = false;
	JButton startButton = new JButton();
	JButton consistButton = new JButton();
	Timer timer = new Timer();
	static TradeController trade = new TradeController();
	ClassLoader thisLoader = getClass().getClassLoader();
	URL stopIconUrl = thisLoader.getResource("lib/stop.png");
	URL startIconUrl = thisLoader.getResource("lib/running.png");

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
		setBounds(100, 100, 500, 580);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new FlowLayout());

		JPanel p1 = new JPanel();
		p1.setPreferredSize(new Dimension(500, 80));
		//p1.setBackground(Color.BLUE);
		p1.setOpaque(false);

		JPanel p2 = new JPanel();
		p2.setPreferredSize(new Dimension(50, 100));
		p2.setBackground(Color.ORANGE);

		startButton.setText("START");
		startButton.setPreferredSize(new Dimension(460, 45));

		startButton.addActionListener(this);
		p1.add(startButton);

		consistButton.setText("CONSIST");
		consistButton.setPreferredSize(new Dimension(460, 30));
		consistButton.addActionListener(new ConsistActionListener());
		p1.add(consistButton);

		BevelBorder border = new BevelBorder(BevelBorder.RAISED);
		p2.setBorder(border);

		ConsolePanel console = new ConsolePanel(460, 400);

		Container contentPane = getContentPane();
		contentPane.add(p1, BorderLayout.NORTH);
		//contentPane.add(p1);
		//contentPane.add(p2);
		contentPane.add(console, BorderLayout.SOUTH);

		//アプリアイコン設定
		ImageIcon icon = new ImageIcon(stopIconUrl);
		setIconImage(icon.getImage());
	}
	/**
	 * @param args
	 */
	public static void main(String args[]){

		FbsGui frame = new FbsGui("fia bit system");

		boolean resultSetup = trade.tradeSetup();

		frame.setVisible(true);

		//if (!resultSetup) System.exit(0);;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		new DataController().moveTempTradeData(Calendar.getInstance());

		if (false == isActive) {
			startButton.setText("STOP");
			ImageIcon icon = new ImageIcon(startIconUrl);
			setIconImage(icon.getImage());
			timer = new Timer();
			timer.schedule(new FbsTimerTask(trade), 0, 20 * 60 * 1000);
		} else {
			startButton.setText("START");
			ImageIcon icon = new ImageIcon(stopIconUrl);
			setIconImage(icon.getImage());
			System.out.println("stop....");
			timer.cancel();
		}

		isActive = !isActive;

	}

}