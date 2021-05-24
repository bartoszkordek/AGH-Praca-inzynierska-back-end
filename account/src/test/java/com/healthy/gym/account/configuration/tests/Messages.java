package com.healthy.gym.account.configuration.tests;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"delete.account.success", "Konto zostało pomyślnie usunięte."},
                {"exception.account.not.found", "Nie ma w bazie takiego użytkownika."},
                {"exception.access.denied", "Nie masz uprawnień do wykonania tej operacji."},
                {"field.name.failure", "Imię powinno mieć od 2 do 60 znaków."},
                {"field.surname.failure", "Nazwisko powinno mieć od 2 do 60 znaków."},
                {"field.email.failure", "Proszę podać poprawny adres email."},
                {"field.phone.number.failure", "Niepoprawny format numeru telefonu."},
                {"field.required", "Pole jest wymagane."},
                {"field.password.failure", "Hasło powinno mieć od 8 do 24 znaków."},
                {"field.password.match.failure", "Podane hasła powinny być identyczne."},
                {"password.change.success", "Hasło zostało pomyślnie zmienione."},
                {"password.exception.old.password.does.not.match",
                        "Podano nieprawidłowe stare hasło. Wpisz poprawne."},
                {"password.exception.old.identical.with.new.password",
                        "Nowe hasło jest takie samo jak stare. Podaj inne hasło."},
                {"request.failure", "Podczas przetwarzania żądania wystąpił błąd."},
                {"account.change.user.data.success", "Pomyślnie zmieniono dane użytkownika."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"delete.account.success", "Account has been removed successfully."},
                {"exception.account.not.found", "There is no such user in the database."},
                {"exception.access.denied", "You are not allowed to perform this operation."},
                {"field.name.failure", "Name should have from 2 to 60 characters."},
                {"field.surname.failure", "Surname should have from 2 to 60 characters."},
                {"field.email.failure", "Provide valid email address."},
                {"field.phone.number.failure", "Invalid phone number format."},
                {"field.required", "Field is required."},
                {"field.password.failure", "Password should have from 8 to 24 characters."},
                {"field.password.match.failure", "Provided passwords should match."},
                {"password.change.success", "Password changed successfully."},
                {"password.exception.old.password.does.not.match",
                        "Provided invalid old password. Type correct one."},
                {"password.exception.old.identical.with.new.password",
                        "New password is equal to old one. Provide different password."},
                {"request.failure", "An error occurred while processing your request."},
                {"account.change.user.data.success", "User data has been changed successfully."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
