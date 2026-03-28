package edu.uvg.service;

import java.security.MessageDigest;

/**
 * Simulación de HASH160 usando SHA-256 para fines didácticos.
 */
public class SimpleHashService implements HashService {
    @Override
    public byte[] hash160(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (Exception e) {
            return new byte[0];
        }
    }
}
