package com.healthy.gym.trainings.events;

import org.springframework.context.ApplicationEvent;

import java.util.Collection;

public class OnChangeStartTimeOfGroupTrainingEvent extends ApplicationEvent {

    private final Collection<String> participantsEmails;

    public OnChangeStartTimeOfGroupTrainingEvent(Collection<String> participantsEmails) {
        super(participantsEmails);
        this.participantsEmails = participantsEmails;
    }

    public Collection<String> getParticipantsEmails() {
        return participantsEmails;
    }
}