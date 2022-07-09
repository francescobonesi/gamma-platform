package it.francesco.gamma.pec.feign;

import it.francesco.gamma.pec.module.Message;
import it.francesco.gamma.pec.module.Pec;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "pec", url = "${pec.url}")
public interface PecClient {

    @GetMapping("/pec/list")
    List<Pec> getPecList(
            @RequestHeader(value = "Authorization") String authorizationHeader
    );

    @GetMapping("/pec/message")
    List<Message> getMessages(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @RequestParam("pec") String pecId
            //TODO add here more parameters
    );

}
