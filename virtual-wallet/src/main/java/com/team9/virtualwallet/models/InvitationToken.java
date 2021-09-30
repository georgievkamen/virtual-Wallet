package com.team9.virtualwallet.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invitation_tokens")
public class InvitationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private long tokenId;

    @Column(name = "invitation_token")
    private String invitationToken;

    @Column(name = "expiration_date")
    private Timestamp expirationDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "inviting_user_id")
    private User invitingUser;

    @Column(name = "invited_email")
    private String invitedEmail;

    @Column(name = "used")
    private boolean used;

    public InvitationToken(User user, String invitedEmail) {
        this.invitationToken = UUID.randomUUID().toString();
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        this.expirationDate = Timestamp.valueOf(localDateTime);
        this.invitingUser = user;
        this.invitedEmail = invitedEmail;
        this.used = false;
    }

    public InvitationToken() {
    }

    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        this.tokenId = tokenId;
    }

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public User getInvitingUser() {
        return invitingUser;
    }

    public void setInvitingUser(User invitingUser) {
        this.invitingUser = invitingUser;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
