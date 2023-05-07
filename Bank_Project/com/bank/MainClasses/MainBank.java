package com.bank.MainClasses;

import com.bank.Accounts.Account;
import com.bank.Accounts.CheckingAccount;
import com.bank.Accounts.SavingAccount;
import com.bank.Exceptions.AccountClosedException;
import com.bank.Exceptions.InsufficientBalanceException;
import com.bank.Exceptions.NoSuchAccountException;
import com.bank.Utilities.CurrencyReader;
import com.bank.Utilities.UIManager;

import java.io.*;
import java.util.HashMap;

public class MainBank implements Serializable{

	//All messages are declared as constants to make it easier to change. Also, to ensure future proofing in case the application need to be made available
	//in more than one language
	public static final String MSG_ACCOUNT_OPENED = "%nAccount opened, account number is: %s%n%n";
	public static final String MSG_ACCOUNT_CLOSED = "%nAccount number %s has been closed, balance is %s%n%n";
	public static final String MSG_ACCOUNT_NOT_FOUND = "%nAccount number %s not found! %n%n";
	public static final String MSG_FIRST_NAME = "Enter first name:  ";
	public static final String MSG_LAST_NAME = "Enter last name:  ";
	public static final String MSG_SSN = "Enter Social Security Number:  ";
	public static final String MSG_ACCOUNT_NAME = "Enter account name:  ";
	public static final String MSG_ACCOUNT_OD_LIMIT = "Enter overdraft limit:  ";
	public static final String MSG_ACCOUNT_CREDIT_LIMIT = "Enter credit limit:  ";
	public static final String MSG_AMOUNT = "Enter amount: ";
	public static final String MSG_ACCOUNT_NUMBER = "Enter account number: ";
	public static final String MSG_ACCOUNT_ACTION = "%n%s was %s, account balance is: %.2f%n%n";
	public static final String MSG_BUY_OR_SELL = "%nThe %s you are %s: ";

	public static final String MSG_CSV_NOT_FOUND = "** Currency file could not be loaded, currency conversion service" +
			" and Foreign currency accounts are not available **%n";


	//Declare main menu and prompt to accept user input
	public static final String[] menuOptions = { "Open Checking Account%n","Open Saving Account%n", "List Accounts%n",
			"View Statement%n", "Account Information%n", "Deposit Funds%n", "Withdraw Funds%n", "Currency Conversion%n",
			"Close an Account%n", "Exit%n" };
	public static final String MSG_PROMPT = "%nEnter choice: ";
	private static final String MSG_CURRENCY_TYPE = "Account Currency: ";
	private static final String MSG_INVALID_CURRENCY_TYPE = "%nInvalid currency type!%n";
	private static final String MSG_OPERATION_UNSUCCESSFUL = "%n%s unsuccessful%n";
	private static final String MSG_AMOUNT_TO_CONVERT = "%nThe amount you are selling :  ";
	private static final String MSG_EXCHANGE_RESULT = "%nThe exchange rate is %.4f and you will get %s %.2f%n";
	private static final String MSG_BANK_DATA_SAVED = "Bank data has been saved to bankData.obj";


	//Declare streams to accept user input / provide output
	InputStream in;
	OutputStream out;

	//Constructor
	public MainBank(InputStream in, OutputStream out) {
		this.in=in;
		this.out=out;
	}


	//Main method.
	public static void main(String[] args) {

		new MainBank(System.in,System.out).run();

	}


