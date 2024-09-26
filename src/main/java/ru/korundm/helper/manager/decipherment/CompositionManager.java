package ru.korundm.helper.manager.decipherment;

import asu.dao.AsuComponentNameService;
import asu.dao.AsuGrpCompService;
import asu.dao.AsuInvoiceStringService;
import asu.entity.AsuComponentName;
import asu.entity.AsuGrpComp;
import asu.entity.AsuInvoiceString;
import asu.entity.AsuSupplier;
import com.fasterxml.jackson.core.type.TypeReference;
import eco.dao.EcoBomItemComponentService;
import eco.dao.EcoBomSpecItemService;
import eco.entity.EcoBomComponent;
import eco.entity.EcoBomItem;
import eco.entity.EcoBomItemComponent;
import eco.entity.EcoBomSpecItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.korundm.constant.BaseConstant;
import ru.korundm.dao.OkpdCodeService;
import ru.korundm.dao.ProductDeciphermentAttrValService;
import ru.korundm.dao.ProductDeciphermentService;
import ru.korundm.dto.decipherment.CompositionProduct;
import ru.korundm.dto.decipherment.DeciphermentDataComponent;
import ru.korundm.dto.decipherment.DeciphermentDataInvoiceComponent;
import ru.korundm.dto.decipherment.DeciphermentDataProduct;
import ru.korundm.entity.OkpdCode;
import ru.korundm.entity.ProductDecipherment;
import ru.korundm.enumeration.ProductDeciphermentAttr;
import ru.korundm.enumeration.ProductDeciphermentTypeEnum;
import ru.korundm.schedule.importation.process.ComponentCategoryProcess;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс менеджер для работы с составом расшифровок
 * Состав используется для
 *  - расшифровка затрат на сырье и материалы
 *  - расшифровка затрат на покупные комплектующие изделия
 *  - расшифровка затрат на тару и упаковку
 *  - расшифровка затрат на изделия собственного производства
 * Расшифровки взаимозависимы, поэтому класс менеджера для них общий
 * @author mazur_ea
 * Date:   26.09.2019
 */
@Component
public final class CompositionManager {

    private final ProductDeciphermentService productDeciphermentService;
    private final EcoBomSpecItemService ecoBomSpecItemService;
    private final AsuComponentNameService asuComponentNameService;
    private final AsuGrpCompService asuGrpCompService;
    private final AsuInvoiceStringService invoiceStringService;
    private final OkpdCodeService okpdCodeService;
    private final EcoBomItemComponentService ecoBomItemComponentService;
    private final ProductDeciphermentAttrValService productDeciphermentAttrValService;

    public CompositionManager(
        ProductDeciphermentService productDeciphermentService,
        EcoBomSpecItemService ecoBomSpecItemService,
        AsuComponentNameService asuComponentNameService,
        AsuGrpCompService asuGrpCompService,
        AsuInvoiceStringService invoiceStringService,
        OkpdCodeService okpdCodeService,
        EcoBomItemComponentService ecoBomItemComponentService,
        ProductDeciphermentAttrValService productDeciphermentAttrValService
    ) {
        this.productDeciphermentService = productDeciphermentService;
        this.ecoBomSpecItemService = ecoBomSpecItemService;
        this.asuComponentNameService = asuComponentNameService;
        this.asuGrpCompService = asuGrpCompService;
        this.invoiceStringService = invoiceStringService;
        this.okpdCodeService = okpdCodeService;
        this.ecoBomItemComponentService = ecoBomItemComponentService;
        this.productDeciphermentAttrValService = productDeciphermentAttrValService;
    }

    /** Допустимые группы компонентов для расшифровки затрат на сырье и материалы */
    private final static List<String> rawMaterialCostGroupList = List.of(
        "29", "30", "37", "46", "56", "57", "58", "59", "60"
    );

    /** Допустимые группы компонентов для расшифровки затрат на покупные комплектующие изделия */
    private final static List<String> purchasedComponentCostGroupList = List.of(
        "01", "02", "03", "04", "05", "06", "08", "09", "10", "11", "12", "13", "14", "15", "16",
        "18", "19", "22", "23", "24", "28", "31", "32", "33", "43", "52", "54"
    );

