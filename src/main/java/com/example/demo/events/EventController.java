package com.example.demo.events;

import com.example.demo.accounts.Account;
import com.example.demo.accounts.CurrentUser;
import com.example.demo.commons.ErrorResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

    private final EventValidator eventValidator;
    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;

    @GetMapping
    public ResponseEntity queryEvents(@CurrentUser Account account,
                                      Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> paged = eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> eventResources = assembler.toModel(paged, e -> EventResource.modelOf(e));
        eventResources.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        if (account != null) {
            eventResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(eventResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@CurrentUser Account account,@PathVariable Integer id) {
        Optional<Event> byId = eventRepository.findById(id);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = byId.get();
        EntityModel<Event> eventResource = EventResource.modelOf(event);
        if (account != null && event.getManager().equals(account)) {
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);
    }


   /* @InitBinder("eventDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }*/
    @PostMapping
    public ResponseEntity createEvent(@CurrentUser Account account,
                                      @RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) { // 어노테이션 에러검증
            EntityModel<Errors> error1 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error1);
        }
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            EntityModel<Errors> error2 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error2);
        }
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(account);
        Event save = eventRepository.save(event);

        URI createdUri = linkTo(EventController.class).slash(save.getId()).toUri();
        EntityModel<Event> eventResource = EventResource.modelOf(save);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(linkTo(EventController.class).slash(save.getId()).withRel("update-event"));
        // eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@CurrentUser Account account,
                                      @PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors) {
        Optional<Event> byId = eventRepository.findById(id);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (errors.hasErrors()) { // 어노테이션 에러검증
            EntityModel<Errors> error1 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error1);
        }
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            EntityModel<Errors> error2 = ErrorResource.modelOf(errors);
            return ResponseEntity.badRequest().body(error2);
        }
        Event event = byId.get();
        if (!event.getManager().equals(account)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        modelMapper.map(eventDto, event);
        Event saved = eventRepository.save(event);
        EntityModel<Event> eventEntityModel = EventResource.modelOf(saved);
        eventEntityModel.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventEntityModel);
    }
}
