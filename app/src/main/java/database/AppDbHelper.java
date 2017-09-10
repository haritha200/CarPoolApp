package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import database.AppDbSchema.AppTable;
import database.AppDbSchema.UserTable;
import database.AppDbSchema.MatchTable;



/**
 * Created by haritha on 2/6/17.
 */
public class AppDbHelper extends SQLiteOpenHelper {

        public static final int VERSION= 1;
        public static final String DATABASENAME= "appdb.db";  //for the constructor

       // private static final String DATABASE_ALTER_APP_1 = "ALTER TABLE "
//            + AppTable.NAME + " ADD COLUMN " + AppTable.Cols.TIMEUSEDLIST + " string;";

        public AppDbHelper(Context context) {
            super(context, DATABASENAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            //CAREFUL WITH THE COMMAS AND SPACING !!
            db.execSQL("create table "+AppTable.NAME+" ("+
                    " _id integer primary key autoincrement, "+
                    AppTable.Cols.TIME+","+
                    AppTable.Cols.TITLE+","+
                    AppTable.Cols.PHONE+","+
                    AppTable.Cols.PICKUP+","+
                    AppTable.Cols.FLAT+","+
                    AppTable.Cols.DATE+","+
                    AppTable.Cols.MATCHED+","+
                    AppTable.Cols.CRIMEID+")"
            );

            db.execSQL("create table "+ UserTable.NAME+" ("+
                    " _id integer primary key autoincrement, "+
                    UserTable.Cols.USERID+")"
            );

            db.execSQL("create table "+MatchTable.NAME+" ("+
                    " _id integer primary key autoincrement, "+
                    MatchTable.Cols.M_DATE+","+
                    MatchTable.Cols.M_FLAT+","+
                    MatchTable.Cols.M_PHONE+","+
                    MatchTable.Cols.M_PICKUP+","+
                    MatchTable.Cols.M_PNAME+","+
                    MatchTable.Cols.M_TIME+","+
                    MatchTable.Cols.TRIPID+")"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*    if (oldVersion < 2) {
                db.execSQL(DATABASE_ALTER_APP_1);
            } */

         //   db.execSQL("DROP TABLE IF EXISTS " + AppTable.NAME);
          //  onCreate(db);
        }
}
