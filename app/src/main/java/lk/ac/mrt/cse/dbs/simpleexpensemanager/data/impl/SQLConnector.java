package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLConnector extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "200368L.sqlite";
    private static final int VERSION = 1;

    // two tables to store account data and transactions
    public static final String TABLE_ACCOUNT = "Account";
    public static final String TABLE_TRANSACTION = "transaction_log";

    // column names for TABLE_ACCOUNT
    // Account_No column used in both tables as a foreign key in TABLE_TRANSACTION
    public static final String Account_No = "accountNo";

    public static final String Bank_Name = "bankName";
    public static final String Account_Holder = "accountHolderName";
    public static final String Account_Balance = "balance";

    // column names for TABLE_TRANSACTIONS
    public static final String ID = "id";
    public static final String Date = "date";
    public static final String Expense_Type = "expenseType";
    public static final String Amount = "amount";

    public SQLConnector(Context context){

        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase database){
        // executing the query for create table "TABLE_ACCOUNT"
        String query1 = "CREATE TABLE " + TABLE_ACCOUNT + "("
                + Account_No + " TEXT PRIMARY KEY, "
                + Bank_Name + " TEXT NOT NULL, "
                + Account_Holder + " TEXT NOT NULL, "
                + Account_Balance + " REAL NOT NULL)";
        database.execSQL(query1);

        // executing the query for create table "TABLE_TRANSACTION"
        String query2 = "CREATE TABLE " + TABLE_TRANSACTION + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Date + " TEXT NOT NULL, "
                + Expense_Type + " TEXT NOT NULL, "
                + Amount + " REAL NOT  NULL, "
                + Account_No + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + Account_No + ") REFERENCES " + TABLE_ACCOUNT + "(" + Account_No + "))";
        database.execSQL(query2);

    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // if table exists
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        // otherwise
        onCreate(database);
    }
}
