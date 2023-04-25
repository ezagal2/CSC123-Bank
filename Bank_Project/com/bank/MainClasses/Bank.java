package com.bank.MainClasses;

import com.bank.Utilities.CSVReader;
import com.bank.Utilities.UniqueCounter;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Bank implements Serializable {
	
	private static TreeMap<Integer,Account> accounts=new TreeMap<Integer, Account>();
	private static Integer currentAccountNum;

	public static CheckingAccount openCheckingAccount(String firstName, String lastName, String ssn, double overdraftLimit, String currencyType) throws FileNotFoundException {
		Customer c=new Customer(firstName,lastName, ssn);
		CheckingAccount a=new CheckingAccount(c,overdraftLimit, currencyType);
		accounts.put(a.getAccountNumber(), a);
		return a;
		
	}
	
	public static SavingAccount openSavingAccount(String firstName, String lastName, String ssn, String currencyType) throws FileNotFoundException {
		Customer c=new Customer(firstName,lastName, ssn);
		SavingAccount a=new SavingAccount(c, currencyType);
		accounts.put(a.getAccountNumber(), a);
		return a;
		
	}

	
	
	public static Account lookup(int accountNumber) throws NoSuchAccountException{
		if(!accounts.containsKey(accountNumber)) {
			throw new NoSuchAccountException("\nAccount number: "+accountNumber+" not found!\n\n");
		}
		
		return accounts.get(accountNumber);
	}
	
	public static void makeDeposit(int accountNumber, double amount) throws AccountClosedException, NoSuchAccountException, FileNotFoundException {
		
		lookup(accountNumber).deposit(amount);
		
	}
	
	public static void makeWithdrawal(int accountNumber, double amount) throws InsufficientBalanceException, NoSuchAccountException, FileNotFoundException {
		lookup(accountNumber).withdraw(amount);
	}
	
	public static void closeAccount(int accountNumber) throws NoSuchAccountException {
		lookup(accountNumber).close();
	}

	
	public static double getBalance(int accountNumber) throws NoSuchAccountException, FileNotFoundException {
		return lookup(accountNumber).getBalance();
	}

	public static void listAccounts(OutputStream out) throws IOException{
		
		out.write((byte)10);
		Collection<Account> col=accounts.values();
		
		for (Account a:col) {
			out.write(a.toString().getBytes());
			out.write((byte)10);
		}
		
		out.write((byte)10);
		out.flush();
	}
	
	public static void printAccountTransactions(int accountNumber, OutputStream out) throws IOException,NoSuchAccountException{
		
		lookup(accountNumber).printTransactions(out);
	}


	public static Object[] convertCurrency(String currencySellType, double currencyAmount, String currencyBuyType) throws FileNotFoundException {
		HashMap<String, String> csvFile = CSVReader.readCSVFile();
		Object[] result = null;
		double exchangeRate;

		boolean isUsdSell = currencySellType.equals("USD");
		boolean isUsdBuy = currencyBuyType.equals("USD");
		boolean hasCurrencyBuyType = csvFile.containsKey(currencyBuyType);
		boolean hasCurrencySellType = csvFile.containsKey(currencySellType);

		if ((isUsdSell && hasCurrencyBuyType && !isUsdBuy) || (!isUsdSell && hasCurrencySellType && isUsdBuy)) {
			result = new Object[3];

			// convert from USD to Foreign
			if (currencySellType.equals("USD") && csvFile.containsKey(currencyBuyType)){
				exchangeRate = Double.parseDouble(csvFile.get(currencyBuyType).substring(csvFile.get(currencyBuyType).indexOf(",") + 1));
				result[0] = exchangeRate;
				result[1] = currencyBuyType;
				result[2] = currencyAmount / exchangeRate;

			//convert from Foreign to USD
			} else{
				exchangeRate = Double.parseDouble(csvFile.get(currencySellType).substring(csvFile.get(currencySellType).indexOf(",") + 1));
				result[0] = exchangeRate;
				result[1] = currencyBuyType;
				result[2] = currencyAmount * exchangeRate;
			}
		}

		return result;
	}

	public static void accountInfo(int accountNum, OutputStream out) throws NoSuchAccountException, IOException {
		lookup(accountNum).accountInfo(out);
	}
	public static Map<Integer,Account> getAccounts (){
		return accounts;
	}
	public static void saveData() {
		File file = new File("bankData.obj");
		if (file.exists()) file.delete();
		try {
			FileOutputStream fileOut = new FileOutputStream(file, true); // Set second argument to true to append to existing file
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(getAccounts());
			currentAccountNum = UniqueCounter.currentValue();
			out.writeObject(currentAccountNum);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
	public static void loadData() {
		File file = new File("bankData.obj");

		FileInputStream fileIn;
		ObjectInputStream in;
		try {
			fileIn = new FileInputStream(file);
			in = new ObjectInputStream(fileIn);
			accounts = (TreeMap<Integer, Account>) in.readObject();
			currentAccountNum = (Integer) in.readObject();
			in.close();
			fileIn.close();
			UniqueCounter.setValue(currentAccountNum);
		} catch (IOException | ClassNotFoundException ignored) {
		}

	}
}
