package com.healthy.gym.equipment.configuration;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Messages {
    private static Map<String, String> getMessagesPL() {
        return Stream.of(new String[][]{
                {"exception.access.denied", "Nie masz uprawnień do wykonania tej operacji."},
                {"exception.internal.error", "Podczas przetwarzania żądania wystąpił błąd."},
                {"exception.multipart.body", "Źle wypełniony formularz."},
                {"exception.unsupported.data.type", "Błąd. Dopuszczalny format pliku to JPEG lub PNG."},
                {"equipment.created", "Nowy sprzęt został utworzony."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    private static Map<String, String> getMessagesEN() {
        return Stream.of(new String[][]{
                {"exception.access.denied", "You are not allowed to perform this operation."},
                {"exception.internal.error" ,"An error occurred while processing your request."},
                {"exception.multipart.body", "Incorrectly completed form."},
                {"exception.unsupported.data.type", "Error. The acceptable file format is JPEG or PNG."},
                {"equipment.created", "New equipment has been created."}
        }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public static Map<String, String> getMessagesAccordingToLocale(TestCountry country) {
        if (country == TestCountry.POLAND) return getMessagesPL();
        return getMessagesEN();
    }
}
