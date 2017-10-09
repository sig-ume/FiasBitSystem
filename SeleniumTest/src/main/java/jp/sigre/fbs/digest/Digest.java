package jp.sigre.fbs.digest;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import jp.sigre.fbs.log.LogMessage;

public class Digest {

	public Digest() {
	}

	private String getKeyStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateToday = new Date();

		String strToday = sdf.format(dateToday);

		return "GOD_BLESS_ME_SYO_UME_" + strToday;
	}

	public void makeDigestFile(String path, int count) {
		String key = getKeyStr();
		makeDigestFile(path, key, count);
	}

	private boolean makeDigestFile(String path, String key, int count) {
		String digest = getDigestStr(key);

		File keyFile = new File(path);
		FileWriter writer = null;
		try {
			if (keyFile.exists()) {
				if (!keyFile.delete()) {
					new LogMessage().writelnLog("Key削除失敗しました。");
				}
			}
			if (!keyFile.createNewFile()) {
				new LogMessage().writelnLog("Keyファイル作成に失敗しました。");
			}

			writer = new FileWriter(keyFile);
			writer.write(digest);
			writer.write("\n" + count);
			//new LogMessage().writelnLog("Keyファイルを作成しました。 " + path);
		} catch (IOException e) {
			return false;
		} finally {

			try {
				if (writer != null) writer.close();
			} catch (IOException e) {
				new LogMessage().writelnLog(e.toString());
			}
		}

		return true;
	}

	//TODO;実装確認
	public boolean checkDigestFile(String path) {

		String key = getKeyStr();

		String digest = getDigestStr(key);

		File keyFile = new File(path);

		if (!keyFile.exists()) {
			new LogMessage().writelnLog("KICKファイルが存在しません。");
			return false;
		}

		String digestInFile = "";
		int count = -1;

		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader(new File(path));
			br = new BufferedReader(reader);
			digestInFile = br.readLine();
			String strCount = br.readLine();

			if (strCount!=null) {
				count = Integer.parseInt(strCount);
			}

		} catch (IOException e) {
			new LogMessage().writelnLog(e.toString());
		} finally {
			try {
				if (br!=null) br.close();
				if (reader!=null) reader.close();
			} catch (IOException e) {

				new LogMessage().writelnLog(e.toString());
			}
		}

		if (!digest.equals(digestInFile)) {
			new LogMessage().writelnLog("KICKファイルが不正です。");
			return false;
		}

		if (count >= 20) {
			new LogMessage().writelnLog("実行回数制限をオーバーしています。");
			return false;
		}

		return true;
	}

	private String getDigestStr(String key) {

		String md5 = "";

		byte[] digest = getDigest(key);
		for (int loop = 0;loop < digest.length;loop++) {
			md5 += Integer.toHexString(0xff&(char)digest[loop]).toString();
		}

		return md5;
	}

	private byte[] getDigest(String key) {
		MessageDigest msgDig;
		byte[] digest = null;
		try {
			String DIG_ALGORITHM = "MD5";
			msgDig = MessageDigest.getInstance(DIG_ALGORITHM);
			digest = msgDig.digest(key.getBytes());

		} catch (Exception e) {
			Arrays.fill(digest,(byte)0);
			return digest;
		}

		return digest;
	}

}
