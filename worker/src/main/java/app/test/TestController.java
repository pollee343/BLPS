package app.test;

import app.dao.ApplicationDAOService;
import app.services.JiraAccessService;
import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-eis")
@RequiredArgsConstructor
public class TestController {

    private final JiraAccessService jiraAccessService;
    private final ApplicationDAOService applicationDAOService;

    @PostMapping("/createTask")
    public String createTask(@RequestBody Long applicationId) throws ResourceException {
        return jiraAccessService.createTask(applicationDAOService.findById(applicationId).get());
    }
}