    /** Допустимые группы компонентов для которых не нужны единицы измерения */
    private final static List<String> groupNoUnitMeasureList = List.of(
        "28", "29", "30", "37", "46", "54", "56", "57", "58", "59", "60", "66"
    );

    /**
     * Проверка группы-позиции компонента на допустимость использования в документе
     * @param groupPosition группа-позиция компонента
     * @param deciphermentType тип расшифровки {@link ProductDeciphermentTypeEnum}
     * @return true, если группа-позиция допустима к попаданию в документ, иначе false
     */
    private static boolean isGroupPossible(String groupPosition, ProductDeciphermentTypeEnum deciphermentType) {
        List<String> groupList = new ArrayList<>();
        switch (deciphermentType) {
            case FORM_4: groupList.addAll(rawMaterialCostGroupList); break;
            case FORM_6_1: groupList.addAll(purchasedComponentCostGroupList); break;
            case FORM_6_2: return true;
        }
        return StringUtils.isNumeric(groupPosition) && groupPosition.length() == 6 && groupList.contains(StringUtils.substring(groupPosition, 0, 2));
    }

    /**
     * Получение корневого элемента иерархии изделий
     * @param decipherment данные расшифровки {@link ProductDecipherment}
     * @return корневой элемент иерархии {@link DeciphermentDataProduct}
     * @throws IOException если не смогли прочитать данные по изделиям состава расшифровки {@link CompositionProduct}
     */
    public DeciphermentDataProduct createRootDataProduct(ProductDecipherment decipherment) throws IOException {
        ProductDeciphermentTypeEnum deciphermentType = decipherment.getType().getEnum();
        List<CompositionProduct> compositionProductList = readCompositionProductData(decipherment);

        // Построение иерархии изделий для отчета
        // Для начала оборачиваем изделия состава в объекты иерархии
        // и сортируем так, чтобы все корневые изделия иерархии были в конце списка (этакий стек, где внизу лежат все корневые элементы - от n до 1 уровня, где 1 - самый верхний)
        List<DeciphermentDataProduct> deciphermentDataProductList = new ArrayList<>();
        for (var compositionProduct : compositionProductList) {
            // Заполнение элемента данных расшифровки
            DeciphermentDataProduct deciphermentDataProduct = new DeciphermentDataProduct();
            readProductData(deciphermentDataProduct, compositionProduct, decipherment);
            deciphermentDataProduct.setUniqueNumber(compositionProduct.getFullHierarchyNumber()); // По этому номеру выстраивается алгоритм иерархии
            deciphermentDataProduct.setUniqueParentNumber(compositionProduct.getParentFullHierarchyNumber()); // По этому номеру выстраивается алгоритм иерархии
            deciphermentDataProductList.add(deciphermentDataProduct);
        }
        deciphermentDataProductList.sort(Comparator.comparing(deciphermentDataProduct ->
            StringUtils.countMatches(deciphermentDataProduct.getUniqueNumber(), BaseConstant.UNDERSCORE), Comparator.reverseOrder()));

        // Алгоритм сбора иерархии
        Set<String> processedProductNumberSet = new HashSet<>(); // набор отработаных номеров изделий копозиции
        while (true) {
            // Определяем изделие для обработки - берется по порядку из списка, и помечается обработанным в наборе
            DeciphermentDataProduct processedDataProduct = null;
            for (var deciphermentDataProduct : deciphermentDataProductList) {
                if (!processedProductNumberSet.contains(deciphermentDataProduct.getUniqueNumber())) {
                    processedDataProduct = deciphermentDataProduct;
                    processedProductNumberSet.add(deciphermentDataProduct.getUniqueNumber());
                    break;
                }
            }
            if (processedDataProduct == null) break;
            // Попытка привязать обрабатываемое изделие к своему предку, если успешно - то необходимо удалить изделие из списка
            // т.к оно уже будет находится в подсписке
            boolean toRemove = false;
            for (var deciphermentDataProduct : deciphermentDataProductList) {
                if (processedDataProduct.getUniqueParentNumber().equals(deciphermentDataProduct.getUniqueNumber())) {
                    processedDataProduct.setParentProduct(deciphermentDataProduct);
                    deciphermentDataProduct.getSubProductList().add(processedDataProduct);
                    toRemove = true;
                    break;
                }
            }
            if (toRemove) deciphermentDataProductList.remove(processedDataProduct);
        }

        // Определяем корневой элемент
        DeciphermentDataProduct rootDataProduct = new DeciphermentDataProduct();
        CompositionProduct compositionProduct = new CompositionProduct(); // в данном случае заполнится только версия
        compositionProduct.setSelectedVersionId(productDeciphermentAttrValService.readLongValue(decipherment, ProductDeciphermentAttr.PURCHASE_SPECIFICATION_VERSION));
        // Для полученного списка необходимо проставить верхний корневой элемент как родительский
        for (var deciphermentDataProduct : deciphermentDataProductList) {
            deciphermentDataProduct.setParentProduct(rootDataProduct);
        }
        rootDataProduct.setSubProductList(deciphermentDataProductList);
        // Для "расшифровка затрат на тару и упаковку" данные по корневому изделию не нужны
        if (!deciphermentType.equals(ProductDeciphermentTypeEnum.FORM_6_2)) {
            readProductData(rootDataProduct, compositionProduct, decipherment);
        }

        return rootDataProduct;
    }

