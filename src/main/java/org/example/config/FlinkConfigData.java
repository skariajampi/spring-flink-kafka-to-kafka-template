package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "flink-config")
public class FlinkConfigData {
    private Integer checkpointInterval;
    private Integer minPauseBetweenCheckpoints;
    private Integer checkpointTimeout;
    private Integer parallelism;
    private Integer maxParallelism;
    private boolean disableGenericTypes;
    private String listStateTtlValue;
    private String bindPort;
    private String checkpointLocation;
}
