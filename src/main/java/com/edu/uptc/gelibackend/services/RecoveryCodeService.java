package com.edu.uptc.gelibackend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edu.uptc.gelibackend.entities.PasswordRecoveryCode;
import com.edu.uptc.gelibackend.repositories.PasswordRecoveryCodeRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class RecoveryCodeService {

    private final PasswordRecoveryCodeRepository repository;

    public RecoveryCodeService(PasswordRecoveryCodeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public String generateCode(String username) {
        // Eliminar cualquier código anterior
        repository.deleteByUsername(username);

        // Generar nuevo código
        String code = String.format("%06d", new Random().nextInt(999999));

        PasswordRecoveryCode entity = new PasswordRecoveryCode();
        entity.setUsername(username);
        entity.setCode(code);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        repository.save(entity);
        return code;
    }

    public boolean verifyCode(String username, String code) {
        return repository.findByUsername(username)
                .filter(rc -> rc.getCode().equals(code))
                .filter(rc -> rc.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Transactional
    public void invalidateCode(String username) {
        repository.deleteByUsername(username);
    }

    @Transactional
    public void saveTempToken(String username, String tempToken) {
        repository.updateTempTokenByUsername(username, tempToken);
    }

    public String getUsernameByTempToken(String tempToken) {
        return repository.findByTempToken(tempToken)
                .filter(rc -> rc.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(PasswordRecoveryCode::getUsername)
                .orElse(null);
    }

    public Optional<PasswordRecoveryCode> findByTempToken(String tempToken) {
        return repository.findByTempToken(tempToken)
                .filter(rc -> rc.getExpiresAt().isAfter(LocalDateTime.now()));
    }

}