	//The core of the program responsible for providing user experience.
	public void run() {

		Account acc;
		int option = 0;

		UIManager ui = new UIManager(this.in,this.out,menuOptions,MSG_PROMPT);
		HashMap<String, String> csvFile = null;
		Bank.loadData();

		try {
			do {

				if (CurrencyReader.isUsable()){
					try{
						csvFile = CurrencyReader.readCSVFile();
					} catch (Exception e) {
						ui.print(MSG_CSV_NOT_FOUND, null);
					}
				}else{
					ui.print(MSG_CSV_NOT_FOUND, null);
				}

				option = ui.getMainOption(); //Render main menu

				switch (option) {
					case 1:

						//Compact statement to accept user input, open account, and print the result including the account number
						CheckingAccount checkingAccount = Bank.openCheckingAccount(ui.readToken(MSG_FIRST_NAME),
								ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN), ui.readDouble(MSG_ACCOUNT_OD_LIMIT), null);
						String currencyType = "USD";
						if (CurrencyReader.isUsable()) {
							do {
								currencyType = ui.readToken(MSG_CURRENCY_TYPE);
								if(!csvFile.containsKey(currencyType)){
									ui.print(MSG_INVALID_CURRENCY_TYPE, null);
								}
							}while (!csvFile.containsKey(currencyType));
						}
						checkingAccount.setCurrencyType(currencyType);
						ui.print(MSG_ACCOUNT_OPENED, new Object[] { checkingAccount.getAccountNumber() });
						break;
					case 2:

						//Compact statement to accept user input, open account, and print the result including the account number
						SavingAccount savingAccount = Bank.openSavingAccount(ui.readToken(MSG_FIRST_NAME),
								ui.readToken(MSG_LAST_NAME), ui.readToken(MSG_SSN), null);
						String type = "USD";
						if (CurrencyReader.isUsable()) {
							do {
								type = ui.readToken(MSG_CURRENCY_TYPE);
								if(!csvFile.containsKey(type)){
									ui.print(MSG_INVALID_CURRENCY_TYPE, null);
								}
							}while (!csvFile.containsKey(type));
						}
						savingAccount.setCurrencyType(type);
						ui.print(MSG_ACCOUNT_OPENED, new Object[] { savingAccount.getAccountNumber() });
						break;

					case 3:

						//Get bank to print list of accounts to the output stream provided as method argument
						Bank.listAccounts(this.out);
						break;

					case 4:
						//find account and get the account to print transactions to the  output stream provided in method arguments
						try {
							Bank.printAccountTransactions(ui.readInt(MSG_ACCOUNT_NUMBER),this.out);
						} catch (NoSuchAccountException e1) {
							this.handleException(ui, e1);

						}

						break;

					case 5:
						//Show Account Information
						try {
							Bank.accountInfo(ui.readInt(MSG_ACCOUNT_NUMBER), this.out);
						}catch (NoSuchAccountException e1) {
							this.handleException(ui, e1);
						}
						break;

					case 6:
						//find account, deposit money and print result

						try {
							int accountNumber=ui.readInt(MSG_ACCOUNT_NUMBER);
							Bank.makeDeposit(accountNumber, ui.readDouble(MSG_AMOUNT));
							ui.print(MSG_ACCOUNT_ACTION, new Object[] {"Deposit","successful",Bank.getBalance(accountNumber)});
						} catch(NoSuchAccountException | AccountClosedException e) {
							this.handleException(ui, e);

						}
						break;

					case 7:
						//find account, withdraw money and print result
						try {
							int accountNumber=ui.readInt(MSG_ACCOUNT_NUMBER);
							Bank.makeWithdrawal(accountNumber, ui.readDouble(MSG_AMOUNT));
							ui.print(MSG_ACCOUNT_ACTION, new Object[] {"Withdrawal","successful",Bank.getBalance(accountNumber)});

						} catch(NoSuchAccountException | InsufficientBalanceException e) {
							this.handleException(ui, e);
						}
						break;
					case 8:

						//Currency Conversion
						if (!CurrencyReader.isUsable()) {
							ui.print(MSG_CSV_NOT_FOUND, null);
							break;
						}
						Object[] result = Bank.convertCurrency(ui.readToken(String.format(MSG_BUY_OR_SELL, "currency", "selling")), ui.readDouble(MSG_AMOUNT), ui.readToken(String.format(MSG_BUY_OR_SELL, "currency", "buying")));
						if (result != null){
							ui.print(MSG_EXCHANGE_RESULT, result);
						}else {
							ui.print(MSG_INVALID_CURRENCY_TYPE, null);
						}
						break;
					case 9:
						//find account and close it

						try {
							int accountNumber=ui.readInt(MSG_ACCOUNT_NUMBER);
							Bank.closeAccount(accountNumber);
							ui.print(MSG_ACCOUNT_CLOSED,
									new Object[] { accountNumber, Bank.getBalance(accountNumber) });

						} catch (NoSuchAccountException e) {
							this.handleException(ui, e);

						}
						break;
				}

			} while (option != menuOptions.length);
			Bank.saveData();
			ui.print(MSG_BANK_DATA_SAVED, null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private  void handleException(UIManager ui, Exception e) throws IOException{
		ui.print(e.getMessage(), new Object[] { });
	}



}
