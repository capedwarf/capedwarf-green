package org.jboss.lhotse.common;

/**
 * Lhotse constants.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class Constants
{
   public static final String HTTP = "http";
   public static final String HOST = "://%1s.appspot.com";

   public static final String IGNORE_GZIP = "Ignore-GZIP";

   public static final String ADMINISTRATOR = "administrator";

   public static final String Lhotse = "Lhotse_";
   public static final String lhotse = "lhotse";
   public static final String LHOTSE_ = "lhotse_";

   public static final String DATA = "data.";

   public static final String LATITUDE = "latitude";
   public static final String LONGITUDE = "longitide";
   public static final String ALTITUDE = "altitude";
   public static final String TOPIC = "topic";
   public static final String TIMESTAMP = "timestamp";
   public static final String EVENT = "event";
   public static final String CLIENT_ID = "client_id";
   public static final String CLIENT_TOKEN = "client_token";

   public static final int HQ_LATITUDE = 45966564;
   public static final int HQ_LONGITUDE = 14297391;

   public static final int MIO = 1000000;

   public static final long SECOND = 1000l;
   public static final long MINUTE = SECOND * 60l;
   public static final long HOUR = MINUTE * 60l;
   public static final long THREE_HOURS = HOUR * 3l;
   public static final long TWELVE_HOURS = HOUR * 12l;
   public static final long DAY = HOUR * 24l;
   public static final long THREE_DAYS = DAY * 3l;
   public static final long WEEK = DAY * 7l;
   public static final long TWO_WEEK = WEEK * 2l;
   public static final long MONTH = DAY * 30l;

   public static final String TAG_CONNECTION = ""; // TODO
}
