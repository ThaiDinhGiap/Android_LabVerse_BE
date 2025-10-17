package com.mss.prm_project.service.serviceimpl;

import com.mss.prm_project.dto.UserDTO;
import com.mss.prm_project.entity.Role;
import com.mss.prm_project.repository.RoleRepository;
import com.mss.prm_project.repository.UserRepository;
import com.mss.prm_project.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("${jwt.access-token-expiration}")
    private long expirationAccessToken;
    @Value("${jwt.refresh-token-expiration}")
    private long expirationRefreshToken;

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public String generateAcessToken(UserDTO user) {
        final Instant now = Instant.now();
        final Date issuedAt = Date.from(now);
        final Date expiresAt = Date.from(now.plusMillis(expirationAccessToken));

        final String roleName = resolveRoleName(user);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("roles", roleName)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(UserDTO user) {
        final Instant now = Instant.now();
        final Date issuedAt = Date.from(now);
        final Date expiresAt = Date.from(now.plusMillis(expirationRefreshToken));

        final String roleName = resolveRoleName(user);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("roles", roleName)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        if (claims != null) {
            Date expiration = claims.getExpiration();
            if (expiration.after(new Date())) {
                return claims.getSubject();
            } else return null;
        }
        return null;
    }

    @Override
    public Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        if (claims != null) {
            return claims.getExpiration();
        }
        return null;
    }

    @Override
    public long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        if (claims != null) {
            Date expiration = claims.getExpiration();
            if (expiration.after(new Date())) {
                return claims.get("id", Integer.class);
            } else return -1;
        }
        return -1;
    }

    private String resolveRoleName(UserDTO user) {
        return roleRepository.findById(user.getRole())
                .map(Role::getRoleName)
                .orElse("USER");
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

