package jp.sigre.LinkDownloader;

/**
 * URLにアクセス失敗したときに投げる例外
 * codeにエラーコードが入る
 * @author sigre
 *
 */
public class WebAccessException extends Exception {

	private int code;

	public WebAccessException(int code) {
		this.code = code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}