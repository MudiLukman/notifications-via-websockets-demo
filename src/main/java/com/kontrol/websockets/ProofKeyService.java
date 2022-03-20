package com.kontrol.websockets;

import org.apache.commons.lang3.RandomStringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@ApplicationScoped
public class ProofKeyService {

    private static final int capacity = 1_000;
    final Map<String, String> proofCodes = Collections.synchronizedMap(new LinkedHashMap<>(capacity));

    public String generateCode(String source) {
        String code = RandomStringUtils.randomAlphanumeric(32);
        if (proofCodes.size() >= capacity) {
            proofCodes.remove(proofCodes.keySet().iterator().next());
        }
        proofCodes.put(code, source);
        return code;
    }

    public String removeCode(String code) {
        return proofCodes.remove(code);
    }
}
