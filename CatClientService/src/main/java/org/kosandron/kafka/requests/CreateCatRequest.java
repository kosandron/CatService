package org.kosandron.kafka.requests;

import org.kosandron.enums.Color;

import java.time.LocalDate;

public record CreateCatRequest(
        String name,
        LocalDate birthDate,
        String breed,
        Color color,
        Long ownerId
) {
}
