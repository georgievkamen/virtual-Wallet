package com.team9.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

import static com.team9.virtualwallet.configs.ApplicationConstants.DEFAULT_PHOTO_URL;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "username")
    private String username;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "blocked")
    private boolean blocked;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "id_verified")
    private boolean idVerified;

    @Column(name = "user_photo")
    private String userPhoto;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "default_wallet_id")
    private Wallet defaultWallet;

    @JsonIgnore
    @Column(name = "deleted")
    private boolean isDeleted;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "contact_list",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "contact_id")
    )
    private Set<User> contacts;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isIdVerified() {
        return idVerified;
    }

    public void setIdVerified(boolean idVerified) {
        this.idVerified = idVerified;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public Set<User> getContacts() {
        return this.contacts;
    }

    public void setContacts(Set<User> contacts) {
        this.contacts = contacts;
    }

    public void addContact(User contact) {
        this.contacts.add(contact);
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void removeContact(String username) {
        this.contacts = contacts.stream().filter(user -> !user.getUsername().equals(username)).collect(Collectors.toSet());
    }

    public boolean isFriend(String username) {
        return this
                .getContacts()
                .stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    public boolean isEmployee() {
        return this
                .getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("employee"));
    }

    public Wallet getDefaultWallet() {
        return defaultWallet;
    }

    public void setDefaultWallet(Wallet defaultWallet) {
        this.defaultWallet = defaultWallet;
    }

    @Transient
    public String getProfileImage() {
        if (userPhoto == null) {
            return DEFAULT_PHOTO_URL;
        }
        return "/images/users/" + getId() + "/" + getUserPhoto();
    }
}
