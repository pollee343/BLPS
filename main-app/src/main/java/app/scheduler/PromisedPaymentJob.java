package app.scheduler;

import app.model.enams.PromisedPaymentStatus;
import app.repositories.PromisedPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PromisedPaymentJob implements Job {

    private final PromisedPaymentRepository promisedPaymentRepository;
    private final RuntimeService runtimeService;

    private static final Logger log = LoggerFactory.getLogger(PromisedPaymentJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<Long> userIds = promisedPaymentRepository.findDistinctUserIdsWithDuePayments(
                List.of(PromisedPaymentStatus.ACTIVE.name(), PromisedPaymentStatus.OVERDUE.name()),
                LocalDateTime.now()
        );

        for (Long userId : userIds) {
            runtimeService.startProcessInstanceByMessage(
                    "PromisedPaymentDueCheck",
                    Map.of("userDataId", userId)
            );
        }

        log.info("Promised payment job completed");
    }
}
