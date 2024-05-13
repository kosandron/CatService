package org.kosandron.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;

@Configuration
public class KafkaConfig {
    @Value("${myproject.cat-reply-topics}")
    private String CAT_REPLY_TOPICS;

    @Value("${myproject.cat-consumer-group}")
    private String CAT_CONSUMER_GROUPS;

    @Value("${myproject.owner-reply-topics}")
    private String OWNER_REPLY_TOPICS;

    @Value("${myproject.owner-consumer-group}")
    private String OWNER_CONSUMER_GROUPS;

  /*  @Bean
    public ReplyingKafkaTemplate<String, Object, Object> replyingCatTemplate(
            DefaultKafkaProducerFactory<String, Object> pf,
            @Qualifier("repliesCatContainer")
            ConcurrentMessageListenerContainer<String, Object> repliesCatContainer) {
        ReplyingKafkaTemplate<String, Object, Object> replyTemplate = new ReplyingKafkaTemplate<>(pf, repliesCatContainer);
        replyTemplate.setDefaultReplyTimeout(Duration.ofSeconds(10));
        replyTemplate.setSharedReplyTopic(true);
        return replyTemplate;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> repliesCatContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
        ConcurrentMessageListenerContainer<String, Object> repliesContainer = containerFactory.createContainer(CAT_REPLY_TOPICS);
        repliesContainer.getContainerProperties().setGroupId(CAT_CONSUMER_GROUPS);
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }

    @Bean
    public ReplyingKafkaTemplate<String, Object, Object> replyingOwnerTemplate(
            DefaultKafkaProducerFactory<String, Object> pf,
            @Qualifier("repliesOwnerContainer")
            ConcurrentMessageListenerContainer<String, Object> repliesOwnerContainer) {
        ReplyingKafkaTemplate<String, Object, Object> replyTemplate = new ReplyingKafkaTemplate<>(pf, repliesOwnerContainer);
        replyTemplate.setDefaultReplyTimeout(Duration.ofSeconds(10));
        replyTemplate.setSharedReplyTopic(true);
        return replyTemplate;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, Object> repliesOwnerContainer(
            ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
        ConcurrentMessageListenerContainer<String, Object> repliesContainer = containerFactory.createContainer(OWNER_REPLY_TOPICS);
        repliesContainer.getContainerProperties().setGroupId(OWNER_CONSUMER_GROUPS);
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }*/
}
