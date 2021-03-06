package com.healthy.gym.auth.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthy.gym.auth.component.Translator;
import com.healthy.gym.auth.component.token.TokenManager;
import com.healthy.gym.auth.enums.GymRole;
import com.healthy.gym.auth.pojo.request.LogInUserRequest;
import com.healthy.gym.auth.service.UserService;
import com.healthy.gym.auth.shared.UserDTO;
import io.jsonwebtoken.Jwts;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final Translator translator;
    private final TokenManager tokenManager;

    public AuthenticationFilter(
            UserService userService,
            Translator translator,
            TokenManager tokenManager
    ) {
        this.userService = userService;
        this.translator = translator;
        this.tokenManager = tokenManager;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {

        try {
            LogInUserRequest credentials = new ObjectMapper()
                    .readValue(request.getInputStream(), LogInUserRequest.class);
            String userEmail = credentials.getEmail();
            UserDTO userDetails = userService.getUserDetailsByEmail(userEmail);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getEmail(),
                            credentials.getPassword(),
                            userDetails.getGymRoles()
                    )
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException {

        String userEmail = ((User) authResult.getPrincipal()).getUsername();
        UserDTO userDetails = userService.getUserDetailsByEmail(userEmail);

        String token = getTokenForUser(userDetails);

        response.addHeader("token", "Bearer " + token);
        response.addHeader("userId", userDetails.getUserId());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    private String getTokenForUser(UserDTO userDetails) {
        List<String> userRoles = userDetails
                .getGymRoles()
                .stream()
                .map(GymRole::getRole)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userDetails.getUserId())
                .claim("roles", userRoles)
                .setExpiration(setTokenExpirationTime())
                .signWith(
                        tokenManager.getSignatureAlgorithm(),
                        tokenManager.getSigningKey()
                )
                .compact();
    }

    private Date setTokenExpirationTime() {
        long currentTime = System.currentTimeMillis();
        long expirationTime = tokenManager.getExpirationTimeInMillis();
        return new Date(currentTime + expirationTime);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException, ServletException {

        String message = translator.toLocale("user.log-in.fail");

        if (failed instanceof AccountExpiredException) {
            message = translator.toLocale("user.log-in.fail.account.expired");
        } else if (failed instanceof CredentialsExpiredException) {
            message = translator.toLocale("user.log-in.fail.credentials.expired");
        } else if (failed instanceof DisabledException) {
            message = translator.toLocale("mail.registration.confirmation.log-in.exception");
        } else if (failed instanceof LockedException) {
            message = translator.toLocale("user.log-in.fail.account.locked");
        }

        String body = getResponseBody(message);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setLocale(LocaleContextHolder.getLocale());
        response.getWriter().println(body);
    }

    private String getResponseBody(String message) throws JsonProcessingException {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        body.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        body.put("message", message);
        body.put("path", "/login");

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(body);
    }
}
