package com.my_hourly.form_16.service;

import com.my_hourly.form_16.repository.Form16Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class Form16CertificateNumberGenerator {

    private final Form16Repository form16Repository;


    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    private static final int CODE_LENGTH = 8;


    private final SecureRandom random =
            new SecureRandom();


    /**
     * Generates a unique 8-character certificate number.
     *
     * Example:
     * A1B2C3D4
     * X7K9P2M4
     */
    public String generate() {

        String certificateNumber;

        do {

            certificateNumber =
                    generateRandomCode();

        } while (
                form16Repository
                        .existsByCertificateNo(
                                certificateNumber
                        )
        );

        return certificateNumber;
    }


    private String generateRandomCode() {

        StringBuilder code =
                new StringBuilder(CODE_LENGTH);


        for (
                int i = 0;
                i < CODE_LENGTH;
                i++
        ) {

            int index =
                    random.nextInt(
                            CHARACTERS.length()
                    );

            code.append(
                    CHARACTERS.charAt(index)
            );
        }


        return code.toString();
    }
}