package app.config;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class CamundaDeploymentBootstrap {

    @Bean
    public ApplicationRunner camundaDeploymentBootstrapRunner(
            RepositoryService repositoryService,
            ResourcePatternResolver resourcePatternResolver
    ) {
        return args -> {
            List<Resource> resourcesToDeploy = new ArrayList<>();
            resourcesToDeploy.addAll(List.of(resourcePatternResolver.getResources("classpath*:/processes/*.bpmn")));

            if (resourcesToDeploy.isEmpty()) {
                System.err.println("Camunda bootstrap: no BPMN resources found for classpath*:/processes/*.bpmn");
                return;
            }

            DeploymentBuilder builder = repositoryService.createDeployment()
                    .name("WorkerBootstrapDeployment")
                    .enableDuplicateFiltering(true);

            for (Resource resource : resourcesToDeploy) {
                if (!resource.exists()) {
                    continue;
                }
                String filename = resource.getFilename();
                if (filename == null) {
                    continue;
                }
                String resourceName = "processes/" + filename;
                try (InputStream in = resource.getInputStream()) {
                    builder.addInputStream(resourceName, in);
                } catch (IOException exception) {
                    throw new IOException("Failed to read resource for deployment: " + resourceName, exception);
                }
            }

            builder.deploy();
            System.out.println("Camunda bootstrap: deployed " + resourcesToDeploy.size() + " BPMN resources (WorkerBootstrapDeployment)");
        };
    }
}
