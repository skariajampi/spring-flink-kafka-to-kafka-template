package org.example.command.handlers.somerecord;

import com.skaria.avro.model.Identifier;
import com.skaria.avro.model.Person;
import com.skaria.avro.model.SomeList;
import com.skaria.avro.model.SomeRecord;
import com.skaria.avro.model.aggregate.domain.CommandRecord;
import com.skaria.avro.model.aggregate.domain.CommandType;
import com.skaria.avro.model.aggregate.domain.DomainAggregateStateRecord;
import com.skaria.avro.model.aggregate.domain.DomainEventRecord;
import com.skaria.avro.model.aggregate.domain.EventType;
import com.skaria.avro.model.aggregate.domain.ProcessSomeRecordCommandRecord;
import com.skaria.avro.model.aggregate.domain.SomeRecordEnrichedEventRecord;
import lombok.extern.slf4j.Slf4j;
import org.example.command.AbstractIdentifierCommandHandler;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class ProcessSomeRecordCommandHandler extends AbstractIdentifierCommandHandler<ProcessSomeRecordCommandRecord, SomeRecordEnrichedEventRecord> {

    @Override
    protected DomainEventRecord getSuccessEvent(ProcessSomeRecordCommandRecord command, CommandRecord commandRecord,
                                                DomainAggregateStateRecord currentState) {
        SomeRecord someRecord = command.getSomeRecord();

        SomeRecordEnrichedEventRecord.Builder someRecordEnrichedEventRecordBuilder = SomeRecordEnrichedEventRecord.newBuilder()
                .setSomeRecord(someRecord)
                .setEventId(UUID.randomUUID())
                .setCreationTimestamp("");

        Map<String, SomeList> someList = currentState.getSomeList();
        Person person = currentState.getPerson();

        if(Objects.nonNull(someList) && !someList.isEmpty()){
            someRecordEnrichedEventRecordBuilder.setMatches(someList);
        }

        if(Objects.nonNull(person)){
            someRecordEnrichedEventRecordBuilder.setPerson(person);
        }

        DomainEventRecord domainEventRecord = DomainEventRecord.newBuilder()
                .setIdentifier(Identifier.newBuilder().setIdentifier(someRecord.getIdentifier().getIdentifier()).build())
                .setEventType(EventType.SOME_RECORD_ENRICHED_EVENT)
                .setCreationTimestamp("")
                .setEvent(someRecordEnrichedEventRecordBuilder.build())
                .build();

        if(Objects.nonNull(domainEventRecord)){
            log.error("Process Some Record Domain Event cannot be null!");
            throw new RuntimeException("Process Some Record Domain Event cannot be null!");
        }
        return domainEventRecord;
    }

    @Override
    protected DomainAggregateStateRecord reduceState(SomeRecordEnrichedEventRecord domainEvent, DomainEventRecord domainEventRecord,
                                                     DomainAggregateStateRecord currentState) {
        return currentState;
    }

    @Override
    public boolean supports(CommandType commandType) {
        return false;
    }
}
