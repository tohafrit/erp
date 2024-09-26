package asu.dao;

import asu.entity.AsuProdModuleInUlist;
import asu.repository.AsuProdModuleInUlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsuProdModuleInUlistService {

    @Autowired
    AsuProdModuleInUlistRepository asuProdModuleInUlistRepository;

    public AsuProdModuleInUlist read(long id) {
        return asuProdModuleInUlistRepository.getOne(id);
    }
}