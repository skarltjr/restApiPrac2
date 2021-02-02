package com.example.demo.events;

import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@NoArgsConstructor
public class EventResource extends EntityModel<Event> {

    public static EntityModel<Event> modelOf(Event event) {
        EntityModel<Event> newEvent = EntityModel.of(event);
        newEvent.add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
        return newEvent;
    }

}
