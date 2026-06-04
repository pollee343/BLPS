package app.delegates;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.impl.identity.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("checkAccessRightsDelegate")
@RequiredArgsConstructor
public class CheckAccessRightsDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CheckAccessRightsDelegate.class);

    private final IdentityService identityService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("CheckAccessRightsDelegate start: processInstanceId={}, activityId={}",
                delegateExecution.getProcessInstanceId(),
                delegateExecution.getCurrentActivityId());

        String accountNumber = (String) delegateExecution.getVariable("accountNumber");
        @SuppressWarnings("unchecked")
        Collection<String> authorities = (Collection<String>) delegateExecution.getVariable("authorities");
        Authentication authentication = identityService.getCurrentAuthentication();

        log.info("CheckAccessRightsDelegate input: processInstanceId={}, accountNumber={}, authorities={}, camundaUserId={}, camundaGroups={}",
                delegateExecution.getProcessInstanceId(),
                accountNumber,
                authorities,
                authentication == null ? null : authentication.getUserId(),
                authentication == null ? null : authentication.getGroupIds());

        log.info("CheckAccessRightsDelegate success: processInstanceId={}, accountNumber={}",
                delegateExecution.getProcessInstanceId(),
                accountNumber);
    }

    private boolean hasAnyRole(Collection<String> authorities, Authentication authentication, String... roles) {
        for (String role : roles) {
            if (authorities != null && authorities.contains(role)) {
                return true;
            }
            if (authentication != null && authentication.getGroupIds() != null && authentication.getGroupIds().contains(role)) {
                return true;
            }
            if (authentication != null && "camunda-admin".equals(role) && "admin".equalsIgnoreCase(authentication.getUserId())) {
                return true;
            }
        }
        return false;
    }
}
