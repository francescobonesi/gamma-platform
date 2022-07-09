package it.francesco.gamma.job.exception;

public class JobApiException extends RuntimeException {

    private final String apiResponseCode;

    public JobApiException(String message, String code) {
        super(message);
        this.apiResponseCode = code;
    }

    public String getApiResponseCode() {
        return apiResponseCode;
    }
}
