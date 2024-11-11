package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {
    private String sourceProcessSomeRecordCommandTopicName;
    private String sourceProcessSomeRecordCommandGroupName;
    private String sourceUpdatePersonCommandTopicName;
    private String sourceUpdatePersonCommandGroupName;
    private String sourceAddListCommandTopicName;
    private String sourceAddListCommandGroupName;
    private String sinkSomeRecordEnrichedEventTopicName;
    private String sinkPersonUpdatedEventTopicName;
    private String sinkListAddedEventTopicName;
    private String bootstrapServers;
    private String schemaRegistryUrl;
}
