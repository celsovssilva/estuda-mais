package com.estudamais.backend.service;

import com.estudamais.backend.entity.User;

public interface JwtService {
 String getSubject(String jwt);
 String gerarToken(User user);
}
