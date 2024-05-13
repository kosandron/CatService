package org.kosandron.dto;

import java.time.LocalDate;

public record InputCatData(String name,
                           LocalDate birthDate,
                           String breed,
                           String color,
                           Long ownerId) {
}
