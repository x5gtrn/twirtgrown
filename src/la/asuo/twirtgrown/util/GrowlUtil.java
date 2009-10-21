package la.asuo.twirtgrown.util;

import info.growl.Growl;
import info.growl.GrowlException;
import info.growl.GrowlUtils;

import java.awt.image.RenderedImage;

public class GrowlUtil {

	private static GrowlUtil instance = null;
	private static final String GROWL_NOTIFICATION_NAME  = "NN";	// TODO Change growl name.
	private Growl growl;
	
	private GrowlUtil() {
		growl = GrowlUtils.getGrowlInstance("twirtgrown");
		growl.addNotification(GROWL_NOTIFICATION_NAME, true);
		try {
			growl.register();
		} catch (GrowlException e) {
			e.printStackTrace();
		}
	}
	
	public static GrowlUtil getInstance() {
		if (instance == null) {
			instance = new GrowlUtil();
		}
		return instance;
	}
	
	public void sendNotification(String title, String body) {
		try {
			growl.sendNotification(GROWL_NOTIFICATION_NAME, title, body);
		} catch (GrowlException e) {
			e.printStackTrace();
		}
	}
	
	public void sendNotification(String title, String body, RenderedImage icon) {
		if (icon == null) {
			sendNotification(title, body);
		} else {
			try {
				growl.sendNotification(GROWL_NOTIFICATION_NAME, title, body, icon);
			} catch (GrowlException e) {
				e.printStackTrace();
			}
		}
	}
}
