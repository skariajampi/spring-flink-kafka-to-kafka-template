package org.example.command;

import com.skaria.avro.model.ErrorRecord;

import java.util.Collections;
import java.util.List;

public class CommandHandlerResult<K, V, STATE> {

    private final CommandHandlerResulType type;
    private final K key;
    private final V value;
    private final STATE newState;
    private final List<ErrorRecord> errors;


    public CommandHandlerResult(CommandHandlerResulType type, K key, V value, STATE newState, List<ErrorRecord> errors) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.newState = newState;
        this.errors = errors;
    }

    public static <K,V, STATE> CommandHandlerResult<K,V, STATE> valid(K key, V value, STATE state){
        return new CommandHandlerResult<>(CommandHandlerResulType.VALID, key, value, state, Collections.emptyList());
    }

    public static <K,V, STATE> CommandHandlerResult<K,V, STATE> error(K key, V value, STATE state){
        return new CommandHandlerResult<>(CommandHandlerResulType.ERROR, key, value, state, Collections.emptyList());
    }

    public boolean isValid(){
        return type == CommandHandlerResulType.VALID;
    }

    public boolean isError(){
        return type == CommandHandlerResulType.ERROR;
    }

    public enum CommandHandlerResulType{
        VALID,
        ERROR
    }
}
