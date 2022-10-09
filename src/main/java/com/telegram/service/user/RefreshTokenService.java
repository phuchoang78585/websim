//package com.telegram.service.user;
//import java.time.Instant;
//import java.util.Optional;
//import java.util.UUID;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.telegram.entity.RefreshToken;
//import com.telegram.exception.TokenRefreshException;
//import com.telegram.repository.RefreshTokenRepository;
//import com.telegram.repository.UserRepository;
//
//@Service
//public class RefreshTokenService {
//  @Value("${bezkoder.app.jwtRefreshExpirationMs}")
//  private Long refreshTokenDurationMs;
//  @Autowired
//  private RefreshTokenRepository refreshTokenRepository;
//  @Autowired
//  private UserRepository userRepository;
//  public Optional<RefreshToken> findByToken(String token) {
//    return refreshTokenRepository.findByToken(token);
//  }
//  public RefreshToken createRefreshToken(Long userId,String secret) {
//    RefreshToken refreshToken = new RefreshToken();
//    refreshToken.setUser(userRepository.findById(userId).get());
//    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
//    refreshToken.setToken(secret);
//    refreshToken = refreshTokenRepository.save(refreshToken);
//    return refreshToken;
//  }
//  public RefreshToken verifyExpiration(RefreshToken token) {
//    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
//      refreshTokenRepository.delete(token);
//      throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
//    }
//    return token;
//  }
//}
