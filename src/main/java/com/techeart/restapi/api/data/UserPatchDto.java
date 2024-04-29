package com.techeart.restapi.api.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.techeart.restapi.api.model.User;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPatchDto
{
    @JsonProperty("email")
    private String email;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("birthDate")
    private LocalDate birthDate;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    public UserPatchDto() { }

    public UserPatchDto(String email, String firstName, String lastName, LocalDate birthDate, String address, String phoneNumber) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public User patchUser(User original)
    {
        return new User(
                original.getId(),
                this.email == null ? original.getEmail() : this.email,
                this.firstName == null ? original.getFirstName() : this.firstName,
                this.lastName == null ? original.getLastName() : this.lastName,
                this.birthDate == null ? original.getBirthDate() : this.birthDate,
                this.address == null ? original.getAddress() : this.address,
                this.phoneNumber == null ? original.getPhoneNumber() : this.phoneNumber
        );
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
}
