package it.francesco.gamma.pec.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class PecClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        if (responseStatus.is5xxServerError()) {
            log.info("API integration {} returned 5xx code", methodKey);
        } else if (responseStatus.is4xxClientError()) {
            log.info("API integration {} returned 4xx code", methodKey);
        } else {
            log.info("API integration {} returned not managed error code", methodKey);
        }

        return new IllegalStateException(responseStatus.toString());
    }
}