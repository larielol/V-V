package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BankAccount {
    private String number;
    private String holder;
    private AccountType type;
    private double balance;
    private List<Transaction> transactions;
    private double overdraftLimit = 100.0;
    
    public BankAccount(String number, String holder, AccountType type, double initialBalance) {
    	if (initialBalance < 0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo");
        }
    	this.number = number;
        this.holder = holder;
        this.type = type;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
    }
    
    public String getNumber() { return number; }
    public String getHolder() { return holder; }
    public AccountType getType() { return type; }
    public double getBalance() { return balance; }
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor do depósito deve ser positivo");
        }
        this.balance += amount;
        this.transactions.add(new Transaction(TransactionType.DEPOSITO, amount, this.number));
    }
    
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Valor do saque deve ser positivo");
        }
        
        double availableBalance = this.balance;
        
        if (this.type == AccountType.CORRENTE) {
            availableBalance += this.overdraftLimit;
        }
        
        if (amount > availableBalance) {
            throw new IllegalArgumentException("Saldo insuficiente para saque");
        }
        
        this.balance -= amount;
        this.transactions.add(new Transaction(TransactionType.SAQUE, amount, this.number));
    }
    
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public void setOverdraftLimit(double limit) {
        if (this.type != AccountType.CORRENTE) {
            throw new IllegalArgumentException("Apenas contas corrente podem ter cheque especial");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("Limite do cheque especial não pode ser negativo");
        }
        if (limit > 1000.0) {
            throw new IllegalArgumentException("Limite do cheque especial não pode exceder R$1000,00");
        }
        
        double oldLimit = this.overdraftLimit;
        this.overdraftLimit = limit;
        
        this.transactions.add(new Transaction(TransactionType.AJUSTE_LIMITE, limit - oldLimit, this.number));
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }
    
    public List<Transaction> getStatement() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getStatement(LocalDateTime startDate, LocalDateTime endDate) {
        return transactions.stream()
            .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
            .collect(Collectors.toList());
    }
    
}
