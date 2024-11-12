package org.example.jobs;

import com.skaria.avro.model.Identifier;
import com.skaria.avro.model.aggregate.domain.CommandRecord;
import com.skaria.avro.model.aggregate.domain.DomainEventRecord;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.example.config.KafkaConfigData;

public class IdentifierStreamingJob {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaSource<Tuple2<Identifier, CommandRecord>> kafkaSourceProcessSomeRecordCommand;
    private final KafkaSink<Tuple2<Identifier, DomainEventRecord>> kafkaSinkProcessSomeRecordEvent;

    public IdentifierStreamingJob(KafkaConfigData kafkaConfigData, KafkaSource<Tuple2<Identifier, CommandRecord>> kafkaSourceProcessSomeRecordCommand, KafkaSink<Tuple2<Identifier, DomainEventRecord>> kafkaSinkProcessSomeRecordEvent) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaSourceProcessSomeRecordCommand = kafkaSourceProcessSomeRecordCommand;
        this.kafkaSinkProcessSomeRecordEvent = kafkaSinkProcessSomeRecordEvent;
    }
}
