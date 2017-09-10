package database;

/**
 * Created by haritha on 2/6/17.
 */

public class AppDbSchema {
    public static final class AppTable {
        public static final String NAME = "AppTable";

        public static final class Cols {
            public static final String CRIMEID = "crimeid";
            public static final String TITLE = "title";
            public static final String PICKUP = "pickup";
            public static final String FLAT = "flat";
            public static final String PHONE = "phone";
            public static final String DATE = "date";
            public static final String TIME = "time";
            public static final String MATCHED = "matched";

        }

    }

    public static final class UserTable {
        public static final String NAME = "UserTable";

        public static final class Cols {
            public static final String USERID = "userid";
        }

    }
    public static final class MatchTable {
        public static final String NAME = "MatchTable";

        public static final class Cols {
            public static final String TRIPID = "tripid";
            public static final String M_PNAME = "name";
            public static final String M_FLAT = "flat";
            public static final String M_PHONE = "phone";
            public static final String M_TIME = "time";
            public static final String M_PICKUP = "pickup";
            public static final String M_DATE = "date";
        }

    }
}