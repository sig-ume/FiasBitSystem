import java.io.File;
import java.io.IOException;

/**
 *
 */

/**
 * @author sigre
 *
 */
public class SampleMain {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String defPath = System.getProperty("user.dir");
		File defFile = new File(defPath);
		System.out.println(defFile.getAbsolutePath());
	}

}
