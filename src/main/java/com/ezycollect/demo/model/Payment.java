package com.ezycollect.demo.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class Payment {
    @NotNull
    @Size(min = 1, max = 255)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 255)
    private String lastName;

    @NotNull
    @Size(min = 1, max = 255)
    private String zipCode;

    @NotNull
    @Size(min = 16, max = 16)
    private String cardNumber;

    @NotNull
    @Positive
    private Integer amount;

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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(firstName, payment.firstName) && Objects.equals(lastName, payment.lastName) && Objects.equals(zipCode, payment.zipCode) && Objects.equals(cardNumber, payment.cardNumber) && Objects.equals(amount, payment.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, zipCode, cardNumber, amount);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", cardNumber='****" + '\'' +
                ", amount=" + amount +
                '}';
    }
}
