package com.team9.virtualwallet;

import com.team9.virtualwallet.models.ConfirmationToken;
import com.team9.virtualwallet.models.Role;
import com.team9.virtualwallet.models.User;
import com.team9.virtualwallet.models.Wallet;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
        mockWallet.setBalance(BigDecimal.valueOf(0));
        mockWallet.setName("mockWallet");
        mockWallet.setUser(user);
        return mockWallet;
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

}
