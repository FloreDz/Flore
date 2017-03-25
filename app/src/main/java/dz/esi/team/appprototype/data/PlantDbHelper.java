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
    private static String DB_PATH = "/data/data/dz.esi.team.appprototype.data/databases/";
    private final Context mContext;
    private String TAG = this.getClass().getSimpleName();
    private SQLiteDatabase mDataBase;


    public PlantDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
        //TODO: check the other code for sdk backward compatibility
    }

    public void createDataBase() throws IOException {
        if (!checkDataBase())  // database does not exist
        {
            this.getReadableDatabase();
            try {
                copyDataBase();   // copy the database from assests
                Log.e(TAG, "createDatabase , database created");
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    //Check that the database exists here: /data/data/package/databases/DB_NAME
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) { /* database does't exist yet. */ }

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
        if (mDataBase != null)
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
