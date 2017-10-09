/**
 *
 */
package jp.sigre.fbs.gui;

import javax.swing.JFrame;

/**
 * @author sigre
 *
 */
public class FbsGui {

	/**
	 * @param args
	 */
	public static void main(String args[]){
		JFrame frame = new JFrame("タイトル");

		frame.setBounds(200, 200, 200, 160);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
