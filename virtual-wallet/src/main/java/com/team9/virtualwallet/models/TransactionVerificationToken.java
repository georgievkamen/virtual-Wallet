package com.team9.virtualwallet.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_verification_tokens")
public class TransactionVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private long tokenId;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "expiration_date")
    private Timestamp expirationDate;

    @ManyToOne(targetEntity = Transaction.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "transaction_id")
    private Transaction transaction;

    public TransactionVerificationToken(Transaction transaction) {
        this.verificationToken = UUID.randomUUID().toString();
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        this.expirationDate = Timestamp.valueOf(localDateTime);
        this.transaction = transaction;
    }

    public TransactionVerificationToken() {

    }

    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        this.tokenId = tokenId;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
