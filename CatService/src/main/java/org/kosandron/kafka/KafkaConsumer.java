package org.kosandron.kafka;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.kosandron.kafka.messageHandlers.MessageHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final List<MessageHandler> handlerList;

    @SneakyThrows
    @KafkaListener(groupId="${myproject.cat-consumer-group}", topics = "${myproject.cat-send-topics}")
    @SendTo
    public Message<?> listen(ConsumerRecord<String, Object> consumerRecord) {
        String key = consumerRecord.key();
        String body = consumerRecord.value().toString();
        System.out.println("key: " + key + " value: " + body);

        String response = "";
        for (MessageHandler handler : handlerList) {
            if (handler.canHandle(key)) {
                response = handler.handle(body);
                break;
            }
        }

        return MessageBuilder.withPayload(response).build();
    }
}
