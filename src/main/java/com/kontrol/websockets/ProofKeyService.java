package com.kontrol.websockets;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ProofKeyService {

    private final Map<UUID, String> proofCodes = new ConcurrentHashMap<>();

    public UUID generateCode(String userId) {
        UUID code = UUID.randomUUID();
        proofCodes.put(code, userId);
        return code;
    }

    public String removeCode(UUID code) {
        return proofCodes.remove(code);
    }
}
