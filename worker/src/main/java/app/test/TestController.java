package app.test;

import app.services.ResourceAccessService;
import jakarta.resource.ResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-eis")
@RequiredArgsConstructor
public class TestController {

    private final ResourceAccessService resourceAccessService;

    @PostMapping("/createTask")
    public String createTask() throws ResourceException {
        return resourceAccessService.createTask();
    }
}
