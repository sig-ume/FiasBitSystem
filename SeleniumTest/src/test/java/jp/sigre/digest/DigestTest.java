package jp.sigre.digest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DigestTest {
	Digest digest = new Digest();
	static String strFolder = "C:\\Users\\sigre\\git\\SeleniumTest\\SeleniumTest\\test\\jp.sigre.digest.Digest";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File folder = new File(strFolder);
		for (File file : folder.listFiles()) {
			file.delete();
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File folder = new File(strFolder);
		for (File file : folder.listFiles()) {
			file.delete();
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMakeDigestFile_正常系1() throws IOException {
		String strFile = strFolder + File.separator + "正常系1.fbs";
		digest.makeDigestFile(strFile, 0);

		File testFile = new File(strFile);
		if (!testFile.exists()) {
			fail("No file made.");
		}

		FileReader is = new FileReader(testFile);
		BufferedReader reader = new BufferedReader(is);

		String s;
		s=reader.readLine();
		assertThat(s.length(), is(not(0)));
		s=reader.readLine();
		assertThat(s, is("0"));

		reader.close();
	}

	@Test
	public void testMakeDigestFile_正常系2() throws IOException {
		String strFile = strFolder + File.separator + "正常系2.fbs";
		digest.makeDigestFile(strFile, 2);

		File testFile = new File(strFile);
		if (!testFile.exists()) {
			fail("No file made.");
		}

		FileReader is = new FileReader(testFile);
		BufferedReader reader = new BufferedReader(is);

		String s;
		s=reader.readLine();
		assertThat(s.length(), is(not(0)));
		s=reader.readLine();
		assertThat(s, is("2"));

		reader.close();
	}

	@Test
	public void testCheckDigestFile_正常系3() {
		String strFile = strFolder + File.separator + "正常系3.fbs";
		digest.makeDigestFile(strFile, 3);
		boolean result = digest.checkDigestFile(strFile);

		assertThat(result, is(true));
	}

	@Test
	public void testCheckDigestFile_異常系1() throws IOException {
		String strFile = strFolder + File.separator + "異常系1.fbs";

		File file = new File(strFile);
		file.createNewFile();
		boolean result = digest.checkDigestFile(strFile);

		assertThat(result, is(false));
	}

	@Test
	public void testCheckDigestFile_異常系2() {
		String strFile = strFolder + File.separator + "異常系2.fbs";
		digest.makeDigestFile(strFile, 21);
		boolean result = digest.checkDigestFile(strFile);

		assertThat(result, is(false));
	}



}
