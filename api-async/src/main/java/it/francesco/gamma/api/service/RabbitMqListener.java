package it.francesco.gamma.api.service;

import it.francesco.gamma.api.model.DbRequest;
import it.francesco.gamma.api.model.JobResponse;
import it.francesco.gamma.api.model.RequestStatus;
import it.francesco.gamma.api.repository.RequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RabbitMqListener {

    public final RequestRepository requestRepository;

    @Autowired
    public RabbitMqListener(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @RabbitListener(queues = "${queue.responses}")
    public void signaturesListener(JobResponse response) {
        log.info("Message received from job: {}", response);

        Optional<DbRequest> requestById = requestRepository.findById(response.getIdentifier());

        if (requestById.isEmpty()) return;

        DbRequest request = requestById.get();
        request.setStatus(response.isSuccess() ? RequestStatus.COMPLETED : RequestStatus.FAILED);
        requestRepository.save(request);

    }

}
