package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InDatabaseAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InDatabaseTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentExpenseManager extends ExpenseManager {
    // private Context context;

    public PersistentExpenseManager(Context context) {
        super(context);
        setup(context);
    }

    @Override
    public void setup(Context context) {
        TransactionDAO new_transactionDAO = new InDatabaseTransactionDAO(context);
        setTransactionsDAO(new_transactionDAO);

        AccountDAO new_accountDAO = new InDatabaseAccountDAO(context);
        setAccountsDAO(new_accountDAO);
        /*
        Account dummyAcct1 = new Account("98765A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);
        */


    }
}
