package com.ftalk.samsu.model.participant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParticipantId implements Serializable {
    private Integer users_id;
    @Column(name = "events_id")
    private Integer eventsId;
}
