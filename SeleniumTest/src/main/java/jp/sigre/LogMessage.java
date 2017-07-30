package jp.sigre;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jp.sigre.selenium.trade.FileUtils;

/**
 *
 */

/**
 * @author sigre
 *
 */
public class LogMessage {

//TODO;コンストラクタでフォルダパス指定

	public void writeInLog(String writing, String outPath){
		Calendar now = Calendar.getInstance(); //インスタンス化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//
		//		int h = now.get(now.HOUR_OF_DAY);//時を取得
		//		int m = now.get(now.MINUTE);     //分を取得
		//		int s = now.get(now.SECOND);      //秒を取得
		//
		//		int y = now.get(Calendar.YEAR);  //年を取得
		//		int mo = now.get(Calendar.MONTH);//月を取得
		//		int d = now.get(Calendar.DATE); //現在の日を取得
		//		String nowTime = y+"/"+mo + "/" + d + "　" +h + ":"+m+":"+s+":";
		String nowTime = sdf.format(now.getTime());
		//		System.out.println(sdf.format(now.getTime()));
		//		System.out.println(y+"/"+mo + "/" + d + "_" +h + ":"+m+":"+s+":");


		new FileUtils().writeFile(nowTime + "," + writing + "\r\n", outPath,  "fbs_sys.log");

	}




}
