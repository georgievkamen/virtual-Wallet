package com.team9.virtualwallet;

import com.team9.virtualwallet.models.*;
import com.team9.virtualwallet.models.enums.TransactionType;
import com.team9.virtualwallet.models.enums.Type;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


public class Helpers {

    public static User createMockCustomer() {
        var mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("mock@user.com");
        mockUser.setUserPhoto("user_photo");
        mockUser.setUsername("mockUser");
        mockUser.setPassword("1234mock1234");
        mockUser.setPhoneNumber("0888888888");
        mockUser.setDeleted(false);
        mockUser.setBlocked(false);
        mockUser.setIdVerified(true);
        mockUser.setEmailVerified(true);
        mockUser.setDefaultWallet(createMockWallet(mockUser));
        mockUser.setRoles(createMockCustomerRole());
        return mockUser;
    }

    public static User createMockEmployee() {
        var mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("mock@user.com");
        mockUser.setUserPhoto("user_photo");
        mockUser.setUsername("mockUser");
        mockUser.setPassword("1234mock1234");
        mockUser.setPhoneNumber("0888888888");
        mockUser.setDeleted(false);
        mockUser.setBlocked(false);
        mockUser.setIdVerified(true);
        mockUser.setEmailVerified(true);
        mockUser.setDefaultWallet(createMockWallet(mockUser));
        mockUser.setRoles(createMockEmployeeRole());
        return mockUser;
    }

    public static Wallet createMockWallet(User user) {
        var mockWallet = new Wallet();
        mockWallet.setId(1);
        mockWallet.setDeleted(false);
        mockWallet.setBalance(BigDecimal.valueOf(0));
        mockWallet.setName("mockWallet");
        mockWallet.setUser(user);
        return mockWallet;
    }

    public static Transaction createMockTransaction() {
        var mockCustomer = createMockCustomer();
        mockCustomer.setId(2);
        var mockRecipient = createMockCustomer();
        var recipientPayment = new PaymentMethod();
        recipientPayment.setId(1);
        recipientPayment.setType(Type.WALLET);
        var senderPayment = new PaymentMethod();
        senderPayment.setId(2);
        senderPayment.setType(Type.WALLET);
        var mockTransaction = new Transaction();
        mockTransaction.setId(1);
        mockTransaction.setTransactionType(TransactionType.SMALL_TRANSACTION);
        mockTransaction.setDescription("desk");
        mockTransaction.setRecipient(mockRecipient);
        mockTransaction.setSender(mockCustomer);
        mockTransaction.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        mockTransaction.setAmount(BigDecimal.valueOf(200));

        mockTransaction.setRecipientPaymentMethod(recipientPayment);
        mockTransaction.setSenderPaymentMethod(senderPayment);

        return mockTransaction;

    }

    public static MultipartFile createMockFile() {
        var mockFile = new MockMultipartFile("name", "fileName".getBytes());
        return mockFile;
    }

    public static Set<Role> createMockCustomerRole() {
        var mockRole = new Role();
        Set<Role> roles = new HashSet<>();
        mockRole.setId(1);
        mockRole.setName("Customer");
        roles.add(mockRole);
        return roles;
    }

    public static Set<Role> createMockEmployeeRole() {
        var mockRole = new Role();
        Set<Role> roles = new HashSet<>();
        mockRole.setId(2);
        mockRole.setName("Employee");
        roles.add(mockRole);
        return roles;
    }

    public static ConfirmationToken createMockConfirmationToken(User user) {
        var confirmationToken = new ConfirmationToken();
        confirmationToken.setTokenId(1);
        confirmationToken.setUser(user);
        confirmationToken.setConfirmationToken("123");
        confirmationToken.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return confirmationToken;
    }

    public static TransactionVerificationToken createMockTransactionVerificationToken(Transaction transaction) {
        var verificationToken = new TransactionVerificationToken();
        verificationToken.setTokenId(1);
        verificationToken.setTransaction(transaction);
        verificationToken.setExpirationDate(Timestamp.valueOf(LocalDateTime.now().plusDays(2)));
        verificationToken.setVerificationToken("123");
        return verificationToken;
    }

    public static InvitationToken createMockInvitationToken(User user, String invitedEmail) {
        var invitationToken = new InvitationToken();
        invitationToken.setInvitationToken("123");
        invitationToken.setTokenId(1);
        invitationToken.setExpirationDate(Timestamp.valueOf(LocalDateTime.now().plusDays(2)));
        invitationToken.setInvitedEmail(invitedEmail);
        invitationToken.setUsed(false);
        invitationToken.setInvitingUser(user);

        return invitationToken;
    }

    public static Card createMockCard(User user) {
        var mockCard = new Card();
        mockCard.setUser(user);
        mockCard.setCardHolder("MOCK USER");
        mockCard.setCardNumber("123456789999999");
        mockCard.setId(1);
        mockCard.setDeleted(false);
        mockCard.setCvv("123");
        mockCard.setExpirationDate(LocalDate.now().plusDays(2));

        return mockCard;
    }

    public static Category createMockCategory(User user) {
        var mockCategory = new Category();
        mockCategory.setUser(user);
        mockCategory.setName("Beer");
        mockCategory.setId(1);
        Set<Transaction> transactions = new HashSet<>();
        transactions.add(new Transaction());
        mockCategory.setTransactions(transactions);

        return mockCategory;
    }

    public static Role createMockRole() {
        var role = new Role();
        role.setId(1);
        role.setName("Customer");

        return role;
    }


}
