package la.asuo.twirtgrown;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import la.asuo.twirtgrown.util.GrowlUtil;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class Twirtgrown {
	public static final int DELAY_TIME = 180 * 1000;	// milliseconds
	public static final int SLEEP_MINUTES = 10;	// minutes
	
	public void proceed(String id, String pass) {
		List<Status> statuses = null;
		Twitter twitter;
		try {
			twitter = new Twitter(id, pass);
			
			// first time
			statuses = twitter.getFriendsTimeline();
			long lastSendedId = sendToGrowlInAscOrder(statuses);
			
			// second time or later
			while (true) {
				statuses = getNewTimeline(twitter, lastSendedId);
				if (statuses.size() > 0) {
					lastSendedId = sendToGrowlInAscOrder(statuses);
				} else {
					// if no new tweets at timeline, wait DELY_TIME and continue.
					try {
						Thread.sleep(DELAY_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (TwitterException e) {
			// TODO check other Exception from Twitter.
			if (e.getMessage().indexOf("Rate limit") > 0) {
				GrowlUtil growl = GrowlUtil.getInstance();
				growl.sendNotification(
						Main.APP_NAME,
						"Rate limit exceeded.\n"
						+ Main.APP_NAME + " will sleep " + SLEEP_MINUTES + " min.");
				try {
					Thread.sleep(SLEEP_MINUTES + 60 * 1000);
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				proceed(id, pass);
			} else {
				e.printStackTrace();
			}
			System.exit(1);
		}
	}
	
	private List<Status> getNewTimeline(Twitter twitter, long sinceId) throws TwitterException {
		List<Status> newTimeLine = new ArrayList<Status>();
		int page = 1;
		Paging paging = new Paging();
		paging.setCount(20);
		paging.setSinceId(sinceId);
		while (true) {
			paging.setPage(page++);
			List<Status> statuses = twitter.getFriendsTimeline(paging);
			if (statuses.size() > 0) {
				newTimeLine.addAll(statuses);
			} else {
				break;
			}
		}
		return newTimeLine;
	}
	
	private long sendToGrowlInAscOrder(List<Status> statuses) {
		Collections.reverse(statuses);
		long lastSendedId = 0L;
		for (Status status : statuses) {
			String name = status.getUser().getName();
			String text = status.getText();
			if (name == null || text == null) {
				continue;
			}
			User user = status.getUser();
			BufferedImage icon;
			try {
				icon = ImageIO.read(user.getProfileImageURL().openStream());
			} catch (IOException e) {
				icon = null;
			}
			
			long diffTime = (status.getCreatedAt().getTime() + DELAY_TIME) - System.currentTimeMillis();
			if (diffTime > 0) {
				try {
					Thread.sleep(diffTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			GrowlUtil growl = GrowlUtil.getInstance();
			growl.sendNotification(name, text, icon);
			lastSendedId = status.getId();
		}
		return lastSendedId;
	}

}
