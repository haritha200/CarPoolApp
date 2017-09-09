package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import database.AppDbSchema.AppTable;

/**
 * Created by haritha on 2/6/17.
 */
public class AppDbHelper extends SQLiteOpenHelper {

        public static final int VERSION= 3;
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
                 //   AppTable.Cols.UUID+", "+
                    AppTable.Cols.COUNTLIST+","+
                    AppTable.Cols.PREVCOUNTLIST+","+
                    AppTable.Cols.PKGNAME+","+
                    AppTable.Cols.LASTUSED+","+
                    AppTable.Cols.TIMEUSED+","+
                    AppTable.Cols.WORLDAVGLIST+","+
                    AppTable.Cols.COUNT+")"
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
