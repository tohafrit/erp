package ru.korundm.controller.user;

import asu.dao.AsuGrpCompService;
import asu.dao.AsuInvoiceStringService;
import asu.dao.AsuPlantService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eco.dao.EcoBomService;
import eco.dao.EcoBomSpecItemService;
import eco.dao.EcoProductService;
import eco.entity.EcoBom;
import eco.entity.EcoBomAttribute;
import eco.entity.EcoLaunch;
import kotlin.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.ObjAttr;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.dao.*;
import ru.korundm.dto.DropdownOption;
import ru.korundm.dto.decipherment.CompositionProduct;
import ru.korundm.dto.decipherment.DeciphermentDataComponent;
import ru.korundm.dto.decipherment.DeciphermentDataInvoice;
import ru.korundm.dto.decipherment.DeciphermentDataInvoiceComponent;
import ru.korundm.entity.FileStorage;
import ru.korundm.entity.ProductDecipherment;
import ru.korundm.entity.ProductDeciphermentAttrVal;
import ru.korundm.entity.User;
import ru.korundm.enumeration.ProductDeciphermentAttr;
import ru.korundm.enumeration.ProductDeciphermentTypeEnum;
import ru.korundm.exception.AlertUIException;
import ru.korundm.form.edit.EditDeciphermentForm;
import ru.korundm.helper.*;
import ru.korundm.helper.manager.decipherment.CompositionManager;
import ru.korundm.util.FileStorageUtil;
import ru.korundm.util.KtCommonUtil;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static kotlin.jvm.JvmClassMappingKt.getKotlinClass;
import static ru.korundm.enumeration.ProductDeciphermentAttr.*;
import static ru.korundm.enumeration.ProductDeciphermentTypeEnum.*;

@Controller
@RequestMapping("/decipherment")
public class DeciphermentController {

    private static final String FILE_INVOICE_FOLDER =
        //"scan-feo";
        "fileserver";

    private static final String FILE_INVOICE_PATH =
        //"\\\\fileserver\\home\\FEO\\scan-feo\\";
        System.getProperty("jboss.server.base.dir") + File.separator + "fileserver" + File.separator;

    @Getter
    @Setter
    @EqualsAndHashCode(of = "id")
    private static final class InvoiceFileData {
        long id;
        String number;
        String supplier;
        String dateString;
        //
        String fileName;
        String filePath;
    }

    private static final Map<ProductDeciphermentTypeEnum, List<ProductDeciphermentAttr>> attrMap = Map.of(
        FORM_4, List.of(PURCHASE_SPECIFICATION_VERSION, COMPOSITION, HEAD_PL_EC_DEPARTMENT, HEAD_CONSTRUCT_DEPARTMENT),
        FORM_6_1, List.of(PURCHASE_SPECIFICATION_VERSION, COMPOSITION, HEAD_PL_EC_DEPARTMENT, HEAD_CONSTRUCT_DEPARTMENT),
        FORM_6_2, List.of(PURCHASE_SPECIFICATION_VERSION, COMPOSITION, HEAD_PL_EC_DEPARTMENT, HEAD_CONSTRUCT_DEPARTMENT),
        FORM_6_3, List.of(PURCHASE_SPECIFICATION_VERSION, COMPOSITION, HEAD_PL_EC_DEPARTMENT, HEAD_CONSTRUCT_DEPARTMENT)
    );

    private final ObjectMapper jsonMapper;
    private final ProductDeciphermentService deciphermentService;
    private final FileStorageService fileStorageService;
    private final EcoProductService ecoProductService;
    private final EcoBomService ecoBomService;
    private final ProductDeciphermentAttrValService deciphermentAttributeValueService;
    private final EcoBomSpecItemService ecoBomSpecItemService;
    private final AsuGrpCompService asuGrpCompService;
    private final AsuPlantService asuPlantService;
    private final AsuInvoiceStringService asuInvoiceStringService;
    private final CompositionManager compositionManager;
    private final BaseService baseService;
    private final UserService userService;

    public DeciphermentController(
        EcoProductService ecoProductService,
        ObjectMapper jsonMapper,
        ProductDeciphermentService deciphermentService,
        AsuPlantService asuPlantService,
        FileStorageService fileStorageService,
        CompositionManager compositionManager,
        EcoBomService ecoBomService,
        ProductDeciphermentAttrValService deciphermentAttributeValueService,
        AsuInvoiceStringService asuInvoiceStringService,
        EcoBomSpecItemService ecoBomSpecItemService,
        AsuGrpCompService asuGrpCompService,
        BaseService baseService,
        UserService userService
    ) {
        this.ecoProductService = ecoProductService;
        this.jsonMapper = jsonMapper;
        this.deciphermentService = deciphermentService;
        this.asuPlantService = asuPlantService;
        this.fileStorageService = fileStorageService;
        this.compositionManager = compositionManager;
        this.ecoBomService = ecoBomService;
        this.deciphermentAttributeValueService = deciphermentAttributeValueService;
        this.asuInvoiceStringService = asuInvoiceStringService;
        this.ecoBomSpecItemService = ecoBomSpecItemService;
        this.asuGrpCompService = asuGrpCompService;
        this.baseService = baseService;
        this.userService = userService;
    }

