package org.kosandron.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
//@RequiredArgsConstructor
public class OwnerKafkaProducer {
    //@Qualifier("replyingOwnerTemplate")
    private final ReplyingKafkaTemplate<String, Object, Object> ownerReplyingTemplate;

    public OwnerKafkaProducer(@Qualifier("replyingOwnerTemplate") ReplyingKafkaTemplate<String, Object, Object> ownerReplyingTemplate) {
        this.ownerReplyingTemplate = ownerReplyingTemplate;
    }
    @Value("${myproject.owner-send-topics}")
    private String SEND_TOPICS;

    public Object kafkaRequestReply(String key, Object request) throws Exception {
        ProducerRecord<String, Object> record = new ProducerRecord<>(SEND_TOPICS, key, request);
        RequestReplyFuture<String, Object, Object> replyFuture = ownerReplyingTemplate.sendAndReceive(record);
        SendResult<String, Object> sendResult = replyFuture.getSendFuture()
                .get(10, TimeUnit.SECONDS);

        ConsumerRecord<String, Object> consumerRecord = replyFuture.get(20, TimeUnit.SECONDS);

        return consumerRecord.value();
    }
}
