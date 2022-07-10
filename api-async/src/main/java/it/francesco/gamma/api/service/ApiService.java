package it.francesco.gamma.api.service;

import it.francesco.gamma.api.model.*;
import it.francesco.gamma.api.repository.RequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static it.francesco.gamma.api.Utils.generateIdentifier;

@Service
@Slf4j
public class ApiService {

    public final RabbitTemplate rabbitTemplate;
    public final RequestRepository requestRepository;
    public final String firmaRequestQueue;
    public final String conservazioneRequestQueue;

    @Autowired
    public ApiService(RabbitTemplate rabbitTemplate,
                      RequestRepository requestRepository,
                      @Value("${queue.firma.requests}") String firmaRequestQueue,
                      @Value("${queue.conservazione.requests}") String conservazioneRequestQueue
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.requestRepository = requestRepository;
        this.firmaRequestQueue = firmaRequestQueue;
        this.conservazioneRequestQueue = conservazioneRequestQueue;
    }

    private DbRequest createRequestInDb(String identifier, ApiAsyncRequest request, String firmaToken, String conservazioneToken) {

        try {

            DbRequest dbRequest = new DbRequest();
            dbRequest.setIdentifier(identifier);
            dbRequest.setStatus(RequestStatus.FIRMA_IN_PROGRESS);
            dbRequest.setUsername(request.getUsername());
            dbRequest.setTenant(request.getTenant());
            dbRequest.setDocumentIdList(String.join(",", request.getDocumentIdList()));
            dbRequest.setConservazioneServiceToken(conservazioneToken);
            dbRequest.setFirmaServiceToken(firmaToken);
            log.info("Creating operation in repository");
            requestRepository.save(dbRequest);
            return dbRequest;

        } catch (DataIntegrityViolationException e) {
            log.debug("No need to worry. " +
                    "Means that while managing this request, " +
                    "the same request arrived and message has been already issued.");
            throw new IllegalStateException(e);
        }

    }

    private void updateRequestInDb(String identifier, RequestStatus status){
        Optional<DbRequest> requestById = requestRepository.findById(identifier);

        if (requestById.isEmpty()) return;

        DbRequest request = requestById.get();
        request.setStatus(status);
        requestRepository.save(request);
    }

    private void sendToQueue(String queue,
                             String identifier, String tenant,
                             String username, List<String> documentIdList,
                             String serviceToken) {
        JobRequest jobRequest = new JobRequest(
                identifier,
                tenant,
                username,
                documentIdList,
                serviceToken
        );
        log.info("Sending message in queue: {}", jobRequest);
        rabbitTemplate.convertAndSend(queue, jobRequest);
    }


    public ApiAsyncResponse request(ApiAsyncRequest request, String firmaToken, String conservazioneToken) {

        String identifier = generateIdentifier(request);
        Optional<DbRequest> requestById = requestRepository.findById(identifier);

        if (requestById.isPresent() && !requestById.get().getStatus().equals(RequestStatus.FAILED)) {
            log.info("Object already exists, will be found when calling the search endpoint");
            return new ApiAsyncResponse(requestById.get());
        } else {
            DbRequest dbCreatedElement = createRequestInDb(identifier, request, firmaToken, conservazioneToken);
            sendToQueue(firmaRequestQueue, identifier, request.getTenant(), request.getUsername(), request.getDocumentIdList(), firmaToken);
            return new ApiAsyncResponse(dbCreatedElement);
        }

    }

    public Optional<DbRequest> getById(String identifier) {
        return requestRepository.findById(identifier);
    }

    public void firmaCompleted(JobResponse jobResponse){

        String identifier = jobResponse.getIdentifier();
        log.info("Firma for id={} has success={}", identifier, jobResponse.isSuccess());
        Optional<DbRequest> requestById = requestRepository.findById(identifier);

        if(requestById.isEmpty()) {
            log.warn("id={} not present in DB. Ignoring.", identifier);
            return;
        }

        DbRequest request = requestById.get();

        if(jobResponse.isSuccess()){
            updateRequestInDb(identifier, RequestStatus.CONSERVAZIONE_IN_PROGRESS);
            sendToQueue(conservazioneRequestQueue, identifier,
                    request.getTenant(), request.getUsername(),
                    Arrays.asList(request.getDocumentIdList().split(",")),
                    request.getConservazioneServiceToken());
            log.info("Id={} has been sent to conservazione queue", identifier);
        }
        else {
            updateRequestInDb(identifier, RequestStatus.FAILED);
            log.info("Id={} failed, saved to DB", identifier);
        }

    }

    public void conservazioneCompleted(JobResponse jobResponse) {
        String identifier = jobResponse.getIdentifier();
        log.info("Firma for id={} has success={}", identifier, jobResponse.isSuccess());

        if(requestRepository.existsById(identifier)) {
            updateRequestInDb(identifier, jobResponse.isSuccess() ? RequestStatus.COMPLETED : RequestStatus.FAILED);
            log.info("Id={} update saved to DB", identifier);
        }
    }
}
