package eco.dao;

import eco.entity.EcoLaunchProduct;
import eco.repository.EcoLaunchProductRepository;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.LongType;
import org.springframework.stereotype.Service;
import ru.korundm.constant.ObjAttr;
import ru.korundm.schedule.importation.process.LaunchProductProcess;
import ru.korundm.schedule.importation.process.LaunchProductStructProcess;
import ru.korundm.util.KtCommonUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class EcoLaunchProductService {

    @PersistenceContext(unitName = "ecoEntityManagerFactory")
    private EntityManager em;

    private final EcoLaunchProductRepository launchProductRepository;

    public EcoLaunchProductService(EcoLaunchProductRepository launchProductRepository) {
        this.launchProductRepository = launchProductRepository;
    }

    public EcoLaunchProduct read(long id) {
        return launchProductRepository.getOne(id);
    }

    public List<EcoLaunchProduct> getAll() {
        return launchProductRepository.findAll();
    }

    public List<LaunchProductProcess.Item> importData() {
        String query = "SELECT\n" +
            "  LP.ID AS lpId,\n" +
            "  LP.LAUNCH_ID AS launchId,\n" +
            "  LP.PRODUCT_ID AS productId,\n" +
            "  CASE WHEN VER.BOM_ID IS NULL THEN VER_LAST.BOM_ID ELSE VER.BOM_ID END AS versionId,\n" +
            "  VER.APPROVE_DATE AS versionApproveDate\n" +
            "FROM\n" +
            "  LAUNCH_PRODUCT LP\n" +
            "  LEFT JOIN (\n" +
            "    SELECT\n" +
            "      LAUNCH_ID,\n" +
            "      PRODUCT_ID,\n" +
            "      BOM_ID,\n" +
            "      APPROVE_DATE\n" +
            "    FROM (\n" +
            "      SELECT\n" +
            "        BA.LAUNCH_ID,\n" +
            "        B.ID BOM_ID,\n" +
            "        B.PRODUCT_ID,\n" +
            "        BA.APPROVE_DATE,\n" +
            "        COUNT(*) OVER(PARTITION BY BA.LAUNCH_ID, B.PRODUCT_ID ORDER BY B.ID DESC) N\n" +
            "      FROM\n" +
            "        BOM_ATTRIBUTE BA\n" +
            "        JOIN\n" +
            "        BOM B\n" +
            "        ON BA.BOM_ID = B.ID\n" +
            "      WHERE\n" +
            "        BA.APPROVE_DATE IS NOT NULL\n" +
            "    ) RES\n" +
            "    WHERE N = 1\n" +
            "  ) VER\n" +
            "  ON VER.PRODUCT_ID = LP.PRODUCT_ID AND VER.LAUNCH_ID = LP.LAUNCH_ID\n" +
            "  LEFT JOIN (\n" +
            "    SELECT\n" +
            "      PRODUCT_ID,\n" +
            "      V_ID BOM_ID\n" +
            "    FROM (\n" +
            "      SELECT\n" +
            "        B.PRODUCT_ID AS PRODUCT_ID,\n" +
            "        B.ID AS V_ID,\n" +
            "        B.MAJOR AS V_MAJOR,\n" +
            "        B.MINOR AS V_MINOR,\n" +
            "        B.MODIFICATION AS V_MODIFICATION,\n" +
            "        ROW_NUMBER() OVER(PARTITION BY B.PRODUCT_ID ORDER BY B.MAJOR DESC, B.MINOR DESC, B.MODIFICATION DESC) NUM\n" +
            "      FROM\n" +
            "        BOM B\n" +
            "    ) RES WHERE RES.NUM = 1\n" +
            "  ) VER_LAST\n" +
            "  ON\n" +
            "  VER_LAST.PRODUCT_ID = LP.PRODUCT_ID";
        var nativeQuery = em.createNativeQuery(query);
        nativeQuery.unwrap(NativeQuery.class)
            .addScalar(ObjAttr.LP_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.LAUNCH_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.PRODUCT_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.VERSION_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.VERSION_APPROVE_DATE, LocalDateType.INSTANCE)
            .setResultTransformer(Transformers.aliasToBean(LaunchProductProcess.Item.class));
        return KtCommonUtil.INSTANCE.typedManyResult(nativeQuery);
    }

    public List<LaunchProductStructProcess.Item> importStructData() {
        String query = "SELECT\n" +
            "  lpId AS lpId,\n" +
            "  BSI.SUB_PRODUCT_ID AS productId,\n" +
            "  BSI.SUB_PRODUCT_COUNT AS amount\n" +
            "FROM (\n" +
            "  SELECT\n" +
            "    LP.ID AS lpId,\n" +
            "    LP.LAUNCH_ID AS launchId,\n" +
            "    LP.PRODUCT_ID AS productId,\n" +
            "    CASE WHEN VER.BOM_ID IS NULL THEN VER_LAST.BOM_ID ELSE VER.BOM_ID END AS versionId,\n" +
            "    VER.APPROVE_DATE AS versionApproveDate\n" +
            "  FROM\n" +
            "    LAUNCH_PRODUCT LP\n" +
            "    LEFT JOIN (\n" +
            "      SELECT\n" +
            "        LAUNCH_ID,\n" +
            "        PRODUCT_ID,\n" +
            "        BOM_ID,\n" +
            "        APPROVE_DATE\n" +
            "      FROM (\n" +
            "        SELECT\n" +
            "          BA.LAUNCH_ID,\n" +
            "          B.ID BOM_ID,\n" +
            "          B.PRODUCT_ID,\n" +
            "          BA.APPROVE_DATE,\n" +
            "          COUNT(*) OVER(PARTITION BY BA.LAUNCH_ID, B.PRODUCT_ID ORDER BY B.ID DESC) N\n" +
            "        FROM\n" +
            "          BOM_ATTRIBUTE BA\n" +
            "          JOIN\n" +
            "          BOM B\n" +
            "          ON BA.BOM_ID = B.ID\n" +
            "        WHERE\n" +
            "          BA.APPROVE_DATE IS NOT NULL\n" +
            "      ) RES\n" +
            "      WHERE N = 1\n" +
            "    ) VER\n" +
            "    ON VER.PRODUCT_ID = LP.PRODUCT_ID AND VER.LAUNCH_ID = LP.LAUNCH_ID\n" +
            "    LEFT JOIN (\n" +
            "      SELECT\n" +
            "        PRODUCT_ID,\n" +
            "        V_ID BOM_ID\n" +
            "      FROM (\n" +
            "        SELECT\n" +
            "          B.PRODUCT_ID AS PRODUCT_ID,\n" +
            "          B.ID AS V_ID,\n" +
            "          B.MAJOR AS V_MAJOR,\n" +
            "          B.MINOR AS V_MINOR,\n" +
            "          B.MODIFICATION AS V_MODIFICATION,\n" +
            "          ROW_NUMBER() OVER(PARTITION BY B.PRODUCT_ID ORDER BY B.MAJOR DESC, B.MINOR DESC, B.MODIFICATION DESC) NUM\n" +
            "        FROM\n" +
            "          BOM B\n" +
            "        WHERE\n" +
            "          B.MAJOR > 0\n" +
            "      ) RES WHERE RES.NUM = 1\n" +
            "    ) VER_LAST\n" +
            "    ON\n" +
            "    VER_LAST.PRODUCT_ID = LP.PRODUCT_ID\n" +
            ") res\n" +
            "JOIN\n" +
            "BOM_SPEC_ITEM BSI\n" +
            "ON BSI.BOM_ID = RES.VERSIONID";
        var nativeQuery = em.createNativeQuery(query);
        nativeQuery.unwrap(NativeQuery.class)
            .addScalar(ObjAttr.LP_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.PRODUCT_ID, LongType.INSTANCE)
            .addScalar(ObjAttr.AMOUNT, IntegerType.INSTANCE)
            .setResultTransformer(Transformers.aliasToBean(LaunchProductStructProcess.Item.class));
        return KtCommonUtil.INSTANCE.typedManyResult(nativeQuery);
    }
}