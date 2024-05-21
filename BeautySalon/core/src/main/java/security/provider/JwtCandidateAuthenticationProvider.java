package security.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@Log4j2
@RequiredArgsConstructor
public class JwtCandidateAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof JwtAuthenticationCandidate auth) {
            String jwtString = auth.getJwt();
            // decode JWT. validate JWT.
            // extract:
            String userId = "..."; // extract from JWT. validate in database if necessary
            List<String> userRoles = new ArrayList<>(); // extract from JWT
            ArrayList<SimpleGrantedAuthority> authorities = userRoles.stream()
                                                                .map(StringUtils::trimToNull)
                                                                .filter(Objects::nonNull)
                                                                .distinct()
                                                                .map(SimpleGrantedAuthority::new)
                                                                .collect(Collectors.toCollection(ArrayList::new));

            return new PreAuthenticatedAuthenticationToken(userId, jwtString, authorities);
        }

        // failed to process JWT
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationCandidate.class.isAssignableFrom(authentication);
    }
}
