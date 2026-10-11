package com.project.Digital.Evidence.Verification.and.Storage.System.service;

import fi.solita.clamav.ClamAVClient;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Service
public class VerificationService {

    @Value("${clamav.host}")
    private String clamavHost;

    @Value("${clamav.port}")
    private int clamavPort;

    private final Tika tika = new Tika();

    private static final Map<String, byte[]> MAGIC_NUMBERS = new HashMap<>();
    static {
        MAGIC_NUMBERS.put("pdf", new byte[]{(byte) 0x25, (byte) 0x50, (byte) 0x44, (byte) 0x46});
        MAGIC_NUMBERS.put("png", new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47});
        MAGIC_NUMBERS.put("jpg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});
    }

    public boolean verifyFileTypeAndMagicBytes(MultipartFile file) throws Exception {
        String detectedType = tika.detect(file.getInputStream());
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) return false;

        byte[] fileBytes = file.getBytes();
        if (fileBytes.length < 4) return false;

        if (originalFilename.endsWith(".pdf")) {
            return detectedType.equals("application/pdf") && matchesHeader(fileBytes, MAGIC_NUMBERS.get("pdf"));
        } else if (originalFilename.endsWith(".png")) {
            return detectedType.equals("image/png") && matchesHeader(fileBytes, MAGIC_NUMBERS.get("png"));
        } else if (originalFilename.endsWith(".jpg") || originalFilename.endsWith(".jpeg")) {
            return detectedType.equals("image/jpeg") && matchesHeader(fileBytes, MAGIC_NUMBERS.get("jpg"));
        }
        return true;
    }

    private boolean matchesHeader(byte[] fileBytes, byte[] magicHeader) {
        if (magicHeader == null || fileBytes.length < magicHeader.length) return false;
        for (int i = 0; i < magicHeader.length; i++) {
            if (fileBytes[i] != magicHeader[i]) return false;
        }
        return true;
    }

    public boolean scanPolyglotPayloads(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        String filename = file.getOriginalFilename();

        if (filename != null && !filename.endsWith(".exe")) {
            // Check if file masquerades as a Windows executable by looking at the very first 2 bytes ('MZ')
            if (bytes.length >= 2 && bytes[0] == 'M' && bytes[1] == 'Z') {
                return false; 
            }
        }
        return true;
    }

    public boolean scanForMalware(MultipartFile file) throws Exception {
    /////
    return true;
}

    public String generateSHA512(MultipartFile file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(file.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
