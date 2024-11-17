package org.example.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.example.jobs.IdentifierStreamingJob;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Slf4j
public class JobRunner implements ApplicationRunner {

    private final IdentifierStreamingJob identifierStreamingJob;
    private final StreamExecutionEnvironment streamExecutionEnvironment;

    public JobRunner(IdentifierStreamingJob identifierStreamingJob, StreamExecutionEnvironment streamExecutionEnvironment) {
        this.identifierStreamingJob = identifierStreamingJob;
        this.streamExecutionEnvironment = streamExecutionEnvironment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        identifierStreamingJob.buildJobTopology(streamExecutionEnvironment);
        log.info("IdentifierStreamingJob topology created...");
        streamExecutionEnvironment.execute("Identifier Streaming Job");
    }
}
