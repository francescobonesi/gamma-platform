package it.francesco.gamma.job.feign;

import it.francesco.gamma.job.model.ClientRequest;
import it.francesco.gamma.job.model.ClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "jobclient", url = "${job.client.url}")
public interface JobClient {

    @PostMapping("${job.client.path}")
    ClientResponse invokeApi(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @RequestBody ClientRequest request
    );

}
