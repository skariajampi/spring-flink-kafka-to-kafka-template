package org.example.config;

import com.skaria.avro.model.Identifier;
import com.skaria.avro.model.aggregate.domain.CommandRecord;
import com.skaria.avro.model.aggregate.domain.DomainEventRecord;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.sink.KafkaSinkBuilder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.KafkaSourceBuilder;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.example.serde.FlinkKafkaAvroDeserialization;
import org.example.serde.FlinkKafkaAvroSerialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class KafkaConfig {
    private static final String DEV_PROFILE = "dev";
    private static final String PROD_PROFILE = "prod";
    private final KafkaConfigData kafkaConfigData;
    private final Environment env;

    public KafkaConfig(KafkaConfigData kafkaConfigData, Environment env) {
        this.kafkaConfigData = kafkaConfigData;
        this.env = env;
    }

    @Bean
    public KafkaSource<Tuple2<Identifier, CommandRecord>> kafkaSourceProcessSomeRecordCommand(){
        KafkaSourceBuilder<Tuple2<Identifier, CommandRecord>> kafkaSourceBuilder =
                defaultKafkaSourceCommandBuilder(kafkaConfigData.getBootstrapServers(),
                                                 kafkaConfigData.getSourceProcessSomeRecordCommandTopicName(),
                                                 kafkaConfigData.getSourceProcessSomeRecordCommandGroupName(),
                                                 kafkaConfigData.getSchemaRegistryUrl());
        //for msf set security properties
        //kafkaSourceBuilder.setProperties()
        return kafkaSourceBuilder.build();

    }

    @Bean
    public KafkaSink<Tuple2<Identifier, DomainEventRecord>> kafkaSinkSomeRecordUpdatedEventRecord(){
        KafkaSinkBuilder<Tuple2<Identifier, DomainEventRecord>> kafkaSinkBuilder =
                defaultKafkaSinkEventBuilder(kafkaConfigData.getBootstrapServers(),
                                                 kafkaConfigData.getSinkSomeRecordEnrichedEventTopicName(),
                                                 kafkaConfigData.getSchemaRegistryUrl());
        //for msf set security properties
        //kafkaSinkBuilder.setProperties()
        return kafkaSinkBuilder.build();

    }

    private KafkaSourceBuilder<Tuple2<Identifier, CommandRecord>> defaultKafkaSourceCommandBuilder(String bootStrapServer,
                                                                                                   String sourceTopicName,
                                                                                                   String sourceGroupName,
                                                                                                   String schemaRegistryUrl){
        return KafkaSource.<Tuple2<Identifier, CommandRecord>>builder()
                .setBootstrapServers(bootStrapServer)
                .setTopics(sourceTopicName)
                .setGroupId(sourceGroupName)
                .setStartingOffsets(OffsetsInitializer.latest())
                .setProperty("partition.discovery.interval.ms", "-1")
                .setProperty("commit.offsets.on.checkpoint", "true")
                .setDeserializer(new FlinkKafkaAvroDeserialization<>(Identifier.class, CommandRecord.class, schemaRegistryUrl));
    }

    private KafkaSinkBuilder<Tuple2<Identifier, DomainEventRecord>> defaultKafkaSinkEventBuilder(String bootStrapServer,
                                                                                                   String sourceTopicName,
                                                                                                   String schemaRegistryUrl){
        return KafkaSink.<Tuple2<Identifier, DomainEventRecord>>builder()
                .setBootstrapServers(bootStrapServer)
                .setRecordSerializer(new FlinkKafkaAvroSerialization<>(Identifier.class, DomainEventRecord.class, sourceTopicName, schemaRegistryUrl));
    }

}
