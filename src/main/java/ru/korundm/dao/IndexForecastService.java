package ru.korundm.dao;

import ru.korundm.entity.IndexForecast;
import ru.korundm.entity.Justification;

import java.util.List;

public interface IndexForecastService extends CommonService<IndexForecast> {

    List<IndexForecast> getAllByParams(Justification justification, Class subclass);
}