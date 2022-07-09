package it.francesco.gamma.job.configuration;

import feign.codec.ErrorDecoder;

import it.francesco.gamma.job.feign.JobErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new JobErrorDecoder();
    }

}
