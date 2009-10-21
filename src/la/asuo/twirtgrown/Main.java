package la.asuo.twirtgrown;

import la.asuo.twirtgrown.util.GrowlUtil;
import la.asuo.twirtgrown.util.Util;

public class Main {
	public static final String APP_NAME = "twirtgrown";
	
	public static void main(String[] args) {
		String[] idPass = Util.getLoginIdPass();
		String id = idPass[0];
		String pass = idPass[1];
		
		GrowlUtil growl = GrowlUtil.getInstance();
		growl.sendNotification(
				APP_NAME,
				APP_NAME + " is launched.\nlogin id: " + idPass[0]);
		
		Twirtgrown twirtgrown = new Twirtgrown();
		twirtgrown.proceed(id, pass);
	}
}
