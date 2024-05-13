package org.kosandron.kafka.requests;

import java.util.Optional;

public record GetFilterCatsRequest(
        String color,
        String breed,
        String name) {
}
