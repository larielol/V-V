package model;

import java.util.HashMap;
import java.util.Map;

public class BankAccountManager {
    private Map<String, BankAccount> accounts;
    
    public BankAccountManager() {
        this.accounts = new HashMap<>();
    }
    
    public BankAccount createAccount(String number, String holder, AccountType type, double initialBalance) {
        if (accounts.containsKey(number)) {
            throw new IllegalArgumentException("Número de conta já existe: " + number);
        }
        
        BankAccount account = new BankAccount(number, holder, type, initialBalance);
        accounts.put(number, account);
        return account;
    }
    
    public BankAccount findAccount(String number) {
        return accounts.get(number);
    }
    
    public void transfer(String sourceAccountNumber, String destinationAccountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser positivo");
        }
        
        BankAccount source = findAccount(sourceAccountNumber);
        BankAccount destination = findAccount(destinationAccountNumber);
        
        if (source == null) {
            throw new IllegalArgumentException("Conta de origem não encontrada: " + sourceAccountNumber);
        }
        if (destination == null) {
            throw new IllegalArgumentException("Conta de destino não encontrada: " + destinationAccountNumber);
        }

        double availableBalance = source.getBalance();
        if (source.getType() == AccountType.CORRENTE) {
            availableBalance += source.getOverdraftLimit();
        }
        
        if (amount > availableBalance) {
            throw new IllegalArgumentException("Saldo insuficiente para transferência");
        }
        
        source.setBalance(source.getBalance() - amount);
        destination.setBalance(destination.getBalance() + amount);
        
        source.addTransaction(new Transaction(TransactionType.TRANSFERENCIA_ENVIADA, amount, destinationAccountNumber));
        destination.addTransaction(new Transaction(TransactionType.TRANSFERENCIA_RECEBIDA, amount, sourceAccountNumber));
    }
}
