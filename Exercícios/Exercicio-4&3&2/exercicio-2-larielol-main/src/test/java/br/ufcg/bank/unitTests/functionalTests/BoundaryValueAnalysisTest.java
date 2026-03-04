package functionalTests;

import model.BankAccount;
import model.BankAccountManager;
import model.AccountType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BoundaryValueAnalysisTest {
    
    private BankAccountManager manager;
    
    @Before
    public void setUp() {
        manager = new BankAccountManager();
    }
    
    // CRIAR CONTA
    
    @Test(expected = IllegalArgumentException.class)
    public void criarConta_SaldoInicialNegativo_DeveLancarExcecao() {
        //Saldo = -0.01
        manager.createAccount("001", "Ariel", AccountType.CORRENTE, -0.01);
    }
    
    @Test
    public void criarConta_SaldoInicialZero_DeveCriarComSucesso() {
        // Saldo = 0.00
        BankAccount account = manager.createAccount("002", "Rute", AccountType.CORRENTE, 0.00);
        assertNotNull(account);
        assertEquals(0.00, account.getBalance(), 0.001);
    }
    
    @Test
    public void criarConta_SaldoInicialPequenoPositivo_DeveCriarComSucesso() {
        // Saldo = 0.01
        BankAccount account = manager.createAccount("003", "Lucas", AccountType.CORRENTE, 0.01);
        assertNotNull(account);
        assertEquals(0.01, account.getBalance(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void criarConta_NumeroContaDuplicado_DeveLancarExcecao() {
        // Número de conta duplicado
        manager.createAccount("004", "Ana", AccountType.CORRENTE, 100.00);
        manager.createAccount("004", "Carlos", AccountType.POUPANCA, 200.00);
    }
    
    // DEPOSITO
    
    @Test(expected = IllegalArgumentException.class)
    public void deposito_ValorNegativo_DeveLancarExcecao() {
        // Valor = -50.00
        BankAccount account = manager.createAccount("005", "Sofia", AccountType.CORRENTE, 100.00);
        account.deposit(-50.00);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deposito_ValorZero_DeveLancarExcecao() {
        // Valor = 0.00
        BankAccount account = manager.createAccount("006", "Lucas", AccountType.CORRENTE, 100.00);
        account.deposit(0.00);
    }
    
    @Test
    public void deposito_ValorPequenoPositivo_DeveRealizarComSucesso() {
        //  Valor = 0.01
        BankAccount account = manager.createAccount("007", "Mariana", AccountType.CORRENTE, 100.00);
        account.deposit(0.01);
        assertEquals(100.01, account.getBalance(), 0.001);
    }
    
    @Test
    public void deposito_ValorGrande_DeveRealizarComSucesso() {
        //  Valor = 10000.00
        BankAccount account = manager.createAccount("008", "Ricardo", AccountType.CORRENTE, 100.00);
        account.deposit(10000.00);
        assertEquals(10100.00, account.getBalance(), 0.001);
    }
    
    // LIMITE DO CHEQUE
    
    @Test(expected = IllegalArgumentException.class)
    public void aprovarLimiteChequeEspecial_ContaPoupanca_DeveLancarExcecao() {
        //Poupança não pode ter cheque especial
        BankAccount account = manager.createAccount("009", "Fernanda", AccountType.POUPANCA, 100.00);
        account.setOverdraftLimit(500.00);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void aprovarLimiteChequeEspecial_LimiteNegativo_DeveLancarExcecao() {
        //Limite = -0.01
        BankAccount account = manager.createAccount("010", "Roberto", AccountType.CORRENTE, 100.00);
        account.setOverdraftLimit(-0.01);
    }
    
    @Test
    public void aprovarLimiteChequeEspecial_LimiteZero_DeveAprovarComSucesso() {
        //  Limite = 0.00
        BankAccount account = manager.createAccount("011", "Carla", AccountType.CORRENTE, 100.00);
        account.setOverdraftLimit(0.00);
        assertEquals(0.00, account.getOverdraftLimit(), 0.001);
    }
    
    @Test
    public void aprovarLimiteChequeEspecial_LimiteNoMaximo_DeveAprovarComSucesso() {
        //  Limite = 1000.00
        BankAccount account = manager.createAccount("012", "Paulo", AccountType.CORRENTE, 100.00);
        account.setOverdraftLimit(1000.00);
        assertEquals(1000.00, account.getOverdraftLimit(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void aprovarLimiteChequeEspecial_LimiteAcimaDoMaximo_DeveLancarExcecao() {
        // Limite = 1000.01
        BankAccount account = manager.createAccount("013", "Juliana", AccountType.CORRENTE, 100.00);
        account.setOverdraftLimit(1000.01);
    }
}
