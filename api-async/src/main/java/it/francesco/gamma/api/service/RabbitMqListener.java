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

    public final ApiService service;

    @Autowired
    public RabbitMqListener(ApiService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${queue.firma.responses}")
    public void firmaListener(JobResponse response) {
        log.info("Message received from firma job: {}", response);
        service.firmaCompleted(response);
    }


    @RabbitListener(queues = "${queue.conservazione.responses}")
    public void conservazioneListener(JobResponse response) {
        log.info("Message received from conservazione job: {}", response);
        service.conservazioneCompleted(response);
    }

}
