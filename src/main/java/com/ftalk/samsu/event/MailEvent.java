package com.ftalk.samsu.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

@Getter
@Setter
public class MailEvent extends ApplicationEvent {
    private Set<String> to;
    private String subject;
    private String body;
    public MailEvent(Object source, Set<String> to, String subject, String body) {
        super(source);
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}
