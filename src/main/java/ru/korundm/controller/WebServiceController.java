package ru.korundm.controller;

import asu.dao.AsuOperationService;
import asu.dao.AsuProdModuleService;
import asu.dao.AsuProdModuleStateOnProdSiteService;
import asu.dao.AsuUchService;
import asu.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Controller
@Deprecated
public class WebServiceController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AsuOperationService asuOperationService;

    @Autowired
    private AsuUchService asuUchService;

    @Autowired
    private AsuProdModuleStateOnProdSiteService asuProdModuleStateOnProdSiteService;

    @Autowired
    private AsuProdModuleService asuProdModuleService;

    @GetMapping("/productListOnProdSiteInState")
    @ResponseBody
    public List<AsuProdModule> productListOnProdSiteInState() {
        AsuUch prodSite = asuUchService.getByCode("10");
        AsuProdModuleStateOnProdSite pmsOnPsList = asuProdModuleStateOnProdSiteService.getByCode("12400");

        /*не проверяю Operation.getFcode.equals("967953"), т.к. timeUsing == null */
        List<AsuOperation> operList = asuOperationService.getAllByToPS(Boolean.TRUE);
        if (operList.isEmpty()) {
            return new ArrayList<>();
        }

        Long pmsOnPsID = pmsOnPsList != null ? pmsOnPsList.getId() : null;

        /*Пропускаем personaList - пустой список*/

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AsuProdModule> criteriaQuery = criteriaBuilder.createQuery(AsuProdModule.class);
        Root<AsuProdModule> root = criteriaQuery.from(AsuProdModule.class);
        criteriaQuery.select(root).where(criteriaBuilder.between(root.get("tcr"), Long.MIN_VALUE, Long.MAX_VALUE));
        /*пропускаем module и launchPlantб тк пустые*/

        return null;
    }

    @GetMapping("/getStatusProductSale")
    @ResponseBody
    public List<AsuProdModule> getStatusProductSale() {
        List<String> barcodes = new ArrayList<>();
        List <AsuProdModule> prodModules = new ArrayList<>();

        for (String barcode : barcodes) {
            if (barcode.matches("[\\d]{12}")) {
                continue;
            }
            AsuProdModule prodModule = asuProdModuleService.getByParams(Integer.parseInt(barcode.substring(8)),
                                                                        barcode.substring(0, 6),
                                                                        barcode.substring(6, 8));
            if(prodModule == null){
                prodModule.setStatus(false);
                continue;
            }
            if(asuProdModuleService.isWasSoldInStructureOfStation(prodModule)){
                prodModule.setStatus(true);
            } else {
                AsuModuleMove lastModuleMove = prodModule.getLastModuleMove();
                if(lastModuleMove == null) {
                    prodModule.setStatus(false);
                } else {
                    Object objBk = lastModuleMove.getObjBk();
                    if      ( objBk == null )  { prodModule.setStatus(false);  }
                    else if ( objBk instanceof String && objBk.equals("moduleMove.sale") )  { prodModule.setStatus(true); }
                    else  { prodModule.setStatus(false); }
                }
            }

        }

        return null;
    }
}