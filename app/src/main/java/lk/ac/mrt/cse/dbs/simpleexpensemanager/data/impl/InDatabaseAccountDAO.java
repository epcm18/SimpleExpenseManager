package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.Account_No;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.Account_Balance;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.Bank_Name;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.Account_Holder;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLConnector.TABLE_ACCOUNT;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class InDatabaseAccountDAO implements AccountDAO {
    private SQLiteDatabase database;
    private final SQLConnector sqlconnect;

    public InDatabaseAccountDAO(Context context) {
        this.sqlconnect = new SQLConnector(context);
    }
    @Override
    public List<String> getAccountNumbersList(){
        database = sqlconnect.getReadableDatabase();

        String[] projection = {
                Account_No
        };

        Cursor cursor = database.query(
                TABLE_ACCOUNT,
                projection,
                null,
                null,
                null,
                null,
                null );

        List<String> accountNumbers = new ArrayList<String>();

        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(Account_No));
            accountNumbers.add(accountNumber);
            System.out.println(accountNumber);
        }

        cursor.close();
        database.close();
        return accountNumbers;
    }
    @Override
    public List<Account> getAccountsList(){
        database = sqlconnect.getReadableDatabase();

        String[] projection = {
                Account_No,
                Bank_Name,
                Account_Holder,
                Account_Balance
        };

        Cursor cursor = database.query(
                TABLE_ACCOUNT,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<Account> accounts = new ArrayList<Account>();

        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String accountNumber = cursor.getString(cursor.getColumnIndexOrThrow(Account_No));
            String bankName = cursor.getString(cursor.getColumnIndexOrThrow(Bank_Name));
            String accountHolderName = cursor.getString((cursor.getColumnIndexOrThrow(Account_Holder)));
            double accountBalance = cursor.getDouble((cursor.getColumnIndexOrThrow(Account_Balance)));
            Account account = new Account(accountNumber, bankName, accountHolderName, accountBalance);
            accounts.add(account);
        }

        cursor.close();
        database.close();
        return accounts;
    }
    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException{
        database = sqlconnect.getReadableDatabase();

        String[] projection = {
                Account_No,
                Bank_Name,
                Account_Holder,
                Account_Balance
        };

        String selection = Account_No + " =?";
        String[] args = {accountNo};

        Cursor cursor = database.query(
                TABLE_ACCOUNT,
                projection,
                selection,
                args,
                null,
                null,
                null
        );

        if (cursor==null) {
            String message = "Invalid User";
            database.close();
            throw new InvalidAccountException(message);
        }

        else {
            cursor.moveToFirst();

            Account account = new Account(accountNo, cursor.getString(cursor.getColumnIndexOrThrow(Bank_Name)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Account_Holder)), cursor.getDouble(cursor.getColumnIndexOrThrow(Account_Balance)));
            database.close();
            return account;
        }
    }
    @Override
    public void addAccount(Account account){
        database = sqlconnect.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Account_No, account.getAccountNo());
        values.put(Bank_Name, account.getBankName());
        values.put(Account_Holder, account.getAccountHolderName());
        values.put(Account_Balance, account.getBalance());

        database.insert(TABLE_ACCOUNT, null, values);
        database.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        database = sqlconnect.getWritableDatabase();
        database.delete(TABLE_ACCOUNT, "Account_No" + " =?", new String [] {accountNo} );
        database.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException{
        database = sqlconnect.getWritableDatabase();

        String[] projection = {
                Account_Balance
        };

        String selection = Account_No + " =?";
        String[] args = {accountNo};

        Cursor cursor = database.query(
                TABLE_ACCOUNT,
                projection,
                selection,
                args,
                null,
                null,
                null
        );

        double balance ;

        if (cursor.moveToFirst()){
            balance = cursor.getDouble(0);
        }
        else {
            String message = "Invalid User";
            throw new InvalidAccountException(message);
        }

        ContentValues values = new ContentValues();
        if (expenseType==ExpenseType.INCOME){
            values.put(Account_Balance, balance + amount);
        }
        else{
            values.put(Account_Balance, balance - amount);
        }

        database.update(TABLE_ACCOUNT, values, Account_No + " =?", new String[] {accountNo});

        cursor.close();
        database.close();
    }
}
