package ru.korundm.entity;


import ru.korundm.enumeration.IndexForecastType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author pakhunov_an
 */
@Entity
@DiscriminatorValue(IndexForecastType.Types.PRICE)
public class IndexForecastPrice extends IndexForecast implements Serializable {
}