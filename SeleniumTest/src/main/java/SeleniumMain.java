import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;

public class SeleniumMain  {
	public static void main(String[] args) throws InterruptedException {

		//TODO:フォルダパスは別ファイルから
		String strFolderPath = "D:\\Program Files\\pleiades\\Juno_4.2\\workspace\\SeleniumTest\\target";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dateToday = new Date();

		String strToday = sdf.format(dateToday);

		String strFilePath = strFolderPath + "\\" + strToday + "_L.csv";

		String strIdPassPath = strFolderPath + "\\" + "idpassword.txt";

		List<TorihikiDataBean> beanList = csvToTorihikiData(new File(strFilePath));

		String[] aryIdPass = csvToIdPass(new File(strIdPassPath));

		String strId = aryIdPass[0];
		String strLoginPass = aryIdPass[1];
		String strTorihPass = aryIdPass[2];

		System.setProperty("webdriver.gecko.driver", "D:\\Program Files\\pleiades\\Juno_4.2\\workspace\\SeleniumTest\\lib\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();

		// 検索は8秒以内に終了して欲しい
		WebDriverWait waitForSearch = new WebDriverWait(driver, 8);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		WebElement element = null;

		//TODO:ID、Passは別ファイルから入力する

		driver.get("https://www.sbisec.co.jp/ETGate");

		element = driver.findElement(By.name("user_id"));

		element.sendKeys(new String[]{strId});

		element = driver.findElement(By.name("user_password"));

		element.sendKeys(new String[]{strLoginPass});

		element = driver.findElement(By.name("ACT_login"));

		element.click();

		waitForSearch.until(ExpectedConditions.presenceOfElementLocated(By.id("logout")));

		System.out.println("Page title is: " + driver.getTitle());

		for (TorihikiDataBean bean : beanList) {


			//element.wait(3000);

			driver.findElement(By.cssSelector("img[alt=\"取引\"]")).click();

			driver.findElement(By.linkText("単元未満株（S株）")).click();
			driver.findElement(By.id("genK")).click();
			// ERROR: Caught exception [Error: Dom locators are not implemented yet!]
			driver.findElement(By.name("odd_agreement")).click();
			driver.findElement(By.name("stock_sec_code")).clear();
			driver.findElement(By.name("stock_sec_code")).sendKeys(new String[]{bean.getCode()});

			driver.findElement(By.name("input_quantity")).clear();
			driver.findElement(By.name("input_quantity")).sendKeys(new String[]{bean.getRealEntryVolume()});
			driver.findElement(By.name("trade_pwd")).clear();
			driver.findElement(By.name("trade_pwd")).sendKeys(new String[]{strTorihPass});
			driver.findElement(By.name("skip_estimate")).click();
			driver.findElement(By.name("ACT_place")).click();



		}

		driver.quit();
	}


	public static List<TorihikiDataBean> csvToTorihikiData(File file) {
		final String[] HEADER = new String[] { "code","dayTime","type","entryMethod","exitMethod","MINI_CHECK_flg","realEntryVolume","entry_money" };

		try {
			CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(file), "SJIS"), ',', '"', 1);
			ColumnPositionMappingStrategy<TorihikiDataBean> strat = new ColumnPositionMappingStrategy<TorihikiDataBean>();
			strat.setType(TorihikiDataBean.class);
			strat.setColumnMapping(HEADER);
			CsvToBean<TorihikiDataBean> csv = new CsvToBean<TorihikiDataBean>();
			return csv.parse(strat, reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String[] csvToIdPass(File file) {
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
			};
		}
	}
}