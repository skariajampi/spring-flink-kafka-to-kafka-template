package org.example.command;

import com.skaria.avro.model.Identifier;
import com.skaria.avro.model.aggregate.domain.CommandRecord;
import com.skaria.avro.model.aggregate.domain.CommandType;
import com.skaria.avro.model.aggregate.domain.DomainAggregateStateRecord;
import com.skaria.avro.model.aggregate.domain.DomainEventRecord;
import org.apache.avro.specific.SpecificRecord;

import java.io.Serializable;

public abstract class AbstractIdentifierCommandHandler<T extends SpecificRecord, DE extends SpecificRecord>
        implements CommandHandler<DomainAggregateStateRecord, Identifier, CommandRecord, CommandType, DomainEventRecord>, Serializable {

    @Override
    public CommandHandlerResult<Identifier, DomainEventRecord, DomainAggregateStateRecord> handleCommand(DomainAggregateStateRecord currentState,
                                                                                                         Identifier identifier,
                                                                                                         CommandRecord commandRecord) {
        T command = (T) commandRecord.getCommand();
        DomainEventRecord domainEventRecord = getSuccessEvent(command, commandRecord, currentState);
        DE domainEvent = (DE) domainEventRecord.getEvent();
        DomainAggregateStateRecord newState = reduceState(domainEvent, domainEventRecord, currentState);

        return CommandHandlerResult.valid(identifier, domainEventRecord, newState);
    }

    protected abstract DomainEventRecord getSuccessEvent(T command, CommandRecord commandRecord, DomainAggregateStateRecord currentState);

    protected abstract DomainAggregateStateRecord reduceState(DE domainEvent, DomainEventRecord domainEventRecord,
                                                              DomainAggregateStateRecord currentState);
}
