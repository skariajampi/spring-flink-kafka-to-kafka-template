package org.example;

import com.amazonaws.services.kinesisanalytics.runtime.KinesisAnalyticsRuntime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@SpringBootApplication
@Slf4j
public class IdentifierMatchStreamingJobApplication {

    public static final String LOCAL = "local";
    public static final String KINESIS_RUNTIME_CONFIG = "config";
    public static final String KINSESIS_SPRING_PROFILE_PROPERTY = "spring-profile";

    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(IdentifierMatchStreamingJobApplication.class)
                .profiles(getSpringProfile());
        springApplicationBuilder.build().run(args);
    }

    private static String getSpringProfile(){
        Map<String, Properties> applicaPropertiesMap;
        String springProfile = LOCAL;
        try {
            applicaPropertiesMap = KinesisAnalyticsRuntime.getApplicationProperties();
            if (!applicaPropertiesMap.isEmpty()) {
                springProfile = applicaPropertiesMap.get(KINESIS_RUNTIME_CONFIG).get(KINSESIS_SPRING_PROFILE_PROPERTY).toString();
            }
            log.info("Spring profile = {}", springProfile);
        } catch(IOException ioException){
            throw new RuntimeException("Failed to obtain AWS Managed Flink Runtime properties = {}", ioException);
        }
        return springProfile;
    }
}