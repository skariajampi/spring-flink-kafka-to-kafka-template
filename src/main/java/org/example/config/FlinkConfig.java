package org.example.config;

import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import static org.apache.flink.configuration.RestOptions.BIND_PORT;
import static org.apache.flink.configuration.TaskManagerOptions.CPU_CORES;
import static org.apache.flink.configuration.TaskManagerOptions.MANAGED_MEMORY_SIZE;
import static org.apache.flink.configuration.TaskManagerOptions.TASK_HEAP_MEMORY;
import static org.apache.flink.configuration.TaskManagerOptions.TASK_OFF_HEAP_MEMORY;

@Configuration
public class FlinkConfig implements FlinkBaseConfig{
    private static final String DEV_PROFILE = "dev";
    private static final String PROD_PROFILE = "prod";
    private final FlinkConfigData flinkConfigData;
    private final Environment env;

    public FlinkConfig(FlinkConfigData flinkConfigData, Environment env) {
        this.flinkConfigData = flinkConfigData;
        this.env = env;
    }

    @Bean
    @Profile("local")
    public StreamExecutionEnvironment streamExecutionEnvironmentLocal(){
        StreamExecutionEnvironment localStreamExecEnv = buildLocalStreamExecEnv();
        defaultEnvConfig(localStreamExecEnv, flinkConfigData);
        localStreamExecEnv.getCheckpointConfig().setCheckpointStorage(flinkConfigData.getCheckpointLocation());
        return localStreamExecEnv;
    }

    private StreamExecutionEnvironment buildLocalStreamExecEnv(){
        org.apache.flink.configuration.Configuration flinkConfig = defaultFlinkConfig();
        flinkConfig.set(BIND_PORT, flinkConfigData.getBindPort());
        flinkConfig.set(CPU_CORES, 8.0);
        flinkConfig.set(TASK_HEAP_MEMORY, MemorySize.ofMebiBytes(1024));
        flinkConfig.set(TASK_OFF_HEAP_MEMORY, MemorySize.ofMebiBytes(512));
        flinkConfig.set(MANAGED_MEMORY_SIZE, MemorySize.ofMebiBytes(1024));
        flinkConfig.setString("s3.entropy.key", "_entropy_");
        flinkConfig.setString("s3.entropy.length", "8");
        flinkConfig.setString("s3.endpoint", "http://127.0.0.1:9000");
        flinkConfig.setString("s3.entropy.key", "_entropy_");
        flinkConfig.setString("s3.path-style", "true");
        flinkConfig.setString("s3.access.key", "minio");
        flinkConfig.setString("s3.secret.key", "minio123");

        FileSystem.initialize(GlobalConfiguration.loadConfiguration(flinkConfig));
        StreamExecutionEnvironment localEnvironmentWithWebUI = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(flinkConfig);
        localEnvironmentWithWebUI.setParallelism(flinkConfigData.getParallelism());
        localEnvironmentWithWebUI.setMaxParallelism(flinkConfigData.getMaxParallelism());
        localEnvironmentWithWebUI.setStateBackend(new HashMapStateBackend());
        return localEnvironmentWithWebUI;
    }

}
