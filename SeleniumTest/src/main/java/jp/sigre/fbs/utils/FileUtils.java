package jp.sigre.fbs.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import jp.sigre.fbs.controller.SepaCombineBean;
import jp.sigre.fbs.log.LogMessage;
import jp.sigre.fbs.selenium.trade.IniBean;
import jp.sigre.fbs.selenium.trade.TradeDataBean;

/**
 * @author sigre
 *
 */
public class FileUtils {


	public List<TradeDataBean> csvToTorihikiData(File file) {
		final String[] HEADER = new String[] { "code","dayTime","type","entryMethod","exitMethod","MINI_CHECK_flg","realEntryVolume","entry_money" };
		CSVReader reader = null;
		try {
			reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "SJIS"), ',', '"', 1);
			ColumnPositionMappingStrategy<TradeDataBean> strat = new ColumnPositionMappingStrategy<>();
			strat.setType(TradeDataBean.class);
			strat.setColumnMapping(HEADER);
			CsvToBean<TradeDataBean> csv = new CsvToBean<>();
			return csv.parse(strat, reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException | NullPointerException e) {
				new LogMessage().writelnLog(e.toString());
			}
		}
	}

	public String[] csvToIdPass(File file) {
		CSVReader reader = null;
		try {

			reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "SJIS"));

			return reader.readNext();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException | NullPointerException e) {
				new LogMessage().writelnLog(e.toString());
			}
		}
	}

	public List<TradeDataBean> csvToFiaKeep(String filePath) {
		final String[] HEADER = new String[] { "code","entryDay","lastEntryDay","entryTimes","averagePrice",
				"type","entryMethod","exitMethod","MINI_CHECK_flg","ideallyVolume","IDEAaveragePrice",
				"IDEA_TOTAL_ENTRY_MONEY","realEntryVolume","REALaveragePrice","REAL_TOTAL_ENTRY_MONEY" };
		CSVReader reader = null;
		try {
			reader = new CSVReader(new InputStreamReader(new FileInputStream(filePath), "SJIS"), ',', '"', 1);
			ColumnPositionMappingStrategy<TradeDataBean> strat = new ColumnPositionMappingStrategy<>();
			strat.setType(TradeDataBean.class);
			strat.setColumnMapping(HEADER);
			CsvToBean<TradeDataBean> csv = new CsvToBean<>();
			return csv.parse(strat, reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException | NullPointerException e) {
				new LogMessage().writelnLog(e.toString());
			}
		}
	}

	public List<SepaCombineBean> csvToSepaCombine(String filePath) {
		final String[] HEADER = new String[] { "code", "checksepa_combine", "ajustRate" };
		CSVReader reader = null;
		try {
			reader = new CSVReader(new InputStreamReader(new FileInputStream(filePath), "SJIS"), ',', '"', 1);
			ColumnPositionMappingStrategy<SepaCombineBean> strat = new ColumnPositionMappingStrategy<>();
			strat.setType(SepaCombineBean.class);
			strat.setColumnMapping(HEADER);
			CsvToBean<SepaCombineBean> csv = new CsvToBean<>();
			return csv.parse(strat, reader);
		} catch (Exception e) {
			new LogMessage().writelnLog(e.toString());
		} finally {
			try {
				reader.close();
			} catch (IOException | NullPointerException e) {
				new LogMessage().writelnLog(e.toString());
			}
		}
		return null;
	}


	public IniBean iniToBean(File file) {
		FileReader fr = null;
		BufferedReader br = null;
		IniBean bean = new IniBean();
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				readIniLine(line, bean);
			}
		} catch (IOException e) {
			new LogMessage().writelnLog(e.toString());
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException | NullPointerException e) {
				new LogMessage().writelnLog(e.toString());
			}
		}

		return bean;
	}

	private void readIniLine(String line, IniBean bean) {
		if (line.startsWith("LS_FilePath")) bean.setLS_FilePath(getLS_FilePath(line));
		if (line.startsWith("ID_FilePath")) bean.setID_FilePath(getID_FilePath(line));
		if (line.startsWith("["))			getUseMethod(line, bean);
		if (line.startsWith("Trade_Visible")) bean.setTradeVisible(getTradeVisible(line));
		if (line.startsWith("Sell_UnusedMethod_Immediately")) bean.setSellUnusedMethod(getSellUnusedMethod(line));

	}

	private String getLS_FilePath(String line) {
		return line.split("\"")[1];
	}

	private String getID_FilePath(String line) {
		return line.split("\"")[1];
	}

	private void getUseMethod(String line, IniBean bean) {
		String[] splitLine = line.split("=");
		String[] methods = splitLine[0].split("&");
		String entryMethod = methods[0].substring(1);
		String exitMethod = methods[1].substring(0, methods[1].length()-1);
		String flag = splitLine[1];

		String[] methodSet = {entryMethod, exitMethod, flag};

		bean.addMethodSet(methodSet);
	}

	private String getTradeVisible(String line) {
		return line.split("=")[1];
	}

	private String getSellUnusedMethod(String line) {
		return line.split("=")[1];
	}

	public void makeTradeDataFile(List<TradeDataBean> list, String outPath, boolean isBuying) {

		String fileName = isBuying? "buy_remains.csv" : "sell_remains.csv";

		makeDataFile(list, outPath, fileName);

	}

	public void makeBackupDataFile(List<TradeDataBean> list, String outPath) {

		String fileName = "backup.csv";

		File target = new File(outPath + File.separator + fileName);

		deleteFile(target);

		makeDataFile(list, outPath, fileName);

	}

	private void makeDataFile(List<TradeDataBean> list, String outPath, String fileName) {
		writeFile("code,dayTime,type,entryMethod,exitMethod,MINI_CHECK_flg,"
				+ "realEntryVolume,entry_money\n", outPath, fileName);

		for (TradeDataBean bean : list) {
			writeFile(bean.toCSV() + "\n", outPath, fileName);
		}
	}


	public void writeFile(String writing,String outPath, String fileName){

		String logFilePath = outPath + File.separator + fileName;

		File file = new File(logFilePath);

		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
					new LogMessage().writelnLog(file.getAbsolutePath() + "の作成に失敗しました。");
				}
			}
			FileWriter filewriter = new FileWriter(file,true);
			filewriter.write(writing );
			filewriter.close();
		}catch(IOException e){
			new LogMessage().writelnLog(e.toString());
		}

	}

	public String getIdPassFilePath(String strFolderPath) {
		return strFolderPath + File.separator + "idpassword.fbs";
	}

	public String getLFilePath(String strFolderPath) {

		return strFolderPath + File.separator + getLFileName();
	}

	public String getSFilePath(String strFolderPath) {

		return strFolderPath + File.separator + getSFileName();
	}

	public String getMovedLFilePath(String strFolderPath) {

		return strFolderPath + "\\old\\" + getLFileName();
	}

	public String getMovedSFilePath(String strFolderPath) {

		return strFolderPath + "\\old\\" + getSFileName();
	}

	public String getBuyRemainsFilePath(String strFolderPath) {

		return strFolderPath + File.separator + "buy_remains.csv";
	}

	public String getSellRemainsFilePath(String strFolderPath) {

		return strFolderPath + File.separator + "sell_remains.csv";
	}

	public String getIniPath(String strFolderPath) {

		return strFolderPath + File.separator + "fbs.ini";
	}

	public String getKeyPath(String strFolderPath) {

		return strFolderPath + File.separator +  getTodayDate() + "_FBS_KICK" + ".fbs";
	}

	private String getLFileName() {

		return getTodayDate() + "_L.csv";
	}

	private String getSFileName() {

		return getTodayDate() + "_S.csv";
	}

	public String getTodayDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateToday = new Date();

		return sdf.format(dateToday);
	}

	public void removeTradeDataFile(String strLsPath, boolean isBuying) {
		//TODO;remainsファイルのフルパスを取得するメソッド作成
		String fileName = isBuying? "buy_remains.csv" : "sell_remains.csv";

		File target = new File(strLsPath + File.separator + fileName);

		deleteFile(target);
	}



	public String getExePath(InputStream inputStream, String prefix, String suffix) {
		try {
			java.nio.file.Path p = java.nio.file.Files.createTempFile(prefix, suffix);
			//System.out.println(p.toString());
			p.toFile().deleteOnExit();
			java.nio.file.Files.copy(inputStream, p, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			return p.toAbsolutePath().toString();
		} catch (Exception e) {
			new LogMessage().writelnLog(e.toString());
			System.exit(1);
		}

		//到達しないはず
		return "";
	}

	public void deleteKickFiles(String strLsFolderPath) {
		String regex = ".*\\d*-\\d*-\\d*_FBS_KICK.fbs";
		Pattern p = Pattern.compile(regex);

		LogMessage log = new LogMessage();

		File lsFolder = new File(strLsFolderPath);
		if (!lsFolder.isDirectory()) return;
		for (File file : lsFolder.listFiles()) {
			Matcher m = p.matcher(file.getName());
			if (m.find()) {
				if (!file.delete()) {
					log.writelnLog("キックファイルの削除に失敗しました。 " + file.getAbsolutePath());
					return ;
				} else {
					log.writelnLog("キックファイルを削除しました。" + file.getAbsolutePath());
				}
			}
		}

	}

	public void deleteFile(String path) {
		File target = new File(path);
		deleteFile(target);
	}

	public void deleteFile(File target) {
		if (target.exists()) {
			if (!target.delete()) {
				new LogMessage().writelnLog(target.getAbsolutePath() + "の削除に失敗しました。");
			} else {
				new LogMessage().writelnLog(target.getAbsolutePath() + "を削除しました。");
			}
		}
	}

	public String getFiaKeepFilePath(String strLsFolderPath) {
		return strLsFolderPath + File.separator + getTodayDate() + "_fias_keep.csv";
	}

	public void deleteKeepFiles(String strLsFolderPath) {
		String regex = ".*\\d*-\\d*-\\d*_fias_keep.csv";
		Pattern p = Pattern.compile(regex);

		LogMessage log = new LogMessage();

		File lsFolder = new File(strLsFolderPath);
		if (!lsFolder.isDirectory()) return;
		for (File file : lsFolder.listFiles()) {
			Matcher m = p.matcher(file.getName());
			if (m.find()) {
				if (!file.delete()) {
					log.writelnLog("Keepファイルの削除に失敗しました。 " + file.getAbsolutePath());
					return ;
				} else {
					log.writelnLog("Keepファイルを削除しました。" + file.getAbsolutePath());
				}
			}
		}

	}

	public void backupDbFile(String strFolderPath) {
		String dbFilePath = getDbFilePath(strFolderPath);

		File dbFile = new File(dbFilePath);
		if (dbFile.exists()) {
			int i = 0;
			while (true) {
				//TradeInfo.sqlite.yyyy-mm-ddiでファイルをコピー
				//問題ないiが見つかるまでcontinue
				String copyPath = dbFilePath + "." + getTodayDate() +i;
				File copyFile = new File(copyPath);
				if (copyFile.exists()) {
					i++;
					continue;
				}
				//指定パスでコピー
				try {
					Files.copy(dbFile, copyFile);
					new LogMessage().writelnLog("DBファイルをコピーしました。" + copyPath);
					return;
				} catch (IOException e) {
					new LogMessage().writelnLog(e.toString());
					return;
				}
			}

		}

		new LogMessage().writelnLog("dbファイルが見つかりません。 " + dbFilePath);
	}

	private String getDbFilePath(String strFolderPath) {
		return strFolderPath + File.separator + "db" + File.separator + "TradeInfo.sqlite";
	}

	public String getSepaCombineFilePath(String strFolderPath) {
		return strFolderPath + File.separator + "FBSsepaCombine.csv";
	}

}
