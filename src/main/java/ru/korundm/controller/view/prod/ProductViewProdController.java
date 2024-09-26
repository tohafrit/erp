package ru.korundm.controller.view.prod;

import eco.dao.EcoLaunchService;
import eco.dao.EcoTBicStatusService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.*;
import ru.korundm.dto.DropdownOption;
import ru.korundm.entity.*;
import ru.korundm.enumeration.BomItemReplacementStatus;
import ru.korundm.enumeration.CompanyTypeEnum;
import ru.korundm.exception.AlertUIException;
import ru.korundm.form.edit.*;
import ru.korundm.form.search.*;
import ru.korundm.helper.FileStorageType;
import ru.korundm.util.CommonUtil;
import ru.korundm.util.KtCommonUtil;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.korundm.enumeration.SpecificationImportDetailType.*;

@ViewController(RequestPath.View.Prod.PRODUCT)
@SessionAttributes(
    names = {
        "productOccurrenceFilterForm",
        "productSpecComponentFilterForm",
        "productCommentFilterForm"
    },
    types = {
        ProductOccurrenceFilterForm.class,
        ProductSpecComponentFilterForm.class,
        ProductCommentFilterForm.class
    }
)
public class ProductViewProdController {

    private static final Pattern designationNumberPattern = Pattern.compile("\\d+");

    private static final String VERSION_HYPHEN_LETTER = " -";
    private static final String VERSION_APPROVE_LETTER = "У";
    private static final String VERSION_ACCEPT_LETTER = "П";

    private static final String COMMA_SEPARATOR = ",";
    private static final String DESIGNATION_DIAPASON_SEPARATOR = "-";

    private static final List<Long> OGK_USERS = List.of(797659L, 509211L, 509214L, 509216L, 509217L, 509220L, 509238L, 509239L, 509242L, 509247L, 509248L, 578855L, 2552086L, 3543675L);
    private static final List<Long> PRODUCT_TYPES = List.of(1L, 2L, 3L, 4L, 5L, 10L);

    private static final String PRODUCT_OCCURRENCE_FILTER_FORM_ATTR = "productOccurrenceFilterForm";
    private static final String PRODUCT_COMMENT_FILTER_FORM_ATTR = "productCommentFilterForm";
    private static final String PRODUCT_SPEC_COMPONENT_FILTER_FORM_ATTR = "productSpecComponentFilterForm";

    private final ProductService productService;
    private final ProductTypeService productTypeService;
    private final ProductLetterService productLetterService;
    private final UserService userService;
    private final ClassificationGroupService classificationGroupService;
    private final BomService bomService;
    private final BomSpecItemService bomSpecItemService;
    private final BomAttributeService bomAttributeService;
    private final BomItemService bomItemService;
    private final BomItemReplacementService bomItemReplacementService;
    private final BomItemPositionService bomItemPositionService;
    private final ComponentCategoryService componentCategoryService;
    private final LaunchService launchService;
    private final SpecificationImportDetailService specificationImportDetailService;
    private final CompanyService companyService;
    private final ProductCommentService productCommentService;
    private final ProductDocumentationService productDocumentationService;
    private final FileStorageService fileStorageService;

    private final EcoLaunchService ecoLaunchService;
    private final EcoTBicStatusService ecoTBicStatusService;

    public ProductViewProdController(
        ProductService productService,
        ProductTypeService productTypeService,
        ProductLetterService productLetterService,
        UserService userService,
        ClassificationGroupService classificationGroupService,
        BomService bomService,
        BomSpecItemService bomSpecItemService,
        BomAttributeService bomAttributeService,
        BomItemService bomItemService,
        BomItemReplacementService bomItemReplacementService,
        BomItemPositionService bomItemPositionService,
        ComponentCategoryService componentCategoryService,
        LaunchService launchService,
        SpecificationImportDetailService specificationImportDetailService,
        CompanyService companyService,
        ProductCommentService productCommentService,
        ProductDocumentationService productDocumentationService,
        FileStorageService fileStorageService,
        EcoLaunchService ecoLaunchService,
        EcoTBicStatusService ecoTBicStatusService
    ) {
        this.productService = productService;
        this.productTypeService = productTypeService;
        this.productLetterService = productLetterService;
        this.userService = userService;
        this.classificationGroupService = classificationGroupService;
        this.bomService = bomService;
        this.bomSpecItemService = bomSpecItemService;
        this.bomAttributeService = bomAttributeService;
        this.bomItemService = bomItemService;
        this.bomItemReplacementService = bomItemReplacementService;
        this.bomItemPositionService = bomItemPositionService;
        this.componentCategoryService = componentCategoryService;
        this.launchService = launchService;
        this.specificationImportDetailService = specificationImportDetailService;
        this.companyService = companyService;
        this.productCommentService = productCommentService;
        this.productDocumentationService = productDocumentationService;
        this.fileStorageService = fileStorageService;
        this.ecoLaunchService = ecoLaunchService;
        this.ecoTBicStatusService = ecoTBicStatusService;
    }

