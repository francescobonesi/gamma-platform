package it.francesco.gamma.pec.api;


import it.francesco.gamma.pec.module.Message;
import it.francesco.gamma.pec.module.Pec;
import it.francesco.gamma.pec.service.PecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class PecController {

    public final PecService pecService;

    @Autowired
    public PecController(PecService pecService) {
        this.pecService = pecService;
    }

    @GetMapping("/pec/list")
    public ResponseEntity<List<Pec>> pecList(@RequestHeader("X-serviceToken") String serviceToken) {
        return ResponseEntity.ok(pecService.getPecList(serviceToken));
    }

    @GetMapping("/message")
    public ResponseEntity<List<Message>> messageSearch(@RequestHeader("X-serviceToken") String serviceToken,
                                                       @RequestParam("pecId") String pecId) {
        return ResponseEntity.ok(pecService.getPecMessages(serviceToken, pecId));
    }

}
