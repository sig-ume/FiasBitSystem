/**
 *
 */
package jp.sigre.fbs.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jp.sigre.fbs.selenium.trade.TradeController;

/**
 * @author sigre
 *
 */
public class ConsistActionListener implements ActionListener {

	/* (非 Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		TradeController trade = new TradeController();
		trade.tradeSetup();
		trade.login();

		trade.consistStock();

		trade.logout();
	}

}
