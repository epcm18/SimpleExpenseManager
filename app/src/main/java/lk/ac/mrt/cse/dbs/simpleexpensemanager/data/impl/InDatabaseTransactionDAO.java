package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.Account_No;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.Amount;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.Date;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.Expense_Type;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.TABLE_ACCOUNT;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.TABLE_TRANSACTION;

public class InDatabaseTransactionDAO implements TransactionDAO {
    private final SQLConnector sqlConnector;
    private SQLiteDatabase database;

    public InDatabaseTransactionDAO(Context context){
        this.sqlConnector = new SQLConnector(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount){
        database = sqlConnector.getWritableDatabase();
        DateFormat dateformat = new SimpleDateFormat("dd-mm-yyyy");
        ContentValues values = new ContentValues();
        values.put(Date, dateformat.format(date));
        values.put(Account_No, accountNo);
        values.put(Expense_Type, String.valueOf(expenseType));
        values.put(Amount, amount);

        database.insert(TABLE_TRANSACTION, null, values);
        database.close();
    }
    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        database = sqlConnector.getReadableDatabase();

        String[] projection = {
                Date,
                Account_No,
                Expense_Type,
                Amount
        };

        Cursor cursor = database.query(
                TABLE_TRANSACTION,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<Transaction> transactions = new ArrayList<Transaction>();

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(Date));
            Date dateType = new SimpleDateFormat("dd-mm-yyyy").parse(date);
            String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(Account_No));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(Expense_Type));
            ExpenseType expenseType = ExpenseType.valueOf(type);
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(Amount));
            Transaction transaction = new Transaction(dateType, accountNumber, expenseType, amount);

            transactions.add(transaction);
        }
        cursor.close();
        database.close();
        return transactions;
    }
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        database = sqlConnector.getReadableDatabase();

        String[] projection = {
                Date,
                Account_No,
                Expense_Type,
                Amount
        };

        Cursor cursor = database.query(
                TABLE_TRANSACTION,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<Transaction> transactions = new ArrayList<Transaction>();

        int size = cursor.getCount();

        while (cursor.moveToNext()){
            String date = cursor.getString(cursor.getColumnIndexOrThrow(Date));
            Date dateType = new SimpleDateFormat("dd-mm-yyyy").parse(date);
            String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(Account_No));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(Expense_Type));
            ExpenseType expenseType = ExpenseType.valueOf(type);
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(Amount));
            Transaction transaction = new Transaction(dateType, accountNumber, expenseType, amount);

            transactions.add(transaction);
        }

        if (size<=limit){
            database.close();
            return transactions;
        }
        database.close();
        return transactions.subList(size-limit, size);
    }
}
