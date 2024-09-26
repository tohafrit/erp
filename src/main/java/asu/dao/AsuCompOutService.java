package asu.dao;

import asu.entity.AsuCompOut;
import asu.repository.AsuCompOutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsuCompOutService {
    @Autowired
    AsuCompOutRepository asuCompOutRepository;

    public AsuCompOut read(long id) {return asuCompOutRepository.getOne(id);}
}
