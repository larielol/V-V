package model;

import java.time.LocalDateTime;

public class Transaction {
    private TransactionType type;
    private double amount;
    private LocalDateTime date;
    private String relatedAccount;
    
    public Transaction(TransactionType type, double amount, String relatedAccount) {
    	this(type, amount, relatedAccount, LocalDateTime.now());
    }
    
    public Transaction(TransactionType type, double amount, String relatedAccount, LocalDateTime date) {
        this.type = type;
        this.amount = amount;
        this.relatedAccount = relatedAccount;
        this.date = date;
    }
    
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getDate() { return date; }
    public String getRelatedAccount() { return relatedAccount; }
    
}
