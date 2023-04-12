import java.io.FileNotFoundException;

public class SavingAccount extends Account{
	private static final long serialVersionUID = 1L;

	public SavingAccount(Customer customer, String currencyType) throws FileNotFoundException {
		super("Saving", customer, currencyType);
	}



	//Withdrawals only allowed against positive balances 
	public void withdraw(double amount) throws InsufficientBalanceException, FileNotFoundException {
		if (getBalance() - amount < 0) {
			throw new InsufficientBalanceException("Not enough funds to cover withdrawal\n");

		}
		super.withdraw(amount);

	}

}
