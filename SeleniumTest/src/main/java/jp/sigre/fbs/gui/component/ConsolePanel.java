/**
 *
 */
package jp.sigre.fbs.gui.component;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author sigre
 *
 */
public class ConsolePanel extends JPanel {

	public ConsolePanel(int x, int y) {
		this.setLayout(null);
		this.setSize(x, y);

//		JTextArea area = new JTextArea(20,10);
//        //area.setEditable(false);  // ReadOnly に
//		//area.setPreferredSize(new Dimension(380, 180));
//        JTextAreaStream stream = new JTextAreaStream(area);
//        PrintStream out = new PrintStream(stream, true);
//        System.setOut(out);    // true は AutoFlush の設定
//        System.setErr(out);

		OutputTextArea area = new OutputTextArea(x, y);

		area.setToSystemErr();
		area.setToSystemOut();

        JScrollPane sp = new JScrollPane(area);
		sp.setPreferredSize(new Dimension(x, y));
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		this.add(sp);

	}

}
