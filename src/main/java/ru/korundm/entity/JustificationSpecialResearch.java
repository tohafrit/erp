package ru.korundm.entity;

import ru.korundm.enumeration.JustificationType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(JustificationType.Types.SPECIAL_RESEARCH)
public class JustificationSpecialResearch extends Justification {
}