    // Редактирование расшифровки
    @GetMapping("/edit")
    public String edit(
        ModelMap model,
        long id
    ) {
        var decipherment = deciphermentService.read(id);
        if (decipherment.getApproved()) throw new AlertUIException("Форма утверждена. Редактирование невозможно");

        EditDeciphermentForm form = new EditDeciphermentForm();
        form.setId(decipherment.getId());
        form.setComment(decipherment.getComment() == null ? "" : decipherment.getComment());
        form.setFileStorage(fileStorageService.readOneSingular(decipherment, FileStorageType.ProductDeciphermentFile.INSTANCE));

        var type = decipherment.getType().getEnum();
        var attrList = attrMap.get(type);
        attrList = attrList == null ? Collections.emptyList() : attrList;
        // Заполнение формы атрибутами
        for (var attribute : attrList) {
            // Определяем ключ для реквеста и его значение в БД (значение при редактировании)
            String parameterKey = type + "." + attribute;
            ProductDeciphermentAttrVal attrVal = deciphermentAttributeValueService.getFirstByDeciphermentAndAttributeKey(decipherment, attribute);
            switch (attribute) {
                case PURCHASE_SPECIFICATION_VERSION:
                    EcoBom ecoBom = attrVal == null ?
                        ecoBomService.getLastApprovedVersion(decipherment.getPeriod().getProduct().getId()) :
                        ecoBomService.read(attrVal.getLongVal());
                    model.addAttribute(parameterKey, ecoBom);
                    break;
                case HEAD_PL_EC_DEPARTMENT:
                    var headEco = deciphermentAttributeValueService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT);
                    var headEcoUser = headEco == null ? null : headEco.getUser();
                    headEcoUser = headEcoUser == null ? userService.findByUserName("mochalov_ap") : headEcoUser;
                    model.addAttribute("headEcoId", headEcoUser == null ? null : headEcoUser.getId());
                    break;
                case HEAD_CONSTRUCT_DEPARTMENT:
                    var headConstruct = deciphermentAttributeValueService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_CONSTRUCT_DEPARTMENT);
                    var headConstructUser = headConstruct == null ? null : headConstruct.getUser();
                    headConstructUser = headConstructUser == null ? userService.findByUserName("lepekhin_ep") : headConstructUser;
                    model.addAttribute("headConstructId", headConstructUser == null ? null : headConstructUser.getId());
                    break;
            }
        }
        model.addAttribute("userList", userService.getAll().stream().map(user -> new DropdownOption(user.getId(), user.getUserOfficialName(), false)).collect(Collectors.toList()));
        model.addAttribute("productId", decipherment.getPeriod().getProduct().getId());
        model.addAttribute("formName", decipherment.getType().getName());
        model.addAttribute("deciphermentType", type);
        model.addAttribute("createDate", decipherment.getCreateDate().format(BaseConstant.INSTANCE.getDATE_FORMATTER()));
        model.addAttribute("createdBy", decipherment.getCreatedBy().getUserOfficialName());
        model.addAttribute("attrList", attrList);
        model.addAttribute("form", form);
        return "prod/include/product/detail/decipherment/editForm4or6_1or6_2or6_3";
    }

    // Сохранение расшифровки
    @PostMapping(
        value = "/edit",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public ValidatorResponse save(
        EditDeciphermentForm form,
        HttpServletRequest request
    ) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ValidatorResponse response = new ValidatorResponse(form);
        baseService.exec(em -> {
            var decipherment = deciphermentService.read(form.getId());
            if (decipherment == null) throw new AlertUIException("Расшифровка не найдена");
            decipherment.setComment(form.getComment());

            if (decipherment.getApproved()) throw new AlertUIException("Форма утверждена. Сохранение невозможно");

            // Установка и валидация по параметрам
            var deciphermentType = decipherment.getType().getEnum();
            var attrList = attrMap.get(deciphermentType);
            attrList = attrList == null ? Collections.emptyList() : attrList;
            List<ProductDeciphermentAttrVal> attrValSaveList = new ArrayList<>();
            for (var attr : attrList) {
                // Общие параметры
                String attrKey = deciphermentType + "." + attr; // ключ параметра в реквесте
                String attrVal = request.getParameter(attrKey); // значение параметра в реквесте
                // значение параметра в БД
                var deciphermentAttrVal = deciphermentAttributeValueService.getFirstByDeciphermentAndAttributeKey(decipherment, attr);
                // Если значение параметра не найдено, то создаем его
                if (deciphermentAttrVal == null) {
                    deciphermentAttrVal = new ProductDeciphermentAttrVal();
                }
                deciphermentAttrVal.setAttribute(attr);
                deciphermentAttrVal.setDecipherment(decipherment);

                // Разбор атрибутов
                // Поведение атрибутов может отличаться от типа расшифровки
                switch (attr) {
                    case PURCHASE_SPECIFICATION_VERSION:
                        validatePoint:
                        {
                            if (StringUtils.isBlank(attrVal)) {
                                response.putError(attr.name(), ValidatorMsg.REQUIRED);
                                break validatePoint;
                            }
                            if (!ecoBomService.existsById(Long.valueOf(attrVal))) {
                                response.putError(attr.name(), "validator.decipherment.productVersionNotExists");
                                break validatePoint;
                            }
                            // Для четырех расшифровок выполняем проверку на соответствие версии
                            if (
                                deciphermentType.equals(FORM_4)
                                || deciphermentType.equals(FORM_6_1)
                                || deciphermentType.equals(FORM_6_2)
                                || deciphermentType.equals(FORM_6_3)
                            ) {
                                List<ProductDeciphermentTypeEnum> deciphermentTypeList = new ArrayList<>();
                                deciphermentTypeList.add(FORM_4);
                                deciphermentTypeList.add(FORM_6_1);
                                deciphermentTypeList.add(FORM_6_2);
                                deciphermentTypeList.add(FORM_6_3);
                                deciphermentTypeList.remove(deciphermentType);
                                for (var attributeValue : deciphermentAttributeValueService.getAttributeValueList(
                                        PURCHASE_SPECIFICATION_VERSION,
                                        deciphermentTypeList,
                                        decipherment.getPeriod().getId()
                                    )
                                ) {
                                    if (attributeValue != null && !Objects.equals(attributeValue.getLongVal(), Long.valueOf(attrVal))) {
                                        response.putError(attr.name(), "validator.decipherment.versionMismatchAssociated");
                                        break validatePoint;
                                    }
                                }
                            }

                            deciphermentAttrVal.setLongVal(Long.valueOf(attrVal));
                            attrValSaveList.add(deciphermentAttrVal);
                        }
                        break;
                    case COMPOSITION:
                        List<CompositionProduct> compositionProductList;
                        if (StringUtils.isNotBlank(attrVal)) {
                            compositionProductList = KtCommonUtil.INSTANCE.safetyReadListValue(jsonMapper, attrVal, getKotlinClass(CompositionProduct.class));
                        } else {
                            break;
                        }
                        // Проверка списка состава
                        validatePoint:
                        {
                            // Проверка выбранных версий
                            if (compositionProductList.stream().anyMatch(item -> item.getSelectedVersionId() == null)) {
                                response.putError(attr.name(), "validator.decipherment.compositionVersionRequired");
                                break validatePoint;
                            }
                            // Проверка наличия указанной версии БД
                            if (compositionProductList.stream().anyMatch(item -> !ecoBomService.existsById(item.getSelectedVersionId()))) {
                                response.putError(attr.name(), "validator.decipherment.compositionStructureChanged");
                                break validatePoint;
                            }
                            // Проверка спецификации в БД
                            if (compositionProductList.stream().anyMatch(item -> !ecoBomSpecItemService.existsByIdAndProductIdAndVersionId(item.getSpecificationId(), item.getProductId(), item.getVersionId()))) {
                                response.putError(attr.name(), "validator.decipherment.compositionStructureChanged");
                                break validatePoint;
                            }
                            // Проверка выбранного изделия по структуре состава
                            if (!deciphermentService.verifyComposition(compositionProductList)) {
                                response.putError(attr.name(), "validator.decipherment.compositionStructureChanged");
                                break validatePoint;
                            }
                            // Проверка на согласованность состава с другими видами расшифровок
                            // "Расшифровка затрат на сырье и материалы" и "расшифровка затрат на покупные комплектующие изделия" могут иметь одинаковые изделия в составе
                            // но они не могут иметь изделия, которые находятся в составе "расшифровка затрат на тару и упаковку" или "расшифровка затрат на изделия собственного производства"
                            // Изделия в составе "расшифровка затрат на тару и упаковку" или "расшифровка затрат на изделия собственного производства" не должны встречатся в других расшифровках
                            {
                                List<ProductDeciphermentTypeEnum> deciphermentTypeList = new ArrayList<>();
                                if (deciphermentType.equals(FORM_4) || deciphermentType.equals(FORM_6_1)) {
                                    deciphermentTypeList.add(FORM_6_2);
                                    deciphermentTypeList.add(FORM_6_3);
                                } else if (deciphermentType.equals(FORM_6_2)) {
                                    deciphermentTypeList.add(FORM_4);
                                    deciphermentTypeList.add(FORM_6_1);
                                    deciphermentTypeList.add(FORM_6_3);
                                } else if (deciphermentType.equals(FORM_6_3)) {
                                    deciphermentTypeList.add(FORM_4);
                                    deciphermentTypeList.add(FORM_6_1);
                                    deciphermentTypeList.add(FORM_6_2);
                                }
                                for (var attributeValue : deciphermentAttributeValueService.getAttributeValueList(
                                        COMPOSITION,
                                        deciphermentTypeList,
                                        decipherment.getPeriod().getId()
                                    )
                                ) {
                                    if (StringUtils.isNotBlank(attributeValue.getJsonVal())) {
                                        List<CompositionProduct> attrCompositionProductList = KtCommonUtil.INSTANCE.safetyReadListValue(jsonMapper, attributeValue.getJsonVal(), getKotlinClass(CompositionProduct.class));
                                        List<String> compositionProductNumberList = compositionProductList.stream().map(CompositionProduct::getFullHierarchyNumber).collect(Collectors.toList());
                                        if (attrCompositionProductList.stream().map(CompositionProduct::getFullHierarchyNumber).anyMatch(compositionProductNumberList::contains)) {
                                            response.putError(attr.name(), "validator.decipherment.compositionStructureChanged");
                                            break validatePoint;
                                        }
                                    }
                                }
                            }
                            // Для "расшифровка затрат на покупные комплектующие изделия" и "расшифровка затрат на сырье и материалы" состав должен совпадать
                            if (deciphermentType.equals(FORM_4) || deciphermentType.equals(FORM_6_1)) {
                                var deciphermentAnother = deciphermentService.getFirstByPeriodIdAndType(decipherment.getPeriod().getId(), deciphermentType.equals(FORM_4) ? FORM_6_1 : FORM_4);
                                if (deciphermentAnother != null) {
                                    var attrValue = deciphermentAttributeValueService.getFirstByDeciphermentAndAttributeKey(deciphermentAnother, COMPOSITION);
                                    if (attrValue == null) {
                                        attrValue = new ProductDeciphermentAttrVal();
                                        attrValue.setDecipherment(deciphermentAnother);
                                        attrValue.setAttribute(COMPOSITION);
                                    }
                                    attrValue.setJsonVal(attrVal);
                                    attrValSaveList.add(attrValue);
                                }
                            }
                            deciphermentAttrVal.setJsonVal(attrVal);
                            attrValSaveList.add(deciphermentAttrVal);
                        }
                        break;
                    case HEAD_PL_EC_DEPARTMENT: {
                        String parameterVal = request.getParameter(ObjAttr.HEAD_ECO_ID);
                        Long headEcoId = NumberUtils.isDigits(parameterVal) ? Long.valueOf(parameterVal) : null;
                        if (headEcoId == null) {
                            response.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED);
                        } else {
                            deciphermentAttrVal.setUser(new User(headEcoId));
                        }
                        attrValSaveList.add(deciphermentAttrVal);
                        break;
                    }
                    case HEAD_CONSTRUCT_DEPARTMENT: {
                        String parameterVal = request.getParameter(ObjAttr.HEAD_CONSTRUCT_ID);
                        Long headConstructId = NumberUtils.isDigits(parameterVal) ? Long.valueOf(parameterVal) : null;
                        if (headConstructId == null) {
                            response.putError(ObjAttr.HEAD_CONSTRUCT_ID, ValidatorMsg.REQUIRED);
                        } else {
                            deciphermentAttrVal.setUser(new User(headConstructId));
                        }
                        attrValSaveList.add(deciphermentAttrVal);
                        break;
                    }
                }
            }
            if (response.isValid()) {
                if (form.getFileStorage() == null || form.getFileStorage().getId() == null) fileStorageService.saveEntityFile(decipherment, FileStorageType.ProductDeciphermentFile.INSTANCE, form.getFile());
                if (!decipherment.getReady() && !decipherment.getApproved()) decipherment.setReady(true);
                deciphermentService.save(decipherment);
                if (!attrValSaveList.isEmpty()) deciphermentAttributeValueService.saveAll(attrValSaveList);
            }
            return Unit.INSTANCE;
        });
        return response;
    }

    @GetMapping("/download-invoices-check")
    public void downloadInvoicesCheck(long deciphermentId) {
        ProductDecipherment decipherment = deciphermentService.read(deciphermentId);
        List<DeciphermentDataInvoiceComponent> dataInvoiceList =
            deciphermentAttributeValueService.readDataJSON(decipherment, INVOICES, new TypeReference<>(){});
        if (CollectionUtils.isEmpty(dataInvoiceList)) {
            throw new AlertUIException("Список накладных пуст");
        }
        if (dataInvoiceList.stream().noneMatch(el -> StringUtils.isNotBlank(el.getFilePath()) || StringUtils.isNotBlank(el.getFileHash()))) {
            throw new AlertUIException("Список накладных пуст");
        }
    }

    // Выгрузка накладных в zip архив
    @GetMapping("/download-invoices")
    public void downloadInvoices(
        HttpServletResponse response,
        long deciphermentId
    ) throws IOException {
        ProductDecipherment decipherment = deciphermentService.read(deciphermentId);
        List<DeciphermentDataInvoiceComponent> dataInvoiceList =
            deciphermentAttributeValueService.readDataJSON(decipherment, INVOICES, new TypeReference<>(){});
        if (CollectionUtils.isEmpty(dataInvoiceList)) {
            return;
        }
        if (dataInvoiceList.stream().noneMatch(el -> StringUtils.isNotBlank(el.getFilePath()) || StringUtils.isNotBlank(el.getFileHash()))) {
            return;
        }
        // Упаковка в zip
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        ZipOutputStream zos = new ZipOutputStream(bos);
        List<String> usedEntryNameList = new ArrayList<>();
        for (var dataInvoice : dataInvoiceList) {
            String filePath = dataInvoice.getFilePath();
            String fileHash = dataInvoice.getFileHash();
            Path entryPath = null;
            if (StringUtils.isNotEmpty(filePath)) {
                Path path = Paths.get(FILE_INVOICE_PATH + filePath);
                if (path.toFile().exists()) {
                    entryPath = path;
                }
            } else if (StringUtils.isNotEmpty(fileHash)) {
                FileStorage<?, ?> fileStorage = fileStorageService.read(Long.valueOf(fileHash));
                if (fileStorage != null) {
                    File file = FileStorageUtil.INSTANCE.file(fileStorage);
                    if (file.exists()) {
                        entryPath = file.toPath();
                    }
                }
            }
            if (entryPath != null) {
                String entryName = entryPath.getFileName().toString();
                if (usedEntryNameList.contains(entryName)) {
                    continue;
                }
                zos.putNextEntry(new ZipEntry(entryName));
                FileInputStream fis = new FileInputStream(entryPath.toFile());
                IOUtils.copy(fis, zos);
                fis.close();
                zos.closeEntry();
                usedEntryNameList.add(entryName);
            }
        }
        zos.finish();
        zos.flush();
        try {
            zos.close();
        } catch (IOException ignored) {}
        try {
            bos.close();
        } catch (IOException ignored) {}
        try {
            baos.close();
        } catch (IOException ignored) {}
        // Выдача в респонз
        var attachmentType = AttachmentMediaType.ZIP;
        response.setContentType(attachmentType.getContentType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
            + MimeUtility.encodeText("накладные." + attachmentType.getExtension(), StandardCharsets.UTF_8.displayName(), "Q") + "\"");
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.getOutputStream().write(baos.toByteArray());
    }

    private Set<InvoiceFileData> generateInvoiceFileDataSet(final Set<InvoiceFileData> dataList) throws IOException {
        Map<InvoiceFileData, Path> result = new HashMap<>();
        Files.walkFileTree(Paths.get(FILE_INVOICE_PATH), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.getFileName().toString().toUpperCase();
                String[] partsArr = fileName.split(StringUtils.SPACE);
                for (var line : dataList) {
                    if (line.number.contains("/")) {
                        String formatNum = line.number.replaceAll(StringUtils.SPACE, StringUtils.EMPTY).replaceAll("/", StringUtils.SPACE).toUpperCase();
                        if (fileName.contains(formatNum)) {
                            putInvoiceFile(result, line, file);
                        }
                    } else {
                        String formatNum1 = line.number.replaceAll("_", "-").toUpperCase();
                        String formatNum2 = line.number.replaceAll("-", "_").toUpperCase();
                        for (String part : partsArr) {
                            String partStr = part.trim();
                            if (Objects.equals(partStr, line.number.toUpperCase()) || Objects.equals(partStr, formatNum1) || Objects.equals(partStr, formatNum2)) {
                                putInvoiceFile(result, line, file);
                            }
                        }
                    }
                }
                return super.visitFile(file, attrs);
            }
        });
        return result.entrySet().stream().map(e -> {
            InvoiceFileData key = e.getKey();
            key.setFileName(e.getValue().getFileName().toString());
            String absolutePath = e.getValue().toFile().getAbsolutePath();
            key.setFilePath(absolutePath.substring(absolutePath.indexOf(FILE_INVOICE_FOLDER) + FILE_INVOICE_FOLDER.length() + 1));
            return key;
        }).collect(Collectors.toSet());
    }

    private final static List<String> SUPPLIER_PREF = List.of("ООО", "ОАО", "ЗАО", "ФГУ ФНЦ НИИСИ", "АНО КБ", "ФГУП", "АО", "ИП");

    private void putInvoiceFile(Map<InvoiceFileData, Path> dataMap, InvoiceFileData data, Path file) throws IOException {
        // Поставщик
        String fileName = file.getFileName().toString().replaceAll("\\.pdf", "").toUpperCase();
        if (fileName.contains("НИИАА") || fileName.contains("ПРОМТЕХ")) return;
        String supplier = StringUtils.isEmpty(data.supplier) ? "" : data.supplier;
        supplier = supplier.replaceAll("\"", "").toUpperCase();
        boolean supplierFound = false;
        if (!supplier.isEmpty()) {
            for (var pref: SUPPLIER_PREF) {
                if (fileName.contains(pref)) {
                    String fileNameSupplier = fileName.substring(fileName.indexOf(pref)).trim();
                    if (fileNameSupplier.startsWith(supplier))  {
                        supplierFound = true;
                    }
                    break;
                }
            }
        }
        if (!supplierFound) {
            return;
        }
        // Дата
        boolean dateFound = false;
        if (StringUtils.isNotBlank(data.dateString)) {
            Path parentFile = file.getParent();
            if (parentFile.toFile().isDirectory() && parentFile.getFileName().toString().contains(data.dateString)) {
                dateFound = true;
            }
        }
        if (!dateFound) {
            return;
        }
        //
        if (dataMap.containsKey(data)) {
            BasicFileAttributes mapFileAttr = Files.readAttributes(dataMap.get(data), BasicFileAttributes.class);
            BasicFileAttributes fileAttr = Files.readAttributes(file, BasicFileAttributes.class);
            if (mapFileAttr.creationTime().toMillis() < fileAttr.creationTime().toMillis()) {
                dataMap.put(data, file);
            }
        } else {
            dataMap.put(data, file);
        }
    }

    // Поиск файла для расшифровки
    @GetMapping(
        value = "/product-invoice/auto-invoice-file",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public InvoiceFileData productInvoiceAutoInvoiceFile(String fileData) throws IOException {
        Set<InvoiceFileData> set = generateInvoiceFileDataSet(Set.of(jsonMapper.readValue(fileData, InvoiceFileData.class)));
        return set.isEmpty() ? null : set.iterator().next();
    }

    // Поиск файлов для расшифровки
    @PostMapping(
        value = "/product-invoice/auto-invoice-file-list",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public Set<InvoiceFileData> productInvoiceAutoInvoiceList(String dataList) throws IOException {
        return generateInvoiceFileDataSet(Set.copyOf(Arrays.asList(jsonMapper.readValue(dataList, InvoiceFileData[].class))));
    }

    // Получение списка версий ЗС для изделия
    // Срабатывает для полей Версии ЗС и Состава
    @GetMapping("/edit/search-version")
    public String searchVersion(
        ModelMap model,
        String mode,
        @RequestParam(value = "productId", required = false) Long productId,
        @RequestParam(value = "productNumber", required = false) String productNumber,
        @RequestParam(value = "selectedVersionId", required = false) Long selectedVersionId
    ) {
        model.addAttribute("mode", mode);
        model.addAttribute("productId", productId);
        model.addAttribute("productNumber", productNumber);
        model.addAttribute("selectedId", selectedVersionId);
        return "prod/include/product/detail/decipherment/searchVersion";
    }

    @GetMapping("/edit/search-version/load")
    @ResponseBody
    public List<?> searchVersionLoad(long productId) {
        @Getter
        class Item {
            long id;
            String version;
            String launches = "";
        }
        List<Item> resultList = Collections.emptyList();
        if (ecoProductService.existById(productId)) {
            List<EcoBom> ecoBomList = ecoBomService.getActualList(productId);
            // Упорядочивание по датам запусков
            ecoBomList.forEach(ecoBom -> ecoBom.getBomAttributeList().sort(Comparator
                .comparing(EcoBomAttribute::getLaunch, Comparator.comparing(EcoLaunch::getYear, Comparator.reverseOrder()))
                .thenComparing(EcoBomAttribute::getLaunch, Comparator.comparing(EcoLaunch::getNumberInYear, Comparator.reverseOrder()))
            ));
            resultList = ecoBomList.stream().map(bom -> {
                Item item = new Item();
                item.id = bom.getId();
                item.version = bom.getVersion();
                bom.getBomAttributeList().forEach(attr -> {
                    item.launches += attr.getLaunch().getFullNumber()
                        + (attr.getApproveDate() == null ? "" : "У") + (attr.getAcceptDate() == null ? "" : "П") + " ";
                });
                return item;
            }).collect(Collectors.toList());
        }
        return resultList;
    }

    // Получение состава для версии ЗС
    @GetMapping("/edit/composition-tree")
    public String compositionTree(
        ModelMap model,
        long deciphermentId,
        long versionId
    ) throws IOException {
        var decipherment = deciphermentService.read(deciphermentId);
        if (decipherment == null) throw new AlertUIException("Расшифровка не найдена");

        var type = decipherment.getType().getEnum();
        // Получение сохраненного состава
        Map<String, String> compositionProductMap = new HashMap<>();
        List<CompositionProduct> compositionProductList = compositionManager.readCompositionProductData(decipherment);
        if (compositionProductList.isEmpty()) {
            // Для "расшифровка затрат на покупные комплектующие изделия" и "расшифровка затрат на сырье и материалы" состав должен совпадать
            // Если расшифровка еще не была создана, то попытка получить состав из возможно созданной расшифровки
            if (type.equals(FORM_6_1) || type.equals(FORM_4)) {
                ProductDecipherment deciphermentOther =
                    deciphermentService.getFirstByPeriodIdAndType(decipherment.getPeriod().getId(), type.equals(FORM_4) ? FORM_6_1 : FORM_4);
                compositionProductList = compositionManager.readCompositionProductData(deciphermentOther);
            }
        }
        // Составление словаря состава
        for (var compositionProduct : compositionProductList) {
            EcoBom ecoBom = ecoBomService.read(compositionProduct.getSelectedVersionId());
            compositionProduct.setSelectedVersionId(ecoBom != null ? ecoBom.getId() : null);
            compositionProduct.setSelectedVersion(ecoBom != null ? ecoBom.getVersion() : null);
            compositionProductMap.put(compositionProduct.getFullHierarchyNumber(), jsonMapper.writeValueAsString(compositionProduct));
        }
        model.addAttribute("compositionProductMap", compositionProductMap);

        // Получение запрещеных изделий в текущем составе, поскольку они используются в других типах расшифровок
        List<ProductDeciphermentTypeEnum> deciphermentTypeList = new ArrayList<>();
        if (type.equals(FORM_4) || type.equals(FORM_6_1)) {
            deciphermentTypeList.add(FORM_6_2);
            deciphermentTypeList.add(FORM_6_3);
        } else if (type.equals(FORM_6_2)) {
            deciphermentTypeList.add(FORM_4);
            deciphermentTypeList.add(FORM_6_1);
            deciphermentTypeList.add(FORM_6_3);
        } else if (type.equals(FORM_6_3)) {
            deciphermentTypeList.add(FORM_4);
            deciphermentTypeList.add(FORM_6_1);
            deciphermentTypeList.add(FORM_6_2);
        }
        Set<String> prohibitedProductNumberSet = new HashSet<>();
        for (var attributeValue : deciphermentAttributeValueService.getAttributeValueList(
                COMPOSITION,
                deciphermentTypeList,
                decipherment.getPeriod().getId()
            )
        ) {
            if (StringUtils.isNotBlank(attributeValue.getJsonVal())) {
                List<CompositionProduct> attrCompositionList = jsonMapper.readValue(attributeValue.getJsonVal(), new TypeReference<>(){});
                attrCompositionList.forEach(attrComposition -> prohibitedProductNumberSet.add(attrComposition.getFullHierarchyNumber()));
            }
        }
        model.addAttribute("prohibitedProductNumberSet", prohibitedProductNumberSet);

        // Получение последней подтвержденной версии по каждому изделию (конкретно по product)
        Map<Long, EcoBom> lastApprovedProductMap = new HashMap<>();
        ecoProductService.getHierarchyProductListByBomId(versionId).forEach(ecoProduct -> lastApprovedProductMap.put(ecoProduct.getId(), ecoBomService.getLastApprovedVersion(ecoProduct.getId())));
        model.addAttribute("lastApprovedProductMap", lastApprovedProductMap);

        model.addAttribute("deciphermentTypeName", type.name());
        model.addAttribute("deciphermentId", deciphermentId);
        model.addAttribute("bom", ecoBomService.read(versionId));
        return "prod/include/product/detail/decipherment/compositionTree";
    }

    // Определение накладных для расшифровки
    @GetMapping("/product-invoice")
    public String productInvoice(
        ModelMap model,
        long deciphermentId
    ) throws IOException {
        ProductDecipherment decipherment = deciphermentService.read(deciphermentId);
        if (decipherment == null) throw new AlertUIException("Расшифровка не найдена");
        if (decipherment.getApproved()) throw new AlertUIException("Форма утверждена. Выбор накладных недоступен");
        List<DeciphermentDataComponent> componentList = compositionManager.getListCompositionComponentData(decipherment);
        model.addAttribute("componentList", componentList);
        model.addAttribute("groupList", asuGrpCompService.getAllByIdList(
            componentList.stream().filter(Objects::nonNull)
                .map(DeciphermentDataComponent::getGroupId).distinct().collect(Collectors.toList()))
        );
        model.addAttribute("deciphermentId", deciphermentId);
        return "prod/include/product/detail/decipherment/productInvoice";
    }

    // Привязанные к расшифровке накладные
    @GetMapping(
        value = "/product-invoice/attached-invoices",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public List<DeciphermentDataInvoiceComponent> productInvoiceAttachedInvoices(Long deciphermentId) throws IOException {
        List<DeciphermentDataInvoiceComponent> invoiceList = Collections.emptyList();
        if (deciphermentService.existsById(deciphermentId)) {
            invoiceList = compositionManager.getComponentInvoiceList(deciphermentService.read(deciphermentId));
        }
        return invoiceList;
    }

    // Сохранение накладных для расшифровки
    @PostMapping(
        value = "/product-invoice/save-invoices",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public void saveProductInvoices(
        @RequestParam Long deciphermentId,
        @RequestParam(defaultValue = "[]") String data,
        @RequestParam(defaultValue = "[]") String manuallyData,
        @RequestParam(required = false) List<MultipartFile> manuallyFileData
    ) throws IOException {
        var decipherment = deciphermentService.read(deciphermentId);
        if (decipherment == null) throw new AlertUIException("Расшифровка не найдена");
        if (decipherment.getApproved()) throw new AlertUIException("Форма утверждена. Сохранение накладных недоступно");
        List<DeciphermentDataInvoiceComponent> dataList = jsonMapper.readValue(data, new TypeReference<>(){});
        List<DeciphermentDataInvoiceComponent> manuallyDataList = jsonMapper.readValue(manuallyData, new TypeReference<>(){});
        // Создание файлов
        List<FileStorage<ProductDecipherment, PluralFileStorableType>> fileList = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(manuallyFileData)) {
            var en = decipherment.getType().getEnum();
            if (en.equals(FORM_4)) {
                fileList = fileStorageService.saveEntityFiles(decipherment, FileStorageType.DeciphermentRawMaterialFile.INSTANCE, manuallyFileData, fileStorageService.readOnePlural(decipherment, FileStorageType.DeciphermentRawMaterialFile.INSTANCE));
            } else if (en.equals(FORM_6_1)) {
                fileList = fileStorageService.saveEntityFiles(decipherment, FileStorageType.DeciphermentPurchasedComponentFile.INSTANCE, manuallyFileData, fileStorageService.readOnePlural(decipherment, FileStorageType.DeciphermentPurchasedComponentFile.INSTANCE));
            } else if (en.equals(FORM_6_2)) {
                fileList = fileStorageService.saveEntityFiles(decipherment, FileStorageType.DeciphermentTareAndPackagingFile.INSTANCE, manuallyFileData, fileStorageService.readOnePlural(decipherment, FileStorageType.DeciphermentTareAndPackagingFile.INSTANCE));
            }
        }
        for (int i = 0; i < fileList.size(); i++) {
            DeciphermentDataInvoiceComponent invoiceData = manuallyDataList.get(i);
            invoiceData.setFileHash(fileList.get(i).getId().toString());
        }
        dataList.addAll(manuallyDataList);
        // Либо прикрепленный файл либо путь к файлу
        dataList.forEach(i -> {
            if (StringUtils.isNotEmpty(i.getFilePath())) {
                i.setFileHash(null);
            }
        });
        // Поиск ранее сохраненного параметра или добавление нового
        var deciphermentAttributeValue = deciphermentAttributeValueService.getFirstByDeciphermentAndAttributeKey(decipherment, INVOICES);
        if (deciphermentAttributeValue == null) {
            deciphermentAttributeValue = new ProductDeciphermentAttrVal();
        } else {
            // Удаляем устаревшие файлы
            List<String> fileHashList = dataList.stream().filter(elem -> StringUtils.isNotBlank(elem.getFileHash())).map(DeciphermentDataInvoiceComponent::getFileHash).collect(Collectors.toList());
            List<DeciphermentDataInvoiceComponent> savedDataList = jsonMapper.readValue(deciphermentAttributeValue.getJsonVal(), new TypeReference<>(){});
            savedDataList.stream().filter(i -> StringUtils.isNotBlank(i.getFileHash()) && !fileHashList.contains(i.getFileHash())).forEach(i -> {
                FileStorage<?, ?> fileStorage = fileStorageService.read(Long.valueOf(i.getFileHash()));
                if (fileStorage != null) {
                    fileStorageService.delete(fileStorage);
                }
            });
        }
        deciphermentAttributeValue.setAttribute(INVOICES);
        deciphermentAttributeValue.setDecipherment(decipherment);
        deciphermentAttributeValue.setJsonVal(jsonMapper.writeValueAsString(dataList));
        if (!decipherment.getReady() && !decipherment.getApproved()) {
            decipherment.setReady(true);
            deciphermentService.save(decipherment);
        }
        deciphermentAttributeValueService.save(deciphermentAttributeValue);
    }

    // Форма фильтра для поиска накладной
    @GetMapping("/search-invoice/filter-invoice")
    public String searchDeciphermentInvoiceForm(ModelMap model) {
        model.addAttribute("plantList", asuPlantService.getAll());
        return "prod/include/product/detail/decipherment/filterInvoice";
    }

    // Форма поиска накладных
    @GetMapping("/product-invoice/search-invoice")
    public String productSearchInvoice(
        ModelMap model,
        @RequestParam(required = false) Long componentId,
        @RequestParam(required = false) String cell,
        @RequestParam(required = false) Long invoiceId,
        Long deciphermentId
    ) {
        //Decipherment decipherment = deciphermentService.read(deciphermentId);
        LocalDate endOn = LocalDate.now();
        model.addAttribute("filterDateFrom", "01.01.2012");
        model.addAttribute("filterDateTo", BaseConstant.INSTANCE.getDATE_FORMATTER().format(endOn));
        model.addAttribute("filterPlantId", 8);
        //
        model.addAttribute("componentId", componentId);
        model.addAttribute("cell", cell);
        model.addAttribute("selectedInvoiceId", invoiceId);
        model.addAttribute("deciphermentId", deciphermentId);
        return "prod/include/product/detail/decipherment/searchInvoice";
    }

    // Загрузка накладных для формы поиска накладных
    @GetMapping(
        value = "/search-invoice/load-invoices",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public List<DeciphermentDataInvoice> loadInvoices(String filterData, String cell) {
        DynamicObject form = KtCommonUtil.INSTANCE.readDynamic(jsonMapper, filterData);
        List<Long> plantIdList = new ArrayList<>();
        plantIdList.add(form.longValue(ObjAttr.PLANT_ID));
        List<DeciphermentDataInvoice> deciphermentDataInvoiceList = asuInvoiceStringService.getDeciphermentInvoices(
            cell, plantIdList, form.date(ObjAttr.DATE_FROM), form.date(ObjAttr.DATE_TO));
        deciphermentDataInvoiceList.sort(Comparator.comparing(DeciphermentDataInvoice::getDate).reversed());
        return deciphermentDataInvoiceList;
    }

    // Прогресс автозаполнения накладных
    @GetMapping("/autofill-invoice/progress-bar")
    public String autofillInvoiceProgressBar() {
        return "prod/include/product/detail/decipherment/additional/invoiceAutofillProgress";
    }

    // Получение данных по компонентам для процесса автозаполнения
    @GetMapping("/autofill-invoice/load-component-data")
    @ResponseBody
    public List<DeciphermentDataComponent> autofillInvoiceLoadComponentData(
        @RequestParam(value = "deciphermentId", required = false) Long deciphermentId
    ) throws IOException {
        return deciphermentService.existsById(deciphermentId)
            ? compositionManager.getListCompositionComponentData(deciphermentService.read(deciphermentId)) : Collections.emptyList();
    }

    // Автозаполнение накладных
    @GetMapping("/autofill-invoice/load-invoice")
    @ResponseBody
    public Object[] autofillInvoiceLoadInvoice(
        @RequestParam(value = "componentId", required = false) Long componentId,
        @RequestParam(value = "cell", required = false) String cell,
        @RequestParam(value = "componentData", defaultValue = "[]") String componentData,
        @RequestParam(value = "deciphermentId", required = false) Long deciphermentId
    ) throws IOException {
        if (!deciphermentService.existsById(deciphermentId) || !StringUtils.isNumeric(cell)) return null;
        //ProductDecipherment decipherment = deciphermentService.read(deciphermentId);
        List<DeciphermentDataComponent> componentList = jsonMapper.readValue(componentData, new TypeReference<>(){});
        LocalDate endDate = LocalDate.now();
        LocalDate beginDate = endDate.minusYears(5);
        List<Long> plantIdList = List.of(8L);

        Object[] result = new Object[]{componentId, null, 1};
        componentList.stream()
            .filter(component -> Objects.equals(componentId, component.getComponentId()))
            .forEach(component -> {
                    // Получаем накладные
                    List<DeciphermentDataInvoice> deciphermentDataInvoiceList = asuInvoiceStringService.getDeciphermentInvoices(cell, plantIdList, beginDate, endDate);
                    // Если не нашли, смещаем интервал начала на 01.01.2012
                    boolean isShiftedSearch = false;
                    if (CollectionUtils.isEmpty(deciphermentDataInvoiceList)) {
                        isShiftedSearch = true;
                        deciphermentDataInvoiceList = asuInvoiceStringService.getDeciphermentInvoices(cell, plantIdList, LocalDate.of(2012, 1, 1), endDate);
                    }
                    // Удаляем те накладные, в которых текущее количество ниже требуемого по расшифровке
                    deciphermentDataInvoiceList.removeIf(invoice -> component.getQuantity() > invoice.getCurrentQuantity());
                    // Берем последнюю по цене накладную. Если на данном шаге есть несколько накладных в списке, то определяем флаг,
                    // который сигнализирует пользователю о наличии альтернативы в цене
                    deciphermentDataInvoiceList.sort(Comparator.comparing(DeciphermentDataInvoice::getPrice).reversed());
                    // Накладная
                    if (CollectionUtils.isNotEmpty(deciphermentDataInvoiceList)) {
                        result[1] = deciphermentDataInvoiceList.get(0);
                    }
                    // Статус
                    if (!isShiftedSearch && CollectionUtils.isNotEmpty(deciphermentDataInvoiceList)) {
                        if (deciphermentDataInvoiceList.size() > 1) { // есть альтернативы по цене
                            result[2] = 2;
                        } else if (deciphermentDataInvoiceList.size() == 1) { // нет альтернатив по цене
                            result[2] = 3;
                        }
                    }
                }
            );
        return result;
    }
}