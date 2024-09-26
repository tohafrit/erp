package eco.repository;

import eco.entity.EcoPresentLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoPresentLogRecordRepository extends JpaRepository<EcoPresentLogRecord, Long> {

        EcoPresentLogRecord findTopByOrderByRegistrationDateDesc();
}