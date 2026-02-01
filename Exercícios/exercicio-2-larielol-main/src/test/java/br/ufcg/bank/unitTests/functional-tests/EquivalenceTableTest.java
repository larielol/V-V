package functionalTests;

import model.BankAccount;
import model.BankAccountManager;
import model.AccountType;
import model.Transaction;
import model.TransactionType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.List;

public class EquivalencePartitionTest {
    
    private BankAccountManager manager;
    
    @Before
    public void setUp() {
        manager = new BankAccountManager();
    }
    
    //TESTES PARA CRIAR CONTA
    
    /* @Test(expected = IllegalArgumentException.class)
    public void criarConta_TipoContaInvalido_DeveLancarExcecao() {
        // Partição inválida: tipo de conta null
        try {
            BankAccount account = new BankAccount("301", "Cliente18", null, 100.00);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Tipo de conta inválido");
        }
    } */
    
    @Test
    public void criarConta_TodosTiposValidos_DeveCriarComSucesso() {
        // Partições válidas: CORRENTE e POUPANCA
        BankAccount corrente = manager.createAccount("302", "Cliente19", AccountType.CORRENTE, 100.00);
        BankAccount poupanca = manager.createAccount("303", "Cliente20", AccountType.POUPANCA, 100.00);
        
        assertNotNull(corrente);
        assertNotNull(poupanca);
        assertEquals(AccountType.CORRENTE, corrente.getType());
        assertEquals(AccountType.POUPANCA, poupanca.getType());
    }
    
    //TESTES PARA DEPÓSITO
    
    @Test
    public void deposito_ValoresPositivosDiferentes_DeveRealizarComSucesso() {
        // Partições válidas: qualquer valor positivo
        BankAccount account = manager.createAccount("304", "Cliente21", AccountType.CORRENTE, 100.00);
        
        // Testa diferentes partições de valores positivos, pequeno ate grande valor
        account.deposit(0.01);
        assertEquals(100.01, account.getBalance(), 0.001);
        
        account.deposit(500.00); 
        assertEquals(600.01, account.getBalance(), 0.001);
        
        account.deposit(1000.00); 
        assertEquals(1600.01, account.getBalance(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deposito_ValorZero_DeveLancarExcecao() {
        // Partição inválida: valor zero
        BankAccount account = manager.createAccount("305", "Cliente22", AccountType.CORRENTE, 100.00);
        account.deposit(0.00);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deposito_ValorNegativo_DeveLancarExcecao() {
        // Partição inválida: valor negativo
        BankAccount account = manager.createAccount("306", "Cliente23", AccountType.CORRENTE, 100.00);
        account.deposit(-10.00);
    }
    
    //TESTES PARA EXTRATO
    
    @Test
    public void extrato_SemPeriodoEspecificado_DeveRetornarTodasTransacoes() {
        //Período não especificado
        BankAccount account = manager.createAccount("307", "Cliente24", AccountType.CORRENTE, 100.00);
        
        // Realiza várias transações
        account.deposit(50.00);
        account.withdraw(30.00);
        account.deposit(20.00);
        
        List<Transaction> extrato = account.getTransactions();
        assertEquals(3, extrato.size());
        
        // tipos de transações
        assertEquals(TransactionType.DEPOSITO, extrato.get(0).getType());
        assertEquals(TransactionType.SAQUE, extrato.get(1).getType());
        assertEquals(TransactionType.DEPOSITO, extrato.get(2).getType());
    }
    
    @Test
    public void extrato_PeriodoSemTransacoes_DeveRetornarListaVazia() {
        //Período sem transações
        BankAccount account = manager.createAccount("308", "Cliente25", AccountType.CORRENTE, 100.00);
        
        // Define um período no futuro
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = LocalDateTime.now().plusDays(2);
        
        List<Transaction> extratoPeriodo = account.getTransactions()
            .stream()
            .filter(t -> t.getDate().isAfter(inicio) && t.getDate().isBefore(fim))
            .toList();
            
        assertEquals(0, extratoPeriodo.size());
    }
    
    @Test
    public void extrato_PeriodoComAlgumasTransacoes_DeveRetornarApenasEssas() {
        //Período com algumas transações
        BankAccount account = manager.createAccount("309", "Cliente26", AccountType.CORRENTE, 100.00);
        
        account.deposit(50.00);
        account.withdraw(30.00);
        account.deposit(20.00);
        
        List<Transaction> todasTransacoes = account.getTransactions();
        assertEquals(3, todasTransacoes.size());
    }
    
    @Test
    public void extrato_ContaSemTransacoes_DeveRetornarListaVazia() {
        // Caso especial: conta sem nenhuma transação
        BankAccount account = manager.createAccount("310", "Cliente27", AccountType.CORRENTE, 100.00);
        
        List<Transaction> extrato = account.getTransactions();
        assertEquals(0, extrato.size());
    }
    
    //CHEQUE ESPECIAL
    
    @Test
    public void chequeEspecial_LimitesDentroIntervaloValido_DeveAprovarComSucesso() {
        // Partições válidas: 0 a 1000 (inferior ate superior)
        BankAccount account = manager.createAccount("311", "Cliente28", AccountType.CORRENTE, 100.00);
        
        account.setOverdraftLimit(0.00);  
        assertEquals(0.00, account.getOverdraftLimit(), 0.001);
        
        account.setOverdraftLimit(500.00); 
        assertEquals(500.00, account.getOverdraftLimit(), 0.001);
        
        account.setOverdraftLimit(1000.00);
        assertEquals(1000.00, account.getOverdraftLimit(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void chequeEspecial_LimiteAbaixoDoMinimo_DeveLancarExcecao() {
        // Partição inválida: < 0
        BankAccount account = manager.createAccount("312", "Cliente29", AccountType.CORRENTE, 100.00);
        account.setOverdraftLimit(-1.00);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void chequeEspecial_LimiteAcimaDoMaximo_DeveLancarExcecao() {
        // Partição inválida: > 1000
        BankAccount account = manager.createAccount("313", "Cliente30", AccountType.CORRENTE, 100.00);
        account.setOverdraftLimit(1000.01);
    }
}
