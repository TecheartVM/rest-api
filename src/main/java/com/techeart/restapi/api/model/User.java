package com.techeart.restapi.api.model;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public class User
{
    private UUID id;

    @Email(message = "Invalid email address.")
    private String email;

    @NotNull(message = "First name is required.")
    @Size(min = 1, max = 50, message = "First name must have between 1 and 50 characters.")
    private String firstName;

    @NotNull(message = "Last name is required.")
    @Size(min = 1, max = 50, message = "Last name must have between 1 and 50 characters.")
    private String lastName;

    @NotNull(message = "Birth date is required.")
    @Past(message = "Birth date must be earlier than current date.")
    private LocalDate birthDate;

    private String address;

    @Pattern(regexp = "^\\d{11,14}$", message = "Phone number must consist of 11 to 14 digit characters.")
    private String phoneNumber;

    public User() {  }

    public User(UUID id, String email, String firstName, String lastName, LocalDate birthDate)
    {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public User(UUID id, String email, String firstName, String lastName, LocalDate birthDate,
                String address, String phoneNumber)
    {
        this(id, email, firstName, lastName, birthDate);
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public User(String email, String firstName, String lastName, LocalDate birthDate)
    {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
