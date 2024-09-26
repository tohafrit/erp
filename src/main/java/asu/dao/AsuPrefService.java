package asu.dao;

import asu.entity.AsuPref;
import asu.repository.AsuPrefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsuPrefService {

    @Autowired
    AsuPrefRepository asuPrefRepository;

    public AsuPref read(long id) {
        return asuPrefRepository.getOne(id);
    }
}
