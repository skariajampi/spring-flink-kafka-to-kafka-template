package org.example.command;

import com.skaria.avro.model.Identifier;
import com.skaria.avro.model.Person;
import com.skaria.avro.model.SomeList;
import com.skaria.avro.model.SomeListId;
import com.skaria.avro.model.aggregate.domain.CommandRecord;
import com.skaria.avro.model.aggregate.domain.CommandType;
import com.skaria.avro.model.aggregate.domain.DomainAggregateStateRecord;
import com.skaria.avro.model.aggregate.domain.DomainEventRecord;
import com.skaria.avro.model.aggregate.domain.IdentifierRemovedFromListEventRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class CommandHandlerProcessor extends KeyedProcessFunction<Identifier, Tuple2<Identifier, CommandRecord>,
        Tuple2<Identifier, DomainEventRecord>> {

    private MapState<String, SomeList> someListFlinkState = null;
    private ValueState<Person> personFlinkState =  null;

    private final List<CommandHandler<DomainAggregateStateRecord, Identifier, CommandRecord, CommandType, DomainEventRecord>> commandHandlerList;
    private final MapStateDescriptor<String, SomeList> someListMapStateDescriptor;
    private final ValueStateDescriptor<Person> personValueStateDescriptor;
    private final OutputTag<Tuple2<Identifier, DomainEventRecord>> someRecordMatchOutputTag;

    public CommandHandlerProcessor(List<CommandHandler<DomainAggregateStateRecord, Identifier, CommandRecord, CommandType, DomainEventRecord>>
                                           commandHandlerList, MapStateDescriptor<String, SomeList> someListMapStateDescriptor,
                                   ValueStateDescriptor<Person> personValueStateDescriptor, OutputTag<Tuple2<Identifier, DomainEventRecord>>
                                           someRecordMatchOutputTag) {
        this.commandHandlerList = commandHandlerList;
        this.someListMapStateDescriptor = someListMapStateDescriptor;
        this.personValueStateDescriptor = personValueStateDescriptor;
        this.someRecordMatchOutputTag = someRecordMatchOutputTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.someListFlinkState = getRuntimeContext().getMapState(someListMapStateDescriptor);
        this.personFlinkState = getRuntimeContext().getState(personValueStateDescriptor);
    }

    @Override
    public void processElement(
            Tuple2<Identifier, CommandRecord> identifierCommandRecordTuple2,
            KeyedProcessFunction<Identifier, Tuple2<Identifier, CommandRecord>, Tuple2<Identifier, DomainEventRecord>>.Context context,
            Collector<Tuple2<Identifier, DomainEventRecord>> collector) throws Exception {

        Identifier identifier = identifierCommandRecordTuple2.f0;
        CommandRecord commandRecord = identifierCommandRecordTuple2.f1;

        DomainAggregateStateRecord domainAggregateStateRecord = getDomainAggregateStateRecord();
        CommandHandlerResult<Identifier, DomainEventRecord, DomainAggregateStateRecord> result =
                runCommand(identifier, commandRecord, domainAggregateStateRecord, commandHandlerList);

        if(commandRecord.getCommandType().name().equals(CommandType.PROCESS_SOME_RECORD_COMMAND.name())){
            if(result.getNewState().getSomeList() != null && !result.getNewState().getSomeList().isEmpty()){
                context.output(someRecordMatchOutputTag, Tuple2.of(identifier, result.getValue()));
            }
        }

        /*if(commandRecord.getCommandType().name().equals(CommandType.ADD_IDENTIFIER_TO_SOME_LIST_COMMAND.name())){
            this.someListFlinkState.putAll(result.getNewState().getSomeList());
        }

        if(commandRecord.getCommandType().name().equals(CommandType.REMOVE_IDENTIFIER_FROM_SOME_LIST_COMMAND.name())){
            IdentifierRemovedFromListEventRecord event = (IdentifierRemovedFromListEventRecord) result.getValue().getEvent();
            this.someListFlinkState.remove(event.getSomeListId().toString());
        }

        if(commandRecord.getCommandType().name().equals(CommandType.UPDATE_PERSON_COMMAND.name())){
            this.personFlinkState.update(result.getNewState().getPerson());
        }*/

    }

    private static CommandHandlerResult<Identifier, DomainEventRecord, DomainAggregateStateRecord>
    runCommand(Identifier identifier, CommandRecord commandRecord, DomainAggregateStateRecord domainAggregateStateRecord,
               List<CommandHandler<DomainAggregateStateRecord, Identifier, CommandRecord, CommandType, DomainEventRecord>> commandHandlers){

        return commandHandlers.stream()
                .filter(handler -> handler.supports(commandRecord.getCommandType()))
                .findFirst()
                .map( handler -> handler.handleCommand(domainAggregateStateRecord, identifier, commandRecord))
                .orElseThrow( () -> new RuntimeException("Invalid Command Type!"));
    }

    private DomainAggregateStateRecord getDomainAggregateStateRecord() {
        DomainAggregateStateRecord domainAggregateStateRecord;
        try{
            DomainAggregateStateRecord.Builder domainAggregateStateRecordBuilder = DomainAggregateStateRecord.newBuilder()
                    .setPerson(null)
                    .setSomeList(null);
            if(someListFlinkState != null){
                if(!someListFlinkState.isEmpty()){
                    Map<String, SomeList> someListDetails = StreamSupport.stream(someListFlinkState.entries().spliterator(), false)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    domainAggregateStateRecordBuilder.setSomeList(someListDetails);
                }
            }

            if(personFlinkState != null){
                domainAggregateStateRecordBuilder.setPerson(personFlinkState.value());
            }

            domainAggregateStateRecord = domainAggregateStateRecordBuilder.build();

        } catch (Exception e) {
            log.error("Unable to build internal 'DomainAggregateStateRecord' - {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return domainAggregateStateRecord;
    }
}
