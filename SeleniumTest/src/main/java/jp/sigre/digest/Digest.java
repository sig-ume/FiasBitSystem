package jp.sigre.digest;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import jp.sigre.LogMessage;

public class Digest {

	final static String DIG_ALGORITHM = "MD5";

	static String target1 = "target\\TestFile\\target1.txt";
	static String target2 = "target\\TestFile\\target2.txt";
	static String target3 = "target\\TestFile\\target3.txt";
	static String target4 = "target\\TestFile\\target4.txt";

	static String target5 = "target\\TestFile\\FBS_KICK_2017-07-20.fbs";

	public Digest() {
	}

	public static void main(String[] args) throws Exception {

		Digest digSample = new Digest();

		String key = digSample.getKeyStr();

		int count = 0;

		System.out.println("key:" + key);

		System.out.println("key Digest         : " + digSample.getDigestStr(key));

		System.out.println("other key Digest   : " + digSample.getDigestStr("test"));

		System.out.println("make               : " + digSample.makeDigestFile(target1, key, 0));

		System.out.println("success            : " + digSample.checkDigestFile(target1, key, count));

		System.out.println("failed(illegal key): " + digSample.checkDigestFile(target2, key, count));

		System.out.println("failed(no file)    : " + digSample.checkDigestFile(target3, key, count));

		System.out.println("make(count 5)      : " + digSample.makeDigestFile(target4, key, 5));

		System.out.println("failed(count 5)    : " + digSample.checkDigestFile(target4, key, count));

		System.out.println("success            : " + digSample.checkDigestFile(target1, key, count));

	}

	private String getKeyStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateToday = new Date();

		String strToday = sdf.format(dateToday);

		String key = "GOD_BLESS_ME_SYO_UME_" + strToday;

		return key;
	}

	public boolean makeDigestFile(String path, int count) {
		String key = getKeyStr();
		return makeDigestFile(path, key, count);
	}

	public boolean makeDigestFile(String path, String key, int count) {
		String digest = getDigestStr(key);

		File keyFile = new File(path);

		FileWriter writer = null;
		try {
			if (keyFile.exists()) {
				keyFile.delete();
			}
			keyFile.createNewFile();
			writer = new FileWriter(keyFile);
			writer.write(digest);
			writer.write("\n" + count);

			writer.close();
		} catch (IOException e) {
			new LogMessage().writelnLog(e.toString());
		}

		return true;
	}

	public boolean checkDigestFile(String path, int count) {
		String key = getKeyStr();
		return checkDigestFile(path, key, count);
	}

	public boolean checkDigestFile(String path, String key, int count) {

		String digest = getDigestStr(key);

		File keyFile = new File(path);

		if (!keyFile.exists()) {
			new LogMessage().writelnLog("KICKファイルが存在しません。");
			return false;
		}

		String digestInFile = "";
		count = -1;

		System.out.println("test1" + path);
		try {
			FileReader reader = new FileReader(new File(path));
			BufferedReader br = new BufferedReader(reader);
			digestInFile = br.readLine();
			String strCount = br.readLine();

			if (strCount!=null) {
				count = Integer.parseInt(strCount);
			}

			br.close();
		} catch (IOException e) {
			new LogMessage().writelnLog(e.toString());
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

	public String getDigestStr(String key) {

		String md5 = "";

		byte[] digest = getDigest(key);
		for (int loop = 0;loop < digest.length;loop++) {
			md5 += Integer.toHexString(0xff&(char)digest[loop]).toString();
		}

		return md5;
	}

	private byte[] getDigest(String key) {
		MessageDigest msgDig = null;
		byte[] digest = null;
		try {
			msgDig = MessageDigest.getInstance(DIG_ALGORITHM);
			digest = msgDig.digest(key.getBytes());

		} catch (Exception e) {
			Arrays.fill(digest,(byte)0);
			return digest;
		}

		return digest;
	}

}
