package eco.repository;

import eco.entity.EcoInOutDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoInOutDocumentRepository extends JpaRepository<EcoInOutDocument, Long> {
}
