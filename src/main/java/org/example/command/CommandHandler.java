package org.example.command;

public interface CommandHandler<STATE, KEY, COMMAND_RECORD, COMMAND_TYPE, DOMAIN_EVENT> {

    CommandHandlerResult<KEY, DOMAIN_EVENT, STATE> handleCommand(STATE currentState, KEY key, COMMAND_RECORD commandRecord);

    boolean supports(COMMAND_TYPE commandType);
}
