package asu.dao;

import asu.repository.AsuSkladRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsuSkladService {

    @Autowired
    private AsuSkladRepository asuSkladRepository;
}
