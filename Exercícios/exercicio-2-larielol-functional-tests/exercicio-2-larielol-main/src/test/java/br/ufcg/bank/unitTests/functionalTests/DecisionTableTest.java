package functionalTests;

import model.BankAccount;
import model.BankAccountManager;
import model.AccountType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DecisionTableTest {
    
    private BankAccountManager manager;
    
    @Before
    public void setUp() {
        manager = new BankAccountManager();
    }
    
    //TABELA DE DECISAO
    // TESTES PARA SAQUE
    
    @Test(expected = IllegalArgumentException.class)
    public void saque_ValorZero_DeveLancarExcecao() {
        // Regra R1: C1 = F (teste de um valor nao positovo)
        BankAccount account = manager.createAccount("101", "Cliente1", AccountType.CORRENTE, 100.00);
        account.withdraw(0.00);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void saque_ValorNegativo_DeveLancarExcecao() {
        // Regra R1: C1 = F 
        BankAccount account = manager.createAccount("102", "Cliente2", AccountType.CORRENTE, 100.00);
        account.withdraw(-50.00);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void saque_PoupancaSemSaldoSuficiente_DeveLancarExcecao() {
        // Regra R2: C1 = T, C2 = F, C3 = F (Poupança sem saldo)
        BankAccount account = manager.createAccount("103", "Cliente3", AccountType.POUPANCA, 50.00);
        account.withdraw(60.00);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void saque_CorrenteExcedeLimiteEspecial_DeveLancarExcecao() {
        // Regra R4: C1 = T, C2 = F, C3 = T, C4 = T (excede limite especial)
        BankAccount account = manager.createAccount("104", "Cliente4", AccountType.CORRENTE, 50.00);
        // Limite padrão é 100.00, entao saldo total = 150.00
        account.withdraw(151.00); // Excede o limite total
    }
    
    @Test
    public void saque_CorrenteUsaChequeEspecialDentroLimite_DeveRealizarComSucesso() {
        // Regra R3: C1 = T, C2 = F, C3 = T, C4 = F (usa cheque especial dentro do limite)
        BankAccount account = manager.createAccount("105", "Cliente5", AccountType.CORRENTE, 50.00);
        // Saldo 50.00 + limite 100.00 = 150.00 disponível
        account.withdraw(60.00); // Usa 10.00 do cheque especial
        assertEquals(-10.00, account.getBalance(), 0.001);
    }
    
    @Test
    public void saque_CorrenteComSaldoSuficiente_DeveRealizarComSucesso() {
        // Regra R5: C1 = T, C2 = T (saldo suficiente)
        BankAccount account = manager.createAccount("106", "Cliente6", AccountType.CORRENTE, 100.00);
        account.withdraw(50.00);
        assertEquals(50.00, account.getBalance(), 0.001);
    }
    
    @Test
    public void saque_PoupancaComSaldoSuficiente_DeveRealizarComSucesso() {
        // Regra R5: C1 = T, C2 = T (saldo suficiente)
        BankAccount account = manager.createAccount("107", "Cliente7", AccountType.POUPANCA, 100.00);
        account.withdraw(50.00);
        assertEquals(50.00, account.getBalance(), 0.001);
    }
    
    //TESTES PARA TRANSFERÊNCIA
    
    @Test(expected = IllegalArgumentException.class)
    public void transferencia_ContaOrigemNaoExiste_DeveLancarExcecao() {
        // C1 = F (conta origem nao existe)
        manager.createAccount("201", "Cliente8", AccountType.CORRENTE, 100.00);
        manager.transfer("999", "201", 50.00); // Origem null
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void transferencia_ContaDestinoNaoExiste_DeveLancarExcecao() {
        // C2 = F (conta destino nao existe)
        manager.createAccount("202", "Cliente9", AccountType.CORRENTE, 100.00);
        manager.transfer("202", "999", 50.00); // Destino null
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void transferencia_ValorZero_DeveLancarExcecao() {
        // C3 = F (valor inválido)
        manager.createAccount("203", "Cliente10", AccountType.CORRENTE, 100.00);
        manager.createAccount("204", "Cliente11", AccountType.CORRENTE, 50.00);
        manager.transfer("203", "204", 0.00);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void transferencia_SaldoOrigemInsuficiente_DeveLancarExcecao() {
        // C4 = F (saldo insuficiente)
        manager.createAccount("205", "Cliente12", AccountType.POUPANCA, 50.00);
        manager.createAccount("206", "Cliente13", AccountType.CORRENTE, 100.00);
        manager.transfer("205", "206", 60.00); // Poupança não tem saldo suficiente
    }
    
    @Test
    public void transferencia_TodasCondicoesVerdadeiras_DeveRealizarComSucesso() {
        // C1 = T, C2 = T, C3 = T, C4 = T
        manager.createAccount("207", "Cliente14", AccountType.CORRENTE, 200.00);
        manager.createAccount("208", "Cliente15", AccountType.POUPANCA, 100.00);
        
        manager.transfer("207", "208", 50.00);
        
        BankAccount origem = manager.findAccount("207");
        BankAccount destino = manager.findAccount("208");
        
        assertEquals(150.00, origem.getBalance(), 0.001);
        assertEquals(150.00, destino.getBalance(), 0.001);
        
        assertEquals(1, origem.getTransactions().size()); 
        assertEquals(1, destino.getTransactions().size()); 
    }
    
    @Test
    public void transferencia_CorrenteUsaChequeEspecial_DeveRealizarComSucesso() {
        // Transferência usando cheque especial
        manager.createAccount("209", "Cliente16", AccountType.CORRENTE, 50.00);
        manager.createAccount("210", "Cliente17", AccountType.POUPANCA, 100.00);
        
        manager.transfer("209", "210", 60.00); // Usa 10.00 do cheque especial
        
        BankAccount origem = manager.findAccount("209");
        BankAccount destino = manager.findAccount("210");
        
        assertEquals(-10.00, origem.getBalance(), 0.001);
        assertEquals(160.00, destino.getBalance(), 0.001);
    }
}
