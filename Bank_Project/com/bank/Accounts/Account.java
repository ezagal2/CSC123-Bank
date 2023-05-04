package com.bank.Accounts;

import com.bank.Exceptions.AccountClosedException;
import com.bank.Exceptions.InsufficientBalanceException;
import com.bank.MainClasses.Customer;
import com.bank.MainClasses.Transaction;
import com.bank.Utilities.CurrencyReader;
import com.bank.Utilities.UniqueCounter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Account implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String accountName;
	private Customer accountHolder;
	private ArrayList<Transaction> transactions;
	private String currencyType;
	private boolean open=true;
	private int accountNumber;

	protected Account(String name, Customer customer, String cType) throws FileNotFoundException {

		accountName=name;
		accountHolder=customer;
		transactions=new ArrayList<Transaction>();
		accountNumber= UniqueCounter.nextValue();
		setCurrencyType(cType);
	}

	public String getCurrencyType() {
		return currencyType;
	}


	
	public String getAccountName() {
		return accountName;
	}

	public Customer getAccountHolder() {
		return accountHolder;
	}

	public double getBalance() {
		double workingBalance=0;
		
		for(Transaction t: transactions) {
			if((t.getType()==Transaction.CREDIT))workingBalance+=t.getAmount();
			else workingBalance-=t.getAmount();
		}
		return workingBalance;
	}
	
	
	
	
	public void deposit(double amount) throws AccountClosedException {
		double balance=getBalance();
		if(!isOpen()&&balance>=0) {
			throw new AccountClosedException("\nAccount is closed with positive balance, deposit not allowed!\n\n");
		}
		transactions.add(new Transaction(Transaction.CREDIT,amount));
	}
	
	
	
	
	public void withdraw(double amount) throws InsufficientBalanceException, FileNotFoundException {
			
		double balance=getBalance();
		double amnt = amount;
		if(!isOpen()&&balance<=0) {
			throw new InsufficientBalanceException("\nThe account is closed and balance is: "+balance+"\n\n");
		}
		transactions.add(new Transaction(Transaction.DEBIT,amnt));
	}
	
	public void close() {
		open=false;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public int getAccountNumber() {
		return accountNumber;
	}

	public String toString() {
		String aName= null;
		aName = accountNumber+"("+accountName+")"+" : "+accountHolder.toString()+ " : "+ currencyType+" : "+String.format("%.2f", getBalance())+" : " + String.format("%.2f", getBalanceInUSD()) + " : " +(open?"Account Open":"Account Closed");

		return aName;
	}
	 
	public void printTransactions(OutputStream out) throws IOException {
		
		out.write("\n\n".getBytes());
		out.write("------------------\n".getBytes());
	
		for(Transaction t: transactions) {
			out.write(t.toString().getBytes());
			out.write((byte)10);
		}
		out.write("------------------\n".getBytes());
		out.write(("Balance: "+String.format("%.2f", getBalance())+"\n\n\n").getBytes());
		out.flush();
		
	}

	public void accountInfo(OutputStream out) throws IOException{
		out.write("\n\n".getBytes());
		out.write(("Account Number: " + accountNumber + "\n").getBytes());
		out.write(("Name: " + accountHolder.getFirstName() + " " + accountHolder.getLastName() + "\n").getBytes());
		out.write(("SSN: " + accountHolder.getSSN() + "\n").getBytes());
		out.write(("Currency: " + currencyType + "\n").getBytes());
		out.write(("Currency Balance: " + currencyType + String.format(" %.2f", getBalance()) + "\n").getBytes());
		out.write(("USD Balance: USD " + String.format("%.2f", getBalanceInUSD()) + "\n\n\n").getBytes());
		out.flush();
	}
	private double getBalanceInUSD (){
		double balanceInUSD = getBalance();
		if (!currencyType.equals("USD")){
			try {
				HashMap<String, String> csvFile = CurrencyReader.readCSVFile();
				balanceInUSD = Double.parseDouble(csvFile.get(currencyType).substring(csvFile.get(currencyType).indexOf(",") + 1)) * getBalance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return balanceInUSD;
	}

	public void setCurrencyType(String type) {
		currencyType = type;
	}
}
