package la.asuo.twirtgrown.util;

import info.growl.Growl;
import info.growl.GrowlException;
import info.growl.GrowlUtils;

import java.awt.image.RenderedImage;

import la.asuo.twirtgrown.Main;

import org.apache.log4j.Logger;

public class GrowlUtil {

	private static GrowlUtil instance = null;
	private static final String GROWL_NOTIFICATION_NAME  = Main.APP_NAME + "GNN";
	private Growl growl;
	
	Logger logger = Logger.getLogger(Main.APP_NAME);
	
	private GrowlUtil() {
		growl = GrowlUtils.getGrowlInstance(Main.APP_NAME);
		growl.addNotification(GROWL_NOTIFICATION_NAME, true);
		try {
			growl.register();
		} catch (GrowlException e) {
			logger.error("GrowlException", e);
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
			logger.error("GrowlException", e);
		}
	}
	
	public void sendNotification(String title, String body, RenderedImage icon) {
		if (icon == null) {
			sendNotification(title, body);
		} else {
			try {
				growl.sendNotification(GROWL_NOTIFICATION_NAME, title, body, icon);
			} catch (GrowlException e) {
				logger.error("GrowlException", e);
			}
		}
	}
}
