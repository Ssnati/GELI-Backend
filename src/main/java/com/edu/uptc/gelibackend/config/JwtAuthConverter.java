package com.edu.uptc.gelibackend.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String RESOURCE_ACCESS_CLAIM = "resource_access";
    private static final String ROLES_CLAIM = "roles";

    private final JwtGrantedAuthoritiesConverter jwtConverter = new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.principal-claim:sub}")
    private String principalClaim;

    @Value("${jwt.auth.converter.resource-id-claim}")
    private String resourceIdClaimName;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        this.validateResourceIdClaim();
        Collection<GrantedAuthority> authorities = new ArrayList<>(jwtConverter.convert(jwt));

        authorities.addAll(this.extractRealmRoles(jwt));
        authorities.addAll(this.extractResourcePermissions(jwt));

        String principal = jwt.getClaimAsString(principalClaim);
        log.debug("Principal claim: {}, Authorities: {}", principal, authorities);

        System.out.println("Principal claim: " + principal);
        System.out.println("Authorities: " + authorities);
        return new JwtAuthenticationToken(jwt, authorities, principal);
    }

    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS_CLAIM);
        if (realmAccess == null) {
            return List.of();
        }

        Object roles = realmAccess.get(ROLES_CLAIM);
        if (!(roles instanceof Collection<?> rolesCollection)) {
            return List.of();
        }

        return rolesCollection.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .toList();
    }

    private Collection<? extends GrantedAuthority> extractResourcePermissions(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS_CLAIM);
        if (resourceAccess == null) {
            return List.of();
        }

        Object resource = resourceAccess.get(resourceIdClaimName);
        if (!(resource instanceof Map)) {
            return List.of();
        }

        Object roles = ((Map<?, ?>) resource).get(ROLES_CLAIM);
        if (!(roles instanceof Collection<?> rolesCollection)) {
            return List.of();
        }

        return rolesCollection.stream()
                .map(role -> new SimpleGrantedAuthority(String.valueOf(role)))
                .toList();
    }

    private void validateResourceIdClaim() {
        if (resourceIdClaimName == null || resourceIdClaimName.isBlank()) {
            throw new IllegalStateException("resource-id-claim must be defined in configuration");
        }
    }
}
