package eco.repository;

import eco.entity.EcoBom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoBomRepository extends JpaRepository<EcoBom, Long> {

    EcoBom findFirstById(long id);

    List<EcoBom> findAllByProductId(Long productId);

    List<EcoBom> findAllByProductIdAndMajorNotOrderByMajorAscMinorAscModificationAsc(Long productId, int major);

    boolean existsByIdAndProductId(Long id, Long productId);

    @Query(nativeQuery = true, value = "" +
        "SELECT\n" +
            "    B.*\n" +
            "FROM\n" +
            "    LAUNCH L\n" +
            "    join\n" +
            "    BOM_ATTRIBUTE BA\n" +
            "    on\n" +
            "    BA.LAUNCH_ID = L.ID\n" +
            "\n" +
            "    join\n" +
            "    BOM B\n" +
            "    on\n" +
            "    BA.BOM_ID = B.ID\n" +
            "where\n" +
            "    L.id = :launchId\n" +
            "    and B.PRODUCT_ID = :productId\n" +
            "    and (BA.APPROVE_DATE is not null or BA.ACCEPT_DATE is not null)\n" +
            "order by\n" +
            "    BA.ACCEPT_DATE DESC NULLS LAST,\n" +
            "    BA.APPROVE_DATE DESC NULLS LAST\n" +
            "fetch first 1 rows only"
    )
    EcoBom lastLaunchVersion(@Param("launchId") Long launchId, @Param("productId") Long productId);

    List<EcoBom> findAllByOrderByMajorDescMinorDescModificationDesc();
}