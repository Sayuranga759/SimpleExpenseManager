package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE = "200532K.sqlite";
    private static final int VERSION = 2;

    //TABLES
    public static final String ACCOUNT = "account";
    public static final String TRANSACTION = "transac";

    //COMMON COLUMNS
    public static final String ACCOUNT_NO = "accountNo";

    //ACCOUNT TABLE - COLUMNS
    public static final String BANK = "bankName";
    public static final String ACC_HOLDER = "accountHolder";
    public static final String BALANCE = "balance";

    //TRANSACTION TABLE - COLUMNS
    public static final String ID = "id";
    public static final String DATE = "date";
    public static final String MONEY_TYPE = "moneyType";
    public static final String AMOUNT = "amount";


    public DBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        String query1 = "CREATE TABLE " + ACCOUNT + "(" +
                ACCOUNT_NO + " TEXT PRIMARY KEY, " +
                BANK + " TEXT ," +
                ACC_HOLDER + " TEXT , " +
                BALANCE + " DOUBLE)";


        String query2 = "CREATE TABLE " + TRANSACTION + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DATE + " TEXT , " +
                MONEY_TYPE + " TEXT , " +
                AMOUNT + " DOUBLE , " +
                ACCOUNT_NO + " TEXT," +
                "FOREIGN KEY (" + ACCOUNT_NO + ") REFERENCES " + ACCOUNT + "(" + ACCOUNT_NO + "))";

        DB.execSQL(query1);
        DB.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        DB.execSQL("drop table if exists " + ACCOUNT);
        DB.execSQL("drop table if exists " + TRANSACTION);

        // create new tables
        onCreate(DB);
    }

}