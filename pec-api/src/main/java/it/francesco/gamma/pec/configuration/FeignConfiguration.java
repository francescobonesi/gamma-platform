package it.francesco.gamma.pec.configuration;

import feign.codec.ErrorDecoder;
import it.francesco.gamma.pec.feign.PecClientErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new PecClientErrorDecoder();
    }

}
