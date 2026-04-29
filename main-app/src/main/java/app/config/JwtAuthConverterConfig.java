package app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;

@Configuration
public class JwtAuthConverterConfig {

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            List<String> authoritiesClaim = jwt.getClaimAsStringList("authorities");

            Collection<SimpleGrantedAuthority> authorities = authoritiesClaim == null
                    ? List.of()
                    : authoritiesClaim.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
        };
    }
}
