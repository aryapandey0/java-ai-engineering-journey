package org.example;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class TicketValidator {

    private static final ValidatorFactory FACTORY =
            Validation.buildDefaultValidatorFactory();

    private static final Validator VALIDATOR =
            FACTORY.getValidator();

    public static void validate(
            SupportTicketAnalysis analysis
    ) {

        var violations = VALIDATOR.validate(analysis);

        if (!violations.isEmpty()) {

            StringBuilder errors = new StringBuilder();

            violations.forEach(violation ->
                    errors.append(
                                    violation.getPropertyPath()
                            ).append(": ")
                            .append(violation.getMessage())
                            .append("; ")
            );

            throw new IllegalArgumentException(
                    "Invalid ticket analysis: " + errors
            );
        }
    }
}