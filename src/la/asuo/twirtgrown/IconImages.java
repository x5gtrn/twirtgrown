package la.asuo.twirtgrown;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import twitter4j.User;

public class IconImages {

	private Map<String, BufferedImage> imageMap;
	private static IconImages instance = null;
	
	Logger logger = Logger.getLogger(Main.APP_NAME);
	
	private IconImages() {
		imageMap = new HashMap<String, BufferedImage>();
	}
	
	public static IconImages getInstance() {
		if (instance == null) {
			instance = new IconImages();
		}
		return instance;
	}
	
	public BufferedImage getIcon(User user) {
		String strUserId = String.valueOf(user.getId());
		if (!imageMap.containsKey(strUserId)) {
			addImage(strUserId, user.getProfileImageURL());
		}
		return (BufferedImage) imageMap.get(strUserId);
	}
	
	public BufferedImage getIcon(int userId) {
		return (BufferedImage) imageMap.get(String.valueOf(userId));
	}
	
	private boolean addImage(String strUserId, URL url) {
		imageMap.remove(strUserId);
		InputStream in = null;
		BufferedImage image;
		if (ImageIO.getUseCache()) {
			ImageIO.setUseCache(false);
		}
		try {
			in = url.openStream();
			image = ImageIO.read(in);
			image.flush();
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
		imageMap.put(strUserId, image);
		logger.debug("ADD new icon image. size=" + imageMap.size());
		return image != null;
	}
}
