package la.asuo.twirtgrown;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import la.asuo.twirtgrown.util.GrowlUtil;
import la.asuo.twirtgrown.util.Util;

import org.apache.log4j.Logger;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class Twirtgrown {
	public static final int DELAY_TIME = 180 * 1000;	// milliseconds
	public static final int SLEEP_MINUTES = 10;	// minutes
	public static final int IGNORE_DIFF_SECONDS = 10; // seconds
	
	Logger logger = Logger.getLogger(Main.APP_NAME);
	
	public void proceed(String id, String pass) {
		List<Status> statuses = null;
		Twitter twitter;
		try {
			twitter = new Twitter(id, pass);
			
			// first time
			logger.info("getting timeline first time.");
			statuses = twitter.getFriendsTimeline();
			long lastSendedId = sendToGrowlInAscOrder(statuses, 0L);
			
			// second time or later
			while (true) {
				logger.debug("getting new timeline...");
				statuses = getNewTimeline(twitter, lastSendedId);
				logger.info("getting new timeline. statuses's count=" + statuses.size());
				if (statuses.size() > 0) {
					lastSendedId = sendToGrowlInAscOrder(statuses, lastSendedId);
				} else {
					// if no new tweets at timeline, wait DELY_TIME and continue.
					try {
						Thread.sleep(DELAY_TIME);
					} catch (InterruptedException e) {
						logger.error("InterruptedException.", e);
					}
				}
			}
		} catch (TwitterException e) {
			// TODO check other Exception from Twitter.
			if (e.getMessage().indexOf("Rate limit") > 0) {
				logger.warn("TwitterException. Rate limit exceeded.");
				GrowlUtil growl = GrowlUtil.getInstance();
				growl.sendNotification(
						Main.APP_NAME,
						"Rate limit exceeded.\n"
						+ Main.APP_NAME + " will sleep " + SLEEP_MINUTES + " min.");
				try {
					Thread.sleep(SLEEP_MINUTES * 60 * 1000);
				} catch (InterruptedException ie) {
					logger.error("InterruptedException.", ie);
				}
				logger.info("proceed again.");
				proceed(id, pass);
			} else {
				logger.error("TwitterException.", e);
			}
			System.exit(1);
		} catch (Throwable t) {
			logger.error("error occured in proceed. ", t);
		}
	}
	
	private List<Status> getNewTimeline(Twitter twitter, long sinceId) throws TwitterException {
		List<Status> newTimeLine = new ArrayList<Status>();
		int page = 1;
		Paging paging = new Paging();
		paging.setCount(20);
		if (sinceId != 0L) {
			paging.setSinceId(sinceId);
		}
		while (true) {
			paging.setPage(page++);
			List<Status> statuses = twitter.getFriendsTimeline(paging);
			if (statuses.size() > 0) {
				if ((statuses.get(0).getCreatedAt().getTime() + DELAY_TIME) - System.currentTimeMillis() < 0) {
					logger.warn("older status before DELAY_TIME. rest are ignored.");
					break;
				}
				newTimeLine.addAll(statuses);
			} else {
				break;
			}
		}
		return newTimeLine;
	}
	
	private long sendToGrowlInAscOrder(List<Status> statuses, long lastSendedId) {
		Collections.reverse(statuses);
		for (Status status : statuses) {
			if (status.getId() == lastSendedId) {
				logger.debug("DUPLICATE!: " + lastSendedId);
				continue;
			}
			if (status.getId() < lastSendedId) {
				logger.debug("LOW ID! ignore: " + lastSendedId);
				continue;
			}
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
					logger.error("InterruptedException.", e);
				}
			} else if (diffTime < IGNORE_DIFF_SECONDS * -1000){
				logger.warn("time-lag over IGNORE_DIFF_SEC! ignore: diffTime=" + diffTime);
				continue;
			}
			GrowlUtil growl = GrowlUtil.getInstance();
			growl.sendNotification(name, text, icon);
			lastSendedId = status.getId();
			
			logger.debug("send status createdAt: " + Util.getTime(status.getCreatedAt()));
		}
		return lastSendedId;
	}
}
