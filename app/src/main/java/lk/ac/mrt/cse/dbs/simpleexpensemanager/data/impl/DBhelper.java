package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBhelper extends SQLiteOpenHelper {
    public static final String DATABASE = "Accounts";

    private static final String ACCOUNT_TABLE = "Account_Details";
    private static final String ACCOUNT_NO = "accountNo";
    private static final String BANK_NAME = "bankName";
    private static final String ACC_HOLDER_NAME = "accountHolderName";
    private static final String BALANCE = "balance";

    private static final String TRANSACTION_TABLE = "Transaction_Details";
    private static final String TRANSACTION_ID = "transactionID";
    private static final String TRANS_DATE = "transactionDate";
    private static final String EXPENSE_TYPE = "expenseType";
    private static final String TRANS_AMOUNT = "transactionAmount";

    public DBhelper(Context context){
        super(context, DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        StringBuilder sql1;
        sql1 = new StringBuilder()
                .append("CREATE TABLE ")
                .append(ACCOUNT_TABLE)
                .append("(")
                .append(ACCOUNT_NO)
                .append(" VARCHAR(30) primary key, ")
                .append(BANK_NAME)
                .append(" text(100), ")
                .append(ACC_HOLDER_NAME)
                .append(" text(200), ")
                .append(BALANCE)
                .append(" NUMERIC(12,2),deleted INT(1) default 0) ");
        sqLiteDatabase.execSQL(sql1.toString());
        StringBuilder sql2;
        sql2 = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TRANSACTION_TABLE)
                .append("(")
                .append(TRANSACTION_ID)
                .append(" INTEGER primary key AUTOINCREMENT, ")
                .append(ACCOUNT_NO)
                .append(" VARCHAR(30), ")
                .append(TRANS_DATE)
                .append(" DATE, ")
                .append(EXPENSE_TYPE)
                .append(" text(15), ")
                .append(TRANS_AMOUNT)
                .append(" NUMERIC(12,2),deleted int(1) default 0, FOREIGN KEY(accountNo) REFERENCES Account_Details(accountNo)) ");
        sqLiteDatabase.execSQL(sql2.toString());

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Account_Details");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Transaction_Details");
        onCreate(sqLiteDatabase);

    }
    public boolean addAccount(Account account) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_NO,account.getAccountNo());
        contentValues.put(BANK_NAME,account.getBankName());
        contentValues.put(ACC_HOLDER_NAME,account.getAccountHolderName());
        contentValues.put(BALANCE,account.getBalance());

        sqLiteDatabase.insert(ACCOUNT_TABLE,null,contentValues);
        return true;
    }
    public boolean addTransaction(String ACCOUNT_NO, String EXPENSE_TYPE, String TRANS_DATE, double TRANS_AMOUNT) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo",ACCOUNT_NO);
        contentValues.put("expenseType",EXPENSE_TYPE);
        contentValues.put("transactionDate",TRANS_DATE);
        contentValues.put("transactionAmount",TRANS_AMOUNT);

        sqLiteDatabase.insert(TRANSACTION_TABLE,null,contentValues);
        return true;
    }
    public boolean deleteAccount (String ACCOUNT_NO) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deleted", 1);
        sqLiteDatabase.update(ACCOUNT_TABLE, contentValues, "accountNo = ? ", new String[]{ACCOUNT_NO});
        return true;
    }
    public boolean updateBalance (String ACCOUNT_NO,double BALANCE) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("balance", BALANCE);
        sqLiteDatabase.update(ACCOUNT_TABLE, contentValues, "accountNo = ? ", new String[]{ACCOUNT_NO});
        return true;
    }
    public ArrayList<Account> getAllAccounts() {
        ArrayList<Account> array_list = new ArrayList<Account>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor result =  sqLiteDatabase.rawQuery( "SELECT * FROM Account_Details WHERE deleted=0", null );
        result.moveToFirst();

        while(result.isAfterLast() == false){
            Account account = new Account(result.getString(result.getColumnIndex(ACCOUNT_NO)),
                    result.getString(result.getColumnIndex(BANK_NAME)),
                    result.getString(result.getColumnIndex(ACC_HOLDER_NAME)),
                    result.getDouble(result.getColumnIndex(BALANCE)));
            array_list.add(account);
            result.moveToNext();
        }
        result.close();
        return array_list;
    }
    public Account getAccount(String accNo) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor result =  sqLiteDatabase.rawQuery( "SELECT * FROM Account_Details WHERE accountNo= '"+ accNo +"' and deleted=0", null );
        result.moveToFirst();
        while(result.isAfterLast() == false){
            Account account = new Account(result.getString(result.getColumnIndex(ACCOUNT_NO)),
                    result.getString(result.getColumnIndex(BANK_NAME)),
                    result.getString(result.getColumnIndex(ACC_HOLDER_NAME)),
                    result.getDouble(result.getColumnIndex(BALANCE)));
            return account;
        }
        result.close();
        return null;
    }
    public ArrayList<String> getAllAccountNumbers() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor result =  sqLiteDatabase.rawQuery( "SELECT * FROM Account_Details WHERE deleted=0", null );
        result.moveToFirst();

        while(result.isAfterLast() == false){
            array_list.add(result.getString(result.getColumnIndex(ACCOUNT_NO)));
            result.moveToNext();
        }
        result.close();
        return array_list;
    }
    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> array_list = new ArrayList<Transaction>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor result =  sqLiteDatabase.rawQuery( "SELECT * FROM Transaction_Details WHERE deleted=0", null );
        result.moveToFirst();
        while(result.isAfterLast() == false){
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            try {
                date = dateFormat.parse(result.getString(result.getColumnIndex(TRANS_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction account;
            if(result.getString(result.getColumnIndex(EXPENSE_TYPE))== "EXPENSE") {
                account = new Transaction(date, result.getString(result.getColumnIndex(ACCOUNT_NO)),
                        ExpenseType.EXPENSE , result.getDouble(result.getColumnIndex(TRANS_AMOUNT)));
            }else{
                account = new Transaction(date, result.getString(result.getColumnIndex(ACCOUNT_NO)),
                        ExpenseType.INCOME , result.getDouble(result.getColumnIndex(TRANS_AMOUNT)));
            }
            array_list.add(account);
            result.moveToNext();
        }
        result.close();
        return array_list;
    }
    public ArrayList<Transaction> getAllTransactionsLimited(int limit) {
        ArrayList<Transaction> array_list = new ArrayList<Transaction>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor result =  sqLiteDatabase.rawQuery( "SELECT * FROM Transaction_Details WHERE deleted=0 ORDER BY transactionID DESC LIMIT "+Integer.toString(limit), null );
        result.moveToFirst();
        while(result.isAfterLast() == false){
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            try {
                date = dateFormat.parse(result.getString(result.getColumnIndex(TRANS_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Transaction account;

            if(result.getString(result.getColumnIndex(EXPENSE_TYPE))== "EXPENSE") {
                account = new Transaction(date, result.getString(result.getColumnIndex(ACCOUNT_NO)),
                        ExpenseType.EXPENSE , result.getDouble(result.getColumnIndex(TRANS_AMOUNT)));
            }else{
                account = new Transaction(date, result.getString(result.getColumnIndex(ACCOUNT_NO)),
                        ExpenseType.INCOME , result.getDouble(result.getColumnIndex(TRANS_AMOUNT)));
            }
            array_list.add(account);
            result.moveToNext();
        }
        result.close();
        return array_list;
    }
    public boolean isAccountValid(String accNo) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor result =  sqLiteDatabase.rawQuery( "SELECT * FROM Account_Details WHERE accountNo= '"+ accNo +"' and deleted=0", null );
        result.moveToFirst();
        while(result.isAfterLast() == false){
            return true;
        }
        result.close();
        return false;
    }

}
