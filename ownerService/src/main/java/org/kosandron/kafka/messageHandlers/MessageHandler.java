package org.kosandron.kafka.messageHandlers;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessageHandler {
    String handle(String value) throws JsonProcessingException;
    boolean canHandle(String key);
}
