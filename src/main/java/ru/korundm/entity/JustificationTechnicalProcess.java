package ru.korundm.entity;

import ru.korundm.enumeration.JustificationType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author pakhunov_an
 */
@Entity
@DiscriminatorValue(JustificationType.Types.TECHNICAL_PROCESS)
public class JustificationTechnicalProcess extends Justification {
}