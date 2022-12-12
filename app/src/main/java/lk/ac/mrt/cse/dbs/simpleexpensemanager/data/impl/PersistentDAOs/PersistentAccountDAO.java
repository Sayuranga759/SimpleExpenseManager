package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentDAOs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DBHelper.ACCOUNT_NO;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DBHelper.BALANCE;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DBHelper.BANK;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DBHelper.ACC_HOLDER;
import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.Database.DBHelper.ACCOUNT;


public class PersistentAccountDAO implements AccountDAO {
    private final DBHelper helper;
    private SQLiteDatabase DB;


    public PersistentAccountDAO(Context context) {
        helper = new DBHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        DB = helper.getReadableDatabase();

        String[] projection = {ACCOUNT_NO};

        Cursor cursor = DB.query(ACCOUNT, projection, null, null, null, null, null);
        List<String> acc_Numbers = new ArrayList<>();

        while(cursor.moveToNext()) {
            String acc_No = cursor.getString(cursor.getColumnIndexOrThrow(ACCOUNT_NO));
            acc_Numbers.add(acc_No);
        }
        cursor.close();
        return acc_Numbers;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();

        DB = helper.getReadableDatabase();

        String[] projection = {ACCOUNT_NO, BANK, ACC_HOLDER, BALANCE};

        Cursor cursor = DB.query(ACCOUNT, projection, null, null, null, null, null);

        while(cursor.moveToNext()) {
            String acc_No = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String bank = cursor.getString(cursor.getColumnIndex(BANK));
            String accountHolder = cursor.getString(cursor.getColumnIndex(ACC_HOLDER));
            double balance = cursor.getDouble(cursor.getColumnIndex(BALANCE));
            Account account = new Account(acc_No,bank,accountHolder,balance);

            accounts.add(account);
        }
        cursor.close();
        return accounts;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        DB = helper.getReadableDatabase();
        String[] projection = {ACCOUNT_NO, BANK, ACC_HOLDER, BALANCE};

        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { accountNo };

        Cursor cursor = DB.query(ACCOUNT, projection, selection, selectionArgs, null, null, null);

        if (cursor == null){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else {
            cursor.moveToFirst();

            Account account = new Account(accountNo, cursor.getString(cursor.getColumnIndex(BANK)),
                    cursor.getString(cursor.getColumnIndex(ACC_HOLDER)), cursor.getDouble(cursor.getColumnIndex(BALANCE)));
            return account;
        }
    }

    @Override
    public void addAccount(Account account) {

        DB = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ACCOUNT_NO, account.getAccountNo());
        values.put(BANK, account.getBankName());
        values.put(ACC_HOLDER, account.getAccountHolderName());
        values.put(BALANCE,account.getBalance());
        
        DB.insert(ACCOUNT, null, values);
        DB.close();
    }

    @Override
    public void removeAccount(String acc_No) throws InvalidAccountException {

        DB = helper.getWritableDatabase();
        DB.delete(ACCOUNT, ACCOUNT_NO + " = ?", new String[] { acc_No });
        DB.close();
    }

    @Override
    public void updateBalance(String acc_No, ExpenseType moneyType, double amount) throws InvalidAccountException {

        DB = helper.getWritableDatabase();
        String[] projection = {BALANCE};
        String selection = ACCOUNT_NO + " = ?";
        String[] selectionArgs = { acc_No };

        Cursor cursor = DB.query(ACCOUNT, projection, selection, selectionArgs, null, null, null);

        double balance;
        if(cursor.moveToFirst())
            balance = cursor.getDouble(0);
        else{
            String msg = "Account " + acc_No + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        ContentValues values = new ContentValues();
        switch (moneyType) {
            case EXPENSE:
                values.put(BALANCE, balance - amount);
                break;
            case INCOME:
                values.put(BALANCE, balance + amount);
                break;
        }
        
        DB.update(ACCOUNT, values, ACCOUNT_NO + " = ?",
                new String[] { acc_No });

        cursor.close();
        DB.close();

    }
}
