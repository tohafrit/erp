package asu.dao;

import asu.entity.AsuProdModuleStateOnProdSite;
import asu.repository.AsuProdModuleStateOnProdSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsuProdModuleStateOnProdSiteService {

    @Autowired
    private AsuProdModuleStateOnProdSiteRepository asuProdModuleStateOnProdSiteRepository;

    public AsuProdModuleStateOnProdSite read(long id) {
        return asuProdModuleStateOnProdSiteRepository.getOne(id);
    }

    public AsuProdModuleStateOnProdSite getByCode(String code) {
        return asuProdModuleStateOnProdSiteRepository.findTopByCodeOrderByCode(code);
    }

    public boolean existsByCode(String code) {
        return asuProdModuleStateOnProdSiteRepository.existsByCode(code);
    }
}
