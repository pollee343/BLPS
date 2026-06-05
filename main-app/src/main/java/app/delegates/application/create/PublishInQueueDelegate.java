package app.delegates.application.create;

import app.dto.messages.ApplicationJiraExportMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component("publishInQueueDelegate")
@RequiredArgsConstructor
public class PublishInQueueDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(PublishInQueueDelegate.class);

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.queues.legal-report}")
    private String queueName;

    @Override
    public void execute(DelegateExecution execution) {
        Object rawApplicationId = execution.getVariable("applicationId");
        Long applicationId = rawApplicationId instanceof Number number ? number.longValue() : null;
        if (applicationId == null) {
            execution.setVariable("errorMessage", "applicationId is required");
            throw new BpmnError("QUEUE_PUBLISH_FAILED", "applicationId is required");
        }

        try {
            String payload = objectMapper.writeValueAsString(new ApplicationJiraExportMessage(applicationId));
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                String processInstanceId = execution.getProcessInstanceId();
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        jmsTemplate.convertAndSend(queueName, payload);
                        log.info("PublishInQueueDelegate after commit: processInstanceId={}, applicationId={}, queue={}",
                                processInstanceId, applicationId, queueName);
                    }
                });
            } else {
                jmsTemplate.convertAndSend(queueName, payload);
                log.info("PublishInQueueDelegate: processInstanceId={}, applicationId={}, queue={}",
                        execution.getProcessInstanceId(), applicationId, queueName);
            }
        } catch (JsonProcessingException | RuntimeException e) {
            execution.setVariable("errorMessage", e.getMessage());
            throw new BpmnError("QUEUE_PUBLISH_FAILED", e.getMessage());
        }
    }
}
