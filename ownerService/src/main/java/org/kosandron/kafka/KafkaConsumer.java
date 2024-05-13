package org.kosandron.kafka;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.kosandron.dto.InputOwnerData;
import org.kosandron.dto.OwnerMainDataDto;
import org.kosandron.entities.Owner;
import org.kosandron.kafka.messageHandlers.MessageHandler;
import org.kosandron.kafka.responses.EmptyResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    @KafkaListener(groupId="${myproject.owner-consumer-group}", topics = "${myproject.owner-send-topics}")
    @SendTo
    public Message<?> listen(ConsumerRecord<String, Object> consumerRecord) {
        String key = consumerRecord.key();
        String body = consumerRecord.value().toString();

        String response = "";
        for (MessageHandler handler : handlerList) {
            if (handler.canHandle(key)) {
                response = handler.handle(body);
                break;
            }
        }

        System.out.println(response);
        return MessageBuilder.withPayload(response).build();
    }
}
