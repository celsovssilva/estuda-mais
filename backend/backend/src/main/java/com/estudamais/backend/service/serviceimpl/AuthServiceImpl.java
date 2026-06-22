package com.estudamais.backend.service.serviceimpl;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.repository.UserRepository;
import com.estudamais.backend.request.LoginRequest;
import com.estudamais.backend.request.RegisterRequest;
import com.estudamais.backend.request.UpdateUserRequest;
import com.estudamais.backend.response.AuthResponse;
import com.estudamais.backend.service.AuthService;
import com.estudamais.backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public AuthResponse login(LoginRequest data) {
        var usernamepassword = new UsernamePasswordAuthenticationToken(data.email(),data.password());
        var auth = this.authenticationManager.authenticate(usernamepassword);
        User user = (User) auth.getPrincipal();
        var token = jwtService.gerarToken(user);

        return new AuthResponse(token, user.getName(), user.getPassword())  ;
    }

    @Override
    public void register(RegisterRequest data) {
        if(this.userRepository.findByEmail(data.email()).isPresent()){
            throw new RuntimeException("esse email já está cadastrado no sistema");
        }
        String senhaHash = passwordEncoder.encode(data.password());

        User newUser = User.builder()
                .name(data.name())
                .email(data.email())
                .password(senhaHash)
                .build();
        this.userRepository.save(newUser);
    }

    @Override
    public void updateProfile(Long userId, UpdateUserRequest data) {
      User user = userRepository.findById(userId)
              .orElseThrow(()-> new RuntimeException("user não disponivel pra atualizar"));
      if(this.userRepository.findByEmail(data.email()).isPresent()){
          throw new RuntimeException("esse email não pode ser atualizado pois está em  uso");
      }
      user.setName(data.name());
      user.setEmail(data.email());
      String senhaHash = passwordEncoder.encode(data.password());
      user.setPassword(senhaHash);

      userRepository.save(user);
    }

    @Override
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                        .orElseThrow(()-> new RuntimeException("user não existe"));
        userRepository.delete(user);
    }
}
