package ru.korundm.controller.action.prod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eco.dao.EcoLaunchService;
import eco.dao.EcoProductService;
import eco.entity.EcoLaunch;
import kotlin.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.ObjAttr;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.*;
import ru.korundm.dto.DropdownOption;
import ru.korundm.dto.product.ProductStructureItem;
import ru.korundm.entity.*;
import ru.korundm.enumeration.BomItemReplacementStatus;
import ru.korundm.enumeration.ComponentType;
import ru.korundm.exception.AlertUIException;
import ru.korundm.form.edit.*;
import ru.korundm.form.search.ProductCommentFilterForm;
import ru.korundm.form.search.ProductListFilterForm;
import ru.korundm.form.search.ProductOccurrenceFilterForm;
import ru.korundm.form.search.ProductSpecComponentFilterForm;
import ru.korundm.helper.*;
import ru.korundm.util.CommonUtil;
import ru.korundm.util.FileStorageUtil;
import ru.korundm.util.KtCommonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static ru.korundm.constant.BaseConstant.ONLY_DIGITAL_PATTERN;
import static ru.korundm.enumeration.SpecificationImportDetailType.*;

@ActionController(RequestPath.Action.Prod.PRODUCT)
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
public class ProductActionProdController {

    private static final Pattern designationNumberPattern = Pattern.compile("\\d+");

    private static final String CLEAR_ALPHABET = "[^0-9]";

    private static final String COMPONENT_SUBSTITUTE_MARK = "#";
    private static final String COMPONENT_NOT_PROCESSED_MARK = "*";

    private static final String VERSION_HYPHEN_LETTER = " -";
    private static final String VERSION_APPROVE_LETTER = "У";
    private static final String VERSION_ACCEPT_LETTER = "П";

    private static final String COMMA_SPACE_SEPARATOR = ", ";

    private static final String COMMA_SEPARATOR = ",";
    private static final String DESIGNATION_DIAPASON_SEPARATOR = "-";

    private static final String EXCEL_POSITION = "SPEDIT";
    private static final String EXCEL_QUANTITY = "QTY";
    private static final String EXCEL_PART_NUMBER = "PART_NUMBER";
    private static final String EXCEL_DESCRIPTION = "DESCRIPTION";
    private static final String EXCEL_DESIGNATION = "REF DES";

    private static final String VERSION_MAJOR = "major";
    private static final String VERSION_MINOR = "minor";
    private static final String VERSION_MODIFICATION = "modification";

    private static final String PRODUCT_OCCURRENCE_FILTER_FORM_ATTR = "productOccurrenceFilterForm";
    private static final String PRODUCT_COMMENT_FILTER_FORM_ATTR = "productCommentFilterForm";
    private static final String PRODUCT_SPEC_COMPONENT_FILTER_FORM_ATTR = "productSpecComponentFilterForm";

    private final ObjectMapper jsonMapper;
    private final BaseService baseService;
    private final ProductService productService;
    private final UserService userService;
    private final BomService bomService;
    private final BomSpecItemService bomSpecItemService;
    private final BomAttributeService bomAttributeService;
    private final BomItemService bomItemService;
    private final BomItemReplacementService bomItemReplacementService;
    private final BomItemPositionService bomItemPositionService;
    private final ComponentService componentService;
    private final ComponentCategoryService componentCategoryService;
    private final LaunchService launchService;
    private final SpecificationImportDetailService specificationImportDetailService;
    private final CompanyService companyService;
    private final ProductCommentService productCommentService;
    private final ProductDocumentationService productDocumentationService;
    private final FileStorageService fileStorageService;
    private final LaunchProductStructService launchProductStructService;
    private final LaunchProductService launchProductService;

    private final EcoLaunchService ecoLaunchService;
    private final EcoProductService ecoProductService;

    public ProductActionProdController(
        ObjectMapper jsonMapper,
        BaseService baseService,
        ProductService productService,
        UserService userService,
        BomService bomService,
        BomSpecItemService bomSpecItemService,
        BomAttributeService bomAttributeService,
        BomItemService bomItemService,
        BomItemReplacementService bomItemReplacementService,
        BomItemPositionService bomItemPositionService,
        ComponentService componentService,
        ComponentCategoryService componentCategoryService,
        LaunchService launchService,
        SpecificationImportDetailService specificationImportDetailService,
        CompanyService companyService,
        ProductCommentService productCommentService,
        ProductDocumentationService productDocumentationService,
        FileStorageService fileStorageService,
        LaunchProductStructService launchProductStructService,
        LaunchProductService launchProductService,
        EcoLaunchService ecoLaunchService,
        EcoProductService ecoProductService
    ) {
        this.jsonMapper = jsonMapper;
        this.baseService = baseService;
        this.productService = productService;
        this.userService = userService;
        this.bomService = bomService;
        this.bomSpecItemService = bomSpecItemService;
        this.bomAttributeService = bomAttributeService;
        this.bomItemService = bomItemService;
        this.bomItemReplacementService = bomItemReplacementService;
        this.bomItemPositionService = bomItemPositionService;
        this.componentService = componentService;
        this.componentCategoryService = componentCategoryService;
        this.launchService = launchService;
        this.specificationImportDetailService = specificationImportDetailService;
        this.companyService = companyService;
        this.productCommentService = productCommentService;
        this.productDocumentationService = productDocumentationService;
        this.fileStorageService = fileStorageService;
        this.launchProductStructService = launchProductStructService;
        this.launchProductService = launchProductService;
        this.ecoLaunchService = ecoLaunchService;
        this.ecoProductService = ecoProductService;
    }

