package org.kosandron.kafka.requests;

public record GetFilterCatsRequest(
        String color,
        String breed,
        String name) {
}
