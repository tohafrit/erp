package asu.dao;

import asu.repository.AsuPersomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsuPersonService {

    @Autowired
    private AsuPersomRepository asuPersomRepository;
}
