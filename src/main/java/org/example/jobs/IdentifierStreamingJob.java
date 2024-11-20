package org.example.jobs;

import com.skaria.avro.model.Identifier;
import com.skaria.avro.model.aggregate.domain.CommandRecord;
import com.skaria.avro.model.aggregate.domain.DomainEventRecord;
import com.skaria.avro.model.aggregate.domain.EventType;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.example.command.CommandHandlerProcessor;
import org.example.config.KafkaConfigData;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IdentifierStreamingJob {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaSource<Tuple2<Identifier, CommandRecord>> kafkaSourceProcessSomeRecordCommand;
    private final KafkaSink<Tuple2<Identifier, DomainEventRecord>> kafkaSinkProcessSomeRecordEvent;
    private final CommandHandlerProcessor commandHandlerProcessor;

    public IdentifierStreamingJob(KafkaConfigData kafkaConfigData, KafkaSource<Tuple2<Identifier, CommandRecord>> kafkaSourceProcessSomeRecordCommand, KafkaSink<Tuple2<Identifier, DomainEventRecord>> kafkaSinkProcessSomeRecordEvent, CommandHandlerProcessor commandHandlerProcessor) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaSourceProcessSomeRecordCommand = kafkaSourceProcessSomeRecordCommand;
        this.kafkaSinkProcessSomeRecordEvent = kafkaSinkProcessSomeRecordEvent;
        this.commandHandlerProcessor = commandHandlerProcessor;
    }

    public void buildJobTopology(StreamExecutionEnvironment streamExecutionEnvironment){
        log.info("Bootstrap server: {}", kafkaConfigData.getBootstrapServers());
        DataStreamSource<Tuple2<Identifier, CommandRecord>> processSomeRecordCommandStream = streamExecutionEnvironment.fromSource(kafkaSourceProcessSomeRecordCommand, WatermarkStrategy.noWatermarks(), "Process SomeRecord Command");


        SingleOutputStreamOperator<Tuple2<Identifier, DomainEventRecord>> processedCommands =
                processSomeRecordCommandStream.keyBy(command -> command.f0)
                .process(commandHandlerProcessor)
                .name("ProcessSomeRecordCommand")
                .setUidHash("95e1c2e1e025359106fe8f6d0ee98972")
                .setDescription("ProcessSomeRecordCommand Processor");

        processedCommands
                .addSink(new SinkFunction<Tuple2<Identifier, DomainEventRecord>>() {
                    @Override
                    public void invoke(Tuple2<Identifier, DomainEventRecord> value, Context context) {
                        System.out.println("Processed Command: " + value);
                    }
                })
                .name("Debug Sink");

        processedCommands
                .filter(processedCommand -> processedCommand.f1.getEventType().equals(EventType.SOME_RECORD_ENRICHED_EVENT))
                .sinkTo(kafkaSinkProcessSomeRecordEvent)
                .name("P")
                .setDescription("");
    }
}
