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
    @Column(name = "users_id")
    private Integer usersId;
    @Column(name = "events_id")
    private Integer eventsId;
}
