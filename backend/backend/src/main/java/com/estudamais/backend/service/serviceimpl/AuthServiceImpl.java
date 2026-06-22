package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.repository.UserRepository;
import com.estudamais.backend.request.LoginRequest;
import com.estudamais.backend.request.RegisterRequest;
import com.estudamais.backend.response.AuthResponse;
import com.estudamais.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AmqpTemplate amqpTemplate;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("E-mail já está em uso!");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);

        String mensagemBoasVindas = user.getName() + ";" + user.getEmail();
        amqpTemplate.convertAndSend("internal.exchange", "welcome.routing.key", mensagemBoasVindas);

        return this.login(new LoginRequest(request.email(), request.password()));
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );


        SecurityContextHolder.getContext().setAuthentication(authentication);


        String jwt = tokenProvider.generateToken(authentication);
        User user = (User) authentication.getPrincipal();

        return new AuthResponse(jwt, user.getName(), user.getEmail());
    }
}