    @GetMapping("/list/load")
    public TabrOut<?> list_load(
        HttpServletRequest request,
        String filterData
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id;
            String conditionalName; // условное наименование
            String techSpecName; // наименование по ТС
            String type; // краткая техническая характеристика
            String decimalNumber; // ТУ изделия
            String letter; // литера
            LocalDateTime archiveDate; // время отправки в архив
            String position; // позиция
            String lead; // ведущий
            String classificationGroup; // классификационная группа
            String comment; // комментарий
        }
        TabrIn input = new TabrIn(request);
        ProductListFilterForm form = jsonMapper.readValue(filterData, ProductListFilterForm.class);
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), productService.getCountByForm(form));
        List<TableItemOut> itemOutList = productService.getByTableDataIn(input, form).stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.conditionalName = item.getConditionalName();
            itemOut.techSpecName = item.getTechSpecName();
            itemOut.type = item.getType().getName();
            itemOut.decimalNumber = item.getDecimalNumber();
            itemOut.letter = item.getLetter() == null ? null : item.getLetter().getName();
            itemOut.archiveDate = item.getArchiveDate();
            itemOut.position = item.getPosition() != null ?
                StringUtils.leftPad(String.valueOf(item.getPosition()), 6, '0') : StringUtils.EMPTY;
            itemOut.lead = item.getLead() == null ? null : item.getLead().getUserOfficialName();
            itemOut.classificationGroup = item.getClassificationGroup() == null ? null : item.getClassificationGroup().getNumber() + " " + item.getClassificationGroup().getCharacteristic();
            itemOut.comment = item.getComment();
            return itemOut;
        }).collect(Collectors.toList());
        output.setData(itemOutList);
        return output;
    }

    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditProductForm form) {
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) baseService.exec(em -> {
            Long formProductId = form.getId();
            Product product = formProductId == null ? new Product() : productService.read(formProductId);
            product.setVer(form.getLockVersion());
            product.setConditionalName(StringUtils.defaultIfBlank(form.getConditionalName(), null));
            product.setTechSpecName(StringUtils.defaultIfBlank(form.getTechSpecName(), null));
            product.setType(form.getType().getId() == null ? null : form.getType());
            product.setDecimalNumber(StringUtils.defaultIfBlank(form.getDecimalNumber(), null));
            product.setLetter(form.getLetter().getId() == null ? null : form.getLetter());
            if (form.isArchive()) {
                product.setArchiveDate(product.getArchiveDate() == null ? LocalDateTime.now() : product.getArchiveDate());
            } else {
                product.setArchiveDate(null);
            }
            product.setPosition(form.getPosition());
            product.setLead(form.getLead().getId() == null ? null : form.getLead());
            product.setClassificationGroup(form.getClassificationGroup().getId() == null ? null : form.getClassificationGroup());
            product.setComment(StringUtils.defaultIfBlank(form.getComment(), null));
            product.setSerial(form.isSerial());
            productService.save(product);
            if (formProductId == null) {
                // Добавляем начальную версию
                var version = new Bom();
                version.setProduct(product);
                version.setDescriptor(bomService.maxDescriptor() + 1);
                version.setCreateDate(LocalDate.now());
                version.setMajor(1);
                bomService.save(version);
                response.putAttribute(ObjAttr.ID, product.getId());
            }
            return Unit.INSTANCE;
        });
        return response;
    }

    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable long id) {
        Product product = productService.read(id);
        if (CollectionUtils.isNotEmpty(product.getBomList())) {
            throw new AlertUIException("Невозможно удалить изделие, у которого есть закупочная спецификация");
        } else if (CollectionUtils.isNotEmpty(product.getBomSpecItemList())) {
            throw new AlertUIException("Невозможно удалить изделие, входящее в состав других изделий");
        } else {
            productService.deleteById(id);
        }
    }

    // Добавление изменения версии
    @GetMapping("/detail/structure/edit-version")
    public Long detail_structure_editVersion(
        Long productId,
        String type,
        boolean isReplacementStatusCopy
    ) {
        return generateProductVersion(productId, type, isReplacementStatusCopy);
    }

    // Удаление версии изделия
    @DeleteMapping("/detail/structure/delete-version/{id}")
    public void detail_structure_deleteVersion(@PathVariable long id) {
        if (id == 0) {
            throw new AlertUIException("Отсутствует версия для удаления");
        } else {
            Bom bom = bomService.read(id);
            if (bom == null) {
                throw new AlertUIException("Версия была удалена");
            } else if (bom.getBomAttributeList().stream().anyMatch(attribute -> attribute.getApproveDate() != null)) {
                throw new AlertUIException("Невозможно удалить версию спецификации, которая утверждена к запуску");
            } else {
                bomService.delete(bom);
            }
        }
    }

    // Проверка и получение изготовителя состава для версии исходя из запуска
    @GetMapping("/detail/structure/info/check-producer-by-launch")
    public Long detail_structure_info_checkProducerByLaunch(
        Long bomId,
        Long launchId
    ) {
        BomAttribute bomAttribute = bomAttributeService.getByLaunchIdAndBomId(launchId, bomId);
        Company producer = bomAttribute == null ? null : bomAttribute.getStructProducer();
        return producer == null ? null : producer.getId();
    }

    // Проверка состояния утверждения производителя к запуску
    @GetMapping("/detail/structure/info/check-version-producer")
    public boolean detail_structure_info_checkVersionProducer(
        Long bomId,
        Long producerId,
        Long launchId
    ) {
        boolean state = false;
        BomAttribute bomAttribute = bomAttributeService.getByLaunchIdAndBomId(launchId, bomId);
        Company producer = bomAttribute == null ? null : bomAttribute.getStructProducer();
        if (producer != null) state = Objects.equals(producer.getId(), producerId);
        return state;
    }

    // Утверждение изготовителя к запуску
    @PostMapping("/detail/structure/info/approve-producer")
    public void detail_structure_info_approveProducer(
        HttpSession session,
        Long bomId,
        Long producerId,
        Long launchId,
        boolean state
    ) {
        if (producerId == null) {
            throw new AlertUIException("Укажите изготовителя для утверждения");
        }
        Bom bom = bomService.read(bomId);
        if (bom == null) {
            throw new AlertUIException("Версия изделия была удалена");
        }
        Company producer = companyService.read(producerId);
        if (producer == null) {
            throw new AlertUIException("Изготовитель был удален");
        }
        Launch launch = launchService.read(launchId);
        if (launch == null) {
            throw new AlertUIException("Запуск не существует");
        }
        BomAttribute bomAttribute = bomAttributeService.getByLaunchIdAndBomId(launchId, bomId);
        if (bomAttribute == null) {
            bomAttribute = new BomAttribute();
            bomAttribute.setBom(bom);
            bomAttribute.setLaunch(launch);
        }
        User user = KtCommonUtil.INSTANCE.getUser(session);
        if (state) {
            bomAttribute.setStructProducer(null);
            bomAttribute.setStructProducerDate(null);
            bomAttribute.setStructProducerUser(null);
        } else {
            bomAttribute.setStructProducer(producer);
            bomAttribute.setStructProducerDate(LocalDate.now());
            bomAttribute.setStructProducerUser(user);
        }
        bomAttributeService.save(bomAttribute);
    }

    // Загрузка древа состава изделия
    @GetMapping("/detail/structure/info/list-load")
    public List<ProductStructureItem> detail_structure_info_listLoad(
        Long bomId
    ) {
        List<ProductStructureItem> structureItemList = new ArrayList<>();
        buildProductStructure(structureItemList, bomId, true);
        return structureItemList;
    }

    // Окно загрузки списка изделий в окне добавления в состав
    @GetMapping("/detail/structure/info/list-add/list-load")
    public TabrOut<?> detail_structure_info_listAdd_listLoad(
        HttpServletRequest request,
        String filterData,
        long bomId
    ) {
        @Getter
        class Item {
            Long id;
            String conditionalName; // условное наименование
            String comment; // комментарий
        }
        var input = new TabrIn(request);
        var form = KtCommonUtil.INSTANCE.readDynamic(jsonMapper, filterData);
        return TabrOut.Companion.instance(input, productService.findStructTableData(input, bomId, form), it -> {
            Item item = new Item();
            item.id = it.getId();
            item.conditionalName = it.getConditionalName();
            item.comment = it.getComment();
            return item;
        });
    }

    // Сохранение выбранного изделия в окне добавления изделия в состав
    @PostMapping("/detail/structure/info/list-add/save")
    public long detail_structure_info_listAdd_save(
        Long bomId,
        Long productId
    ) {
        var id = new AtomicLong(0L);
        baseService.exec(em -> {
            Product product = productService.read(productId);
            Bom bom = bomService.read(bomId);
            if (product == null) throw new AlertUIException("Редактируемое изделие было удалено");
            if (bom == null) throw new AlertUIException("Редактируемая версия изделия была удалена");
            if (Objects.equals(productId, bom.getProduct().getId())) throw new AlertUIException("Изделие не может входить в свой собственный состав");
            if (bomSpecItemService.existsByBomIdAndProductId(bomId, productId)) throw new AlertUIException("Изделие уже добавлено в состав");

            BomSpecItem item = new BomSpecItem();
            item.setBom(bom);
            item.setProduct(product);
            item.setQuantity(1);
            bomSpecItemService.save(item);

            launchProductService.getAllByVersionIdAndApproveDateIsNull(bom.getId()).forEach(it -> {
                var lps = new LaunchProductStruct();
                lps.setLaunchProduct(it);
                lps.setProduct(product);
                lps.setAmount(1);
                launchProductStructService.save(lps);
            });

            id.set(item.getId());
            return Unit.INSTANCE;
        });
        return id.get();
    }

    // Метод сохранения изделия состава при его редактировании
    @PostMapping("/detail/structure/info/list-edit/save")
    public ValidatorResponse detail_structure_info_listEdit_save(
        EditProductStructureItemForm form
    ) {
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) baseService.exec(em -> {
            var bomSpecItem = form.getId() == null ? null : bomSpecItemService.read(form.getId());
            if (bomSpecItem == null) throw new AlertUIException("Запись была удалена из состава");
            bomSpecItem.setLockVersion(form.getLockVersion());
            boolean isNotApprovedOrAccepted = bomSpecItem.getBom().getBomAttributeList().stream()
                .anyMatch(attribute -> attribute.getAcceptDate() == null && attribute.getApproveDate() == null);
            if (isNotApprovedOrAccepted) bomSpecItem.setQuantity(form.getQuantity() == null ? 1 : form.getQuantity());
            bomSpecItem.setProducer(form.getProducer().getId() == null ? null : form.getProducer());
            bomSpecItemService.save(bomSpecItem);
            launchProductStructService.getAllByVersionIdAndProductId(bomSpecItem.getBom().getId(), bomSpecItem.getProduct().getId()).forEach(it -> it.setAmount(bomSpecItem.getQuantity()));
            return Unit.INSTANCE;
        });
        return response;
    }

    // Удаление изделия из состава
    @DeleteMapping("/detail/structure/info/list-delete/{id}")
    public void detail_structure_info_listDelete(@PathVariable long id) {
        baseService.exec(em -> {
            var bsi = bomSpecItemService.read(id);
            var bom = bsi == null ? null : bsi.getBom();
            var product = bsi == null ? null : bsi.getProduct();
            if (bom != null && product != null) launchProductStructService.deleteAllByVersionIdAndProductId(bom.getId(), product.getId());
            bomSpecItemService.deleteById(id);
            return Unit.INSTANCE;
        });
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

    // Метод рекурсивного построения списка состава
    private void buildProductStructure(final List<ProductStructureItem> structureList, Long bomId, final boolean main) {
        if (structureList == null || bomId == null) {
            return;
        }
        List<BomSpecItem> bomSpecItemList = bomSpecItemService.getAllByBomId(bomId);
        bomSpecItemList.forEach(specItem -> {
            ProductStructureItem structureItem = new ProductStructureItem();
            Product product = specItem.getProduct();
            structureItem.setId(specItem.getId());
            structureItem.setProductId(product.getId());
            structureItem.setConditionalName(product.getConditionalName());
            structureItem.setQuantity(specItem.getQuantity());
            structureItem.setProducer(specItem.getProducer() == null ? null : specItem.getProducer().getName());
            structureItem.setMain(main);
            //
            List<Bom> bomList = bomService.getAllSortedVersionByProductId(specItem.getProduct().getId());
            if (!bomList.isEmpty()) {
                List<ProductStructureItem> subStructureList = new ArrayList<>();
                // Согласно легаси логике нужно взять последний bom в списке
                buildProductStructure(subStructureList, bomList.get(bomList.size() - 1).getId(), false);
                if (!subStructureList.isEmpty()) {
                    structureItem.setChildList(subStructureList);
                }
            }
            structureList.add(structureItem);
        });
    }

    // Загрузка данных по входимости
    @GetMapping("/detail/occurrence/load")
    public TabrOut<?> detail_occurrence_load(
        HttpServletRequest request,
        ModelMap model,
        String filterForm,
        Long productId
    ) throws IOException {
        ProductOccurrenceFilterForm form = jsonMapper.readValue(filterForm, ProductOccurrenceFilterForm.class);
        model.addAttribute(PRODUCT_OCCURRENCE_FILTER_FORM_ATTR, form);
        @Getter
        class TableItemOut {
            long id; // идентификатор версии
            long productId; // идентификатор изделия
            String lead; // ведущий
            String conditionalName; // условное наименование изделия
            String version; // версия
            long identifier; // идентификатор
            String approvedText; // утвержденные запуски
            String acceptedText; // принятые запуски
        }
        TabrIn input = new TabrIn(request);
        // Получаем отфильтрованые версии
        List<Bom> bomList = bomService.getForProductOccurrence(form, productId);
        List<TableItemOut> itemOutTotalList = new ArrayList<>();
        bomList.forEach(bom -> {
            TableItemOut itemOut = new TableItemOut();
            //
            Product product = bom.getProduct();
            itemOut.id = bom.getId();
            itemOut.productId = product.getId();
            itemOut.lead = product.getLead() == null ? null : product.getLead().getUserOfficialName();
            itemOut.conditionalName = product.getConditionalName();
            itemOut.version = bom.getVersion();
            itemOut.identifier = bom.getDescriptor();
            //
            List<BomAttribute> bomAttributeList = bom.getBomAttributeList();
            // Сортировка по запускам от позднего до раннего
            bomAttributeList.sort(
                Comparator
                    .comparing(BomAttribute::getLaunch, Comparator.comparing(Launch::getYear, Comparator.reverseOrder()))
                    .thenComparing(BomAttribute::getLaunch, Comparator.comparing(Launch::getNumber, Comparator.reverseOrder()))
            );
            //
            List<String> approvedText = new ArrayList<>();
            List<String> acceptedText = new ArrayList<>();
            for (var bomAttribute : bomAttributeList) {
                if (bomAttribute.getApproveDate() != null) {
                    approvedText.add(bomAttribute.getLaunch().getNumberInYear());
                }
                if (bomAttribute.getAcceptDate() != null) {
                    acceptedText.add(bomAttribute.getLaunch().getNumberInYear());
                }
            }
            itemOut.approvedText = String.join(COMMA_SPACE_SEPARATOR, approvedText);
            itemOut.acceptedText = String.join(COMMA_SPACE_SEPARATOR, acceptedText);
            itemOutTotalList.add(itemOut);
        });
        // Фильтр по тексту
        itemOutTotalList.removeIf(item ->
            StringUtils.isNotBlank(form.getApproveSearchText()) &&
                !StringUtils.contains(item.getApprovedText(), form.getApproveSearchText())
        );
        itemOutTotalList.removeIf(item ->
            StringUtils.isNotBlank(form.getAcceptSearchText()) &&
                !StringUtils.contains(item.getAcceptedText(), form.getAcceptSearchText())
        );
        // Сортировка
        if (CollectionUtils.isNotEmpty(input.getSorters()) && CollectionUtils.isNotEmpty(itemOutTotalList)) {
            boolean isAsc = ASC.equals(input.getSorters().get(0).getDir());
            Comparator<String> orderStringComparator = Comparator.nullsLast(isAsc ? Comparator.<String>naturalOrder() : Comparator.<String>reverseOrder());
            Comparator<Long> orderLongComparator = Comparator.nullsLast(isAsc ? Comparator.<Long>naturalOrder() : Comparator.<Long>reverseOrder());
            Comparator<TableItemOut> comparator = Comparator.comparing(TableItemOut::getLead, orderStringComparator);
            switch (input.getSorters().get(0).getField()) {
                case "lead":
                    break;
                case "conditionalName":
                    comparator = Comparator.comparing(TableItemOut::getConditionalName, orderStringComparator);
                    break;
                case "version":
                    comparator = Comparator.comparing(TableItemOut::getVersion, orderStringComparator);
                    break;
                case "identifier":
                    comparator = Comparator.comparing(TableItemOut::getIdentifier, orderLongComparator);
                    break;
                case "approvedText":
                    comparator = Comparator.comparing(TableItemOut::getApprovedText, orderStringComparator);
                    break;
                case "acceptedText":
                    comparator = Comparator.comparing(TableItemOut::getAcceptedText, orderStringComparator);
                    break;
            }
            itemOutTotalList.sort(comparator);
        }
        // Вытаскиваем кусок данных для страницы
        int pageSize = input.getSize();
        int itemOutTotalListSize = itemOutTotalList.size();
        List<TableItemOut> itemOutList = new ArrayList<>(pageSize);
        for (int from = input.getStart(), to = from + pageSize; from < to; from++) {
            if (from >= itemOutTotalListSize) continue;
            itemOutList.add(itemOutTotalList.get(from));
        }
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setData(itemOutList);
        output.setCurrentPage(input.getPage());
        output.setLastPage(pageSize, itemOutTotalListSize);
        return output;
    }

    // Загрузка данных по комментариям
    @GetMapping("/detail/comment/load")
    public List<?> detail_comment_load(
        HttpSession session,
        ModelMap model,
        HttpServletRequest request,
        String filterForm,
        Long productId
    ) throws IOException {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String createdBy; // кто создал комментарий Фамилия И.О.
            Long createdById; // идентификатор автора
            LocalDateTime createDate; // дата и время создания комментария
            String comment; // комментарий
            boolean isAllowedEdit; // разрешено редактировать комментарий или нет
        }
        ProductCommentFilterForm form = jsonMapper.readValue(filterForm, ProductCommentFilterForm.class);
        model.addAttribute(PRODUCT_COMMENT_FILTER_FORM_ATTR, form);
        User user = KtCommonUtil.INSTANCE.getUser(session);
        List<TableItemOut> itemOutList = productCommentService.getAllByProduct(productId).stream().map(productComment -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = productComment.getId();
            itemOut.createdBy = productComment.getUser().getUserOfficialName();
            itemOut.createDate = productComment.getCreationDate();
            itemOut.createdById = productComment.getUser().getId();
            itemOut.comment = productComment.getComment();
            itemOut.isAllowedEdit = Objects.equals(user, productComment.getUser());
            return itemOut;
        }).sorted(Comparator.comparing(TableItemOut::getCreateDate).reversed()).collect(Collectors.toList());

        // Сортировка
        TabrIn input = new TabrIn(request);
        if (CollectionUtils.isNotEmpty(input.getSorters()) && CollectionUtils.isNotEmpty(itemOutList)) {
            boolean isAsc = ASC.equals(input.getSorters().get(0).getDir());
            Comparator<String> orderStringComparator = Comparator.nullsLast(isAsc ? Comparator.<String>naturalOrder() : Comparator.<String>reverseOrder());
            Comparator<LocalDateTime> orderLocalDateTimeComparator = isAsc ? Comparator.naturalOrder() : Comparator.reverseOrder();
            Comparator<TableItemOut> comparator = Comparator.comparing(TableItemOut::getCreatedBy, orderStringComparator);
            switch (input.getSorters().get(0).getField()) {
                case "createdBy":
                    break;
                case "createDate":
                    comparator = Comparator.comparing(TableItemOut::getCreateDate, orderLocalDateTimeComparator);
                    break;
                case "comment":
                    comparator = Comparator.comparing(TableItemOut::getComment, orderStringComparator);
                    break;
            }
            itemOutList.sort(comparator);
        }

        // Фильтр по дате создания c
        if (form.getCreateDateFrom() != null) {
            itemOutList.removeIf(item -> item.getCreateDate().isBefore(form.getCreateDateFrom().atStartOfDay()));
        }
        // Фильтр по дате создания по
        if (form.getCreateDateTo() != null) {
            itemOutList.removeIf(item -> item.getCreateDate().isAfter(form.getCreateDateTo().atTime(LocalTime.MAX)));
        }

        // Фильтр по тексту поля "комментарий"
        itemOutList.removeIf(item ->
            StringUtils.isNotBlank(form.getComment()) &&
                !StringUtils.contains(item.getComment().toLowerCase(), form.getComment().toLowerCase())
        );

        // Фильтр по автору комментария
        if (form.getCreatedBy() != null) {
            User createdBy = userService.read(form.getCreatedBy());
            itemOutList.removeIf(item -> form.getCreatedBy() != null && !Objects.equals(createdBy, userService.read(item.getCreatedById())));
        }
        return itemOutList;
    }

    @PostMapping("/detail/comment/edit/save")
    public ValidatorResponse detail_comment_edit_save(
        EditProductCommentForm form
    ) {
        Long formId = form.getId();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            ProductComment productComment = formId == null ? new ProductComment() : productCommentService.read(formId);
            productComment.setComment(form.getComment());
            productComment.setUser(form.getCreatedBy());
            productComment.setCreationDate(form.getCreatedDate());
            productComment.setProduct(form.getProduct());
            productCommentService.save(productComment);
            if (formId == null) {
                response.putAttribute("addedCommentId", productComment.getId());
            }
        }
        return response;
    }

    // Удаление пользовательского комментария
    @DeleteMapping("/detail/comment/delete/{id}")
    public void detail_comment_delete(
        HttpSession session,
        @PathVariable long id
    ) {
        User user = KtCommonUtil.INSTANCE.getUser(session);
        ProductComment productComment = productCommentService.read(id);
        if (Objects.equals(user, productComment.getUser())) {
            productCommentService.delete(productComment);
        } else {
            throw new AlertUIException("Удалять можно только созданный вами комментарий");
        }
    }

    // Добавление изменения версии
    @GetMapping("/detail/specification/edit-version")
    public Long detail_specification_editVersion(
        Long productId,
        String type,
        boolean isReplacementStatusCopy
    ) {
        return generateProductVersion(productId, type, isReplacementStatusCopy);
    }

    // Удаление версии изделия
    @DeleteMapping("/detail/specification/delete-version/{id}")
    public void detail_specification_deleteVersion(
        @PathVariable long id
    ) {
        if (id == 0) {
            throw new AlertUIException("Отсутствует версия для удаления");
        } else {
            Bom bom = bomService.read(id);
            if (bom == null) {
                throw new AlertUIException("Версия была удалена");
            } else if (bom.getBomAttributeList().stream().anyMatch(el -> el.getApproveDate() != null || el.getAcceptDate() != null)) {
                throw new AlertUIException("Невозможно удалить версию спецификации, которая утверждена/принята к запуску");
            } else if (bom.getProductionName() != null) {
                throw new AlertUIException("Невозможно редактировать закупочную спецификацию, если ей соответствует рабочая спецификация");
            } else {
                bomService.delete(bom);
            }
        }
    }

    // Метод редактирования версии изделия
    private Long generateProductVersion(Long productId, String type, boolean isReplacementStatusCopy) {
        Product product = productService.read(productId);
        if (product == null) {
            throw new AlertUIException("Изделие было удалено");
        }
        List<Bom> bomList = bomService.getAllSortedVersionDescByProductId(productId);
        Bom lastBom = bomList.isEmpty() ? null : bomList.get(0);
        if (lastBom != null) {
            List<BomAttribute> lastBomAttributeList = lastBom.getBomAttributeList();
            if (lastBomAttributeList.isEmpty() || lastBomAttributeList.stream().allMatch(attr -> attr.getApproveDate() == null)) {
                throw new AlertUIException("Невозможно добавить изменение версии спецификации, пока предыдущая версия не утверждена / не принята к запуску");
            }
        }
        Bom bom = new Bom();
        bom.setProduct(product);
        bom.setDescriptor(bomService.maxDescriptor() + 1);
        bom.setCreateDate(LocalDate.now());
        if (VERSION_MAJOR.equals(type)) {
            if (lastBom == null) {
                bom.setMajor(1);
            } else {
                bom.setMajor(lastBom.getMajor() + 1);
            }
            bomService.save(bom);
        } else if (VERSION_MINOR.equals(type)) {
            if (lastBom == null) {
                throw new AlertUIException("Отсутствуют версии для создания изменения");
            } else {
                bom.setMajor(lastBom.getMajor());
                bom.setMinor(lastBom.getMinor() + 1);
            }
            bomService.save(bom);
        } else if (VERSION_MODIFICATION.equals(type)) {
            if (lastBom == null) {
                throw new AlertUIException("Отсутствуют версии для создания модификации");
            } else {
                bom.setMajor(lastBom.getMajor());
                bom.setMinor(lastBom.getMinor());
                bom.setModification(lastBom.getModification() + 1);
            }
            bomService.save(bom);
        }
        if (bom.getId() != null && lastBom != null) {
            copyStructure(bom, lastBom.getId());
            copySpecification(bom, lastBom.getId(), isReplacementStatusCopy);
        }
        return bom.getId();
    }

    // Метод проверки состояния по запуску
    @GetMapping("/detail/specification/info/check-state-by-launch")
    public Object detail_specification_info_checkStateByLaunch(
        Long bomId,
        Long launchId
    ) {
        @Getter
        class ItemOut {
            boolean bomApproved;
            boolean bomAccept;
        }
        ItemOut itemOut = new ItemOut();
        if (launchId != null) {
            BomAttribute attribute = bomAttributeService.getByLaunchIdAndBomId(launchId, bomId);
            if (attribute != null) {
                itemOut.bomApproved = attribute.getApproveDate() != null;
                itemOut.bomAccept = attribute.getAcceptDate() != null;
            }
        }
        return itemOut;
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

    // Сохранение вхождения спецификации при редактировании
    @PostMapping("/detail/specification/info/edit-spec-item/save")
    public ValidatorResponse detail_specification_info_editSpecItem_save(
        EditProductSpecItemForm form
    ) {
        BomItem bomItem = bomItemService.read(form.getId());
        if (bomItem == null) {
            throw new AlertUIException("Вхождение спецификации было удалено");
        }
        ValidatorResponse response = new ValidatorResponse(form);
        int positionCount = 0;
        List<BomItemPosition> bomItemPositionList = bomItem.getBomItemPositionList();
        if (form.isUnit() && !form.isApprovedOrAccepted()) { // Штучная категория
            bomItemPositionList.clear();
            List<EditProductSpecItemForm.Position> formPositionList = form.getPositionList();
            for (var formPosition: formPositionList) {
                String letter = formPosition.getLetter();
                String[] designationArray = StringUtils.defaultIfBlank(formPosition.getValue(), StringUtils.EMPTY).split(COMMA_SEPARATOR);
                for (var diapason: designationArray) {
                    if (diapason.contains(DESIGNATION_DIAPASON_SEPARATOR)) {
                        String[] diapasonPair = diapason.split(DESIGNATION_DIAPASON_SEPARATOR);
                        int first = Integer.parseInt(diapasonPair[0]);
                        int second = Integer.parseInt(diapasonPair[1]);
                        for (int i = first; i <= second; i++) {
                            BomItemPosition bomItemPosition = new BomItemPosition();
                            bomItemPosition.setBomItem(bomItem);
                            bomItemPosition.setDesignation(letter + i);
                            bomItemPositionList.add(bomItemPosition);
                            positionCount++;
                        }
                    } else {
                        BomItemPosition bomItemPosition = new BomItemPosition();
                        bomItemPosition.setBomItem(bomItem);
                        bomItemPosition.setDesignation(letter + diapason);
                        bomItemPositionList.add(bomItemPosition);
                        positionCount++;
                    }
                }
            }
        }
        if (!form.isApprovedOrAccepted() && form.getQuantity() != null && form.getQuantity() > 0 && !form.getPositionList().isEmpty() && positionCount > form.getQuantity()) {
            response.putError("quantity", "validator.editProductSpecItemForm.quantityDifferent");
        }
        if (response.isValid()) {
            bomItem.setGivenRawMaterial(form.isGivenRawMaterial());
            bomItem.setProducerList(companyService.getAllById(form.getProducerIdList()));
            if (!form.isApprovedOrAccepted() && form.getQuantity() != null) bomItem.setQuantity(form.getQuantity());
            bomItemService.save(bomItem);
        }
        return response;
    }

    // Установка версии изделия в задел
    @GetMapping("/detail/specification/info/version-stock")
    public boolean detail_specification_info_versionStock(
        Long bomId,
        boolean state
    ) {
        Bom bom = bomService.read(bomId);
        boolean isStock = bom.getStock();
        if (state == isStock) {
            boolean isAcceptedOrApproved = bom.getBomAttributeList().stream()
                .anyMatch(attribute -> attribute.getAcceptDate() != null || attribute.getApproveDate() != null);
            if (isAcceptedOrApproved) {
                throw new AlertUIException("Утвержденные (принятые) спецификации можно редактировать только в части замен и давальческого сырья");
            }
            bom.setStock(!isStock);
            bomService.save(bom);
            state = !isStock;
        } else {
            state = isStock;
        }
        return state;
    }

    @Getter
    private static class SpecListItem {
        private long id;
        private String category; // категория
        private Long categoryId; // id категории
        private String position; // позиция
        private String positionReplacement; // позиция замены
        private String name; // наименование
        private String nameReplacement; // наименование замены
        private String description; // описание
        private String descriptionReplacement; // описание замены
        private double quantity; // количество
        private String designation; // позиционные обозначения
        private String firmware; // прошивка
        private boolean givenRawMaterial; // давальческое сырье
        private String producers; // изготовители
        private boolean isApproved;
    }

    // Загрузка основной таблицы спецификации
    @GetMapping("/detail/specification/info/list-spec-load")
    public List<?> detail_specification_info_listSpecLoad(
        HttpServletRequest request,
        Long bomId,
        String filterData
    ) {
        DynamicObject form = KtCommonUtil.INSTANCE.readDynamic(jsonMapper, filterData);
        TabrIn input = new TabrIn(request);
        return generateSpecListData(input.getSorters().isEmpty() ? null : input.getSorters().get(0), form, bomId);
    }

    // Выгрузка спецификации в excel
    @GetMapping("/detail/specification/info/spec-download")
    public void detail_specification_info_specDownload(
        HttpServletResponse response,
        HttpServletRequest request,
        Long bomId,
        String filterData
    ) {
        DynamicObject form = KtCommonUtil.INSTANCE.readDynamic(jsonMapper, filterData);
        TabrIn input = new TabrIn(request);
        var data = generateSpecListData(input.getSorters().isEmpty() ? null : input.getSorters().get(0), form, bomId);
        var wb = new XSSFWorkbook();
        var createSheet = new Object() {
            public void exec(String name, boolean isMain) {
                var sheet = wb.createSheet(name);
                var mainRow = sheet.createRow(0);
                mainRow.createCell(0).setCellValue("Позиция");
                mainRow.createCell(1).setCellValue("Наименование");
                mainRow.createCell(2).setCellValue("Описание");
                mainRow.createCell(3).setCellValue("Количество");
                mainRow.createCell(4).setCellValue("Поз.обоз.");
                mainRow.createCell(5).setCellValue("Прошивка");
                mainRow.createCell(6).setCellValue("Изготовители");
                for (int i = 0; i < data.size(); i++) {
                    var item = data.get(i);
                    var row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(isMain ? item.position : item.positionReplacement);
                    row.createCell(1).setCellValue(isMain ? item.name : item.nameReplacement);
                    row.createCell(2).setCellValue(isMain ? item.description : item.descriptionReplacement);
                    row.createCell(3).setCellValue(item.quantity);
                    row.createCell(4).setCellValue(item.designation);
                    row.createCell(5).setCellValue(item.firmware);
                    row.createCell(6).setCellValue(item.producers);
                }
            }
        };
        createSheet.exec("Спецификация", true);
        createSheet.exec("Спецификация с заменами", false);
        KtCommonUtil.INSTANCE.attachDocumentXLSX(response, wb, "Спецификация");
    }

    private List<SpecListItem> generateSpecListData(TabrSorter sorter, DynamicObject form, long bomId) {
        List<SpecListItem> resList = bomItemService.getAllByBomId(bomId).stream().map(bomItem -> {
            var item = new SpecListItem();
            Component component = bomItem.getComponent();
            var posList = bomItem.getBomItemPositionList();
            item.id = bomItem.getId();
            item.category = component.getCategory().getName();
            item.categoryId = component.getCategory().getId();
            item.position = component.getFormattedPosition();
            item.name = component.getName();
            item.description = component.getDescription();
            item.quantity = bomItem.getQuantity();
            // Поиск компонента замены
            var replacement = findComponentReplacement(bomItem);
            if (replacement != null) {
                var repComp = replacement.getComponent();
                item.positionReplacement = repComp.getFormattedPosition();
                item.nameReplacement = repComp.getName();
                item.descriptionReplacement = repComp.getDescription();
            }
            // Получение обозначений
            List<String> designationList = new ArrayList<>();
            parseDesignationData(posList).forEach((letter, value) ->
                Arrays.stream(StringUtils.defaultIfBlank(value, StringUtils.EMPTY).split(COMMA_SEPARATOR)).forEach(diapason -> {
                    if (diapason.contains(DESIGNATION_DIAPASON_SEPARATOR)) {
                        String[] diapasonPair = diapason.split(DESIGNATION_DIAPASON_SEPARATOR);
                        designationList.add(letter + diapasonPair[0] + DESIGNATION_DIAPASON_SEPARATOR + letter + diapasonPair[1]);
                    } else {
                        designationList.add(letter + diapason);
                    }
                })
            );
            item.designation = String.join(COMMA_SEPARATOR, designationList);
            item.firmware = posList.stream().map(BomItemPosition::getFirmware).filter(Objects::nonNull).collect(Collectors.joining("; "));
            item.givenRawMaterial = bomItem.isGivenRawMaterial();
            item.producers = bomItem.getProducerList().stream().map(Company::getName).collect(Collectors.joining(COMMA_SPACE_SEPARATOR));
            item.isApproved = component.isApproved();
            return item;
        }).collect(Collectors.toList());
        // Фильтр
        Stream<SpecListItem> itemOutListStream = resList.stream();
        String formName = form.string(ObjAttr.NAME);
        if (StringUtils.isNotBlank(formName)) {
            itemOutListStream = itemOutListStream.filter(item -> StringUtils.containsIgnoreCase(item.getName(), formName));
        }
        String formPosition = form.string(ObjAttr.POSITION);
        if (StringUtils.isNotBlank(formPosition)) {
            itemOutListStream = itemOutListStream
                .filter(item -> item.getPosition() != null)
                .filter(item -> StringUtils.contains(item.getPosition(), formPosition));
        }
        String formDescription = form.string(ObjAttr.DESCRIPTION);
        if (StringUtils.isNotBlank(formDescription)) {
            itemOutListStream = itemOutListStream.filter(item -> StringUtils.containsIgnoreCase(item.getDescription(), formDescription));
        }
        List<Long> categoryIdList = form.listLong(ObjAttr.CATEGORY_ID_LIST);
        if (CollectionUtils.isNotEmpty(categoryIdList)) {
            itemOutListStream = itemOutListStream.filter(item -> categoryIdList.contains(item.categoryId));
        }
        resList = itemOutListStream.collect(Collectors.toList());
        // Сортировка
        if (CollectionUtils.isNotEmpty(resList)) {
            // Всегда сортируем по группе
            Comparator<SpecListItem> comparator = Comparator.comparing(SpecListItem::getCategory);
            if (sorter != null) {
                boolean isAsc = ASC.equals(sorter.getDir());
                Comparator<String> orderStringComparator = Comparator.nullsLast(isAsc ? Comparator.<String>naturalOrder() : Comparator.<String>reverseOrder());
                Comparator<Double> orderDoubleComparator = Comparator.nullsLast(isAsc ? Comparator.<Double>naturalOrder() : Comparator.<Double>reverseOrder());
                switch (sorter.getField()) {
                    case ObjAttr.POSITION:
                        comparator = comparator.thenComparing(SpecListItem::getPosition, orderStringComparator);
                        break;
                    case ObjAttr.NAME:
                        comparator = comparator.thenComparing(SpecListItem::getName, orderStringComparator);
                        break;
                    case ObjAttr.DESCRIPTION:
                        comparator = comparator.thenComparing(SpecListItem::getDescription, orderStringComparator);
                        break;
                    case ObjAttr.QUANTITY:
                        comparator = comparator.thenComparing(SpecListItem::getQuantity, orderDoubleComparator);
                        break;
                    case ObjAttr.DESIGNATION:
                        comparator = comparator.thenComparing(SpecListItem::getDesignation, orderStringComparator);
                        break;
                    case ObjAttr.FIRMWARE:
                        comparator = comparator.thenComparing(SpecListItem::getFirmware, orderStringComparator);
                        break;
                }
            }
            resList.sort(comparator);
        }
        return resList;
    }

    @GetMapping("/detail/specification/info/spec-replacement-component/list-load")
    public TabrOut<?> detail_specification_info_specReplacementComponent_listLoad(
        HttpServletRequest request,
        String filterData,
        Long bomItemId
    ) {
        @Getter
        class Item {
            long id;
            String mark; // метка
            String name; // наименование
            String substituteComponent; // инфо о заместителе
            String producer; // производитель
            String position; // позиция
            String category; // категория
            String description; // описание
        }
        var bi = bomItemService.read(bomItemId);
        var form = KtCommonUtil.INSTANCE.readDynamic(jsonMapper, filterData);
        form.set(ObjAttr.SHOW_DESIGN, bi.getComponent().isApproved());
        TabrIn input = new TabrIn(request);
        return TabrOut.Companion.instance(input, componentService.findTableSpecCompReplacementData(input, form), it -> {
            Item item = new Item();
            item.id = it.getId();
            if (it.getSubstituteComponent() != null) {
                item.mark = COMPONENT_SUBSTITUTE_MARK;
            } else if (!it.isProcessed()) {
                item.mark = COMPONENT_NOT_PROCESSED_MARK;
            }
            item.name = it.getName();
            var substituteComponent = it.getSubstituteComponent();
            item.substituteComponent = substituteComponent == null ? null : substituteComponent.getFormattedPosition() + COMMA_SPACE_SEPARATOR + substituteComponent.getName();
            item.producer = it.getProducer() == null ? null : it.getProducer().getName();
            item.position = it.getFormattedPosition();
            item.category = it.getCategory().getName();
            item.description = it.getDescription();
            return item;
        });
    }

    // Замена компонента в спецификации на другой компонент из справочника
    @PostMapping("/detail/specification/info/spec-replacement-component/save")
    public void detail_specification_info_specReplacementComponent_save(
        Long bomId,
        Long bomItemId,
        Long replaceId
    ) {
        List<BomItem> bomItemList = bomItemService.getAllByBomId(bomId);
        BomItem currentBomItem = bomItemService.read(bomItemId);
        Component componentReplace = componentService.read(replaceId);
        List<BomItem> saveBomItemList = bomItemList.stream().filter(bomItem -> Objects.equals(bomItem, currentBomItem)).peek(bomItem -> {
            if (bomItem.getComponent().getCategory().isUnit() && componentReplace.getCategory().isUnit()) {
                bomItem.setComponent(componentReplace);
            } else {
                bomItem.setComponent(componentReplace);
                bomItem.setQuantity(1);
                bomItem.setGivenRawMaterial(Boolean.FALSE);
                bomItem.getProducerList().clear();
                bomItem.getBomItemPositionList().clear();
            }
            bomItem.getBomItemReplacementList().clear();
            BomItemReplacement bomItemReplacementMain = new BomItemReplacement();
            bomItemReplacementMain.setComponent(componentReplace);
            bomItemReplacementMain.setBomItem(bomItem);
            bomItemReplacementMain.setStatus(BomItemReplacementStatus.ALLOWED);
            bomItemReplacementMain.setReplacementDate(LocalDate.now());
            bomItemReplacementMain.setStatusDate(LocalDate.now());
            bomItem.getBomItemReplacementList().add(bomItemReplacementMain);
        }).collect(Collectors.toList());
        if (!saveBomItemList.isEmpty()) {
            bomItemService.saveAll(saveBomItemList);
        }
    }

    // Удаление позиции из основной таблицы
    @DeleteMapping("/detail/specification/info/list-spec-delete/{id}/{bomId}")
    public void detail_specification_info_listSpecDelete(
        @PathVariable Long id,
        @PathVariable Long bomId
    ) {
        if (bomAttributeService.getAllByBomId(bomId).stream().anyMatch(el -> el.getAcceptDate() != null || el.getApproveDate() != null)) {
            throw new AlertUIException("Невозможно удалить позицию, т.к. спецификация утверждена/принята к запуску");
        }
        bomItemService.deleteById(id);
    }

    @GetMapping("/detail/specification/info/spec-add-component/list-load")
    public TabrOut<?> detail_specification_info_specAddComponent_listLoad(
        ModelMap model,
        HttpServletRequest request,
        String filterForm
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id;
            String mark; // метка
            String name; // наименование
            String substituteComponent; // инфо о заместителе
            String producer; // производитель
            String position; // позиция
            String category; // категория
            String description; // описание
        }
        ProductSpecComponentFilterForm form = jsonMapper.readValue(filterForm, ProductSpecComponentFilterForm.class);
        model.addAttribute(PRODUCT_SPEC_COMPONENT_FILTER_FORM_ATTR, form);
        TabrIn input = new TabrIn(request);
        TabrResultQuery<Component> dataResultQuery = componentService.queryDataByFilterForm(input, form);
        List<TableItemOut> itemOutList = dataResultQuery.getData().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            if (item.getSubstituteComponent() != null) {
                itemOut.mark = COMPONENT_SUBSTITUTE_MARK;
            } else if (!item.isProcessed()) {
                itemOut.mark = COMPONENT_NOT_PROCESSED_MARK;
            }
            itemOut.name = item.getName();
            Component substituteComponent = item.getSubstituteComponent();
            itemOut.substituteComponent = substituteComponent == null ?
                null : substituteComponent.getFormattedPosition() + COMMA_SPACE_SEPARATOR + substituteComponent.getName();
            itemOut.producer = item.getProducer() == null ? null : item.getProducer().getName();
            itemOut.position = item.getFormattedPosition();
            itemOut.category = item.getCategory().getName();
            itemOut.description = item.getDescription();
            return itemOut;
        }).collect(Collectors.toList());
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), dataResultQuery.getCount());
        output.setData(itemOutList);
        return output;
    }

    // Сохранение компонента в таблице спецификаций
    @PostMapping("/detail/specification/info/spec-add-component/save")
    public Long detail_specification_info_specAddComponent_save(
        Long bomId,
        Long componentId
    ) {
        Bom bom = bomService.read(bomId);
        if (bom == null) {
            throw new AlertUIException("Редактируемая версия была удалена");
        }
        if (bomAttributeService.getAllByBomId(bomId).stream().anyMatch(el -> el.getAcceptDate() != null || el.getApproveDate() != null)) {
            throw new AlertUIException("Утвержденные (принятые) спецификации можно редактировать только в части замен и давальческого сырья");
        }
        Component component = componentService.read(componentId);
        if (component == null) {
            throw new AlertUIException("Выбранный компонент был удален");
        }
        component = component.getSubstituteComponent() == null ? component : component.getSubstituteComponent();
        if (
            bomItemService.existsByBomIdAndComponentId(bomId, component.getId())
                && bomItemReplacementService.existsByComponentIdAndBomId(component.getId(), bomId)
        ) {
            throw new AlertUIException("Выбранный компонент уже добавлен в спецификацию");
        }
        Component purchaseComponent = component.getPurchaseComponent();
        if (
            purchaseComponent != null
                && bomItemService.existsByBomIdAndComponentId(bomId, purchaseComponent.getId())
                && bomItemReplacementService.existsByComponentIdAndBomId(purchaseComponent.getId(), bomId)
        ) {
            throw new AlertUIException("Выбранный компонент имеет компонент закупки, который уже добавлен в спецификацию");
        }
        //
        BomItem bomItem = new BomItem();
        bomItem.setBom(bom);
        bomItem.setComponent(component);
        bomItem.setQuantity(1);
        // Для нештучной категории всегда должна быть 1 позиция
        if (!component.getCategory().isUnit()) {
            BomItemPosition bomItemPosition = new BomItemPosition();
            bomItemPosition.setBomItem(bomItem);
            bomItem.getBomItemPositionList().add(bomItemPosition);
        }
        // Основной компонент замены
        BomItemReplacement bomItemReplacementMain = new BomItemReplacement();
        bomItemReplacementMain.setComponent(component);
        bomItemReplacementMain.setBomItem(bomItem);
        bomItemReplacementMain.setStatus(BomItemReplacementStatus.ALLOWED);
        bomItemReplacementMain.setReplacementDate(LocalDate.now());
        bomItemReplacementMain.setStatusDate(LocalDate.now());
        bomItem.getBomItemReplacementList().add(bomItemReplacementMain);
        // Компонент замены по закупке
        if (purchaseComponent != null) {
            BomItemReplacement bomItemReplacementPurchase = new BomItemReplacement();
            bomItemReplacementPurchase.setComponent(purchaseComponent);
            bomItemReplacementPurchase.setBomItem(bomItem);
            bomItemReplacementPurchase.setStatus(BomItemReplacementStatus.ALLOWED);
            bomItemReplacementPurchase.setReplacementDate(LocalDate.now());
            bomItemReplacementPurchase.setStatusDate(LocalDate.now());
            bomItem.getBomItemReplacementList().add(bomItemReplacementPurchase);
        }
        bomItemService.save(bomItem);
        return bomItem.getId();
    }

    // Загрузка списка замен
    @GetMapping("/detail/specification/info/replacement/list-load")
    public List<?> detail_specification_info_replacement_listLoad(
        Long bomItemId,
        Long mainComponentId
    ) {
        @Getter
        class Item {
            long id;
            Long componentId; // компонент
            String position; // позиция
            String name; // наименование
            String producer; // производитель
            String description; // описание
            boolean kd; // КД
            String status; // статус
            boolean purchaseInProduct; // закупка в изделии (тот, что в БД)
            boolean purchase; // флаг закупки (вычисляемый)
            LocalDate replacementDate; // дата замены
            LocalDate statusDate; // дата статуса
        }
        List<Item> list = bomItemReplacementService.getAllByBomItemId(bomItemId).stream().map(it -> {
            var item = new Item();
            Component component = it.getComponent();
            item.id = it.getId();
            item.componentId = component.getId();
            item.position = component.getFormattedPosition();
            item.name = component.getName();
            item.producer = component.getProducer() != null ? component.getProducer().getName() : StringUtils.EMPTY;
            item.description = component.getDescription();
            item.kd = Objects.equals(component.getId(), mainComponentId);
            item.status = it.getStatus().getName();
            item.purchaseInProduct = it.isPurchase();
            item.replacementDate = it.getReplacementDate();
            item.statusDate = it.getStatusDate();
            return item;
        }).sorted(Comparator.comparingLong(Item::getId)).collect(Collectors.toList());
        var replacement = findComponentReplacement(bomItemService.read(bomItemId));
        if (replacement != null) {
            Long replacementId = replacement.getId();
            list.stream()
                .filter(it -> Objects.equals(it.getId(), replacementId))
                .findFirst()
                .ifPresent(it -> it.purchase = true);
        }
        return list;
    }

    // Поиск компонента замены по приоритету - purchased флаг > глобальный компонент замены по закупке > целевой компонент
    private BomItemReplacement findComponentReplacement(BomItem bomItem) {
        if (bomItem == null) return null;
        var replacementList = bomItem.getBomItemReplacementList();
        var replacement = replacementList.stream().filter(BomItemReplacement::isPurchase).findFirst().orElse(null);
        if (replacement == null) {
            replacement = replacementList.stream()
                .filter(it -> Objects.equals(it.getComponent(), bomItem.getComponent().getPurchaseComponent()))
                .findFirst().orElse(null);
        }
        if (replacement == null) {
            replacement = replacementList.stream()
                .filter(it -> Objects.equals(it.getComponent(), bomItem.getComponent()))
                .findFirst().orElse(null);
        }
        return replacement;
    }

    // Убрать или установить компонент к закупке
    @PostMapping("/detail/specification/info/replacement/purchase-replacement")
    public void detail_specification_info_replacement_purchaseReplacement(
        Long id,
        Long bomItemId,
        boolean purchaseInProduct
    ) {
        List<BomItemReplacement> replacementList = bomItemReplacementService.getAllByBomItemId(bomItemId);
        for (var replacement: replacementList) {
            replacement.setPurchase(false);
            if (replacement.getId().equals(id)) {
                replacement.setPurchase(!purchaseInProduct);
            }
        }
        bomItemReplacementService.saveAll(replacementList);
    }

    // Удаление замены
    @DeleteMapping("/detail/specification/info/replacement/delete-replacement/{id}/{bomItemId}")
    public void detail_specification_info_replacement_deleteReplacement(
        @PathVariable Long id,
        @PathVariable Long bomItemId
    ) {
        BomItem bomItem = bomItemService.read(bomItemId);
        if (bomItem == null) {
            throw new AlertUIException("Редактируемое вхождение спецификации было удалено");
        }
        BomItemReplacement bomItemReplacement = bomItemReplacementService.read(id);
        if (
            bomItemReplacement != null && (
                Objects.equals(bomItem.getComponent(), bomItemReplacement.getComponent())
                    || Objects.equals(bomItem.getComponent().getPurchaseComponent(), bomItemReplacement.getComponent())
            )
        ) {
            throw new AlertUIException("Целевые компоненты замены не могут быть удалены");
        }
        bomItemReplacementService.delete(bomItemReplacement);
    }

    // Сохранение статуса замены
    @PostMapping("/detail/specification/info/replacement/edit-status/save")
    public void detail_specification_info_replacement_editStatus_save(
        Long id,
        BomItemReplacementStatus status
    ) {
        BomItemReplacement replacement = bomItemReplacementService.read(id);
        if (replacement == null) {
            throw new AlertUIException("Замена была удалена");
        }
        replacement.setStatus(status);
        replacement.setStatusDate(LocalDate.now());
        bomItemReplacementService.save(replacement);
    }


    // Загрузка списка компонентов замен
    @GetMapping("/detail/specification/info/replacement/add-component/list-load")
    public TabrOut<?> detail_specification_info_replacement_addComponent_listLoad(
        HttpServletRequest request,
        String filterData
    ) throws JsonProcessingException {
        @Getter
        class Item {
            long id;
            String mark; // метка
            String position; // позиция
            String name; // наименование
            String substituteComponent; // инфо о заместителе
            String category; // категория
            String description; // описание
        }
        ProductSpecComponentFilterForm form = jsonMapper.readValue(filterData, ProductSpecComponentFilterForm.class);
        TabrIn input = new TabrIn(request);
        return TabrOut.Companion.instance(input, componentService.queryDataByFilterForm(input, form), it -> {
            var item = new Item();
            item.id = it.getId();
            if (it.getSubstituteComponent() != null) {
                item.mark = COMPONENT_SUBSTITUTE_MARK;
            } else if (!it.isProcessed()) {
                item.mark = COMPONENT_NOT_PROCESSED_MARK;
            }
            item.position = it.getFormattedPosition();
            item.name = it.getName();
            Component substituteComponent = it.getSubstituteComponent();
            item.substituteComponent = substituteComponent == null ? null : substituteComponent.getFormattedPosition() + COMMA_SPACE_SEPARATOR + substituteComponent.getName();
            item.category = it.getCategory().getName();
            item.description = it.getDescription();
            return item;
        });
    }

    // Сохранение компонента в таблице замен
    @PostMapping("/detail/specification/info/replacement/add-component/save")
    public void detail_specification_info_replacement_addComponent_save(
        Long bomItemId,
        Long componentId,
        @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN) LocalDate replacementDate
    ) {
        BomItem bomItem = bomItemService.read(bomItemId);
        if (bomItem == null) {
            throw new AlertUIException("Редактируемое вхождение замены было удалено");
        }
        Component component = componentService.read(componentId);
        if (component == null) {
            throw new AlertUIException("Выбранный компонент был удален");
        }
        // Берем компонент заместитель
        component = component.getSubstituteComponent() == null ? component : component.getSubstituteComponent();
        Long bomId = bomItem.getBom().getId();
        if (
            bomItemService.existsByBomIdAndComponentId(bomId, component.getId())
                && bomItemReplacementService.existsByComponentIdAndBomId(component.getId(), bomId)
        ) {
            throw new AlertUIException("Выбранный компонент уже добавлен в спецификацию");
        }
        BomItemReplacement replacement = new BomItemReplacement();
        replacement.setComponent(component);
        replacement.setBomItem(bomItem);
        replacement.setStatus(BomItemReplacementStatus.ALLOWED);
        replacement.setReplacementDate(replacementDate == null ? LocalDate.now() : replacementDate);
        replacement.setStatusDate(LocalDate.now());
        bomItemReplacementService.save(replacement);
    }

    // Загрузка списка позиций для вхождения спецификации
    @GetMapping("/detail/specification/info/position/list-load")
    public List<?> detail_specification_info_position_listLoad(
        Long bomItemId
    ) {
        @Getter
        class TableItemOut {
            long id;
            double quantity; // количество
            String designation; // позиционное обозначение
            String firmware; // прошивка
            String letter; // литера
            Integer letterValue; // значение литеры
        }
        BomItem bomItem = bomItemService.read(bomItemId);
        return bomItemPositionService.getAllByBomItemId(bomItemId).stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.quantity = bomItem.getQuantity();
            String designation = item.getDesignation();
            if (designation != null) {
                itemOut.designation = item.getDesignation();
                Matcher designationMatcher = designationNumberPattern.matcher(designation);
                itemOut.letterValue = CommonUtil.convertStringToType(designationMatcher.find() ? designationMatcher.group() : StringUtils.EMPTY, Integer.class);
                itemOut.letter = designation.replaceAll(itemOut.letterValue == null ? StringUtils.EMPTY : itemOut.letterValue.toString(), StringUtils.EMPTY);
            }
            itemOut.firmware = item.getFirmware();
            return itemOut;
        }).sorted(
            Comparator.comparing(TableItemOut::getLetter, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(TableItemOut::getLetterValue, Comparator.nullsLast(Comparator.naturalOrder()))
        ).collect(Collectors.toList());
    }

    // Сохранение позиционного обозначения в окне позиционных обозначений
    @PostMapping("/detail/specification/info/position/edit/save")
    public ValidatorResponse detail_specification_info_position_edit_save(
        EditProductSpecPositionForm form
    ) {
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            BomItemPosition position = bomItemPositionService.read(form.getId());
            if (position == null) {
                throw new AlertUIException("Редактируемая позиция была удалена");
            }
            position.setLockVersion(form.getLockVersion());
            position.setFirmware(StringUtils.isBlank(form.getFirmware()) ? null : form.getFirmware().trim());
            bomItemPositionService.save(position);
        }
        return response;
    }

    @DeleteMapping("/detail/specification/info/list-spec-clear/{bomId}")
    public void detail_specification_info_listSpecClear(
        @PathVariable Long bomId
    ) {
        if (bomAttributeService.getAllByBomId(bomId).stream().anyMatch(el -> el.getAcceptDate() != null || el.getApproveDate() != null)) {
            throw new AlertUIException("Утвержденные (принятые) спецификации можно редактировать только в части замен и давальческого сырья");
        }
        List<BomItem> bomItemList = bomItemService.getAllByBomId(bomId);
        bomItemList.forEach(bomItem -> bomItem.getProducerList().clear());
        bomItemService.deleteAll(bomItemList);
        specificationImportDetailService.deleteAllByBomId(bomId);
    }

    // Окно загрузки списка изделий для копирования спецификации
    @GetMapping("/detail/specification/info/list-spec-copy/list-load")
    public TabrOut<?> detail_specification_info_listSpecCopy_listLoad(
        HttpServletRequest request,
        String filterForm,
        Long bomId
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id;
            String conditionalName; // условное наименование
            String comment; // комментарий
        }
        ProductListFilterForm form = jsonMapper.readValue(filterForm, ProductListFilterForm.class);
        form.getExcludeProductIdList().add(bomService.read(bomId).getProduct().getId());

        TabrIn input = new TabrIn(request);
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), productService.getCountByForm(form));
        List<TableItemOut> itemOutList = productService.getByTableDataIn(input, form).stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.conditionalName = item.getConditionalName();
            itemOut.comment = item.getComment();
            return itemOut;
        }).collect(Collectors.toList());
        output.setData(itemOutList);
        return output;
    }

    // Таблица сравнения
    @GetMapping("/detail/specification/info/comparison/compare")
    public List<?> detail_specification_info_comparison_compare(
        String compareForm
    ) throws JsonProcessingException {
        @Getter
        class CompareBomItem {
            Integer position; // позиция
            String name; // наименование
            String category; // категория
            double leftQuantity; // количество из первой версии
            double rightQuantity; // количество из правой версии
        }
        DynamicObject form = jsonMapper.readValue(compareForm, DynamicObject.class);
        Long bomAId = form.longValue("versionA");
        Long bomBId = form.longValue("versionB");

        var mergeList = bomItemService.getAllByBomId(bomAId).stream().map(bomItem -> {
            CompareBomItem compareBomItem = new CompareBomItem();
            compareBomItem.position = bomItem.getComponent().getPosition();
            compareBomItem.name = bomItem.getComponent().getName();
            compareBomItem.category = bomItem.getComponent().getCategory().getName();
            compareBomItem.leftQuantity = bomItem.getQuantity();
            return compareBomItem;
        }).collect(Collectors.toList());

        var compareList = bomItemService.getAllByBomId(bomBId).stream().map(bomItem -> {
            CompareBomItem compareBomItem = new CompareBomItem();
            compareBomItem.position = bomItem.getComponent().getPosition();
            compareBomItem.name = bomItem.getComponent().getName();
            compareBomItem.category = bomItem.getComponent().getCategory().getName();
            compareBomItem.rightQuantity = bomItem.getQuantity();
            return compareBomItem;
        }).collect(Collectors.toList());

        List<Integer> positionList = mergeList.stream().map(CompareBomItem::getPosition).collect(Collectors.toList());
        for (var compareBomItem: compareList) {
            if (positionList.contains(compareBomItem.getPosition())) {
                for (var merge: mergeList) {
                    if (Objects.equals(merge.getPosition(), compareBomItem.getPosition())) {
                        merge.rightQuantity = compareBomItem.getRightQuantity();
                        break;
                    }
                }
            } else {
                positionList.add(compareBomItem.getPosition());
                mergeList.add(compareBomItem);
            }
        }
        mergeList.sort(Comparator.comparing(CompareBomItem::getCategory));
        return mergeList.stream().filter(it -> it.leftQuantity != it.rightQuantity).collect(Collectors.toList());
    }

    // Окно загрузки списка изделий в окно выбора
    @GetMapping("/detail/specification/info/comparison/select/list-load")
    public TabrOut<?> detail_specification_info_comparison_select_listLoad(
        HttpServletRequest request,
        String filterForm
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id;
            String conditionalName; // условное наименование
            String comment; // комментарий
        }
        ProductListFilterForm form = jsonMapper.readValue(filterForm, ProductListFilterForm.class);
        TabrIn input = new TabrIn(request);
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), productService.getCountByForm(form));
        List<TableItemOut> itemOutList = productService.getByTableDataIn(input, form).stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.conditionalName = item.getConditionalName();
            itemOut.comment = item.getComment();
            return itemOut;
        }).collect(Collectors.toList());
        output.setData(itemOutList);
        return output;
    }

    // Загрузка версий для выбранного изделия
    @GetMapping("/detail/specification/info/list-spec-copy/version-list")
    public List<DropdownOption> detail_specification_info_listSpecCopy_versionList(
        Long productId
    ) {
        return buildProductDetailVersionList(productId, null);
    }

    // Копирование спецификации
    @PostMapping("/detail/specification/info/list-spec-copy/save")
    public void detail_specification_info_listSpecCopy_save(
        Long copyBomId,
        Long bomId,
        boolean isReplacementStatusCopy
    ) {
        if (copyBomId == null) {
            throw new AlertUIException("Не указана версия спецификации, которую требуется копировать");
        }
        if (bomItemService.existsByBomId(bomId)) {
            throw new AlertUIException("Спецификация уже заполнена");
        }
        copySpecification(bomService.read(bomId), copyBomId, isReplacementStatusCopy);
    }

    // Импорт ЗС
    @PostMapping("/detail/specification/info/excel-import")
    public void detail_specification_info_excelImport(
        @RequestParam Long bomId,
        @RequestParam MultipartFile excel
    ) throws IOException {
        if (bomAttributeService.getAllByBomId(bomId).stream().anyMatch(el -> el.getAcceptDate() != null || el.getApproveDate() != null)) {
            throw new AlertUIException("Утвержденные (принятые) спецификации можно редактировать только в части замен и давальческого сырья");
        }
        if (bomItemService.existsByBomId(bomId)) {
            throw new AlertUIException("Спецификация уже содержит компоненты. Для выполнения действия очистите спецификацию.");
        }
        specificationImportDetailService.deleteAllByBomId(bomId);
        if (excel.getSize() > 0) {
            Workbook workbook = null;
            String name = excel.getOriginalFilename();
            if (name != null) {
                InputStream inputStream = excel.getInputStream();
                if (name.endsWith(AttachmentMediaType.XLS.getExtension())) {
                    workbook = new HSSFWorkbook(inputStream);
                } else if (name.endsWith(AttachmentMediaType.XLSX.getExtension())) {
                    workbook = new XSSFWorkbook(inputStream);
                }
            }
            if (workbook != null) {
                @Getter
                @AllArgsConstructor
                class SpecificationExcelData {
                    final String position;
                    final String quantity;
                    final String partNumber;
                    final String description;
                    final int rowNumber;
                    String designation;
                }

                Bom bom = bomService.read(bomId);

                List<SpecificationExcelData> specificationExcelDataList = new ArrayList<>();
                boolean isRowFound = false;
                int EXCEL_POSITION_CELL_INDEX = 0;
                int EXCEL_QUANTITY_CELL_INDEX = 0;
                int EXCEL_DESIGNATION_CELL_INDEX = 0;
                int EXCEL_PART_NUMBER_CELL_INDEX = 0;
                int EXCEL_DESCRIPTION_CELL_INDEX = 0;
                List<String> positionList = new ArrayList<>();
                Sheet sheet = workbook.getSheetAt(0);
                for (var row : sheet) {
                    if (!isRowFound) {
                        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                            switch (row.getCell(i).getStringCellValue().toUpperCase()) {
                                case EXCEL_POSITION:
                                    isRowFound = true;
                                    EXCEL_POSITION_CELL_INDEX = i;
                                    break;
                                case EXCEL_QUANTITY:
                                    isRowFound = true;
                                    EXCEL_QUANTITY_CELL_INDEX = i;
                                    break;
                                case EXCEL_DESIGNATION:
                                    isRowFound = true;
                                    EXCEL_DESIGNATION_CELL_INDEX = i;
                                    break;
                                case EXCEL_PART_NUMBER:
                                    isRowFound = true;
                                    EXCEL_PART_NUMBER_CELL_INDEX = i;
                                    break;
                                case EXCEL_DESCRIPTION:
                                    isRowFound = true;
                                    EXCEL_DESCRIPTION_CELL_INDEX = i;
                                    break;
                            }
                        }
                    } else {
                        Cell positionCell = row.getCell(EXCEL_POSITION_CELL_INDEX);
                        String position = getCellValueAsString(positionCell);
                        Cell designationCell = row.getCell(EXCEL_DESIGNATION_CELL_INDEX);
                        String designation = getCellValueAsString(designationCell);
                        if (!position.isEmpty() && positionList.contains(position)) {
                            for (SpecificationExcelData specificationExcelData : specificationExcelDataList) {
                                if (Objects.equals(specificationExcelData.position, position)) {
                                    specificationExcelData.designation = String.format("%s,%s", specificationExcelData.designation, designation);
                                }
                            }
                        } else {
                            positionList.add(position);
                            Cell quantityCell = row.getCell(EXCEL_QUANTITY_CELL_INDEX);
                            Cell partNumberCell = row.getCell(EXCEL_PART_NUMBER_CELL_INDEX);
                            Cell descriptionCell = row.getCell(EXCEL_DESCRIPTION_CELL_INDEX);
                            SpecificationExcelData specificationExcelData = new SpecificationExcelData(
                                position,
                                getCellValueAsString(quantityCell),
                                getCellValueAsString(partNumberCell),
                                getCellValueAsString(descriptionCell),
                                row.getRowNum() + 1,
                                designation
                            );
                            // Добавляем если только позиция или наименование заполнены
                            if (!specificationExcelData.position.isEmpty() || !specificationExcelData.partNumber.isEmpty()) {
                                specificationExcelDataList.add(specificationExcelData);
                            } else {
                                SpecificationImportDetail specificationImportDetail = new SpecificationImportDetail();
                                specificationImportDetail.setRowNumber(specificationExcelData.getRowNumber());
                                specificationImportDetail.setBom(bom);
                                specificationImportDetail.setType(MISTAKE_COMPONENT);
                                specificationImportDetail.setDescription(MISTAKE_COMPONENT.getDescription() + ": позиция и название (пустые поля)");
                                specificationImportDetailService.save(specificationImportDetail);
                            }
                        }
                    }
                }
                // Проверка позиций
                for (var excelItem: specificationExcelDataList) {
                    Integer parsedPosition = CommonUtil.isDouble(excelItem.position) ? Double.valueOf(excelItem.position).intValue() : null;
                    if (parsedPosition == null) {
                        SpecificationImportDetail specificationImportDetail = new SpecificationImportDetail();
                        specificationImportDetail.setRowNumber(excelItem.getRowNumber());
                        specificationImportDetail.setBom(bom);
                        specificationImportDetail.setType(MISTAKE_COMPONENT);
                        specificationImportDetail.setDescription(MISTAKE_COMPONENT.getDescription() + ": поле позиции содержит не числовое значение");
                        specificationImportDetailService.save(specificationImportDetail);
                    }
                }
                if (!specificationExcelDataList.isEmpty()) {
                    List<BomItem> bomItemList = new ArrayList<>();
                    List<BomItemPosition> bomItemPositionList = new ArrayList<>();
                    for (var excelItem: specificationExcelDataList) {
                        Integer parsedPosition = CommonUtil.isDouble(excelItem.position) ? Double.valueOf(excelItem.position).intValue() : null;
                        if (parsedPosition == null) continue;

                        SpecificationImportDetail specificationImportDetail = new SpecificationImportDetail();
                        specificationImportDetail.setRowNumber(excelItem.getRowNumber());
                        specificationImportDetail.setBom(bom);

                        Component component = componentService.getByPosition(parsedPosition);
                        if (component == null) {
                            // Добавление нового компонента
                            component = new Component();
                            component.setDescription(excelItem.description);
                            component.setName(excelItem.partNumber);
                            component.setType(ComponentType.NEW_COMPONENT.getId());
                            component.setModifiedDatetime(LocalDateTime.now());
                            component.setCategory(componentCategoryService.read(9999L));
                            component.setPosition(parsedPosition);
                            componentService.save(component);

                            // Подробная информация импорта о добавлении нового компонента
                            specificationImportDetail.setType(NEW_COMPONENT);
                            specificationImportDetail.setDescription(NEW_COMPONENT.getDescription());
                        } else {
                            // Подробная информация импорта о существовании компонента
                            specificationImportDetail.setType(EXIST_COMPONENT);
                            specificationImportDetail.setDescription(EXIST_COMPONENT.getDescription());
                        }
                        specificationImportDetail.setComponent(component);
                        specificationImportDetailService.save(specificationImportDetail);

                        // Добавляем запись в спецификацию
                        BomItem bomItem = new BomItem();
                        bomItem.setBom(bom);
                        bomItem.setComponent(component);
                        bomItem.setQuantity(Double.parseDouble(excelItem.getQuantity()));
                        // Собираем позиционные обозначения
                        String[] designations = !excelItem.designation.isEmpty() ? excelItem.designation.split(COMMA_SEPARATOR) : null;
                        if (designations != null) {
                            for (var designation : designations) {
                                String[] designationDiapasons = designation.split(DESIGNATION_DIAPASON_SEPARATOR);
                                if (designationDiapasons.length == 2) {
                                    int start = Integer.parseInt(designationDiapasons[0].replaceAll(CLEAR_ALPHABET, StringUtils.EMPTY));
                                    int end = Integer.parseInt(designationDiapasons[1].replaceAll(CLEAR_ALPHABET, StringUtils.EMPTY));
                                    String alphabet = designationDiapasons[0].replace(String.valueOf(start), StringUtils.EMPTY);
                                    for (int i = start; i <= end; i++) {
                                        BomItemPosition bomItemPosition = new BomItemPosition();
                                        bomItemPosition.setBomItem(bomItem);
                                        bomItemPosition.setDesignation(alphabet + i);
                                        bomItemPositionList.add(bomItemPosition);
                                    }
                                } else {
                                    BomItemPosition bomItemPosition = new BomItemPosition();
                                    bomItemPosition.setBomItem(bomItem);
                                    bomItemPosition.setDesignation(designation);
                                    bomItemPositionList.add(bomItemPosition);
                                }
                            }
                        } else {
                            if (!excelItem.getQuantity().isEmpty()) {
                                if (excelItem.getQuantity().replaceAll("\\.", StringUtils.EMPTY).matches(ONLY_DIGITAL_PATTERN)) {
                                    BomItemPosition bomItemPosition = new BomItemPosition();
                                    bomItemPosition.setBomItem(bomItem);
                                    bomItemPositionList.add(bomItemPosition);
                                } else {
                                    // Дополнительная запись в детальную информацию о неправильном контенте в поле кол-во
                                    SpecificationImportDetail moreSpecificationImportDetail = new SpecificationImportDetail();
                                    moreSpecificationImportDetail.setRowNumber(excelItem.getRowNumber());
                                    moreSpecificationImportDetail.setBom(bom);
                                    moreSpecificationImportDetail.setType(MISTAKE_COMPONENT);
                                    moreSpecificationImportDetail.setDescription(MISTAKE_COMPONENT.getDescription() + ": строка добавлена в спецификацию, но необходимо проверить поле с количеством");
                                    specificationImportDetailService.save(moreSpecificationImportDetail);
                                }
                            }
                        }
                        bomItemList.add(bomItem);
                    }
                    baseService.exec(em -> {
                        bomItemService.saveAll(bomItemList);
                        bomItemPositionService.saveAll(bomItemPositionList);
                        bomItemList.forEach(it -> {
                            BomItemReplacement plr = new BomItemReplacement();
                            plr.setStatus(BomItemReplacementStatus.ALLOWED);
                            plr.setBomItem(it);
                            plr.setComponent(it.getComponent());
                            bomItemReplacementService.save(plr);
                        });
                        return Unit.INSTANCE;
                    });
                }
            }
        }
    }

    /**
     * Метод для получения значения ячейки в виде строки
     * @param cell ячейка
     * @return значение ячейки
     */
    private String getCellValueAsString(Cell cell) {
        String value = StringUtils.EMPTY;
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    value = String.valueOf(cell.getNumericCellValue());
                    break;
                case STRING:
                    value = cell.getStringCellValue();
                    break;
            }
        }
        return value.trim();
    }

    // Очищаем таблицу импорта
    @DeleteMapping("/detail/specification/info/excel-import-detail/clear/{bomId}")
    public void detail_specification_info_excelImportDetail_clear(
        @PathVariable Long bomId
    ) {
        specificationImportDetailService.deleteAllByBomId(bomId);
    }

    /**
     * Копирование спецификации
     * @param currentBom версия, в которую копируется спецификация
     * @param copyBomId  идентификатор версии, из которой копируется спецификация
     * @param isReplacementStatusCopy флаг копирования статусов замен
     */
    private void copySpecification(Bom currentBom, Long copyBomId, boolean isReplacementStatusCopy) {
        if (currentBom != null && Objects.equals(currentBom.getId(), copyBomId)) {
            throw new AlertUIException("Версии спецификации равны. Выберите другую");
        }
        List<BomItem> copyBomItemList = new ArrayList<>();
        for (var bomItem : bomItemService.getAllByBomId(copyBomId)) {
            BomItem bi = new BomItem();
            bi.setBom(currentBom);
            bi.setComponent(bomItem.getComponent());
            bi.setQuantity(bomItem.getQuantity());
            List<BomItemPosition> bomItemPositionList = new ArrayList<>();
            for (var bomItemPosition: bomItem.getBomItemPositionList()) {
                BomItemPosition bip = new BomItemPosition();
                bip.setBomItem(bi);
                bip.setDesignation(bomItemPosition.getDesignation());
                bip.setFirmware(bomItemPosition.getFirmware());
                bomItemPositionList.add(bip);
            }
            List<BomItemReplacement> bomItemReplacementList = new ArrayList<>();
            for (var bomItemReplacement: bomItem.getBomItemReplacementList()) {
                BomItemReplacement bir = new BomItemReplacement();
                bir.setBomItem(bi);
                bir.setComponent(bomItemReplacement.getComponent());
                // Целевой и не целевой компонент
                if (Objects.equals(bomItem.getComponent(), bomItemReplacement.getComponent())) {
                    bir.setStatus(isReplacementStatusCopy ? bomItemReplacement.getStatus() : BomItemReplacementStatus.ALLOWED);
                } else {
                    bir.setStatus(isReplacementStatusCopy ? bomItemReplacement.getStatus() : BomItemReplacementStatus.NOT_PROCESSED);
                }
                bir.setPurchase(bomItemReplacement.isPurchase());
                bomItemReplacementList.add(bir);
            }
            List<BomItemReplacement> biBomItemReplacementList = bi.getBomItemReplacementList();
            biBomItemReplacementList.clear();
            biBomItemReplacementList.addAll(bomItemReplacementList);
            //
            List<BomItemPosition> biBomItemPositionList = bi.getBomItemPositionList();
            biBomItemPositionList.clear();
            biBomItemPositionList.addAll(bomItemPositionList);
            copyBomItemList.add(bi);
        }
        bomItemService.saveAll(copyBomItemList);
    }

    /**
     * Копирование состава
     * @param currentBom версия, в которую копируется состав
     * @param copyBomId  идентификатор версии, из которой копируется состав
     */
    private void copyStructure(Bom currentBom, Long copyBomId) {
        List<BomSpecItem> copySpecItemList = bomSpecItemService.getAllByBomId(copyBomId);
        List<BomSpecItem> comSpecItemList = copySpecItemList.stream()
            .map(copyBomSpecItem -> {
                BomSpecItem bomSpecItem = new BomSpecItem();
                bomSpecItem.setBom(currentBom);
                bomSpecItem.setProducer(copyBomSpecItem.getProducer());
                bomSpecItem.setProduct(copyBomSpecItem.getProduct());
                bomSpecItem.setQuantity(copyBomSpecItem.getQuantity());
                return bomSpecItem;
            }).collect(Collectors.toList());
        bomSpecItemService.saveAll(comSpecItemList);
    }

    // Утверждение/снятие утверждения для спецификации
    @PostMapping("/detail/specification/info/list-spec-approved")
    public void detail_specification_info_listSpecApproved(
        Long bomId,
        Long launchId,
        boolean isApproved
    ) {
        Launch launch = launchService.read(launchId);
        if (launch == null) throw new AlertUIException("Запуск не был найден");
        Bom bom = bomService.read(bomId);
        if (bom == null) throw new AlertUIException("Версия не была найдена");
        checkReplacementStatusesForApproveAcceptVersion(bom);
        baseService.exec(em -> {
            BomAttribute attribute = bomAttributeService.getByLaunchIdAndBomId(launchId, bomId);
            if (attribute == null) {
                attribute = new BomAttribute();
                attribute.setBom(bom);
                attribute.setLaunch(launch);
            } else if (isApproved && attribute.getAcceptDate() != null) {
                throw new AlertUIException("Принятые спецификации можно редактировать только в части замен и давальческого сырья");
            }
            var now = LocalDate.now();
            attribute.setApproveDate(isApproved ? null : now);
            // Выполняем копию состава при каждом подтверждении версии изделия. Необходимо для использования в запусках
            if (!isApproved) {
                var lp = launchProductService.getByLaunchIdAndProductId(launchId, bom.getProduct().getId());
                if (lp != null) {
                    lp.setVersionApproveDate(now);
                    launchProductStructService.deleteAllByLaunchProductId(lp.getId());
                    em.flush();
                    bomSpecItemService.getAllByBomId(bomId).forEach(item -> {
                        var lps = new LaunchProductStruct();
                        lps.setLaunchProduct(lp);
                        lps.setProduct(item.getProduct());
                        lps.setAmount(item.getQuantity());
                        launchProductStructService.save(lps);
                    });
                }
            }
            bomAttributeService.save(attribute);
            return Unit.INSTANCE;
        });
    }

    // Принятие/отмена принятия для спецификации
    @PostMapping("/detail/specification/info/list-spec-accept")
    public void detail_specification_info_listSpecAccept(
        Long bomId,
        Long launchId,
        boolean isAccepted
    ) {
        Launch launch = launchService.read(launchId);
        if (launch == null) {
            throw new AlertUIException("Запуск не был найден");
        }
        Bom bom = bomService.read(bomId);
        if (bom == null) {
            throw new AlertUIException("Версия не была найдена");
        }
        checkReplacementStatusesForApproveAcceptVersion(bom);
        BomAttribute attribute = bomAttributeService.getByLaunchIdAndBomId(launchId, bomId);
        if (attribute == null) {
            attribute = new BomAttribute();
            attribute.setBom(bom);
            attribute.setLaunch(launch);
        }
        attribute.setAcceptDate(isAccepted ? null : LocalDate.now());
        bomAttributeService.save(attribute);
    }

    // Проверка статусов для подтверждения/принятия версии
    private void checkReplacementStatusesForApproveAcceptVersion(Bom bom) {
        List<BomItemReplacementStatus> statusList = List.of(BomItemReplacementStatus.NOT_PROCESSED, BomItemReplacementStatus.CATALOG);
        List<String> positionList = new ArrayList<>();
        bom.getBomItemList().forEach(bomItem ->
            bomItem.getBomItemReplacementList().forEach(replacement -> {
                    if (statusList.contains(replacement.getStatus())) {
                        String position = bomItem.getComponent().getFormattedPosition();
                        if (StringUtils.isNotBlank(position)) {
                            positionList.add(position);
                        }
                    }
                }
            ));
        List<String> quantityList = bom.getBomItemList().stream().filter(bomItem -> bomItem.getQuantity() == 0)
            .map(bomItem -> bomItem.getComponent().getFormattedPosition()).collect(Collectors.toList());
        if (!positionList.isEmpty()) {
            throw new AlertUIException(
                "Нельзя утвердить (принять) к запуску спецификацию, которая содержит необработанные замены. Измените статус замен следующих компонентов:\n" +
                    String.join(StringUtils.SPACE + StringUtils.LF, positionList)
            );
        } else if (!quantityList.isEmpty()) {
            throw new AlertUIException(
                "Кол-во экземпляров компонента в спецификации должно быть выражено положительным числом. Укажите кол-во у следующих компонентов:\n" +
                    String.join(StringUtils.SPACE + StringUtils.LF, quantityList)
            );
        }
    }

    // Загрузка отчетов
    @GetMapping("/list/report/load")
    public List<?> list_report_load(
        int reportType,
        String filterForm
    ) {
        DynamicObject filterMap = KtCommonUtil.INSTANCE.readDynamic(jsonMapper, filterForm);
        if (reportType == 1) { // ЗС для запуска
            @Getter
            class ItemOut {
                String productName;
                String developer;
                String version;
                String approvedToLaunch;
            }
            //
            long launchId = filterMap.longNotNull("launchId", 0);
            EcoLaunch launch = ecoLaunchService.read(launchId);
            int type = filterMap.intNotNull("typeId", 0);
            //
            return ecoProductService.reportSpecForLaunch(launchId).stream().filter(line -> {
                String launchNumber = line.getLaunchNumber();
                String launchVersion = line.getLaunchVersion();
                if (launchVersion != null && launchVersion.contains("[0]")) {
                    line.setLaunchVersion(launchVersion.replaceAll("\\[0]", ""));
                }
                switch (type) {
                    case 1: return true;
                    case 2: return StringUtils.isBlank(launchNumber) && StringUtils.isNotBlank(launchVersion); // Неутвержденные
                    case 3: return StringUtils.isNotBlank(launchNumber) && StringUtils.isNotBlank(launchVersion) && !Objects.equals(launch.getFullNumber(), launchNumber); // Устаревшие
                    case 4: return StringUtils.isBlank(launchVersion); // Отсутствующие
                    case 5: return StringUtils.isNotBlank(line.getFirstApproved()); // Новые
                    case 6: return StringUtils.isNotBlank(line.getModified()); // Изменившиеся
                    default: return false;
                }
            }).map(line -> {
                ItemOut item = new ItemOut();
                item.productName = line.getProductName();
                item.developer = line.getLastName();
                item.version = line.getLaunchVersion();
                item.approvedToLaunch = line.getLaunchNumber();
                return item;
            }).collect(Collectors.toList());
        } else if (reportType == 2) { // Проверка ввода допустимых замен по закупке
            LocalDate startDate = filterMap.date("startDate");
            LocalDate endDate = filterMap.date("endDate");
            return ecoProductService.reportMissAnalogs(startDate, endDate);
        } else if (reportType == 3) { // Статус введенных допустимых замен
            LocalDate startDate = filterMap.date("startDate");
            LocalDate endDate = filterMap.date("endDate");
            List<Long> statusIdList = filterMap.listLong("statusIdList");
            return ecoProductService.reportAnalogStatus(startDate, endDate, statusIdList);
        }
        return Collections.emptyList();
    }

    // Загрузка списка документации
    @GetMapping("/detail/documentation/load")
    public TabrOut<?> detail_documentation_load(HttpServletRequest request, long productId) {
        @Getter
        class Item {
            Long id;
            String fileHash;
            String name;
            String comment;
        }
        var input = new TabrIn(request);
        var tableData = productDocumentationService.findTableData(input, productId);
        var fileList = fileStorageService.readAny(tableData.getData(), FileStorageType.ProductDocumentationFile.INSTANCE);
        return TabrOut.Companion.instance(input, tableData, res -> {
            var item = new Item();
            item.id = res.getId();
            var fs = FileStorageUtil.INSTANCE.extractSingular(fileList, res, FileStorageType.ProductDocumentationFile.INSTANCE);
            item.fileHash = fs == null ? null : fs.getUrlHash();
            item.name = res.getName();
            item.comment = res.getComment();
            return item;
        });
    }

    @PostMapping("/detail/documentation/edit/save")
    public ValidatorResponse detail_documentation_edit_save(EditProductDocumentationForm form) {
        ValidatorResponse response = new ValidatorResponse(form);
        baseService.exec(em -> {
            if (response.isValid()) {
                Long formId = form.getId();
                ProductDocumentation doc = formId == null ? new ProductDocumentation() : productDocumentationService.read(formId);
                doc.setName(form.getName().trim());
                doc.setComment(StringUtils.defaultIfBlank(form.getComment(), null));
                doc.setProduct(new Product(form.getProductId()));
                productDocumentationService.save(doc);
                if (!form.getFile().isEmpty()) {
                    fileStorageService.saveEntityFile(doc, FileStorageType.ProductDocumentationFile.INSTANCE, form.getFile());
                }
                if (formId == null) response.putAttribute(ObjAttr.ID, doc.getId());
            }
            return null;
        });
        return response;
    }

    // Загрузка списка документации
    @DeleteMapping("/detail/documentation/delete/{id}")
    public void detail_documentation_delete(@PathVariable long id) {
        productDocumentationService.deleteById(id);
    }
}