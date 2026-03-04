package tddTests;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;

import model.AccountType;
import model.BankAccount;
import model.BankAccountManager;
import model.Transaction;
import model.TransactionType;

public class BankAccountTest {
	
    @Test
    public void shouldCreateAccountWithValidData() {

        String accountNumber = "12345";
        String holder = "Lucas Ariel";
        AccountType type = AccountType.CORRENTE;
        double initialBalance = 100.0;
        
        BankAccount account = new BankAccount(accountNumber, holder, type, initialBalance);
        
        assertEquals(accountNumber, account.getNumber());
        assertEquals(holder, account.getHolder());
        assertEquals(type, account.getType());
        assertEquals(initialBalance, account.getBalance(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNegativeInitialBalance() {
        new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, -50.0);
    }
    
    @Test
    public void shouldDepositPositiveAmount() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        
        account.deposit(50.0);
        
        assertEquals(150.0, account.getBalance(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNegativeDeposit() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        account.deposit(-50.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowZeroDeposit() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        account.deposit(0.0);
    }
    
    @Test
    public void shouldRegisterTransactionOnDeposit() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        
        account.deposit(50.0);
        
        assertEquals(1, account.getTransactions().size());
        Transaction transaction = account.getTransactions().get(0);
        assertEquals(TransactionType.DEPOSITO, transaction.getType());
        assertEquals(50.0, transaction.getAmount(), 0.001);
    }
    
    @Test
    public void shouldWithdrawWithSufficientBalance() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        
        account.withdraw(50.0);
        
        assertEquals(50.0, account.getBalance(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowWithdrawWithInsufficientBalanceInPoupanca() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.POUPANCA, 100.0);
        account.withdraw(150.0);
    }

    @Test
    public void shouldAllowOverdraftUpTo100InCorrente() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        
        account.withdraw(150.0);
        
        assertEquals(-50.0, account.getBalance(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowOverdraftBeyond100InCorrente() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        account.withdraw(250.0);
    }

    @Test
    public void shouldRegisterTransactionOnWithdraw() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        
        account.withdraw(50.0);
        
        assertEquals(1, account.getTransactions().size());
        Transaction transaction = account.getTransactions().get(0);
        assertEquals(TransactionType.SAQUE, transaction.getType());
        assertEquals(50.0, transaction.getAmount(), 0.001);
    }
    
    @Test
    public void shouldTransferBetweenAccounts() {
        BankAccountManager manager = new BankAccountManager();
        BankAccount source = manager.createAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 200.0);
        BankAccount destination = manager.createAccount("67890", "Rute Pereira", AccountType.POUPANCA, 100.0);
        
        manager.transfer("12345", "67890", 50.0);
        
        assertEquals(150.0, source.getBalance(), 0.001);
        assertEquals(150.0, destination.getBalance(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotTransferWithInsufficientBalance() {
        BankAccountManager manager = new BankAccountManager();
        BankAccount source = manager.createAccount("12345", "Lucas Ariel", AccountType.POUPANCA, 50.0);
        BankAccount destination = manager.createAccount("67890", "Rute Pereira", AccountType.POUPANCA, 100.0);
        
        manager.transfer("12345", "67890", 100.0);
    }

    @Test
    public void shouldRegisterTransferTransactions() {
        BankAccountManager manager = new BankAccountManager();
        BankAccount source = manager.createAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 200.0);
        BankAccount destination = manager.createAccount("67890", "Rute Pereira", AccountType.POUPANCA, 100.0);
        
        manager.transfer("12345", "67890", 50.0);
        
        Transaction sourceTransaction = source.getTransactions().get(0);
        assertEquals(TransactionType.TRANSFERENCIA_ENVIADA, sourceTransaction.getType());
        assertEquals(50.0, sourceTransaction.getAmount(), 0.001);
        assertEquals("67890", sourceTransaction.getRelatedAccount());
        
        Transaction destinationTransaction = destination.getTransactions().get(0);
        assertEquals(TransactionType.TRANSFERENCIA_RECEBIDA, destinationTransaction.getType());
        assertEquals(50.0, destinationTransaction.getAmount(), 0.001);
        assertEquals("12345", destinationTransaction.getRelatedAccount());
    }
    
    @Test
    public void shouldGetStatementInChronologicalOrder() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        
        account.deposit(50.0);
        account.withdraw(30.0);
        
        List<Transaction> statement = account.getStatement();
        
        assertEquals(2, statement.size());
        assertEquals(TransactionType.DEPOSITO, statement.get(0).getType());
        assertEquals(TransactionType.SAQUE, statement.get(1).getType());
    }
    
    @Test
    public void shouldGetStatementForPeriod() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        
        Transaction oldTransaction = new Transaction(TransactionType.DEPOSITO, 100.0, "12345", 
            LocalDateTime.now().minusDays(31));
        Transaction recentTransaction = new Transaction(TransactionType.DEPOSITO, 50.0, "12345", 
            LocalDateTime.now().minusDays(15));
        
        account.addTransaction(oldTransaction);
        account.addTransaction(recentTransaction);
        
        List<Transaction> last30Days = account.getStatement(LocalDateTime.now().minusDays(30), LocalDateTime.now());
        
        assertEquals(1, last30Days.size());
        assertEquals(50.0, last30Days.get(0).getAmount(), 0.001);
    }
    
    @Test
    public void shouldSetOverdraftLimitForCorrente() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        
        account.setOverdraftLimit(500.0);
        
        assertEquals(500.0, account.getOverdraftLimit(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowNegativeOverdraftLimit() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        account.setOverdraftLimit(-100.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowOverdraftLimitAbove1000() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        account.setOverdraftLimit(1500.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowOverdraftForPoupanca() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.POUPANCA, 100.0);
        account.setOverdraftLimit(500.0);
    }

    @Test
    public void shouldUseCustomOverdraftLimit() {
        BankAccount account = new BankAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        account.setOverdraftLimit(500.0);
        
        account.withdraw(600.0);
        
        assertEquals(-500.0, account.getBalance(), 0.001);
    }
	/*
    @Test
    public void integrationTestCompleteFlow() {
        BankAccountManager manager = new BankAccountManager();
        
        BankAccount account1 = manager.createAccount("001", "Lucas", AccountType.CORRENTE, 500.0);
        BankAccount account2 = manager.createAccount("002", "Rute", AccountType.POUPANCA, 300.0);
        
        account1.setOverdraftLimit(200.0);
        account1.deposit(100.0);
        account2.withdraw(50.0);
        manager.transfer("001", "002", 150.0); 
        
        assertEquals(2, account1.getTransactions().size());
        assertEquals(2, account2.getTransactions().size());
        
        assertEquals(450.0, account1.getBalance(), 0.001);
        assertEquals(400.0, account2.getBalance(), 0.001);
    }*/
}
