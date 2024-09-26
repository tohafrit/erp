package ru.korundm.dao;

import ru.korundm.entity.ServiceSymbol;

import java.util.List;

public interface ServiceSymbolService extends CommonService<ServiceSymbol> {

    ServiceSymbol getByCode(String code);

    List<ServiceSymbol> getAllByTechnologicalProcess(boolean technicalProcess);

    List<ServiceSymbol> getAllByOperationCard(boolean operationCard);

    List<ServiceSymbol> getAllByRouteMap(boolean routeMap);
}