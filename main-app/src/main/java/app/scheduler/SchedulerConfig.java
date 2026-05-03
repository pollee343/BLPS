package app.scheduler;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchedulerConfig {

    @Bean
    public JobDetail promisedPaymentJobDetail() {
        return JobBuilder.newJob(PromisedPaymentJob.class)
                .withIdentity("promisedPaymentJob")
                .build();
    }

    @Bean
    public Trigger promisedPaymentJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(promisedPaymentJobDetail())
                .withIdentity("promisedPaymentTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(20).repeatForever())
                .build();
    }
}
