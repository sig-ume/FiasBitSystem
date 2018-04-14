package jp.sigre.fbs.gui.component;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.TextArea;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class OutputTextArea extends TextArea {
	private TextAreaOutputStream out;

	public OutputTextArea(int x, int y) throws HeadlessException {
		super();
		this.setEditable(false);
		out = new TextAreaOutputStream(this);
		this.setPreferredSize(new Dimension(x-3, y-3));
		this.setFont(new Font("Arial", Font.PLAIN, 18));
	}

	public void setToSystemOut(){
		System.setOut(new PrintStream(this.getOut()));
	}

	public void setToSystemErr(){
		System.setErr(new PrintStream(this.getOut()));
	}

	public TextAreaOutputStream getOut() {
		return out;
	}

	public void flush(){
		this.append(out.toString());
		out.reset();
	}

	public void setFont() {
		this.setFont(new Font("Arial", Font.PLAIN, 16));

	}

	public void changeSize(int x, int y) {
		this.setPreferredSize(new Dimension(x-3, y-3));

	}
}

class TextAreaOutputStream extends ByteArrayOutputStream {
	private OutputTextArea textarea;

	public TextAreaOutputStream(OutputTextArea textarea) {
		super();
		this.textarea = textarea;
	}

	public synchronized void write(byte[] b, int off, int len) {
		super.write(b, off, len);
		textarea.flush();
	}

	public synchronized void write(int b) {
		super.write(b);
		textarea.flush();
	}

	public void write(byte[] b) throws IOException {
		super.write(b);
		textarea.flush();
	}

}
