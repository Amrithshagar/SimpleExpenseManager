package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentMemoryAccountDAO  implements AccountDAO {
    private DBhelper dataBaseHelper;

    public PersistentMemoryAccountDAO(DBhelper dataBaseHelper) {
        this.dataBaseHelper = dataBaseHelper;
    }

    @Override
    public List<String> getAccountNumbersList() {
        return dataBaseHelper.getAllAccountNumbers();
    }

    @Override
    public List<Account> getAccountsList() {
        return dataBaseHelper.getAllAccounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account value = dataBaseHelper.getAccount(accountNo);
        if(value == null){
            String message = "Account" + accountNo + "is invalid";
            throw new InvalidAccountException(message);
        }
        return dataBaseHelper.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        dataBaseHelper.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if(dataBaseHelper.isAccountValid(accountNo) == false){
            String message = "Account" + accountNo + "is invalid";
            throw new InvalidAccountException(message);
        }
        dataBaseHelper.deleteAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if(!dataBaseHelper.isAccountValid(accountNo)){
            String message = "Account" + accountNo + "is invalid";
            throw new InvalidAccountException(message);
        }
        Account account = getAccount(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        dataBaseHelper.updateBalance(accountNo,account.getBalance());
    }
}
