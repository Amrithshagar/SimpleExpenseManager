package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentMemoryTransactionDAO implements TransactionDAO {
    private DBhelper dataBaseHelper;

    public PersistentMemoryTransactionDAO(DBhelper dataBaseHelper) {
        this.dataBaseHelper = dataBaseHelper;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat date_1 = new SimpleDateFormat("yyyy-mm-dd");
        String strDate = date_1.format(date);
        dataBaseHelper.addTransaction(strDate,accountNo,expenseType.toString(),amount);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return dataBaseHelper.getAllTransactions();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        return dataBaseHelper.getAllTransactionsLimited(limit);
    }
}
