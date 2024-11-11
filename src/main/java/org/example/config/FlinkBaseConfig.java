package org.example.config;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;

public interface FlinkBaseConfig {

    default void defaultEnvConfig(StreamExecutionEnvironment streamExecutionEnvironment, FlinkConfigData flinkConfigData){
        streamExecutionEnvironment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
        streamExecutionEnvironment.getCheckpointConfig().setCheckpointInterval(flinkConfigData.getCheckpointInterval());
        streamExecutionEnvironment.getCheckpointConfig().setCheckpointTimeout(flinkConfigData.getCheckpointTimeout());
        streamExecutionEnvironment.getCheckpointConfig().setMinPauseBetweenCheckpoints(flinkConfigData.getMinPauseBetweenCheckpoints());
        streamExecutionEnvironment.getCheckpointConfig().enableUnalignedCheckpoints();
        streamExecutionEnvironment.getCheckpointConfig().setExternalizedCheckpointCleanup(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        streamExecutionEnvironment.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(0, Time.of(5, TimeUnit.SECONDS)));
    }

    default Configuration defaultFlinkConfig(){
        Configuration configuration = new Configuration();
        configuration.set(CheckpointingOptions.INCREMENTAL_CHECKPOINTS, true);
        configuration.set(ExecutionCheckpointingOptions.ENABLE_CHECKPOINTS_AFTER_TASKS_FINISH, true);
        return configuration;
    }
}
