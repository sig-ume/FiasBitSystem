/**
 *
 */
package jp.sigre.selenium.trade;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import jp.sigre.LogMessage;

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
			ColumnPositionMappingStrategy<TradeDataBean> strat = new ColumnPositionMappingStrategy<TradeDataBean>();
			strat.setType(TradeDataBean.class);
			strat.setColumnMapping(HEADER);
			CsvToBean<TradeDataBean> csv = new CsvToBean<TradeDataBean>();
			return csv.parse(strat, reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String[] csvToIdPass(File file) {
		CSVReader reader = null;
		try {

			reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "SJIS"));
			String[] nextLine = reader.readNext();
			System.out.println(nextLine.length);
			return nextLine;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            br.close();
	            fr.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    return bean;
	}

	private void readIniLine(String line, IniBean bean) {
		if (line.startsWith("LS_FilePath")) bean.setlS_FilePath(getLS_FilePath(line));
		if (line.startsWith("ID_FilePath")) bean.setiD_FilePath(getID_FilePath(line));
		if (line.startsWith("["))			getUseMethod(line, bean);

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

	public void makeTradeDataFile(List<TradeDataBean> list, String outPath) {
		writeFile("\"code\",\"dayTime\",\"type\",\"entryMethod\",\"exitMethod\",\"MINI_CHECK_flg\","
				+ "\"realEntryVolume\",\"entry_money\"", outPath, "trade_remains.csv");

		for (TradeDataBean bean : list) {
			writeFile(bean.toCSV(), outPath, "trade_remains.csv");
		}

		new LogMessage().writeInLog("売買失敗件数：" + list.size(), outPath);
	}


	public void writeFile(String writing,String outPath, String fileName){

		String logFilePath = outPath + File.separator + fileName;

		System.out.println(logFilePath);
		File file = new File(logFilePath);
		//			File folder = new File(file_name);
		//			folder.mkdirs();

		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		try{
			//				File file = new File(newFile);

			FileWriter filewriter = new FileWriter(file,true);
			filewriter.write(writing );
			filewriter.close();
		}catch(IOException e){
			System.out.println(e);
		}

		System.out.print(writing);
	}

	public String getIdPassFilePath(String strFolderPath) {
		return strFolderPath + File.separator + "idpassword.fbs";
	}

	public String getBuyDataFilePath(String strFolderPath) {

		String strFilePath = strFolderPath + "\\" + getBuyDataFileName();

		return strFilePath;
	}

	public String getsellDataFilePath(String strFolderPath) {

		String strFilePath = strFolderPath + "\\" + getSellDataFileName();

		return strFilePath;
	}

	public String getMovedTradeDataPath(String strFolderPath) {

		String strFilePath = strFolderPath + "\\old\\" + getBuyDataFileName();

		return strFilePath;
	}

	private String getBuyDataFileName() {

		return getTodayDate() + "_L.csv";
	}

	private String getSellDataFileName() {

		return getTodayDate() + "_S.csv";
	}

	private String getTodayDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateToday = new Date();

		return sdf.format(dateToday);
	}

}
