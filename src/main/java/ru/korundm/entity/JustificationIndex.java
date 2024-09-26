package ru.korundm.entity;


import ru.korundm.enumeration.JustificationType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author pakhunov_an
 */
@Entity
@DiscriminatorValue(JustificationType.Types.INDEX)
public class JustificationIndex extends Justification implements Serializable {
}