package com.practica.security.service;

import com.practica.security.config.Jwt;
import com.practica.security.controller.request.UserLoginRequest;
import com.practica.security.controller.request.UserRequest;
import com.practica.security.entity.UserEntity;
import com.practica.security.model.User;
import com.practica.security.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final Jwt jwt;

    @Transactional
    public User save(final UserRequest request) throws Exception {
        if (Objects.requireNonNullElse(request.getId(), 0) != 0) {
            existsById(request.getId());
        }
        UserEntity userEntity = repository.save(buildUserSaved(request));
        return ObjectUtils.isNotEmpty(userEntity) ? buildModel(userEntity) : null;
    }

    private  User buildModel(UserEntity userEntity) {
        return User
                .builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .username(userEntity.getUsername())
                .status(userEntity.getStatus())
                .build();
    }

    private static UserEntity buildUserSaved(UserRequest request) {
        return UserEntity
                .builder()
                .id(Objects.nonNull(request.getId()) ? request.getId() : null)
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .status(true)
                .build();
    }

    public User findByUser(final Integer id) throws Exception {
        existsById(id);
       var userEntity = repository.findById(id);
       return buildModel(userEntity.get());
    }

    public void existsById(final Integer id) throws Exception {
        if (!repository.existsById(id)) {
            throw new Exception("Not Found");
        }
    }

    public User validateUser(UserLoginRequest request) {
        String resultPassword = "";
        if (ObjectUtils.isNotEmpty(request) && ObjectUtils.isNotEmpty(request.getPassword())) {
            resultPassword = generateSHA256Hash(request.getPassword());
        }
        return verifyPassword(request.getUsername(), resultPassword);
    }

    private User verifyPassword(String userEmail, String password) {
        User userNew = null;
        User userEntity = findByEmail(userEmail);
        if (Objects.nonNull(userEntity)) {
            String passwordValidate = generateSHA256Hash(userEntity.getPassword());
            if (passwordValidate.equals(password)) {
                log.info("Password Valid");
                userNew = buildUserVerify(userEmail, userEntity);
            }
        }
        return userNew;
    }

    private User buildUserVerify(String userEmail, User userEntity) {
        return User
                .builder()
                .id(userEntity.getId())
                .email(userEmail)
                .password(userEntity.getPassword())
                .username(userEntity.getUsername())
                .status(userEntity.getStatus())
                .token(jwt.create(String.valueOf(userEntity.getId()),userEntity.getUsername(), userEntity.getEmail()))
                .build();
    }

    public User findByEmail(final String email) {
        log.info("findByEmail :" + email);
        var entityOptional = repository.findByEmail(email);
        return entityOptional.isPresent() ? User
                .builder()
                .id(entityOptional.get().getId())
                .email(entityOptional.get().getEmail())
                .password(entityOptional.get().getPassword())
                .username(entityOptional.get().getUsername())
                .status(entityOptional.get().getStatus())
                .build() : null;
    }

    public static String generateSHA256Hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Convierte la contraseña en bytes
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

            // Calcula el hash SHA-256 de los bytes de la contraseña
            byte[] hashBytes = digest.digest(passwordBytes);

            // Convierte el hash en una representación hexadecimal
            StringBuilder hexHash = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexHash.append('0');
                }
                hexHash.append(hex);
            }

            return hexHash.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    public User findCurrentUser(String token) {
        try {
            log.info("Inf Curren User ");
            return jwt.findCurrentUser(token);
        }catch (ExpiredJwtException e){
            throw new ExpiredJwtException(null, null, "Token expirado");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