    /**
     * Метод заполнения изделия данными о самом изделии и его компонентах
     * @param deciphermentDataProduct изделие иерархии состава {@link DeciphermentDataProduct}
     * @param compositionProduct изделие состава расшифровки (сохраненное в окне редактирования) {@link CompositionProduct}
     * @param decipherment расшифровка
     */
    private void readProductData(DeciphermentDataProduct deciphermentDataProduct, CompositionProduct compositionProduct, ProductDecipherment decipherment) {
        if (compositionProduct == null) return;
        // Получение спецификации изделия и определение ключевых параметров
        EcoBomSpecItem ecoBomSpecItem = ecoBomSpecItemService.findFirstByIdAndProductIdAndVersionId(
            compositionProduct.getSpecificationId(),
            compositionProduct.getProductId(),
            compositionProduct.getVersionId()
        );
        if (ecoBomSpecItem != null) {
            var ecoProduct = ecoBomSpecItem.getProduct();
            var dNumber = StringUtils.defaultString(ecoProduct.getDNumber(), "").trim();
            var productName = ecoProduct.getFullName();
            if (ecoProduct.getFullName().contains(dNumber) && !dNumber.isEmpty()) productName = ecoProduct.getFullName();
            else if (!dNumber.isEmpty()) productName = ecoProduct.getFullName() + " " + dNumber + "ТУ";

            deciphermentDataProduct.setName(productName);
            deciphermentDataProduct.setProductCount(ecoBomSpecItem.getSubProductCount());
        }
        // Получение компонентов для изделия и их ключевых данных
        deciphermentDataProduct.setComponentList(readComponentData(compositionProduct, decipherment));
    }

    /**
     * Метод получения данных по изделиям для "расшифровки затрат на изделия собственного производства"
     * @param decipherment расшифровка
     * @return список изделий {@link DeciphermentDataProduct}
     */
    public List<DeciphermentDataProduct> readOwnProductionProductData(ProductDecipherment decipherment) throws IOException {
        List<DeciphermentDataProduct> productList = new ArrayList<>();
        // Получаем состав
        List<CompositionProduct> compositionProductList = readCompositionProductData(decipherment);
        compositionProductList.forEach(compositionProduct -> {
            EcoBomSpecItem ecoBomSpecItem = ecoBomSpecItemService.findFirstByIdAndProductIdAndVersionId(
                compositionProduct.getSpecificationId(),
                compositionProduct.getProductId(),
                compositionProduct.getVersionId()
            );
            if (ecoBomSpecItem != null) {
                var ecoProduct = ecoBomSpecItem.getProduct();
                var dNumber = StringUtils.defaultString(ecoProduct.getDNumber(), "").trim();
                var productName = ecoProduct.getFullName();
                if (ecoProduct.getFullName().contains(dNumber) && !dNumber.isEmpty()) productName = ecoProduct.getFullName();
                else if (!dNumber.isEmpty()) productName = ecoProduct.getFullName() + " " + dNumber + "ТУ";

                DeciphermentDataProduct deciphermentDataProduct = new DeciphermentDataProduct();
                deciphermentDataProduct.setName(productName);
                deciphermentDataProduct.setProductCount(ecoBomSpecItem.getSubProductCount());
                productList.add(deciphermentDataProduct);
            }
        });
        return productList;
    }

