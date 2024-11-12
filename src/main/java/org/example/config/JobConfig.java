package org.example.config;

import com.skaria.avro.model.Identifier;
import com.skaria.avro.model.Person;
import com.skaria.avro.model.SomeList;
import com.skaria.avro.model.aggregate.domain.CommandRecord;
import com.skaria.avro.model.aggregate.domain.CommandType;
import com.skaria.avro.model.aggregate.domain.DomainAggregateStateRecord;
import com.skaria.avro.model.aggregate.domain.DomainEventRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.OutputTag;
import org.example.command.CommandHandler;
import org.example.command.CommandHandlerProcessor;
import org.example.command.handlers.somerecord.ProcessSomeRecordCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class JobConfig {

    private final MapStateDescriptor<String, SomeList> someListMapStateDescriptor;
    private final ValueStateDescriptor<Person> personValueStateDescriptor;
    private final OutputTag<Tuple2<Identifier, DomainEventRecord>> someRecordMatchOutputTag;


    public JobConfig(MapStateDescriptor<String, SomeList> someListMapStateDescriptor, ValueStateDescriptor<Person> personValueStateDescriptor, OutputTag<Tuple2<Identifier, DomainEventRecord>> someRecordMatchOutputTag) {
        this.someListMapStateDescriptor = someListMapStateDescriptor;
        this.personValueStateDescriptor = personValueStateDescriptor;
        this.someRecordMatchOutputTag = someRecordMatchOutputTag;
    }

    @Bean
    public CommandHandlerProcessor commandHandlerProcessor(){
        return new CommandHandlerProcessor(commandHandlers(), someListMapStateDescriptor, personValueStateDescriptor, someRecordMatchOutputTag);
    }

    private List<CommandHandler<DomainAggregateStateRecord, Identifier, CommandRecord, CommandType, DomainEventRecord>> commandHandlers() {
        return List.of(new ProcessSomeRecordCommandHandler());
    }
}
