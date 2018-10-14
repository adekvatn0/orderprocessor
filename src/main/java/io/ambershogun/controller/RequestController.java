package io.ambershogun.controller;

import io.ambershogun.ampq.RabbitConfiguration;
import io.ambershogun.request.entity.Request;
import io.ambershogun.request.enums.RequestState;
import io.ambershogun.repository.RequestRepository;
import io.ambershogun.response.MessageResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class RequestController<E extends Request> {

    private final RequestRepository requestRepository;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RequestController(RequestRepository requestRepository, RabbitTemplate rabbitTemplate) {
        this.requestRepository = requestRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RequestMapping(value = "/requests", method = RequestMethod.POST)
    public ResponseEntity createRequest(@Valid @RequestBody E request) {
        request.setState(RequestState.RECEIVED);
        request = requestRepository.save(request);

        rabbitTemplate.convertAndSend(RabbitConfiguration.QUEUE_NAME, request);

        return ResponseEntity.ok(new MessageResponse(request.getId()));
    }

    @RequestMapping(value = "/requests/{id}", method = RequestMethod.GET)
    public ResponseEntity getRequest(@PathVariable long id) {
        Optional<Request> optionalRequest = requestRepository.findById(id);

        if (optionalRequest.isPresent()) {
            Request request = optionalRequest.get();
            switch (request.getState()) {
                case PROCESSED:
                    return ResponseEntity.ok(optionalRequest.get());
                case RECEIVED:
                    return ResponseEntity.ok(new MessageResponse("Request still processing..."));
                case ERROR:
                    return ResponseEntity.ok(new MessageResponse("Request processed with error"));
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Status unknown"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("No request with such id were found"));
        }
    }
}
