package app.config;

import app.auth.DbLoginModule;
import app.auth.RoleAuthorityGranter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.jaas.AuthorityGranter;
import org.springframework.security.authentication.jaas.DefaultJaasAuthenticationProvider;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.Map;

@Configuration
public class JaasConfig {

    @Bean
    public InMemoryConfiguration jaasConfiguration() {
        AppConfigurationEntry entry = new AppConfigurationEntry(
                DbLoginModule.class.getName(),
                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                Map.of()
        );
        return new InMemoryConfiguration(entry);
    }

    @Bean
    public DefaultJaasAuthenticationProvider jaasAuthenticationProvider(
            InMemoryConfiguration configuration,
            RoleAuthorityGranter authorityGranter
    ) {
        DefaultJaasAuthenticationProvider provider = new DefaultJaasAuthenticationProvider();
        provider.setConfiguration(configuration);
        provider.setLoginContextName("SPRINGSECURITY");
        provider.setAuthorityGranters(new AuthorityGranter[]{authorityGranter});
        return provider;
    }

    public static class InMemoryConfiguration extends javax.security.auth.login.Configuration {
        private final AppConfigurationEntry[] entries;

        public InMemoryConfiguration(AppConfigurationEntry entry) {
            this.entries = new AppConfigurationEntry[]{entry};
        }

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            return entries;
        }
    }
}
