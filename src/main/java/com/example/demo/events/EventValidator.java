package com.example.demo.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EventValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return EventDto.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventDto event = (EventDto) target;
        if(event.getBasePrice() > event.getMaxPrice() && event.getMaxPrice() > 0){
            errors.reject("wrongPrice","Values of prices are wrong"); // 글로벌에러
        }
        if (event.getEndEventDateTime().isBefore(event.getBeginEventDateTime())
                || event.getEndEventDateTime().isBefore(event.getCloseEnrollmentDateTime()) ||
                event.getEndEventDateTime().isBefore(event.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime","wrongValue","endEventDateTime is wrong"); // 필드에러
        }
        if (event.getCloseEnrollmentDateTime().isBefore(event.getBeginEnrollmentDateTime())) {
            errors.rejectValue("closeEnrollmentDateTime","wrongValue","closeDateTime is wrong");
        }
    }
}
