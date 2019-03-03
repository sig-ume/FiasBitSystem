/**
 *
 */
package jp.sigre.fbs.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.sigre.fbs.database.ConnectDB;
import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.selenium.trade.TradeDataBean;
import jp.sigre.fbs.utils.FileUtils;
import jp.sigre.google.GoogleCalendarUtils;
import jp.sigre.google.HolidayBean;

/**
 * @author sigre
 *
 */
public class DataController {

	private LogMessage log = new LogMessage();
	private boolean isHoliday = false;

	final String DATE_PATTERN = "yyyy-MM-dd";

	public DataController() {
	}

	public boolean updateSepaCombine(String lsFilePath) {

		//TODO;分割銘柄が割り切れない場合は？

		final String WILDCARD = "wildcard";

		LogMessage log = new LogMessage();

		FileUtils file = new FileUtils();
		String strSepaComFilePath = file.getSepaCombineFilePath(lsFilePath);
		File sepaComFile = new File(strSepaComFilePath);

		if (!sepaComFile.exists()) {
			log.writelnLog("分割併合ファイルがありません。");
			return false;
		}

		List<SepaCombineBean> sepaComList = new FileUtils().csvToSepaCombine(strSepaComFilePath);

		if (sepaComList == null) {
			log.writelnLog("おそらく分割併合ファイルの形式が不正です。");
			return false;
		}

		if (sepaComList.size()==0) {
			log.writelnLog("分割併合銘柄がありません。");
			return false;
		}

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		for (SepaCombineBean bean : sepaComList) {
			TradeDataBean tradeBean = db.getTradeViewOfCode(bean.getCode());

			if (tradeBean.getRealEntryVolume().equals("0")) continue;

			tradeBean.setDayTime(file.getTodayDate());
			tradeBean.setEntry_money("0");
			tradeBean.setEntryMethod(WILDCARD);
			tradeBean.setExitMethod(WILDCARD);
			//分割併合は３
			tradeBean.setMINI_CHECK_flg("3");

			System.out.println(tradeBean.getCode() + " :" + tradeBean.getRealEntryVolume());
			int realEntryVolume = Integer.parseInt(tradeBean.getRealEntryVolume());

			int flag = Integer.parseInt(bean.getChecksepa_combine());
			double ratio = Double.parseDouble(bean.getAjustRate());

			//0:combine, 1:separate
			double sepaComVolume = 0;
			if (flag==1) sepaComVolume = realEntryVolume * (ratio - 1);
			else if (flag==0) sepaComVolume = -1 * (realEntryVolume - (realEntryVolume % ratio))  * (ratio - 1) / ratio - realEntryVolume % ratio;
			else continue;

			String strSepaComVolume = "";
			if (sepaComVolume % 1 == 0)  strSepaComVolume = String.valueOf((int)sepaComVolume);
			else strSepaComVolume = String.valueOf(sepaComVolume);
			tradeBean.setRealEntryVolume(strSepaComVolume);

			db.insertTradeData(tradeBean);

		}

		return true;
	}

