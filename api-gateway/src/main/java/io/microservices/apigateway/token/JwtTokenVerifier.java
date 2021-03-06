package io.microservices.apigateway.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.microservices.apigateway.configuration.ApplicationProperties;
import io.microservices.apigateway.exception.InvalidTokenException;
import io.microservices.apigateway.rest.filter.TokenAuthGatewayFilterFactory;
import io.microservices.apigateway.utils.EncryptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

@Service
public class JwtTokenVerifier {

    private static final Logger LOG = LoggerFactory.getLogger(JwtTokenVerifier.class);

    private final ApplicationProperties applicationProperties;
    private final PublicKey tokenSignKey;

    public JwtTokenVerifier(ApplicationProperties applicationProperties) throws NoSuchAlgorithmException,
            IOException, InvalidKeySpecException {
        this.applicationProperties = applicationProperties;
        this.tokenSignKey = EncryptionUtils.readPublicKey(applicationProperties.getTokenEncryption().getSignTokenKeyPath());
    }

    public boolean isValidToken(String token) {
        try {
            Jws<Claims> jws = parse(token);
            if (jws.getBody().getExpiration().after(new Date()))
                return true;
        } catch(ExpiredJwtException e) {
            LOG.error("Expired token {}", e.getMessage(), e);
        } catch(UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            LOG.error("Invalid token {}", e.getMessage(), e);
        } catch(Exception e) {
            LOG.error("Unexpected error during parse token. {}", e.getMessage(), e);
        }
        return false;
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
    }

}
