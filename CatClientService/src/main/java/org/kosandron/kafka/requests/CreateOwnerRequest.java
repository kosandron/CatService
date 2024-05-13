package org.kosandron.kafka.requests;

import java.time.LocalDate;

public record CreateOwnerRequest(String name, LocalDate birthDay) {
}
