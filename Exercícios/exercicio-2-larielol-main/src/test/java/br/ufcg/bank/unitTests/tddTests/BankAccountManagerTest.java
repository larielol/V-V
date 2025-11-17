package tddTests;

import org.junit.Test;

import model.AccountType;
import model.BankAccount;
import model.BankAccountManager;

import static org.junit.Assert.*;

public class BankAccountManagerTest {

    @Test
    public void shouldCreateAccountWithUniqueNumber() {
        BankAccountManager manager = new BankAccountManager();
        
        BankAccount account = manager.createAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        
        assertNotNull(account);
        assertEquals("12345", account.getNumber());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowDuplicateAccountNumbers() {
        BankAccountManager manager = new BankAccountManager();
        
        manager.createAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        manager.createAccount("12345", "Rute Pereira", AccountType.POUPANCA, 200.0);
    }

    @Test
    public void shouldFindAccountByNumber() {
        BankAccountManager manager = new BankAccountManager();
        manager.createAccount("12345", "Lucas Ariel", AccountType.CORRENTE, 100.0);
        
        BankAccount found = manager.findAccount("12345");
        
        assertNotNull(found);
        assertEquals("12345", found.getNumber());
    }

    @Test
    public void shouldReturnNullWhenAccountNotFound() {
        BankAccountManager manager = new BankAccountManager();
        
        BankAccount found = manager.findAccount("99999");
        
        assertNull(found);
    }
}