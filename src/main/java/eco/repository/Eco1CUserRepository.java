package eco.repository;

import eco.entity.Eco1CUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Eco1CUserRepository extends JpaRepository<Eco1CUser, Long> {
}