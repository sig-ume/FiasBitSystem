package jp.sigre.LinkDownloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

/**
 * pom.xmlの<dependencies>内に下記を追加
 *
 *	<dependency>
 *		<groupId>commons-codec</groupId>
 *		<artifactId>commons-codec</artifactId>
 *		<version>1.10</version>
 *	</dependency>
 *
 * @author sigre
 *
 */

public class DownloadController {

	public String getData(String strUrl)
			throws MalformedURLException, IOException, UnknownHostException, WebAccessException {
		URL url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		int intRes = conn.getResponseCode();

		//アクセス失敗（存在しないURL、認証失敗など）
		if (200 != intRes) throw new WebAccessException(intRes);

		InputStream is = conn.getInputStream();
		String result = convertInputStreamToString(is);

		return result;

	}

	/**
	 *
	 * @param strUrl	サイトURL
	 * @param id		認証ID
	 * @param pass		認証パスワード
	 * @return			CSVを1行ごとにわけたStringリスト
	 * @throws IOException				取得した文字列が処理失敗した場合
	 * @throws UnknownHostException		サイト自体が存在しない場合
	 * @throws WebAccessException		ファイルが存在しない場合
	 * @throws MalformedURLException	httpじゃないURLが入った場合
	 */
	public List<String> getData(String strUrl, String id, String pass)
			throws IOException, UnknownHostException, WebAccessException, MalformedURLException {

		URL url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		String strKey = id + ":" + pass;

		String encodedBytes = new String(Base64.encodeBase64(strKey.getBytes()));

		conn.setRequestProperty("Authorization", "Basic " + encodedBytes);
		System.out.println(conn.getResponseCode());

		int intRes = conn.getResponseCode();

		//アクセス失敗（存在しないURL、認証失敗など）
		if (200 != intRes) throw new WebAccessException(intRes);

		InputStream is = conn.getInputStream();
		String strCsv = convertInputStreamToString(is);

		strCsv = strCsv.replace("\r", "");
		return Arrays.asList(strCsv.split("\n"));
	}

	static String convertInputStreamToString(InputStream is) throws IOException {
	    InputStreamReader reader = new InputStreamReader(is, "SJIS");
	    StringBuilder builder = new StringBuilder();
	    char[] buffer = new char[512];
	    int read;
	    while (0 <= (read = reader.read(buffer))) {
	        builder.append(buffer, 0, read);
	    }
	    return builder.toString();
	}
}

