package org.kosandron.kafka.requests;

public record DeleteCatByOwnerIdRequest(Long ownerId, Long catId) {
}
