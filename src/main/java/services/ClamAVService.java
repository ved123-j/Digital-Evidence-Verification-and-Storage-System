//package com.evidence.system.service;
package services;
import fi.solita.clamav.ClamAVClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class ClamAVService
{

    @Value("${clamav.host}")
    private String clamavHost;

    @Value("${clamav.port}")
    private int clamavPort;

    public boolean scanForMalware(MultipartFile file) throws Exception {
        // Instantiate external library client using injected properties
        ClamAVClient client = new ClamAVClient(clamavHost, clamavPort);

        byte[] reply;
        try (InputStream is = file.getInputStream()) {
            reply = client.scan(is);
        }

        // Correct static method call from ClamAVClient library
        return ClamAVClient.isCleanReply(reply);
    }
}