    /**
     * Метод чтения данных по компонентам для изделия состава расшифровки
     * @param compositionProduct изделие состава расшифровки (сохраненное в окне редактирования) {@link CompositionProduct}
     * @param decipherment - расшифровка {@link ProductDecipherment}
     * @return список компонентов для изделия иерархии {@link DeciphermentDataComponent}
     */
    private List<DeciphermentDataComponent> readComponentData(CompositionProduct compositionProduct, ProductDecipherment decipherment) {
        List<DeciphermentDataComponent> resultList = new ArrayList<>();
        // Получаем компоненты спецификации с KD = 1 (флаг KD = 1 только у одного компонента в bomItem)
        List<EcoBomItemComponent> rawItemComponentList = ecoBomItemComponentService.getByBomIdAndKd(compositionProduct.getSelectedVersionId(), Boolean.TRUE);
        // Для некоторых категорий существует особый расчет количества
        rawItemComponentList.forEach(itemComponent -> {
            int orderId = itemComponent.getComponent().getCategory().getOrderId();
            if ((orderId >= 20 && orderId <= 320) || orderId == 350 || orderId == 360) {
                itemComponent.getBomItem().setQuantity(1D);
            }
        });
        // Агрегированный список компонентов
        // Это значит что все bomItemComponent с KD = 1 объединены в одну позицию по bomComponent.id
        DeciphermentDataComponent deciphermentDataComponent;
        for (var itemComponent : rawItemComponentList) {
            deciphermentDataComponent = resultList.stream().filter(item -> Objects.equals(item.getComponentId(), itemComponent.getComponent().getId())).findFirst().orElse(null);
            if (deciphermentDataComponent == null) {
                deciphermentDataComponent = new DeciphermentDataComponent();
                deciphermentDataComponent.setComponentId(itemComponent.getComponent().getId());
                deciphermentDataComponent.setEcoName(itemComponent.getComponent().getName());
                deciphermentDataComponent.setCell(itemComponent.getComponent().getCell());
                deciphermentDataComponent.setQuantity(itemComponent.getBomItem().getQuantity());
                deciphermentDataComponent.setDescription(itemComponent.getComponent().getDescription());
                deciphermentDataComponent.setUnitMeasure(defineUnitMeasure(itemComponent.getComponent()));
                resultList.add(deciphermentDataComponent);
            } else {
                deciphermentDataComponent.setQuantity(BigDecimal.valueOf(deciphermentDataComponent.getQuantity()).add(BigDecimal.valueOf(itemComponent.getBomItem().getQuantity())).doubleValue());
            }
        }
        // Получаем словарь глобальных замен (rawItemComponentList - список который содержит компоненты с KD = 1)
        Map<EcoBomItem, EcoBomComponent> globalReplacementMap = rawItemComponentList.stream()
            .filter(itemComponent -> itemComponent.getComponent().getPurchaseComponent() != null)
            .collect(Collectors.toMap(EcoBomItemComponent::getBomItem, itemComponent -> itemComponent.getComponent().getPurchaseComponent()));

        // Определяем компоненты закупки
        for (var dataComponent : resultList) {
            // Получаем список bomItem в который включена искомая компонента
            List<EcoBomItem> itemList = rawItemComponentList.stream()
                .filter(itemComponent -> Objects.equals(itemComponent.getComponent().getId(), dataComponent.getComponentId()))
                .map(EcoBomItemComponent::getBomItem)
                .distinct()
                .collect(Collectors.toList());
            // Получаем все возможные bomItemComponent
            List<EcoBomItemComponent> itemComponentList = new ArrayList<>();
            itemList.forEach(item -> itemComponentList.addAll(item.getEcoBomItemComponentList()));

            // Для полученных itemComponent в пределах bomItem, ищем компонент к закупке
            Map<EcoBomItem, EcoBomItemComponent> finalPurchaseMap = new HashMap<>();
            itemList.forEach(item -> {
                // Только со статусом - Разрешен
                List<EcoBomItemComponent> allowedItemComponentList = itemComponentList.stream()
                    .filter(itemComponent -> Objects.equals(item, itemComponent.getBomItem()) && itemComponent.getStatus().getId() == 2)
                    .collect(Collectors.toList());
                // Выбор компонента закупки по приоритету purchased > getComponent().getPurchaseComponent() > kd
                // Закупка - в пределах bomItem может быть только один флаг закупки purchased
                List<EcoBomItemComponent> foundItemComponentList = allowedItemComponentList.stream().filter(EcoBomItemComponent::isPurchase).collect(Collectors.toList());
                // Глобальная замена - замена по всему справочнику - в пределах bomItem может быть только одна глобальная замена getComponent().getPurchaseComponent()
                if (foundItemComponentList.isEmpty()) {
                    EcoBomComponent replacementComponent = globalReplacementMap.get(item);
                    foundItemComponentList = allowedItemComponentList.stream()
                        .filter(itemComponent -> Objects.equals(replacementComponent, itemComponent.getComponent()))
                        .collect(Collectors.toList());
                }
                // Компонент kd - в пределах bomItem может быть только один флаг KD
                if (foundItemComponentList.isEmpty()) {
                    foundItemComponentList = allowedItemComponentList.stream().filter(EcoBomItemComponent::isKd).collect(Collectors.toList());
                }
                if (foundItemComponentList.size() == 1) {
                    finalPurchaseMap.put(item, foundItemComponentList.get(0));
                }
            });

            // Вычисляем некорректные замены
            // Исторически была ошибка, что замену можно было сделать не для агрегированного (показывать позиционные обозначения), и это породило ошибки
            // В нормальной ситуации замену можно делать только для агрегированного списка
            // Выполнив замену для позиции в агрегированном виде (не показывать позиционные обозначения) для всех bomItemComponent, входящих в эту позицию
            // создадутся одинаковые данные по заменам
            // Поэтому в этих созданных заменах не должно быть расхождения
            // TODO при переносе эта часть не будет нужна
            if (!finalPurchaseMap.isEmpty()) {
                boolean isCorrect = false;
                for (var item : itemList) {
                    EcoBomItemComponent replacementItemComponent = finalPurchaseMap.get(item);
                    isCorrect = finalPurchaseMap.entrySet().stream().allMatch(entry ->
                        replacementItemComponent != null
                        && Objects.equals(replacementItemComponent.getComponent(), entry.getValue().getComponent())
                        && replacementItemComponent.isKd() == entry.getValue().isKd()
                        && replacementItemComponent.isPurchase() == entry.getValue().isPurchase()
                        && Objects.equals(replacementItemComponent.getStatus(), entry.getValue().getStatus())
                        && Objects.equals(replacementItemComponent.getDateReplaced(), entry.getValue().getDateReplaced())
                        && Objects.equals(replacementItemComponent.getDateProcessed(), entry.getValue().getDateProcessed())
                    );
                    if (!isCorrect) {
                        break; // Найдено хотябы одно расхождение
                    }
                }

                // Если расхождений не найдено берем любой элемент замены - они одинаковы
                if (isCorrect) {
                    EcoBomItemComponent itemComponent = finalPurchaseMap.entrySet().iterator().next().getValue();
                    String originalName = dataComponent.getEcoName();
                    String finalName = itemComponent.getComponent().getName();
                    if (!Objects.equals(originalName, finalName)) {
                        dataComponent.setEcoName(String.format("%s (%s)", originalName, finalName));
                    }
                    dataComponent.setCell(itemComponent.getComponent().getCell());
                }
            }
        }

        // Если компонент имеет не допустимую группу, то обрабатывать его не нужно
        resultList.removeIf(dataComponent -> !isGroupPossible(dataComponent.getCell(), decipherment.getType().getEnum()));

        // Данные по ASU/ERP
        resultList.forEach(dataComponent -> {
            AsuComponentName asuComponentName = asuComponentNameService.getByCellAndAnalog(dataComponent.getCell(), 0L);
            if (asuComponentName != null) {
                dataComponent.setAsuName(asuComponentName.getName());
            }
            AsuGrpComp asuGrpComp = asuGrpCompService.getByCell(dataComponent.getCell());
            if (asuGrpComp != null) {
                dataComponent.setGroupId(asuGrpComp.getId());
                dataComponent.setGroupName(asuGrpComp.getNazGrp());
            }
            OkpdCode okpdCode = okpdCodeService.getLastByCell(dataComponent.getCell());
            if (okpdCode != null) {
                dataComponent.setOkpdCode(okpdCode.getCode());
            }
        });

        resultList.sort(Comparator.comparing(DeciphermentDataComponent::getCell));
        return resultList;
    }

