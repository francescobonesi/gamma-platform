package it.francesco.gamma.job.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import it.francesco.gamma.job.exception.JobApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class JobErrorDecoder implements ErrorDecoder {

    public static final String ERROR_RESPONSE_CODE = "Error response code";

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        if (responseStatus.is5xxServerError()) {
            log.info("API integration {} returned 5xx code", methodKey);
            return new JobApiException(ERROR_RESPONSE_CODE, responseStatus.toString());
        } else if (responseStatus.is4xxClientError()) {
            log.info("API integration {} returned 4xx code", methodKey);
            return new JobApiException(ERROR_RESPONSE_CODE, responseStatus.toString());
        } else {
            log.info("API integration {} returned not managed error code", methodKey);
            return new Exception(ERROR_RESPONSE_CODE);
        }
    }
}