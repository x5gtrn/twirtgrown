package la.asuo.twirtgrown.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Util {
	private Util() {}
	
	public static String[] getLoginIdPass() {
		String[] idPass = new String[2];
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			file = new File("lib/_idpass.txt");
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			if (br.ready()) {
				idPass[0] = br.readLine();
				idPass[1] = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fr != null) {
					br.close();
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return idPass;
	}
}