    /**
     * Метод получения полного списка компонентов для всех изделий в составе
     * @param decipherment информация о расшифровке {@link ProductDecipherment}
     * @return список компонентов для всех изделий в составе {@link DeciphermentDataComponent}
     * @throws IOException если не смогли прочитать данные по изделиям состава расшифровки {@link CompositionProduct}
     */
    public List<DeciphermentDataComponent> getListCompositionComponentData(ProductDecipherment decipherment) throws IOException {
        List<DeciphermentDataComponent> componentList = new ArrayList<>();
        Long rootProductVersionId = productDeciphermentAttrValService.readLongValue(decipherment, ProductDeciphermentAttr.PURCHASE_SPECIFICATION_VERSION);
        if (rootProductVersionId != null && !ProductDeciphermentTypeEnum.FORM_6_2.equals(decipherment.getType().getEnum())) {
            CompositionProduct compositionProduct = new CompositionProduct();
            compositionProduct.setSelectedVersionId(rootProductVersionId);
            componentList.addAll(readComponentData(compositionProduct, decipherment));
        }
        readCompositionProductData(decipherment).forEach(compositionProduct -> componentList.addAll(readComponentData(compositionProduct, decipherment)));
        // Суммируем компоненты
        List<DeciphermentDataComponent> finalList = new ArrayList<>();
        DeciphermentDataComponent deciphermentDataComponent;
        for (var component : componentList) {
            deciphermentDataComponent = finalList.stream().filter(item -> Objects.equals(item.getComponentId(), component.getComponentId())).findFirst().orElse(null);
            if (deciphermentDataComponent == null) {
                finalList.add(component);
            } else {
                deciphermentDataComponent.setQuantity(BigDecimal.valueOf(deciphermentDataComponent.getQuantity()).add(BigDecimal.valueOf(component.getQuantity())).doubleValue());
            }
        }
        return finalList;
    }