	public boolean updateDbAndSbiStock(List<List<TradeDataBean>> beanLists) {

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		List<TradeDataBean> dbList = beanLists.get(0);



		String strToday = new FileUtils().getTodayDate();

		//DBに過剰にキープされている銘柄の株数を減らす
		for (TradeDataBean dbBean : dbList) {
			int intStock = Integer.parseInt(dbBean.getRealEntryVolume());

			//指定の銘柄のデータを取得
			List<TradeDataBean> ascList = db.getTradeViewOfCodeMethodsStockOrderAsc(dbBean.getCode());

			for (TradeDataBean ascBean : ascList) {

				int intAscStock = Integer.parseInt(ascBean.getRealEntryVolume());

				if (intStock <= intAscStock) {
					ascBean.setRealEntryVolume(String.valueOf(-1 * intStock));
					ascBean.setCorrectedEntryVolume(ascBean.getRealEntryVolume());

					ascBean.setDayTime(strToday);
					ascBean.setEntry_money("0");
					//DBとSBIのストック齟齬は4
					ascBean.setMINI_CHECK_flg("4");
					ascBean.setType("DD");

					//取得した銘柄、メソッドから株数をマイナス
					db.insertTradeData(ascBean);
					break;
				} else {
					ascBean.setRealEntryVolume(String.valueOf(-1 * intAscStock));
					ascBean.setCorrectedEntryVolume(ascBean.getRealEntryVolume());

					ascBean.setDayTime(strToday);
					ascBean.setEntry_money("0");
					ascBean.setMINI_CHECK_flg("4");
					ascBean.setType("DD");

					//取得した銘柄、メソッドから株数をマイナス
					db.insertTradeData(ascBean);

					intStock -= intAscStock;
				}
			}

		}

		List<TradeDataBean> sbiList = beanLists.get(1);

		for (TradeDataBean sbiBean : sbiList) {
			//Beanの不足情報を追加。codeとRealEntryVoume以外。
			sbiBean.setCorrectedEntryVolume(sbiBean.getRealEntryVolume());
			sbiBean.setDayTime(strToday);
			sbiBean.setEntry_money("0");
			sbiBean.setEntryMethod("wildcard");
			sbiBean.setExitMethod("wildcard");
			sbiBean.setMINI_CHECK_flg("4");
			sbiBean.setType("DD");

			//DBに挿入
			db.insertTradeData(sbiBean);
		}

		return true;

	}

	/**
	 * FiaElite,Keepの齟齬（DBに過剰なデータがある）を修正する。
	 * DBから特定の銘柄、メソッドを売却する
	 *
	 * @param beanList
	 * @return
	 */
	public List<TradeDataBean> updateDbAndFia(List<TradeDataBean> beanList) {

		String strToday = new FileUtils().getTodayDate();
		for (TradeDataBean bean : beanList) {
			//株数をマイナスにして
			bean.setDayTime(strToday);
			bean.setEntry_money("0");
			//eliteとの比較は5
			bean.setMINI_CHECK_flg("5");
			bean.setType("DD");

		}


		return beanList;
	}

	public String moveTempTradeData(Calendar nowCal) {

		//TODO:今日が平日なら処理する。休日ならしない。
		//TODO:平日じゃない場合は直前の平日を処理日とする

		isHoliday = false;

		//getUsualCalでisHolidayがかわる
		Calendar usualCal = getUsualCal(nowCal);

		String strDay = isHoliday ? "休日" : "平日";
		log.writelnLog("本日は" + strDay + "です。");

		if (isHoliday) moveTempTradeDateHoliday(usualCal);
		else moveTempTradeDateUsual(usualCal);

		//原状復帰
		isHoliday = false;

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

		String strDate = sdf.format(usualCal.getTime());
		return strDate;
	}

	protected Calendar getUsualCal(Calendar nowCal) {

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

		String strDate = sdf.format(nowCal.getTime());
		List<HolidayBean> holidays = db.getHolidaysPastEqualToday(strDate);

//		for (int i = 0; i < holidays.size(); i++) {
//			HolidayBean holiday = holidays.get(i);
//			String strDate1 = holiday.getDate();
//
//			Calendar cal1 = Calendar.getInstance();
//			String[] splitStrDate1 = strDate1.split("-");
//			cal1.set(Integer.parseInt(splitStrDate1[0]),
//					Integer.parseInt(splitStrDate1[1]),
//					Integer.parseInt(splitStrDate1[2]));
//
//			int donichi = cal1.get(Calendar.DAY_OF_WEEK);
//
//			if (strDate1.equals(strDate) || donichi == Calendar.SUNDAY || donichi == Calendar.SATURDAY) {
//				nowCal.add(Calendar.DATE, -1);
//				isHoliday = true;
//				strDate = sdf.format(nowCal.getTime());
//				i = 0;
//			}
//		}

		while(true) {

			int donichi = nowCal.get(Calendar.DAY_OF_WEEK);
			if (donichi == Calendar.SUNDAY || donichi == Calendar.SATURDAY || isShukujituDay(nowCal, holidays)) {
				isHoliday = true;
				nowCal.add(Calendar.DAY_OF_MONTH, -1);
				continue;
			}

			break;
		}

		db.closeStatement();

		return nowCal;

	}

