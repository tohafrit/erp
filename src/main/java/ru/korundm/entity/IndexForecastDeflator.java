package ru.korundm.entity;

import ru.korundm.enumeration.IndexForecastType;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@DiscriminatorValue(IndexForecastType.Types.DEFLATOR)
public class IndexForecastDeflator extends IndexForecast implements Serializable {
}