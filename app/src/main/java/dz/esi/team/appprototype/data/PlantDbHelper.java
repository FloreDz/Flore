package dz.esi.team.appprototype.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by The King Mohamed on 25/03/2017.
 */

public class PlantDbHelper extends SQLiteOpenHelper {


    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "FloreDB.sqlite";
    private static String DB_PATH = "";
    private final Context mContext;
    private String TAG = this.getClass().getSimpleName();
    private SQLiteDatabase mDataBase;


    public PlantDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        DB_PATH = "/data/data/dz.esi.team.appprototype/databases/";
        Log.v(TAG, "DB path " + DB_PATH);
        //TODO: check the other code for sdk backward compatibility
        //TODO: do not forget to change the package name
    }

    public void createDataBase() throws IOException {
        if (!checkDataBase())  // database does not exist
        {
            Log.v("into if, DB not exist", "from PlantDbHelper class -> createDataBase method.");
            this.getReadableDatabase();
            try {
                copyDataBase();   // copy the database from assests
                Log.v(TAG, "createDatabase , database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        } else
            Log.v("DB exists!", "from PlantDbHelper class -> createDataBase method.");
    }

    // check if the database exists here: /data/data/package/databases/
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException sqle) { /* database does't exist yet. */
            Log.e(TAG, "database does't exist : " + sqle.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "2nd catch : " + e.getMessage());
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    // copy the database from assets to the just created folder
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    // open the database, so we can query it
    public void openDataBase() throws SQLException {
        String mPath = DB_PATH + DB_NAME;
        Log.v("mPath", mPath);
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null )
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

