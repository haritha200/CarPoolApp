package database;

/**
 * Created by haritha on 2/6/17.
 */

public class AppDbSchema {
    public static final class AppTable{
        public static final String NAME= "AppTable";

        public static final class Cols{
            public static final String COUNTLIST= "countlist";
            public static final String LASTUSED= "lastused";
            public static final String COUNT= "count";
            public static final String PKGNAME="pkgname";
            public static final String TIMEUSED="timeused";
            public static final String WORLDAVGLIST="worldavglist";

            public static final String PREVCOUNTLIST="prevcountlist";

        }

    }



}