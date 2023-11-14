package com.ftalk.samsu.model.participant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "participants")
public class Participant {
    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ParticipantId participantId;

    @Column(name = "checkin_time")
    private Date checkin;

    @Column(name = "checkout_time")
    private Date checkout;
}
