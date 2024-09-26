package eco.repository;

import eco.entity.EcoPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoPurchaseRepository extends JpaRepository<EcoPurchase, Long> {

    @Query("select distinct user.lastName from EcoPurchase")
    List<String> findDistinctCreatedBy();
}