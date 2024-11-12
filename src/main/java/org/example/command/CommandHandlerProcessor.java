package org.example.command;

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
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.List;

@Slf4j
public class CommandHandlerProcessor extends KeyedProcessFunction<Identifier, Tuple2<Identifier, CommandRecord>,
        Tuple2<Identifier, DomainEventRecord>> {

    private final List<CommandHandler<DomainAggregateStateRecord, Identifier, CommandRecord, CommandType, DomainEventRecord>> commandHandlerList;
    private final MapStateDescriptor<String, SomeList> someListMapStateDescriptor;
    private final ValueStateDescriptor<Person> personValueStateDescriptor;
    private final OutputTag<Tuple2<Identifier, DomainEventRecord>> someRecordMatchOutputTag;

    public CommandHandlerProcessor(List<CommandHandler<DomainAggregateStateRecord, Identifier, CommandRecord, CommandType, DomainEventRecord>> commandHandlerList, MapStateDescriptor<String, SomeList> someListMapStateDescriptor, ValueStateDescriptor<Person> personValueStateDescriptor, OutputTag<Tuple2<Identifier, DomainEventRecord>> someRecordMatchOutputTag) {
        this.commandHandlerList = commandHandlerList;
        this.someListMapStateDescriptor = someListMapStateDescriptor;
        this.personValueStateDescriptor = personValueStateDescriptor;
        this.someRecordMatchOutputTag = someRecordMatchOutputTag;
    }

    @Override
    public void processElement(
            Tuple2<Identifier, CommandRecord> identifierCommandRecordTuple2,
            KeyedProcessFunction<Identifier, Tuple2<Identifier, CommandRecord>, Tuple2<Identifier, DomainEventRecord>>.Context context,
            Collector<Tuple2<Identifier, DomainEventRecord>> collector) throws Exception {

    }
}
