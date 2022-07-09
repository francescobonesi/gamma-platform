package it.francesco.gamma.api;

import it.francesco.gamma.api.model.ApiAsyncRequest;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {

    private Utils() {
        // cannot instantiate Utils, only static methods
    }

    public static String generateIdentifier(ApiAsyncRequest request) {
        return DigestUtils.sha256Hex(
                request.getDocumentIdList().stream().map(DigestUtils::sha256Hex).collect(Collectors.joining())
        );
    }
}
