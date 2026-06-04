package app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@ConditionalOnProperty(name = "app.jta.enabled", havingValue = "true")
public class TransactionConfig {

    @Bean
    public JtaTransactionManager transactionManager() {
        JtaTransactionManager transactionManager = new JtaTransactionManager();
        transactionManager.setUserTransactionName("java:comp/UserTransaction");
        transactionManager.setTransactionManagerName("java:/TransactionManager");
        return transactionManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate(JtaTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}
