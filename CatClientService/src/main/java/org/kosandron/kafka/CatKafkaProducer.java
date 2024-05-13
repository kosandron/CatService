package org.kosandron.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CatKafkaProducer {
    private ReplyingKafkaTemplate<String, Object, Object> catReplyingTemplate;

    public CatKafkaProducer(@Qualifier("replyingCatTemplate") ReplyingKafkaTemplate<String, Object, Object> catReplyingTemplate) {
        this.catReplyingTemplate = catReplyingTemplate;
    }

    @Value("${myproject.cat-send-topics}")
    private String SEND_TOPICS;

    public Object kafkaRequestReply(String key, Object request) throws Exception {
        ProducerRecord<String, Object> record = new ProducerRecord<>(SEND_TOPICS, key, request);
        RequestReplyFuture<String, Object, Object> replyFuture = catReplyingTemplate.sendAndReceive(record);
        SendResult<String, Object> sendResult = replyFuture.getSendFuture()
                .get(60, TimeUnit.SECONDS);

        ConsumerRecord<String, Object> consumerRecord = replyFuture.get(60, TimeUnit.SECONDS);

        return consumerRecord.value();
    }
}

