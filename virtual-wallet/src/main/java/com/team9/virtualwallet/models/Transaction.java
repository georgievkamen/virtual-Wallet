package com.team9.virtualwallet.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int id;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "sender_payment_method_id")
    private PaymentMethod senderPaymentMethod;

    @ManyToOne
    @JoinColumn(name = "recipient_payment_method_id")
    private PaymentMethod recipientPaymentMethod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "category_transactions",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Category category;

    public Transaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;

    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getSenderPaymentMethod() {
        return senderPaymentMethod;
    }

    public void setSenderPaymentMethod(PaymentMethod senderPaymentMethod) {
        this.senderPaymentMethod = senderPaymentMethod;
    }

    public PaymentMethod getRecipientPaymentMethod() {
        return recipientPaymentMethod;
    }

    public void setRecipientPaymentMethod(PaymentMethod recipientPaymentMethod) {
        this.recipientPaymentMethod = recipientPaymentMethod;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
