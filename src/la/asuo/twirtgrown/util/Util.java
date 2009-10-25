package la.asuo.twirtgrown.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

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
	
	public static String getTime() {
		return getTime(Calendar.getInstance().getTime());
	}
	
	public static String getTime(Date date) {
		return date.toString();
	}
	
	public static BufferedImage getBufferedImage(URL url) {
		InputStream in = null;
		BufferedImage image;
		if (ImageIO.getUseCache()) {
			ImageIO.setUseCache(false);
		}
		try {
			in = url.openStream();
			image = ImageIO.read(in);
		} catch (IOException e) {
			image = null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
		return image;
	}
}