    @ModelAttribute(PRODUCT_OCCURRENCE_FILTER_FORM_ATTR)
    public ProductOccurrenceFilterForm productOccurrenceFilterFormAttr() {
        ProductOccurrenceFilterForm form = new ProductOccurrenceFilterForm();
        form.setActive(true);
        return form;
    }

    @ModelAttribute(PRODUCT_COMMENT_FILTER_FORM_ATTR)
    public ProductCommentFilterForm productCommentFilterFormAttr() {
        return new ProductCommentFilterForm();
    }

    @ModelAttribute(PRODUCT_SPEC_COMPONENT_FILTER_FORM_ATTR)
    public ProductSpecComponentFilterForm productSpecComponentFilterFormAttr() {
        return new ProductSpecComponentFilterForm();
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/product/list";
    }

    @GetMapping("/list/filter")
    public String list_filter(ModelMap model) {
        model.addAttribute("productTypeList", productTypeService.getAllById(PRODUCT_TYPES));
        model.addAttribute("letterList", productLetterService.getAll());
        model.addAttribute("leadList", userService.getAllById(OGK_USERS));
        model.addAttribute("classificationGroupList", classificationGroupService.getAll().stream().map(it -> new DropdownOption(it.getId(), it.getNumber() + " " + it.getCharacteristic(), false)).collect(Collectors.toList()));
        return "prod/include/product/list/filter";
    }

    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditProductForm form = new EditProductForm();
        if (id != null) {
            Product product = productService.read(id);
            form.setId(product.getId());
            form.setLockVersion(product.getVer());
            form.setConditionalName(product.getConditionalName());
            form.setTechSpecName(product.getTechSpecName());
            form.setType(product.getType());
            form.setDecimalNumber(product.getDecimalNumber());
            form.setLetter(product.getLetter());
            form.setArchive(product.getArchiveDate() != null);
            form.setPosition(product.getPosition());
            form.setLead(product.getLead());
            form.setClassificationGroup(product.getClassificationGroup());
            form.setComment(product.getComment());
            form.setSerial(product.getSerial());
        }
        model.addAttribute("form", form);
        model.addAttribute("productTypeList", productTypeService.getAllById(PRODUCT_TYPES));
        model.addAttribute("letterList", productLetterService.getAll());
        model.addAttribute("leadList", userService.getAllById(OGK_USERS));
        model.addAttribute("classificationGroupList", classificationGroupService.getAll());
        return "prod/include/product/list/edit";
    }

    // Детальная информация об изделии
    @GetMapping("/detail")
    public String detail(ModelMap model, long id) {
        Product product = productService.read(id);
        model.addAttribute("productName",
            StringUtils.isBlank(product.getConditionalName()) ? product.getTechSpecName() : product.getConditionalName());
        model.addAttribute("productId", product.getId());
        return "prod/include/product/detail";
    }

    // Основная информация об изделии
    @GetMapping("/detail/general")
    public String detail_general(ModelMap model, long productId) {
        var product = productService.read(productId);
        if (product == null) throw new AlertUIException("Изделие не найдено");
        model.addAttribute("product", product);
        return "prod/include/product/detail/general";
    }

    // Состав изделия
    @GetMapping("/detail/structure")
    public String detail_structure(
        ModelMap model,
        Long productId,
        @RequestParam(required = false) Long selectedBomId
    ) {
        boolean loadBomFromSession = false;
        Long versionSelectedId;
        if (selectedBomId == null) {
            // Последняя утвержденная к запуску, а если в
            // сессионном кэше браузера есть сохраненная версия, то необходимо загружать ее
            loadBomFromSession = true;
            Bom bomApprovedLast = bomService.getApprovedToLastLaunch(productId);
            versionSelectedId = bomApprovedLast == null ? null : bomApprovedLast.getId();
        } else {
            versionSelectedId = selectedBomId;
        }
        model.addAttribute("versionList", buildProductDetailVersionList(productId, versionSelectedId));
        model.addAttribute("productId", productId);
        model.addAttribute("loadBomFromSession", loadBomFromSession);
        return "prod/include/product/detail/structure";
    }

    // Информация о составе изделия
    @GetMapping("/detail/structure/info")
    public String detail_structure_info(ModelMap model, Long bomId) {
        Bom bom = bomService.read(bomId);
        if (bom == null) {
            throw new AlertUIException("Версия была удалена");
        }
        model.addAttribute("producerList", companyService.getAllByType(CompanyTypeEnum.PRODUCT_PRODUCER));
        model.addAttribute("launchList", launchService.findAllSortedYearNumberDesc(null));
        model.addAttribute("bomId", bom.getId());
        model.addAttribute("bomDescriptor", StringUtils.leftPad(String.valueOf(bom.getDescriptor()), 5, '0'));
        model.addAttribute("bomProductionName", bom.getProductionName());
        return "prod/include/product/detail/structure/info";
    }

    // Окно добавления изделия в состав
    @GetMapping("/detail/structure/info/list-add")
    public String detail_structure_info_listAdd(ModelMap model, Long bomId) {
        Bom bom = bomService.read(bomId);
        if (bom == null) throw new AlertUIException("Версия изделия не найдена");
        model.addAttribute("bomId", bomId);
        model.addAttribute("productName", bom.getProduct().getConditionalName());
        return "prod/include/product/detail/structure/info/listAdd";
    }

    // Окно фильтра добавления изделия в состав
    @GetMapping("/detail/structure/info/list-add/filter")
    public String detail_structure_info_listAdd_filter() {
        return "prod/include/product/detail/structure/info/list-add/filter";
    }

    // Окно редактирования изделия в составе
    @GetMapping("/detail/structure/info/list-edit")
    public String detail_structure_info_listEdit(ModelMap model, long bomSpecItemId) {
        BomSpecItem bomSpecItem = bomSpecItemService.read(bomSpecItemId);
        EditProductStructureItemForm form = new EditProductStructureItemForm();
        form.setId(bomSpecItem.getId());
        form.setLockVersion(bomSpecItem.getLockVersion());
        form.setQuantity(bomSpecItem.getQuantity());
        form.setProducer(bomSpecItem.getProducer());
        model.addAttribute("form", form);
        model.addAttribute("productName", bomSpecItem.getBom().getProduct().getConditionalName());
        model.addAttribute("subProductName", bomSpecItem.getProduct().getConditionalName());
        model.addAttribute("producerList", companyService.getAllByType(CompanyTypeEnum.PRODUCT_PRODUCER));
        return "prod/include/product/detail/structure/info/listEdit";
    }

    // Получение актуальных версий для выпадающего списка
    private List<DropdownOption> buildProductDetailVersionList(Long productId, Long selectedBomId) {
        final StringBuilder stringBuilder = new StringBuilder();
        List<Bom> bomList = bomService.getAllSortedVersionDescByProductId(productId);
        // Извлечение версий в небходимом формате
        return bomList.stream().map(bom -> {
            List<BomAttribute> bomAttributeList = bomAttributeService.getAllByBomId(bom.getId());
            bomAttributeList = bomAttributeList.stream()
                .filter(attr -> attr.getApproveDate() != null || attr.getAcceptDate() != null)
                .collect(Collectors.toList());
            // Упорядочивание атрибутов по датам запусков
            bomAttributeList.sort(Comparator
                .comparing(BomAttribute::getLaunch, Comparator.comparing(Launch::getYear, Comparator.reverseOrder()))
                .thenComparing(BomAttribute::getLaunch, Comparator.comparing(Launch::getNumber, Comparator.reverseOrder()))
            );
            stringBuilder.setLength(0);
            stringBuilder.append(bom.getVersion());
            stringBuilder.append(bomAttributeList.isEmpty() ? StringUtils.EMPTY : VERSION_HYPHEN_LETTER);
            bomAttributeList.forEach(attribute -> {
                stringBuilder.append(StringUtils.SPACE);
                stringBuilder.append(attribute.getLaunch().getNumberInYear());
                stringBuilder.append(attribute.getApproveDate() == null ? StringUtils.EMPTY : VERSION_APPROVE_LETTER);
                stringBuilder.append(attribute.getAcceptDate() == null ? StringUtils.EMPTY : VERSION_ACCEPT_LETTER);
            });
            return new DropdownOption(bom.getId(), stringBuilder.toString(), Objects.equals(bom.getId(), selectedBomId));
        }).collect(Collectors.toList());
    }

    // Документация
    @GetMapping("/detail/documentation")
    public String detail_documentation(ModelMap model, long productId) {
        model.addAttribute("product", productService.read(productId));
        return "prod/include/product/detail/documentation";
    }

    @GetMapping("/detail/documentation/edit")
    public String detail_documentation_edit(ModelMap model, Long id, long productId) {
        var form = new EditProductDocumentationForm();
        form.setProductId(productId);
        if (id != null) {
            var doc = productDocumentationService.read(id);
            form.setId(doc.getId());
            form.setName(doc.getName());
            form.setComment(doc.getComment());
            form.setFileStorage(fileStorageService.readOneSingular(doc, FileStorageType.ProductDocumentationFile.INSTANCE));
        }
        model.addAttribute("form", form);
        return "prod/include/product/detail/documentation/edit";
    }

    // Входимость
    @GetMapping("/detail/occurrence")
    public String detail_occurrence(ModelMap model, long productId) {
        model.addAttribute("productId", productId);
        return "prod/include/product/detail/occurrence";
    }

    // Фильтр входимости
    @GetMapping("/detail/occurrence/filter")
    public String detail_occurrence_filter() {
        return "prod/include/product/detail/occurrence/filter";
    }

    // Комментарий
    @GetMapping("/detail/comment")
    public String detail_comment(ModelMap model, long productId) {
        model.addAttribute("productId", productId);
        return "prod/include/product/detail/comment";
    }

    // Фильтр комментариев
    @GetMapping("/detail/comment/filter")
    public String detail_comment_filter(ModelMap model) {
        model.addAttribute("userList", userService.getByLastNameIn(productCommentService.getDistinctUser()));
        return "prod/include/product/detail/comment/filter";
    }

    // Добавление/редактирования пользовательского комментария
    @GetMapping("/detail/comment/edit")
    public String detail_comment_edit(HttpSession session, ModelMap model, Long productId, Long commentId) {
        Product product = productService.read(productId);
        User user = KtCommonUtil.INSTANCE.getUser(session);
        EditProductCommentForm form = new EditProductCommentForm();
        if (commentId != null) {
            ProductComment productComment = productCommentService.read(commentId);
            if (!Objects.equals(user, productComment.getUser())) {
                throw new AlertUIException("Редактировать можно только созданный вами комментарий");
            }
            form.setId(productComment.getId());
            form.setCreatedBy(productComment.getUser());
            form.setCreatedDate(productComment.getCreationDate());
            form.setComment(productComment.getComment());
            form.setProduct(productComment.getProduct());
        } else {
            form.setCreatedBy(user);
            form.setCreatedDate(LocalDateTime.now());
            form.setProduct(product);
        }
        model.addAttribute("productConditionalName", product.getConditionalName());
        model.addAttribute("form", form);
        return "prod/include/product/detail/comment/edit";
    }

    // Спецификация
    @GetMapping("/detail/specification")
    public String detail_specification(
        ModelMap model,
        Long productId,
        @RequestParam(required = false) Long selectedBomId,
        @RequestParam(required = false) Boolean loadLastAcceptOrApproved
    ) {
        boolean loadBomFromSession = false;
        Long versionSelectedId;
        if (BooleanUtils.toBoolean(loadLastAcceptOrApproved)) { // Последняя утвержденная(принятая)
            Bom lastApprovedOrAccepted = bomService.getLastApprovedOrAccepted(productId);
            versionSelectedId = lastApprovedOrAccepted == null ? null : lastApprovedOrAccepted.getId();
        } else if (selectedBomId == null) {
            // Последняя утвержденная к запуску, а если в
            // сессионном кэше браузера есть сохраненная версия, то необходимо загружать ее
            loadBomFromSession = true;
            Bom bomApprovedLast = bomService.getApprovedToLastLaunch(productId);
            versionSelectedId = bomApprovedLast == null ? null : bomApprovedLast.getId();
        } else {
            versionSelectedId = selectedBomId;
        }
        model.addAttribute("versionList", buildProductDetailVersionList(productId, versionSelectedId));
        model.addAttribute("productId", productId);
        model.addAttribute("loadBomFromSession", loadBomFromSession);
        return "prod/include/product/detail/specification";
    }

    // Окно спецификации изделия
    @GetMapping("/detail/specification/info")
    public String detail_specification_info(ModelMap model, Long bomId) {
        Bom bom = bomService.read(bomId);
        if (bom == null) {
            throw new AlertUIException("Версия была удалена");
        }
        model.addAttribute("launchList", launchService.findAllSortedYearNumberDesc(null));
        model.addAttribute("bomId", bom.getId());
        model.addAttribute("bomIsStock", bom.getStock());
        model.addAttribute("bomDescriptor", StringUtils.leftPad(String.valueOf(bom.getDescriptor()), 5, '0'));
        String prefix = bom.getSapsanProductBomList().stream()
            .map(SapsanProductBom::getSapsanProduct).filter(Objects::nonNull)
            .map(SapsanProduct::getPrefix)
            .collect(Collectors.joining(", "));
        model.addAttribute("prefix", prefix);
        model.addAttribute("bomProductionName", bom.getProductionName());
        model.addAttribute("hasImport", specificationImportDetailService.existsByBomId(bomId));
        return "prod/include/product/detail/specification/info";
    }

    // Окно редактирования вхождения спецификации
    @GetMapping("/detail/specification/info/edit-spec-item")
    public String detail_specification_info_editSpecItem(ModelMap model, long id) {
        BomItem bomItem = bomItemService.read(id);
        if (bomItem == null) throw new AlertUIException("Вхождение спецификации было удалено");
        boolean isUnit = bomItem.getComponent().getCategory().isUnit();
        //
        EditProductSpecItemForm form = new EditProductSpecItemForm();
        form.setId(bomItem.getId());
        form.setUnit(isUnit);
        form.setLockVersion(bomItem.getLockVersion());
        form.setGivenRawMaterial(bomItem.isGivenRawMaterial());
        form.setProducerIdList(bomItem.getProducerList().stream().map(Company::getId).collect(Collectors.toList()));
        form.setQuantity(bomItem.getQuantity());
        form.setApprovedOrAccepted(
            bomItem.getBom().getBomAttributeList().stream()
                .anyMatch(attribute -> attribute.getAcceptDate() != null || attribute.getApproveDate() != null)
        );
        List<BomItemPosition> positionList = bomItem.getBomItemPositionList();
        if (isUnit) { // Штучная категория
            // Собираем пользовательское представление
            List<EditProductSpecItemForm.Position> formPositionList = parseDesignationData(positionList).entrySet().stream().map(entry -> {
                EditProductSpecItemForm.Position formPosition = new EditProductSpecItemForm.Position();
                formPosition.setLetter(entry.getKey());
                formPosition.setValue(entry.getValue());
                return formPosition;
            }).collect(Collectors.toList());
            form.setPositionList(formPositionList);
        }
        model.addAttribute("form", form);
        model.addAttribute("producerList", companyService.getAllByType(CompanyTypeEnum.PRODUCT_PRODUCER));
        model.addAttribute("productInfo", bomItem.getBom().getProduct().getConditionalName() + ", " + bomItem.getBom().getVersion());
        model.addAttribute("componentInfo", bomItem.getComponent().getPosition() + ", " + bomItem.getComponent().getName());
        return "prod/include/product/detail/specification/info/editSpecItem";
    }

    // Метод разбора позиций вхождения в спецификацию
    private Map<String, String> parseDesignationData(List<BomItemPosition> positionList) {
        Map<String, String> resultMap = new HashMap<>();
        // Собираем словарь литера - список значения литеры
        Map<String, List<Integer>> designationMap = new HashMap<>();
        positionList.forEach(position -> {
            String designation = position.getDesignation();
            if (designation != null) {
                Matcher designationMatcher = designationNumberPattern.matcher(designation);
                String letterPosition = designationMatcher.find() ? designationMatcher.group() : StringUtils.EMPTY;
                String letter = designation.replaceAll(letterPosition, StringUtils.EMPTY);
                if (StringUtils.isNotBlank(letter)) {
                    if (designationMap.containsKey(letter)) { // добавляем в существующий ключ
                        List<Integer> valueList = designationMap.get(letter);
                        if (CommonUtil.isInteger(letterPosition)) {
                            valueList.add(Integer.parseInt(letterPosition));
                        }
                    } else { // Новый ключ и его значения
                        List<Integer> valueList = new ArrayList<>();
                        if (CommonUtil.isInteger(letterPosition)) {
                            valueList.add(Integer.parseInt(letterPosition));
                        }
                        designationMap.put(letter, valueList);
                    }
                }
            }
        });
        // Собираем словарь-результат литера - строка значений через разделитель
        designationMap.forEach((letter, valueList) -> {
            Collections.sort(valueList); // сортировка обязательна для формирования строки
            //
            StringBuilder stringBuilder = new StringBuilder();
            int listSize = valueList.size();
            for (int i = 0; i < listSize;) {
                int shift = 0;
                while (true) {
                    int posFrom = i + shift;
                    int posTo = i + 1 + shift;
                    if (listSize > posTo && valueList.get(posTo) - valueList.get(posFrom) == 1) {
                        shift++;
                    } else {
                        break;
                    }
                }
                if (shift == 0) {
                    if (i == 0) {
                        stringBuilder.append(valueList.get(i));
                    } else {
                        stringBuilder.append(COMMA_SEPARATOR).append(valueList.get(i));
                    }
                    i++;
                } else if (shift == 1) {
                    if (i == 0) {
                        stringBuilder.append(valueList.get(i));
                    } else {
                        stringBuilder.append(COMMA_SEPARATOR).append(valueList.get(i));
                    }
                    stringBuilder.append(COMMA_SEPARATOR).append(valueList.get(i + 1));
                    i = i + 2;
                } else {
                    if (i == 0) {
                        stringBuilder.append(valueList.get(i));
                    } else {
                        stringBuilder.append(COMMA_SEPARATOR).append(valueList.get(i));
                    }
                    stringBuilder.append(DESIGNATION_DIAPASON_SEPARATOR).append(valueList.get(i + shift));
                    i = i + shift + 1;
                }
            }
            resultMap.put(letter, stringBuilder.toString());
        });
        return resultMap;
    }

    // Окно спецификации изделия
    @GetMapping("/detail/specification/info/filter")
    public String detail_specification_info_filter(ModelMap model) {
        model.addAttribute("categoryList", componentCategoryService.getAll());
        return "prod/include/product/detail/specification/info/filter";
    }

    // Загрузка окна со списком компонентов
    @GetMapping("/detail/specification/info/spec-replacement-component")
    public String detail_specification_info_specReplacementComponent(
        ModelMap model,
        Long bomId,
        Long bomItemId
    ) {
        Bom bom = bomService.read(bomId);
        if (bom == null) {
            throw new AlertUIException("Редактируемая версия была удалена");
        }
        if (bomAttributeService.getAllByBomId(bomId).stream().anyMatch(el -> el.getAcceptDate() != null || el.getApproveDate() != null)) {
            throw new AlertUIException("Утвержденные (принятые) спецификации можно редактировать только в части замен и давальческого сырья");
        }
        BomItem bomItem = bomItemService.read(bomItemId);
        model.addAttribute("bomId", bomId);
        model.addAttribute("bomItemId", bomItem.getId());
        model.addAttribute("categoryList", componentCategoryService.getAll());
        model.addAttribute("producerList", companyService.getAllByType(CompanyTypeEnum.COMPONENT_PRODUCER));
        model.addAttribute("productInfo", bomItem.getBom().getProduct().getConditionalName() + ", " + bomItem.getBom().getVersion());
        model.addAttribute("componentInfo", bomItem.getComponent().getPosition() + ", " + bomItem.getComponent().getName());
        model.addAttribute("categoryId", bomItem.getComponent().getCategory().getId());
        return "prod/include/product/detail/specification/info/specReplacementComponent";
    }

    @GetMapping("/detail/specification/info/spec-replacement-component/filter")
    public String detail_specification_info_specReplacementComponent_filter(ModelMap model) {
        model.addAttribute("categoryList", componentCategoryService.getAll().stream().map(it -> new DropdownOption(it.getId(), it.getName(), false)).collect(Collectors.toList()));
        model.addAttribute("producerList", companyService.getAllByType(CompanyTypeEnum.COMPONENT_PRODUCER).stream().map(it -> new DropdownOption(it.getId(), it.getName(), false)).collect(Collectors.toList()));
        return "prod/include/product/detail/specification/info/spec-replacement-component/filter";
    }

    // Загрузка окна со списком компонентов
    @GetMapping("/detail/specification/info/spec-add-component")
    public String detail_specification_info_specAddComponent(
        ModelMap model,
        Long bomId
    ) {
        Bom bom = bomService.read(bomId);
        if (bom == null) throw new AlertUIException("Версия изделия не найдена");
        model.addAttribute("bomId", bomId);
        model.addAttribute("categoryList", componentCategoryService.getAll());
        model.addAttribute("producerList", companyService.getAllByType(CompanyTypeEnum.COMPONENT_PRODUCER));
        model.addAttribute("productInfo", bom.getProduct().getConditionalName() + ", " + bom.getVersion());
        return "prod/include/product/detail/specification/info/specAddComponent";
    }

    // Загрузка замен для позиций спецификации
    @GetMapping("/detail/specification/info/replacement")
    public String detail_specification_info_replacement(
        ModelMap model,
        Long bomItemId,
        boolean scroll
    ) {
        BomItem bomItem = bomItemService.read(bomItemId);
        if (bomItem == null) {
            throw new AlertUIException("Редактируемое вхождение спецификации было удалено");
        }
        model.addAttribute("bomItemId", bomItemId);
        model.addAttribute("mainComponentId", bomItem.getComponent().getId());
        model.addAttribute("scroll", scroll);
        return "prod/include/product/detail/specification/info/replacement";
    }

    // Загрузка окна редактирования статуса
    @GetMapping("/detail/specification/info/replacement/edit-status")
    public String detail_specification_info_replacement_editStatus(
        ModelMap model,
        Long id
    ) {
        BomItemReplacement replacement = bomItemReplacementService.read(id);
        if (replacement == null) {
            throw new AlertUIException("Замена была удалена");
        }
        List<BomItemReplacementStatus> statusList = new ArrayList<>();
        BomItemReplacementStatus status = replacement.getStatus();
        if (status.equals(BomItemReplacementStatus.NOT_PROCESSED) || status.equals(BomItemReplacementStatus.CATALOG)) {
            statusList.add(BomItemReplacementStatus.ALLOWED);
            statusList.add(BomItemReplacementStatus.PROHIBITED);
        } else if (status.equals(BomItemReplacementStatus.ALLOWED)) {
            statusList.add(BomItemReplacementStatus.ALLOWED);
        } else if (status.equals(BomItemReplacementStatus.PROHIBITED)) {
            statusList.add(BomItemReplacementStatus.PROHIBITED);
        }
        model.addAttribute("compName", replacement.getComponent().getName());
        model.addAttribute("id", id);
        model.addAttribute("statusList", statusList);
        return "prod/include/product/detail/specification/info/replacement/editStatus";
    }

    // Загрузка окна со списком компонентов замен
    @GetMapping("/detail/specification/info/replacement/add-component")
    public String detail_specification_info_replacement_addComponent(
        ModelMap model,
        Long bomItemId
    ) {
        BomItem bomItem = bomItemService.read(bomItemId);
        model.addAttribute("bomItemId", bomItemId);
        model.addAttribute("categoryList", componentCategoryService.getAll());
        model.addAttribute("productInfo", bomItem.getBom().getProduct().getConditionalName() + ", " + bomItem.getBom().getVersion());
        model.addAttribute("componentInfo", bomItem.getComponent().getPosition() + ", " + bomItem.getComponent().getName());
        model.addAttribute("categoryId", bomItem.getComponent().getCategory().getId());
        return "prod/include/product/detail/specification/info/replacement/addComponent";
    }

    @GetMapping("/detail/specification/info/replacement/add-component/filter")
    public String detail_specification_info_replacement_addComponent_filter(ModelMap model) {
        model.addAttribute("categoryList", componentCategoryService.getAll().stream().map(it -> new DropdownOption(it.getId(), it.getName(), false)).collect(Collectors.toList()));
        return "prod/include/product/detail/specification/info/replacement/add-component/filter";
    }

    // Загрузка позиций для вхождения спецификации
    @GetMapping("/detail/specification/info/position")
    public String detail_specification_info_position(
        ModelMap model,
        Long bomItemId
    ) {
        BomItem bomItem = bomItemService.read(bomItemId);
        if (bomItem == null) {
            throw new AlertUIException("Вхождение было удалено");
        }
        model.addAttribute("isUnit", bomItem.getComponent().getCategory().isUnit());
        model.addAttribute("bomItemId", bomItemId);
        return "prod/include/product/detail/specification/info/position";
    }

    // Загрузка окна редактирования в окне позиционных обозначений
    @GetMapping("/detail/specification/info/position/edit")
    public String detail_specification_info_position_edit(
        ModelMap model,
        Long id
    ) {
        BomItemPosition position = bomItemPositionService.read(id);
        if (position == null) {
            throw new AlertUIException("Редактируемая позиция была удалена");
        }
        EditProductSpecPositionForm form = new EditProductSpecPositionForm();
        form.setId(id);
        form.setLockVersion(position.getLockVersion());
        form.setFirmware(position.getFirmware());
        model.addAttribute("form", form);
        return "prod/include/product/detail/specification/info/position/edit";
    }

    // Окно копирования спецификации
    @GetMapping("/detail/specification/info/list-spec-copy")
    public String detail_specification_info_listSpecCopy(
        ModelMap model,
        Long bomId
    ) {
        if (bomAttributeService.getAllByBomId(bomId).stream().anyMatch(el -> el.getAcceptDate() != null || el.getApproveDate() != null)) {
            throw new AlertUIException("Утвержденные (принятые) спецификации можно редактировать только в части замен и давальческого сырья");
        }
        if (bomItemService.existsByBomId(bomId)) {
            throw new AlertUIException("Спецификация уже содержит компоненты. Для выполнения действия очистите спецификацию.");
        }
        model.addAttribute("bomId", bomId);
        return "prod/include/product/detail/specification/info/listSpecCopy";
    }

    // Окно сравнения спецификации
    @GetMapping("/detail/specification/info/comparison")
    public String detail_specification_info_comparison(
        ModelMap model,
        Long bomId
    ) {
        Bom bom = bomService.read(bomId);
        model.addAttribute("productId", bom.getProduct().getId());
        return "prod/include/product/detail/specification/info/comparison";
    }

    // Фильтр для сравнения спецификаций
    @GetMapping("/detail/specification/info/comparison/filter")
    public String detail_specification_info_comparison_filter(
        ModelMap model,
        Long productAId,
        Long productBId
    ) {
        model.addAttribute("productA", productService.read(productAId));
        model.addAttribute("productB", productService.read(productBId));
        model.addAttribute("versionListA", buildProductDetailVersionList(productAId, null));
        model.addAttribute("versionListB", buildProductDetailVersionList(productBId, null));
        return "prod/include/product/detail/specification/info/comparison/filter";
    }

    // Окно выбора изделия
    @GetMapping("/detail/specification/info/comparison/select")
    public String detail_specification_info_comparison_select(
        ModelMap model,
        String productLetter
    ) {
        model.addAttribute("productLetter", productLetter);
        return "prod/include/product/detail/specification/info/comparison/select";
    }

    // Детали импорта
    @GetMapping("/detail/specification/info/excel-import-detail")
    public String detail_specification_info_excelImportDetail(
        ModelMap model,
        Long bomId
    ) {
        List<SpecificationImportDetail> specificationImportDetailList = specificationImportDetailService.getAllByBomId(bomId);
        model.addAttribute("newList",
            specificationImportDetailList.stream().filter(s -> NEW_COMPONENT.equals(s.getType())).collect(Collectors.toList()));
        model.addAttribute("existList",
            specificationImportDetailList.stream().filter(s -> EXIST_COMPONENT.equals(s.getType())).collect(Collectors.toList()));
        model.addAttribute("ignoreList",
            specificationImportDetailList.stream().filter(s -> MISTAKE_COMPONENT.equals(s.getType())).collect(Collectors.toList()));
        model.addAttribute("bomId", bomId);
        return "prod/include/product/detail/specification/info/excelImportDetail";
    }

    // Отчеты в изделиях
    @GetMapping("/list/report")
    public String list_report(ModelMap model, int reportType) {
        if (reportType == 1) { // ЗС для запуска
            List<DropdownOption> typeList = List.of(
                new DropdownOption(1L, "Все", false),
                new DropdownOption(2L, "Неутвержденные", false),
                new DropdownOption(3L, "Устаревшие", false),
                new DropdownOption(4L, "Отсутствующие", false),
                new DropdownOption(5L, "Новые", false),
                new DropdownOption(6L, "Изменившиеся", false)
            );
            model.addAttribute("launchList", ecoLaunchService.getAll());
            model.addAttribute("typeList", typeList);
        } else if (reportType == 2) { // Проверка ввода допустимых замен по закупке
            model.addAttribute("startDate", LocalDate.now().minusYears(1));
            model.addAttribute("endDate", LocalDate.now());
        } else if (reportType == 3) { // Статус введенных допустимых замен
            model.addAttribute("startDate", LocalDate.now().minusYears(1));
            model.addAttribute("endDate", LocalDate.now());
            model.addAttribute("statusList", ecoTBicStatusService.getAll());
        }
        model.addAttribute("reportType", reportType);
        return "prod/include/product/list/report";
    }
}