	private boolean isShukujituDay(Calendar cal, List<HolidayBean> holidays) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String actualDate = sdf.format(cal.getTime());

		for (int i=0; i<holidays.size(); i++) {
			if (actualDate.equals(holidays.get(i).getDate())) return true;
		}

		return false;
	}

	private String moveTempTradeDateUsual(Calendar nowCal) {
		int hour = nowCal.get(Calendar.HOUR_OF_DAY);
		Calendar exeCal = (Calendar) nowCal.clone();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		//単元用日付
		Calendar exe0Cal = (Calendar) nowCal.clone();

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		int count = 0;

		//S株処理
		if (hour >= 13) {
			//１０時半以前の取引を処理
			exeCal.set(Calendar.HOUR_OF_DAY, 10);
		} else if (hour >= 10) {

			//前日から最も近い平日を取得u
			exeCal.add(Calendar.DAY_OF_MONTH, -1);
			exeCal = getUsualCal(exeCal);
			System.out.println("最も近い平日: " + exeCal);

			//前日２１時半
			exeCal.set(Calendar.HOUR_OF_DAY, 21);

		} else {

			//前日から最も近い平日を取得
			exeCal.add(Calendar.DAY_OF_MONTH, -1);
			exeCal = getUsualCal(exeCal);
			System.out.println("最も近い平日: " + exeCal);
			//前日の１０時半までの注文を処理
			exeCal.set(Calendar.HOUR_OF_DAY, 10);
		}

		exeCal.set(Calendar.MINUTE, 30);
		exeCal.set(Calendar.SECOND, 0);
		exeCal.set(Calendar.MILLISECOND, 0);

		String borderDate = sdf.format(exeCal.getTime());
		System.out.println("borderDate: " + borderDate);

		int count3 = db.moveTempTradeSData(borderDate);
		if (count3 > 0) {
			db.deleteTempTradeSData(borderDate);
			count += count3;
		}

		log.writelnLog(borderDate + "以前のS株取引(" + count3 + "件)を保有株数へ反映しました。");

		int count4 = db.moveTempTradeFurikaeData(borderDate);
		if (count4 > 0) db.deleteTempTradeFurikaeData(borderDate);

		//AM9時～PM3時であれば、単元の注文をすべて処理
		if (hour >= 9 && hour < 15) {

			int count2 = db.moveTempTradeTangenData();
			if (count2 > 0) db.deleteTempTradeTangenData();

			count += count2;
		} else if (hour < 9){

			//前日から最も近い平日を取得
			exe0Cal.add(Calendar.DAY_OF_MONTH, -1);
			exe0Cal = getUsualCal(exe0Cal);

			//AM９時以前なら前日15時までの取引を処理
			exe0Cal.set(Calendar.HOUR_OF_DAY, 15);
			exe0Cal.set(Calendar.MINUTE, 0);
			exe0Cal.set(Calendar.SECOND, 0);
			exe0Cal.set(Calendar.MILLISECOND, 0);

			String exe0Date = sdf.format(exe0Cal.getTime());

			int count2 = db.moveTempTradeTangenData(exe0Date);
			if (count2 > 0) db.deleteTempTradeTangenData(exe0Date);

			log.writelnLog(exe0Date + "以前の単元株取引(" + count2 + "件)を保有株数へ反映しました。");

			count += count2;

		} else {
			//PM15時以降なら１５時以前の取引を処理
			exe0Cal.set(Calendar.HOUR_OF_DAY, 15);
			exe0Cal.set(Calendar.MINUTE, 0);
			exe0Cal.set(Calendar.SECOND, 0);
			exe0Cal.set(Calendar.MILLISECOND, 0);

			String exe0Date = sdf.format(exe0Cal.getTime());

			int count2 = db.moveTempTradeTangenData(exe0Date);
			if (count2 > 0) db.deleteTempTradeTangenData(exe0Date);

			log.writelnLog(exe0Date + "以前の単元株取引(" + count2 + "件)を保有株数へ反映しました。");

			count += count2;

		}

		//if (count > 0) log.writelnLog(borderDate + "以前の取引(" + count + "件)を保有株数へ反映しました。");

		db.closeStatement();


		return borderDate;
	}

	private String moveTempTradeDateHoliday(Calendar nowCal) {

		Calendar exeCal = (Calendar) nowCal.clone();

		//単元用日付
		Calendar exe0Cal = (Calendar) nowCal.clone();

		ConnectDB db = new ConnectDB();
		db.connectStatement();

		int count = 0;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		exe0Cal.set(Calendar.HOUR_OF_DAY, 15);
		exe0Cal.set(Calendar.MINUTE, 0);
		exe0Cal.set(Calendar.SECOND, 0);
		exe0Cal.set(Calendar.MILLISECOND, 0);

		String exe0Date = sdf.format(exe0Cal.getTime());
		int count2 = db.moveTempTradeTangenData(exe0Date);
		if (count2 > 0) {
			db.deleteTempTradeTangenData(exe0Date);
			count += count2;
		}

		log.writelnLog(exe0Date + "以前の単元株取引(" + count2 + "件)を保有株数へ反映しました。");

		exeCal.set(Calendar.HOUR_OF_DAY, 10);

		exeCal.set(Calendar.MINUTE, 30);
		exeCal.set(Calendar.SECOND, 0);
		exeCal.set(Calendar.MILLISECOND, 0);

		String borderDate = sdf.format(exeCal.getTime());

		int count3 = db.moveTempTradeSData(borderDate);
		if (count3 > 0) {
			db.deleteTempTradeSData(borderDate);
			count += count3;
		}

		log.writelnLog(borderDate + "以前のS株取引(" + count3 + "件)を保有株数へ反映しました。");

		int count4 = db.moveTempTradeFurikaeData(borderDate);
		if (count4 > 0) db.deleteTempTradeFurikaeData(borderDate);


		db.closeStatement();

		//if (count > 0) log.writelnLog(borderDate + "以前の取引(" + count + "件)を保有株数へ反映しました。");

		return borderDate;
	}

	public void updateHolidayTable(String strDate) {
		GoogleCalendarUtils googleCal = new GoogleCalendarUtils();
		ConnectDB db = new ConnectDB();
		db.connectStatement();

		List<HolidayBean> holidays = db.getHolidaysPastEqualToday(strDate);

		System.out.println("holidays count:" + holidays.size());

		//取得テーブルがNullじゃない、かつ当日以前の休日がMAXの100日じゃない場合は終了
		if (holidays.size() != 30 && holidays.size() != 0) {
			log.writelnLog("休日テーブルの更新は不要です。");
			return;
		}

		log.writelnLog("休日テーブルの更新を行います。");
		//休日を全削除後
		db.deleteHolidays();

		//休日情報が残っていない場合は新しい情報を追加
		holidays = new ArrayList<>();
		try {
			holidays = googleCal.getHolidayBeans(strDate, 30);
		} catch (IOException e) {
			log.writelnLog("Googleとの接続に問題があります。Googleサービスに手動で接続してみてください。");
		} catch (ParseException e) {
			log.writelnLog("日付の指定に問題があります。開発者にご連絡ください。");
		}

		for (HolidayBean bean : holidays) {
			db.insertHolidays(bean);
		}

		db.closeStatement();
	}

	/**
	 * TODO
	 * @param beanList
	 * @return
	 */
	public List<List<TradeDataBean>> getSameCodeLists(List<TradeDataBean> beanList) {

		List<List<TradeDataBean>> result = new ArrayList<>();

		for (int i = 0; i < beanList.size() && beanList.size() > 0; i++) {

			List<TradeDataBean> smallReslt = new ArrayList<>();
			TradeDataBean bean = beanList.get(i);
			smallReslt.add(bean);
			beanList.remove(i);
			i--;

			for (int j = 0; j < beanList.size() && beanList.size() > 0; j++) {
				TradeDataBean beanJ = beanList.get(j);

				if (beanJ.getCode().equals(bean.getCode())) {
					smallReslt.add(beanJ);
					beanList.remove(j);
					j--;

				}
			}

			result.add(smallReslt);

			//if (beanList.size() <= 0) break;
		}

		return result;
	}
}
