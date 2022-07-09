package it.francesco.gamma.api.service;

import it.francesco.gamma.api.model.ApiAsyncRequest;
import it.francesco.gamma.api.model.DbRequest;
import it.francesco.gamma.api.model.JobRequest;
import it.francesco.gamma.api.model.RequestStatus;
import it.francesco.gamma.api.repository.RequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static it.francesco.gamma.api.Utils.generateIdentifier;

@Service
@Slf4j
public class ApiService {

    public final RabbitTemplate rabbitTemplate;
    public final RequestRepository requestRepository;
    public final RabbitMqListener listener;
    public final String sendQueue;

    @Autowired
    public ApiService(RabbitTemplate rabbitTemplate,
                      RequestRepository requestRepository,
                      RabbitMqListener listener,
                      @Value("${queue.requests}") String sendQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.requestRepository = requestRepository;
        this.listener = listener;
        this.sendQueue = sendQueue;
    }

    private void saveToDb(String identifier, ApiAsyncRequest request) {

        try {

            DbRequest dbRequest = new DbRequest();
            dbRequest.setIdentifier(identifier);
            dbRequest.setStatus(RequestStatus.IN_PROGRESS);
            dbRequest.setUsername(request.getUsername());
            dbRequest.setTenant(request.getTenant());
            dbRequest.setDocumentIdList(String.join(",", request.getDocumentIdList()));
            log.info("Saving/Updating operation in repository");
            requestRepository.save(dbRequest);

        } catch (DataIntegrityViolationException e) {
            log.debug("No need to worry. " +
                    "Means that while managing this request, " +
                    "the same request arrived and message has been already issued.");
        }

    }

    private void sendToQueue(String identifier, ApiAsyncRequest request) {
        JobRequest jobRequest = new JobRequest(
                identifier,
                request.getTenant(),
                request.getUsername(),
                request.getDocumentIdList(),
                request.getServiceAuthorizationToken()
        );
        log.info("Sending message in queue: {}", jobRequest);
        rabbitTemplate.convertAndSend(sendQueue, jobRequest);
    }


    public String request(ApiAsyncRequest request) {

        String identifier = generateIdentifier(request);
        Optional<DbRequest> requestById = requestRepository.findById(identifier);

        if (requestById.isPresent() && !requestById.get().getStatus().equals(RequestStatus.FAILED)) {
            log.info("Object already exists, will be found when calling the search endpoint");
        } else {
            saveToDb(identifier, request);
            sendToQueue(identifier, request);
        }
        return identifier;

    }

    public Optional<DbRequest> getById(String identifier) {
        return requestRepository.findById(identifier);
    }
}