    /**
     * Метод получения накладных компонентов
     * @param decipherment информация о расшифровке {@link ProductDecipherment}
     * @return список накладных компонентов {@link DeciphermentDataInvoiceComponent}
     * @throws IOException если не смогли прочитать список компонентов {@link DeciphermentDataInvoiceComponent}
     */
    public List<DeciphermentDataInvoiceComponent> getComponentInvoiceList(ProductDecipherment decipherment) throws IOException {
        List<DeciphermentDataInvoiceComponent> dataInvoiceList = productDeciphermentAttrValService.readDataJSON(decipherment, ProductDeciphermentAttr.INVOICES, new TypeReference<>(){});
        if (dataInvoiceList == null) {
            dataInvoiceList = Collections.emptyList();
        }
        List<DeciphermentDataInvoiceComponent> removeList = new ArrayList<>();
        for (var dataInvoice : dataInvoiceList) {
            AsuInvoiceString invoiceString = invoiceStringService.read(dataInvoice.getInvoiceId());
            if (invoiceString != null) {
                dataInvoice.setName(invoiceString.getInvoice().getName());
                dataInvoice.setDate(Instant.ofEpochMilli(invoiceString.getInvoice().getDateIn()).atZone(ZoneId.systemDefault()).toLocalDate());
                dataInvoice.setPrice(BigDecimal.valueOf(invoiceString.getPrice()).divide(BigDecimal.valueOf(invoiceString.getCoeff()), 2, RoundingMode.HALF_UP).doubleValue());
                AsuSupplier supplier = invoiceString.getInvoice().getSupplier();
                if (supplier != null) {
                    dataInvoice.setSupplier(supplier.getName());
                    dataInvoice.setInn(supplier.getInn());
                }
                if (invoiceString.getOkei() != null) {
                    dataInvoice.setUnitMeasure(invoiceString.getOkei().getSymbolNat());
                }
            } else { // Когда накладная привязана к расшифровке, но в БД ее нет
                removeList.add(dataInvoice);
            }
        }
        if (!removeList.isEmpty()) {
            dataInvoiceList.removeAll(removeList);
        }
        return dataInvoiceList;
    }

