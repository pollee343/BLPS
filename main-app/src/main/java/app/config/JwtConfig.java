package app.config;

import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import com.nimbusds.jose.proc.SecurityContext;

import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;

@Configuration
public class JwtConfig {

    @Value("${jwt.keystore.location}")
    private Resource keystore;

    @Value("${jwt.keystore.password}")
    private String keystorePassword;

    @Value("${jwt.keystore.alias}")
    private String keyAlias;

    @Bean
    public KeyStore keyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream is = keystore.getInputStream()) {
            keyStore.load(is, keystorePassword.toCharArray());
        }
        return keyStore;
    }

    @Bean
    public RSAPrivateKey rsaPrivateKey(KeyStore keyStore) throws Exception {
        Key key = keyStore.getKey(keyAlias, keystorePassword.toCharArray());
        if (!(key instanceof RSAPrivateKey privateKey)) {
            throw new IllegalStateException("Private key is not RSA");
        }
        return privateKey;
    }

    @Bean
    public RSAPublicKey rsaPublicKey(KeyStore keyStore) throws Exception {
        Certificate certificate = keyStore.getCertificate(keyAlias);
        PublicKey publicKey = certificate.getPublicKey();
        if (!(publicKey instanceof RSAPublicKey rsaPublicKey)) {
            throw new IllegalStateException("Public key is not RSA");
        }
        return rsaPublicKey;
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .build();

        JWKSource<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
