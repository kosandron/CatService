package org.kosandron.kafka.requests;

public record AddCatByOwnerIdRequest(Long ownerId, Long catId) {
}