    /**
     * Метод получение изделий состава из JSON атрибута расшифровки в БД
     * @param decipherment данные о расшифровке {@link ProductDecipherment}
     * @return список изделий состава {@link CompositionProduct}
     * @throws IOException если не смогли преобразовать JSON строку в объект {@link CompositionProduct}
     */
    public List<CompositionProduct> readCompositionProductData(ProductDecipherment decipherment) throws IOException {
        List<CompositionProduct> compositionProductList = productDeciphermentAttrValService.readDataJSON(decipherment, ProductDeciphermentAttr.COMPOSITION, new TypeReference<>(){});
        if (compositionProductList == null) {
            compositionProductList = Collections.emptyList();
        }
        // Те изделия, что не соответствуют иерархии состава обработаны не будут
        if (!compositionProductList.isEmpty()) {
            compositionProductList.removeAll(productDeciphermentService.inapplicableCompositionProductList(compositionProductList));
        }
        return compositionProductList;
    }

    // форматирование названия группы
    public static String formatGroupName(String group) {
        if (group != null) {
            group = group.replace("(см)", "");
            group = group.replace("(см. кв.)", "");
            group = group.replace("(см.)", "");
            group = group.replace("(мм. кв.)", "");
            group = group.replace(" кг", "");
            group = group.replace("(Диаметр х Длина)", "");
            group = group.replace("(ДС)", "");
            group = group.replace("(БРИЗ)", "");
            group = group.replace("(нормаль)", "");
            group = group.trim();
        }
        return group;
    }

    // Определение единицы измерения
    private static String defineUnitMeasure(EcoBomComponent comp) {
        String cell = comp.getCell();
        if (StringUtils.isNumeric(cell) && cell.length() == 6) {
            String grPos = StringUtils.substring(cell, 0, 2);
            if (groupNoUnitMeasureList.contains(grPos)) return StringUtils.EMPTY;
            // штучные категории, иначе берем единицу измерения
            if (ComponentCategoryProcess.unitCategoryIdList.contains(comp.getCategory().getId())) {
                return "шт";
            } else {
                return comp.getUnit() == null ? StringUtils.EMPTY : comp.getUnit().getName();
            }
        }
        return StringUtils.EMPTY;
    }

    // Форматирование названия компоненты
    public static String formattedName(DeciphermentDataComponent component) {
        String name = component.getEcoName();
        String cell = component.getCell();
        String description = component.getDescription();
        if (StringUtils.isNumeric(cell) && cell.length() == 6 && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(description)) {
            description = description.trim();
            String groupPosition = StringUtils.substring(cell, 0, 2);
            if (groupPosition.equals("24")) {
                String[] arrDescription = description.split(StringUtils.SPACE);
                for (var elem : arrDescription) {
                    elem = elem.trim();
                    elem = elem.replaceAll(",", "");
                    Pattern pattern = Pattern.compile("^\\D+\\.\\d+\\.\\d+$");
                    Matcher matcher = pattern.matcher(elem);
                    if (matcher.matches()) {
                        name = name + StringUtils.SPACE + elem;
                        break;
                    }
                }
            } else if (groupPosition.equals("33")) {
                String searchString = null;
                if (name.contains("изм.")) {
                    searchString = "изм.";
                } else if (name.contains("б/изм")) {
                    searchString = "б/изм";
                }
                if (searchString != null) {
                    name = StringUtils.substring(name, 0, name.indexOf(searchString) - 1);
                }
                name = description + StringUtils.SPACE + name.trim();
            }
        }
        return name;
    }
}