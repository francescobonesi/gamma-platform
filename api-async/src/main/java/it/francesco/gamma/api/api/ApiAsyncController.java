package it.francesco.gamma.api.api;


import it.francesco.gamma.api.model.ApiAsyncRequest;
import it.francesco.gamma.api.model.ApiAsyncResponse;
import it.francesco.gamma.api.model.DbRequest;
import it.francesco.gamma.api.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
public class ApiAsyncController {

    public final ApiService apiService;

    @Autowired
    public ApiAsyncController(ApiService signatureService) {
        this.apiService = signatureService;
    }

    @GetMapping("/status/{identifier}")
    public ResponseEntity<ApiAsyncResponse> getSaved(@PathVariable("identifier") String identifier) {

        Optional<DbRequest> optDbRequest = apiService.getById(identifier);

        if (optDbRequest.isEmpty()) return ResponseEntity.notFound().build();

        DbRequest request = optDbRequest.get();
        ApiAsyncResponse response = new ApiAsyncResponse(
                identifier,
                request.getTenant(),
                request.getUsername(),
                Arrays.asList(request.getDocumentIdList().split(",")),
                request.getStatus()
        );
        return ResponseEntity.ok(response);

    }


    @PostMapping("/request")
    public ResponseEntity<ApiAsyncResponse> request(
            @RequestHeader("X-conservazioneServiceToken") String conservazioneServiceToken,
            @RequestHeader("X-firmaServiceToken") String firmaServiceToken,
            @RequestBody ApiAsyncRequest request) {
        ApiAsyncResponse response = apiService.request(request, firmaServiceToken, conservazioneServiceToken);
        return ResponseEntity.ok().body(response);
    }

}
