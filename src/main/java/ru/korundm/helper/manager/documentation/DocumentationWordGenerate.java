package ru.korundm.helper.manager.documentation;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import ru.korundm.constant.BaseConstant;
import ru.korundm.dto.letter.LetterAllotmentItemDto;
import ru.korundm.dto.prod.WarehouseStateMatValueDto;
import ru.korundm.dto.prod.WarehouseStateReportFirstDocDto;
import ru.korundm.dto.prod.WarehouseStateReportSecondDocDto;
import ru.korundm.dto.prod.WarehouseStateResidueDocDto;
import ru.korundm.entity.*;
import ru.korundm.enumeration.ContractType;
import ru.korundm.enumeration.InvoiceType;
import ru.korundm.report.word.helper.CTBorderProperties;
import ru.korundm.report.word.util.*;
import ru.korundm.util.CommonUtil;
import ru.korundm.util.KtCommonUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public final class DocumentationWordGenerate {

    private static final String BLANK_WORLD = "blank" + File.separator + "word";

    /**
     * Сопроводительное письмо Заказчику
     */
    public XWPFDocument coveringCustomerLetterGenerate(Contract contract, ContractSection section, Invoice invoice, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "customLetter.docx"));
        Map<String, String> map = new HashMap<>();

        if (section.getNumber() != 0) {
            var sectionName = StringUtils.isEmpty(section.getExternalNumber()) ? String.valueOf(section.getNumber()) : section.getExternalNumber();
            map.put("DOP", sectionName);
        }

        map.put("CUSTOMST", contract.getCustomer().getChiefPosition());
        map.put("CUSTOMER", contract.getCustomer().getName());
        map.put("CUSNAM", contract.getCustomer().getChiefName());
        map.put("CUSTOMADDR", contract.getCustomer().getJuridicalAddress());
        map.put("CUSTOMFAX", getFaxNumber(contract.getCustomer().getPhoneNumber()));
        map.put("CONTRACTN", contract.getFullNumber());
        map.put("DATE", section.getCreateDate().format(BaseConstant.INSTANCE.getDATE_FORMATTER()));

        map.put("SUBJECT", contract.getType() == ContractType.REPAIR_OTK || contract.getType() == ContractType.REPAIR_PZ
            ? "проведение ремонтных работ" : "поставку продукции специального назначения");

        map.put("INVOICE", invoice.getNumber().toString());
        map.put("USER", user.getUserOfficialName());

        return replaceTagInDocument(document, map);
    }

    /**
     * Текст договора
     */
    public XWPFDocument contractGenerate(ContractSection section, Account korundAccount, Account customerAccount, BigDecimal totalCost, BigDecimal vat, Company korund) throws IOException {
        var fileName = "contract.docx";
        var contract = section.getContract();
        if (contract.getType() == ContractType.SUPPLY_OF_EXPORTED) {
            fileName = "exportContract.docx";
        } else if (contract.getType() == ContractType.REPAIR_OTK) {
            fileName = "repairContract_OTK.docx";
        } else if (contract.getType() == ContractType.REPAIR_PZ) {
            fileName = "repairContract_PZ.docx";
        }

        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, fileName));
        Map<String, String> map = new HashMap<>();

        LocalDate currentDate = LocalDate.now();
        map.put("YEAR", String.valueOf(currentDate.getYear()));
        map.put("CONTRACTN", contract.getFullNumber());
        map.put("DATE", section.getCreateDate().format(BaseConstant.INSTANCE.getDATE_FORMATTER()));
        map.put("TOTALPRICE", totalCost.toString());
        map.put("TOTALWORD", CommonUtil.moneyToWords(totalCost));
        map.put("VATDIGIT", vat.toString());
        map.put("VATWORD", CommonUtil.moneyToWords(vat));

        map.put("OAOIN", korund.getInn());
        map.put("OAOKPP", korund.getKpp());
        map.put("OAOPOSTAL", korund.getMailAddress());
        map.put("OAOPH", korund.getPhoneNumber());

        map.put("OAOAC", korundAccount.getAccount());
        map.put("OAOBANK", korundAccount.getBank().getName());
        map.put("OAOBL", korundAccount.getBank().getLocation());
        map.put("OAOKS", korundAccount.getBank().getCorrespondentAccount());
        map.put("OAOBIK", korundAccount.getBank().getBik());

        var customer = contract.getCustomer();
        map.put("CUSTOMER", customer.getName());
        map.put("CUSTOMINN", customer.getInn());
        map.put("CUSTKPP", customer.getKpp());
        map.put("CUSTOMST", customer.getChiefPosition());
        map.put("CUSNAM", customer.getChiefName());
        map.put("CUSTOMADDR", customer.getMailAddress());
        map.put("CUSTOMPH", customer.getPhoneNumber());
        map.put("CUSTINSP", customer.getInspectorName());
        map.put("CUSINNAM", customer.getInspectorHead());

        map.put("CUSTOMAC", customerAccount.getAccount());
        map.put("CUSTBANK", customerAccount.getBank().getName());
        map.put("CUSTBL", customerAccount.getBank().getLocation());
        map.put("CUSTKS", customerAccount.getBank().getCorrespondentAccount());
        map.put("CUSTBIK", customerAccount.getBank().getBik());

        return replaceTagInDocument(document, map);
    }

    /**
     * Ведомость исполнения
     */
    public XWPFDocument executionGenerate(Contract contract, ContractSection section, BigDecimal totalSum, BigDecimal totalVat) throws IOException {
        var fileName = "deliveryList.docx";
        if (contract.getType() == ContractType.REPAIR_OTK || contract.getType() == ContractType.REPAIR_PZ) {
            fileName = "executionList.docx";
        }

        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, fileName));
        Map<String, String> map = new HashMap<>();

        LocalDate currentDate = LocalDate.now();
        map.put("YEAR", String.valueOf(currentDate.getYear()));
        map.put("CONTRACTN", contract.getFullNumber());

        var sectionName = Objects.equals(section.getExternalNumber(), "") ? String.valueOf(section.getNumber()) : section.getExternalNumber();

        if (section.getNumber() == 0) {
            map.put("ADAG", "");
            map.put("SECTION", "договору");
        } else {
            map.put("ADAG", "к дополнительному соглашению №" + sectionName + " ");
            map.put("SECTION", "дополнительному соглашению №" + sectionName);
        }

        try {
            map.put("DATE", getMainContractSection(contract).getCreateDate().format(BaseConstant.INSTANCE.getDATE_FORMATTER()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.put("TOTALCOST", totalSum.toString());
        map.put("TOTWITHV", totalSum.add(totalVat).toString());
        map.put("VATSUM", totalVat.toString());
        map.put("TOTALWORD", CommonUtil.moneyToWords(totalSum.add(totalVat)));
        map.put("VATWORD", CommonUtil.moneyToWords(totalVat));

        if (contract.getCustomer() != null) {
            map.put("CUSTOMER", contract.getCustomer().getName());
            map.put("CUSTOMST", contract.getCustomer().getChiefPosition());
            map.put("CUSNAM", contract.getCustomer().getChiefName());
            map.put("CUSTINSP", contract.getCustomer().getInspectorName());
            map.put("CUSINNAM", contract.getCustomer().getInspectorHead());
        }
        if (document.getTables() != null) {
            var table = document.getTables().get(0);

            var lotGroup = section.getLotGroupList();
            var rowNum = 0; //счетчик строк
            long q;
            var noteItems = "";

            for (var i = 0; i < lotGroup.size(); i++) {
                var temp = "";
                // кол-во ContractItem'ов с одинак ценой (колонка "кол-во") :
                double lotCount = 0.0; // численное
                var lotCountString = ""; //строковое (с учетом знаков "\n")
                // значение суммы без НДС :
                BigDecimal sumNew = BigDecimal.ZERO;           // численное
                var sumString = ""; // строковое (с учетом знаков "\n")

                var priceString = "";    // строка для записи цены (с учетом знаков "\n")
                var countEnter = "";  // строка для накапливания \n

                var group = lotGroup.get(i);
                var subList = group.getLotList();

                var name = group.getProduct().getProductionName() + "\n" + group.getProduct().getDecimalNumber();

                if (!group.getServiceType().getName().toLowerCase(Locale.ROOT).contains("изготовление")) {
                    name = group.getServiceType().getName() + "\n" + name;
                }
                if (group.getProduct().getLetter().getName().equals("О") || group.getProduct().getLetter().getName().equals("-")) {
                    if (noteItems.length() > 0) {
                        noteItems += ", ";
                    }
                    noteItems += String.valueOf(i + 1);
                }

                for (var j = 0; j < subList.size(); j++, rowNum++) {
                    var lot = subList.get(j);
                    q = lot.getAmount();
                    var deliveryDate = lot.getDeliveryDate();
                    var price = lot.getPrice().toString();

                    if (temp.length() > 0) {
                        temp += "\n";
                    }
                    temp += (int) q + " шт. - " + deliveryDate.format(DateTimeFormatter.ofPattern("dd.MM.yy"));

                    if (j != 0 && (subList.get(j - 1).getPrice() == lot.getPrice())) {
                        lotCount += q;
                        countEnter += "\n";
                        sumNew = sumNew.add(lot.getPrice().multiply(new BigDecimal(q)));
                    } else {
                        if (j == 0) {
                            lotCount += q;
                            priceString += price + "\n";
                        } else {
                            lotCountString += (int) lotCount + countEnter + "\n";
                            priceString += countEnter + price + "\n";
                            sumString += sumNew + countEnter + "\n";
                        }
                        countEnter = "";
                        lotCount = (double) q;
                        sumNew = lot.getPrice().multiply(new BigDecimal(q));
                    }
                    if (j == subList.size() - 1) {
                        lotCountString += (int) lotCount + countEnter;
                        sumString += sumNew + countEnter;
                    }
                }

                var row = table.createRow();
                row.getCell(0).setText((i + 1) + ".");
                row.getCell(1).setText(name);
                row.getCell(2).setText(lotCountString);
                row.getCell(3).setText(priceString);
                row.getCell(4).setText(sumString);
                if (contract.getType() != ContractType.REPAIR_OTK && contract.getType() != ContractType.REPAIR_PZ) {
                    row.getCell(5).setText(temp);
                }
            }
            if (contract.getType() != ContractType.REPAIR_OTK && contract.getType() != ContractType.REPAIR_PZ) {
                if (noteItems.length() > 0) {
                    map.put("NOTE", "п. " + noteItems);
                } else {
                    //удаляем таблицу с примечанием, если нет не прошедших гос.проверку изделий
                    document.removeBodyElement(document.getPosOfTable(document.getTables().get(1)));
                }
            }
        }
        return replaceTagInDocument(document, map);
    }

    /**
     * Протокол согласования разногласий
     */
    public XWPFDocument disputeReconciliationProtocol(ContractSection section) throws IOException {
        var fileName = "diffProtocol.docx";
        var contract = section.getContract();
        if (contract.getType() == ContractType.REPAIR_OTK) {
            fileName = "diffProtocol_Repair_OTK.docx";
        } else if (contract.getType() == ContractType.REPAIR_PZ) {
            fileName = "diffProtocol_Repair_PZ.docx";
        }

        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, fileName));
        Map<String, String> map = new HashMap<>();

        LocalDate currentDate = LocalDate.now();
        map.put("YEAR", String.valueOf(currentDate.getYear()));

        map.put("CONTRACTNAME", contract.getFullNumber());

        var mainSection = getMainContractSection(contract);
        map.put("CONTRACTDATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        if (section.getNumber() != 0) {
            var sectionName = StringUtils.isEmpty(section.getExternalNumber()) ? section.getNumber() : section.getExternalNumber();
            map.put("DOP", "по дополнительному соглашению №" + sectionName + " ");
            map.put("VAR0", "дополнительного соглашения");
            map.put("VAR1", " №" + sectionName + " к договору");
            map.put("VAR2", "дополнительное соглашение №" + sectionName + " к договору");
        } else {
            map.put("DOP", "");
            map.put("VAR0", "договора");
            map.put("VAR1", "");
            map.put("VAR2", "договор");
        }

        if (contract.getCustomer() != null) {
            map.put("CUSTOMER", contract.getCustomer().getName());
            map.put("CUSTOMST", contract.getCustomer().getChiefPosition());
            map.put("CUSNAM", contract.getCustomer().getChiefName());
            map.put("CUSTINSP", contract.getCustomer().getInspectorName());
            map.put("CUSINNAM", contract.getCustomer().getInspectorHead());
        }

        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо Заказчику с протоколом согласования разногласий
     */
    public XWPFDocument letterDisputeReconciliationProtocol(ContractSection section, User user) throws IOException {
        var fileName = "letterToCustomerOnDiff_Repair_OTK.docx";
        var contract = section.getContract();
        if (contract.getType() == ContractType.REPAIR_OTK) {
            fileName = "letterToCustomerOnDiff.docx";
        } else if (contract.getType() == ContractType.REPAIR_PZ) {
            fileName = "letterToCustomerOnDiff_Repair_PZ.docx";
        }

        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, fileName));
        Map<String, String> map = new HashMap<>();

        if (contract.getCustomer() != null) {
            map.put("CUSTOMER", contract.getCustomer().getName());
            map.put("CUSTOMST", contract.getCustomer().getChiefPosition());
            map.put("CUSNAM", contract.getCustomer().getChiefName());
            map.put("CUSTOMADDR", contract.getCustomer().getFactualAddress());
            map.put("CUSTOMFAX", getFaxNumber(contract.getCustomer().getPhoneNumber()));
        }
        var sectionName = StringUtils.isEmpty(section.getExternalNumber()) ? String.valueOf(section.getNumber()) : section.getExternalNumber();
        map.put("CONTRACTNAM", contract.getFullNumber());

        var mainSection = getMainContractSection(contract);
        map.put("CONTRACTDATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        if (section.getNumber() != 0) {
            map.put("SECTION", "дополнительному соглашению");
            map.put("NUMBER", sectionName);
        } else {
            map.put("SECTION", "договору");
            map.put("NUMBER", contract.getFullNumber());
        }

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Простое письмо
     */
    public XWPFDocument simpleLetter(ContractSection section, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "letter.docx"));
        Map<String, String> map = new HashMap<>();

        Contract contract = section.getContract();
        if (contract.getCustomer() != null) {
            map.put("CUSTOMER", contract.getCustomer().getName());
            map.put("CUSTOMST", contract.getCustomer().getChiefPosition());
            map.put("CUSNAM", contract.getCustomer().getChiefName());
            map.put("CUSTOMADDR", contract.getCustomer().getFactualAddress());
            map.put("CUSTOMFAX", getFaxNumber(contract.getCustomer().getPhoneNumber()));
        }

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо Заказчику с документами (протокол цены)
     */
    public XWPFDocument customerLetterPriceProtocol(ContractSection section, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "customerLetterPriceProtocol.docx"));
        Map<String, String> map = new HashMap<>();

        Contract contract = section.getContract();
        if (contract.getCustomer() != null) {
            map.put("CUSTOMER", contract.getCustomer().getName());
            map.put("CUSTOMST", contract.getCustomer().getChiefPosition());
            map.put("CUSNAM", contract.getCustomer().getChiefName());
            map.put("CUSTOMADDR", contract.getCustomer().getJuridicalAddress());
            map.put("CUSTOMFAX", getFaxNumber(contract.getCustomer().getPhoneNumber()));
        }
        if (section.getNumber() == 0) {
            map.put("CONTRACTNAM", contract.getFullNumber());
        } else {
            map.put("CONTRACTNAM", contract.getFullNumber() + " дополнительное соглашение №" + section.getNumber());
        }

        var mainSection = getMainContractSection(contract);
        map.put("CONTRACTDATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо Заказчику с документами (акт)
     */
    public XWPFDocument customerLetterDeed(ContractSection section, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "customerLetterDeed.docx"));
        Map<String, String> map = new HashMap<>();

        Contract contract = section.getContract();
        if (contract.getCustomer() != null) {
            map.put("CUSTOMER", contract.getCustomer().getName());
            map.put("CUSTOMST", contract.getCustomer().getChiefPosition());
            map.put("CUSNAM", contract.getCustomer().getChiefName());
            map.put("CUSTOMADDR", contract.getCustomer().getJuridicalAddress());
            map.put("CUSTOMFAX", getFaxNumber(contract.getCustomer().getPhoneNumber()));
        }
        if (section.getNumber() == 0) {
            map.put("CONTRACTNAM", contract.getFullNumber());
        } else {
            map.put("CONTRACTNAM", contract.getFullNumber() + " дополнительное соглашение №" + section.getNumber());
        }

        var mainSection = getMainContractSection(contract);
        map.put("CONTRACTDATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо в ПЗ (копия)
     */
    public XWPFDocument letterPZ(ContractSection section, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "letterForPZ.docx"));
        Map<String, String> map = new HashMap<>();

        if (section.getNumber() == 0) {
            map.put("CONTRACTNAME", section.getContract().getFullNumber());
        } else {
            map.put("CONTRACTNAME", section.getContract().getFullNumber() + " дополнительное соглашение №" + section.getNumber());
        }

        var mainSection = getMainContractSection(section.getContract());
        map.put("CONTRACTDATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо в ПЗ с протоколами согласования разногласий
     */
    public XWPFDocument letterPZDisputeReconciliationProtocol(ContractSection section, User user) throws IOException {
        var fileName = "letterToInspectorOnDiff_Repair_OTK.docx";
        var contract = section.getContract();
        if (contract.getType() == ContractType.REPAIR_OTK) {
            fileName = "letterToInspectorOnDiff.docx";
        } else if (contract.getType() == ContractType.REPAIR_PZ) {
            fileName = "letterToInspectorOnDiff_Repair_PZ.docx";
        }
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, fileName));
        Map<String, String> map = new HashMap<>();

        map.put("CONTRACTNAM", contract.getFullNumber());

        var mainSection = getMainContractSection(contract);
        map.put("CONTRACTDATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        if (section.getNumber() != 0) {
            map.put("SECTION", "к дополнительному соглашению №" + section.getNumber());
        } else {
            map.put("SECTION", "");
        }

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо в ПЗ с протоколом цены
     */
    public XWPFDocument letterPZPriceProtocol(ContractSection section, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "letterToPZPriceProtoRepair.docx"));
        Map<String, String> map = new HashMap<>();

        var mainSection = getMainContractSection(section.getContract());
        map.put("DATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        if (section.getNumber() != 0) {
            map.put("CNAME", "дополнительному соглашению №" + section.getNumber() + " к договору №" + section.getContract().getFullNumber());
        } else {
            map.put("CNAME", section.getContract().getFullNumber());
        }

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Служебная записка в бухгалтерию (оригинал)
     */
    public XWPFDocument serviceNoteAccounting(ContractSection section, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "letterToAccDeptRepair.docx"));
        Map<String, String> map = new HashMap<>();

        var mainSection = getMainContractSection(section.getContract());
        map.put("DATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        if (section.getNumber() != 0) {
            map.put("CNAME", "оформленное и подписанное дополнительное соглашение №" + section.getNumber() + " к договору №" + section.getContract().getFullNumber());
            map.put("CTYPE", "Дополнительное соглашение");
        } else {
            map.put("CNAME", "оформленный и подписанный договор №" + section.getContract().getFullNumber());
            map.put("CTYPE", "Договор");
        }

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Служебная записка в бухгалтерию (оригинал акта)
     */
    public XWPFDocument serviceNoteAccountingDeed(ContractSection section, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "letterToAccDeptRepair_Act.docx"));
        Map<String, String> map = new HashMap<>();

        var mainSection = getMainContractSection(section.getContract());
        map.put("DATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        if (section.getNumber() != 0) {
            map.put("CNAME", "дополнительному соглашению №" + section.getNumber() + " к Договору №" + section.getContract().getFullNumber());
        } else {
            map.put("CNAME", "Договору №" + section.getContract().getFullNumber());
        }

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Служебная записка в бухгалтерию (запрос на акт)
     */
    public XWPFDocument serviceNoteAccountingDeedRequest(ContractSection section, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "letterToAccDept_ActRequest_Repair.docx"));
        Map<String, String> map = new HashMap<>();

        var mainSection = getMainContractSection(section.getContract());
        map.put("DATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        if (section.getNumber() != 0) {
            map.put("CNAME", "дополнительному соглашению №" + section.getNumber() + " к Договору №" + section.getContract().getFullNumber());
        } else {
            map.put("CNAME", "Договору №" + section.getContract().getFullNumber());
        }
        map.put("CUSTOMER ", section.getContract().getCustomer().getName());

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Служебная записка
     */
    public XWPFDocument serviceNote(ContractSection section, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "serviceNote.docx"));
        Map<String, String> map = new HashMap<>();

        var mainSection = getMainContractSection(section.getContract());
        map.put("CONTRDATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        map.put("CONTRACT", section.getContract().getFullNumber());

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо о переносе сроков
     */
    public XWPFDocument customerLetterPostponement(ContractSection section, User user) throws IOException {
        var fileName = "termLetterDop.docx";
        if (section.getNumber() == 0) {
            fileName = "termLetter.docx";
        }

        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, fileName));
        Map<String, String> map = new HashMap<>();

        Contract contract = section.getContract();
        if (contract.getCustomer() != null) {
            map.put("CUSTOMER", contract.getCustomer().getName());
            map.put("LEADER", contract.getCustomer().getChiefPosition());
            map.put("NAME", contract.getCustomer().getChiefName());
            map.put("CUSTOMADDR", contract.getCustomer().getJuridicalAddress());
            map.put("CUSTOMFAX", getFaxNumber(contract.getCustomer().getPhoneNumber()));
        }
        if (section.getNumber() == 0) {
            map.put("CONTRACTADDITION", contract.getFullNumber());
        } else {
            map.put("INSPECTION", contract.getCustomer().getInspectorName());
            map.put("INSPHEAD", contract.getCustomer().getInspectorHead());
            map.put("CONTRACT", contract.getFullNumber());
            map.put("DOP", StringUtils.isEmpty(section.getExternalNumber()) ? String.valueOf(section.getNumber()) : section.getExternalNumber());
        }

        var mainSection = getMainContractSection(contract);
        map.put("DATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Акт приемки-передачи продукции
     */
    public XWPFDocument deed(ContractSection section, BigDecimal totalSum, BigDecimal paid, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "act.docx"));
        Map<String, String> map = new HashMap<>();

        var customer = section.getContract().getCustomer();
        if (customer != null) {
            map.put("CUSTNAME", customer.getName());
            map.put("CUSTPOSITION", customer.getChiefPosition());
            map.put("CUSTPERS", customer.getChiefName());
        }
        if (section.getNumber() != 0) {
            map.put("CONTRNAMEDATIVE", "дополнительному соглашению " + section.getNumber() + "к Договору №" + section.getContract().getFullNumber());
            map.put("CONTRNAMEGENITIVE", "дополнительного соглашения " + section.getNumber() + "к Договору №" + section.getContract().getFullNumber());
        } else {
            map.put("CONTRNAMEDATIVE", "Договору №" + section.getContract().getFullNumber());
            map.put("CONTRNAMEGENITIVE", "Договора №" + section.getContract().getFullNumber());
        }

        var mainSection = getMainContractSection(section.getContract());

        map.put("CONTRDATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        map.put("COSTDIGIT", new DecimalFormat("0.00").format(totalSum));
        map.put("COSTWORD", CommonUtil.moneyToWords(totalSum));

        map.put("PAIDDIGIT", new DecimalFormat("0.00").format(paid));
        map.put("PAIDWORD", CommonUtil.moneyToWords(paid));

        map.put("DIFFDIGIT", new DecimalFormat("0.00").format(totalSum.subtract(paid)));
        map.put("DIFFWORD", CommonUtil.moneyToWords(totalSum.subtract(paid)));

        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо на оплату аванса
     */
    public XWPFDocument customerLetterAdvancePayment(ContractSection section, Invoice invoice, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "letterOnPrePayment.docx"));
        Map<String, String> map = new HashMap<>();
        if (section.getNumber() == 0) {
            map.put("CONTRACTWORD", "Договор на поставку");
            map.put("CNTENDING", "");
            map.put("DOP", "договору ");
        } else {
            map.put("CONTRACTWORD", "Дополнительное соглашение №" + section.getNumber());
            map.put("CNTENDING", "");
            map.put("DOP", "дополнительному соглашению №" + section.getNumber() + "к договору ");
        }
        map.put("CONTRACTNAM", section.getContract().getFullNumber());
        map.put("INVOICE", String.valueOf(invoice.getNumber()));

        map.put("CUSTOMER", section.getContract().getCustomer().getName());
        map.put("CUSTOMST", section.getContract().getCustomer().getChiefPosition());
        map.put("CUSNAM", section.getContract().getCustomer().getChiefName());
        map.put("CUSTOMADDR", section.getContract().getCustomer().getJuridicalAddress());
        map.put("CUSTOMFAX", getFaxNumber(section.getContract().getCustomer().getPhoneNumber()));

        var mainSection = getMainContractSection(section.getContract());

        map.put("CONTRACTDATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо на окончательную оплату
     */
    public XWPFDocument customerLetterPayment(ContractSection section, Invoice invoice, User user) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "letterOnFinPayment.docx"));
        Map<String, String> map = new HashMap<>();
        if (section.getNumber() == 0) {
            map.put("CONTRACTNAM", "Договор №" + section.getContract().getFullNumber());
        } else {
            map.put("CONTRACTNAM", "дополнительному соглашению №" + section.getNumber() + "к договору " + section.getContract().getFullNumber());
        }
        map.put("INVOICE", String.valueOf(invoice.getNumber()));

        map.put("CUSTOMER", section.getContract().getCustomer().getName());
        map.put("CUSTOMST", section.getContract().getCustomer().getChiefPosition());
        map.put("CUSNAM", section.getContract().getCustomer().getChiefName());
        map.put("CUSTOMADDR", section.getContract().getCustomer().getJuridicalAddress());
        map.put("CUSTOMFAX", getFaxNumber(section.getContract().getCustomer().getPhoneNumber()));

        var mainSection = getMainContractSection(section.getContract());

        map.put("CONTRACTDATE", mainSection.getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо неправильные платежные поручения
     */
    public XWPFDocument customerLetterIncorrectPayment(ContractSection section, Payment payment, User user, BigDecimal vat) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "letterOnWrongPayment.docx"));
        Map<String, String> map = new HashMap<>();

        map.put("CUSTOMER", section.getContract().getCustomer().getName());
        map.put("CUSTOMST", section.getContract().getCustomer().getChiefPosition());
        map.put("CUSNAM", section.getContract().getCustomer().getChiefName());
        map.put("CUSTOMADDR", section.getContract().getCustomer().getJuridicalAddress());
        map.put("CUSTOMFAX", getFaxNumber(section.getContract().getCustomer().getPhoneNumber()));

        map.put("PAYM_NUM", payment.getNumber());
        map.put("PAYM_DATE", payment.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));

        map.put("PAYMENTSR", new DecimalFormat("#").format(payment.getAmount()));
        map.put("PAYMENTSINWORDS", CommonUtil.moneyToWords(payment.getAmount()));
        var totalVat = payment.getAmount().multiply(vat).divide(BigDecimal.valueOf(100));
        map.put("PAYMENTSNDSR", new DecimalFormat("#.##").format(totalVat));
        map.put("USER", user.getUserOfficialName());
        return replaceTagInDocument(document, map);
    }

    /**
     * Письмо на склад/Распоряжение
     */
    public XWPFDocument letterWarehouse(ContractSection section, List<Payment> paymentList, List<Allotment> allotmentList, User user, Double vat)
        throws Exception {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "shipmentCommand.docx"));
        Map<String, String> map = new HashMap<>();
        var contract = section.getContract();

        map.put("DATE", String.valueOf(LocalDate.now().getYear()));
        map.put("NUM", "");

        var type = "";
        switch (contract.getType()) {
            case PRODUCT_SUPPLY:
            case SUPPLY_OF_EXPORTED:
                type = "Договор";
                break;
            case ORDER_WITHOUT_EXECUTION_OF_THE_CONTRACT:
                type = "Заказ";
                break;
            default:
                type = "Договор";
        }

        if (document.getTables() != null) {
            var table = document.getTables().get(0);
            var customer = contract.getCustomer().getName();
            var location = contract.getCustomer().getLocation();
            var contractName = contract.getFullNumber();
            var date = getMainContractSection(contract).getCreateDate();

            if (section.getNumber() > 0) {
                var sectionName = StringUtils.isEmpty(section.getExternalNumber()) ? String.valueOf(section.getNumber()) : section.getExternalNumber();
                contractName += "\n Дополнительное соглашение №" + sectionName;
            }

            if (allotmentList != null) {
                var amount = 0.0;
                var serials = "";
                var totalPrice = BigDecimal.ZERO;
                var prodId = 0.0;
                var isRepeat = false;
                XWPFTableRow row = null;

                for (var i = 0; i < allotmentList.size(); i++) {
                    var item = allotmentList.get(i);
                    var price = item.getLot().getNeededPrice();
                    var itemAmount = item.getAmount();
                    var name = item.getLot().getLotGroup().getProduct().getTechSpecName();
                    isRepeat = item.getLot().getLotGroup().getProduct().getId() == prodId;

                    if (isRepeat) {
                        amount += itemAmount;
                        totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(itemAmount)));
                        for (var value : item.getMatValueList()) {
                            serials += (serials.length() != 0 ? ", " : "") + value.getSerialNumber();
                        }
                        if (row != null) {
                            List<XWPFTableCell> cells = row.getTableCells();
                            for (XWPFTableCell cell : cells) {
                                cell.getCTTc().setPArray(new CTP[]{CTP.Factory.newInstance()});
                            }
                            row.getCell(2).setText(name
                                + " (" + serials + ")"
                            );
                            row.getCell(3).setText(new DecimalFormat("###,###.00").format(totalPrice));
                            row.getCell(4).setText(String.valueOf(amount));
                        }
                    } else {
                        prodId = item.getLot().getLotGroup().getProduct().getId();
                        row = table.createRow();
                        serials = "";
                        for (var value : item.getMatValueList()) {
                            serials += (serials.length() != 0 ? ", " : "") + value.getSerialNumber();
                        }
                        amount = itemAmount;
                        totalPrice = price.multiply(BigDecimal.valueOf(amount));

                        row.getCell(2).setText(name + " (" + serials + ")");
                        row.getCell(3).setText(new DecimalFormat("###,###.00").format(price));
                        row.getCell(4).setText(String.valueOf(amount));
                        if (i == 0) {
                            row.getCell(0).setText(customer + "\n" + location);
                            row.getCell(1).setText(type + "\n" + contractName + "\n от " + date.format(DateTimeFormatter.ofPattern("dd.MM.yy")));
                        }
                    }

                }
                map.put("COSTNOVAT", new DecimalFormat("###,###.00").format(totalPrice));
                var vatPrice = totalPrice.multiply(BigDecimal.valueOf(vat)).divide(BigDecimal.valueOf(100));
                map.put("VATSUM", new DecimalFormat("###,###.00").format(vatPrice));
                map.put("COSTWITHVAT", new DecimalFormat("###,###.00").format(totalPrice.add(vatPrice)));
            }
            if (paymentList.size() > 0) {
                var paymentString = "";
                for (var item : paymentList) {
                    paymentString += "№ " + item.getNumber() + " от " + item.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")) + "\n";
                }
                map.put("PAYMENTLIST", paymentString);
            } else {
                map.put("PAYMENTLIST", "");
            }
            map.put("USER", user.getUserOfficialName());
        }
        return replaceTagInDocument(document, map);
    }

    /**
     * Накладная для ОКР
     */
    public XWPFDocument shipmentWaybill(ModelMap waybillMap)
        throws Exception {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "m15.docx"));
        Map<String, String> map = new HashMap<>();
        map.put("NUMB", waybillMap.get("number") == null ? "" : waybillMap.get("number").toString());
        map.put("DATE", waybillMap.get("createDate") == null ? "" : waybillMap.get("createDate").toString());
        map.put("RECIEVER", waybillMap.get("payer") == null ? "" : waybillMap.get("payer").toString());
        map.put("CONTRACT", "договор " + (waybillMap.get("sectionName") == null ? "" : waybillMap.get("sectionName").toString()) + " с " + (waybillMap.get("payer") == null ? "" : waybillMap.get("payer").toString()));
        map.put("PERMITTER", waybillMap.get("permitUser") == null ? "" : waybillMap.get("permitUser").toString());
        map.put("PERSON", waybillMap.get("reciever") == null ? "" : waybillMap.get("reciever").toString());
        map.put("SHIPPER", waybillMap.get("giveUser") == null ? "" : waybillMap.get("giveUser").toString());
        map.put("BUCHG", waybillMap.get("accountantUser") == null ? "" : waybillMap.get("accountantUser").toString());
        String letter = waybillMap.get("letterOfAttorney") == null ? "" : waybillMap.get("letterOfAttorney").toString();
        map.put("PROXY", "дов.№" + letter);

        List<XWPFTable> tables = document.getTables();
        XWPFTable table = tables.get(tables.size() - 4);

        List<MatValue> list = (List<MatValue>) waybillMap.get("matValues");
        var amount = 0.0;
        var serials = "";
        var prodId = 0.0;
        var isRepeat = false;
        var price = BigDecimal.ZERO;
        var totalPrice = BigDecimal.ZERO;
        XWPFTableRow row = null;
        var amountName = 0; //количество наименований
        var vat = BigDecimal.ZERO;
        var waybillPrice = BigDecimal.ZERO;
        var waybillVAT = BigDecimal.ZERO;

        for (var item : list) {
            if (item.getPresentLogRecord() != null) {
                var allotment = item.getAllotment();
                price = allotment.getLot().getNeededPrice();
                waybillPrice = waybillPrice.add(price);
                waybillVAT = waybillVAT.add(price.multiply(BigDecimal.valueOf(item.getAllotment().getLot().getVat().getValue()).divide(BigDecimal.valueOf(100))));
                var name = allotment.getLot().getLotGroup().getProduct().getTechSpecName();
                isRepeat = allotment.getLot().getLotGroup().getProduct().getId() == prodId;

                totalPrice.add(price);

                if (isRepeat) {
                    amount++;
                    serials += (serials.length() != 0 ? "\n " : "") + item.getSerialNumber();
                    if (row != null) {
                        List<XWPFTableCell> cells = row.getTableCells();
                        for (XWPFTableCell cell : cells) {
                            cell.getCTTc().setPArray(new CTP[]{CTP.Factory.newInstance()});
                        }
                    }
                } else {
                    prodId = allotment.getLot().getLotGroup().getProduct().getId();
                    row = table.createRow();
                    row.addNewTableCell();
                    row.addNewTableCell();
                    row.addNewTableCell();
                    row.addNewTableCell();
                    row.addNewTableCell();
                    serials = "";
                    serials += (serials.length() != 0 ? "\n" : "") + item.getSerialNumber();
                    amount = 1;
                    amountName++;
                }
                totalPrice = price.multiply(BigDecimal.valueOf(amount));
                vat = price.multiply(BigDecimal.valueOf(allotment.getLot().getVat().getValue())).multiply(BigDecimal.valueOf(amount)).divide(BigDecimal.valueOf(100));

                row.getCell(2).setText(name + " зав.№" + serials);
                row.getCell(6).setText(new DecimalFormat("#").format(amount));
                row.getCell(7).setText(new DecimalFormat("#").format(amount));
                row.getCell(8).setText(new DecimalFormat("###,###.00").format(price));
                row.getCell(9).setText(new DecimalFormat("###,###.00").format(totalPrice));
                row.getCell(10).setText(new DecimalFormat("###,###.00").format(vat));
                row.getCell(11).setText(new DecimalFormat("###,###.00").format(totalPrice.add(vat)));
            }
        }

        String priceStr = CommonUtil.moneyToWords(waybillPrice.add(waybillVAT));
        priceStr = priceStr.replace("(", "");
        priceStr = priceStr.replace(")", "");
        map.put("TWORD", priceStr);

        String vatStr = CommonUtil.moneyToWords(waybillVAT);
        vatStr = vatStr.replace("(", "");
        vatStr = vatStr.replace(")", "");
        map.put("VAT", vatStr.toLowerCase());
        map.put("AMOUNT", String.valueOf(amountName));

        return replaceTagInDocument(document, map);
    }

    /**
     * МСН
     */
    public XWPFDocument internalWaybill(InternalWaybill waybill) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "fromProdm.docx"));
        Map<String, String> map = new HashMap<>();
        map.put("MSN_NUMB", String.valueOf(waybill.getNumber()));
        map.put("MSN_DATE", waybill.getAcceptDate() == null ? "" : waybill.getAcceptDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        List<XWPFTable> tables = document.getTables();
        XWPFTable table = tables.get(tables.size() - 1);

        List<MatValue> list = waybill.getMatValueList();
        var amount = 0.0;
        var serials = "";
        var prodId = 0.0;
        var isRepeat = false;
        var price = BigDecimal.ZERO;
        var totalPrice = BigDecimal.ZERO;
        XWPFTableRow row = null;
        var isFirst = true;

        for (var item : list) {
            if (item.getPresentLogRecord() != null) {
                var allotment = item.getAllotment();
                var section = allotment.getLot().getLotGroup().getContractSection();
                var name = allotment.getLot().getLotGroup().getProduct().getTechSpecName();
                isRepeat = allotment.getLot().getLotGroup().getProduct().getId() == prodId;
                price = allotment.getLot().getNeededPrice();
                totalPrice.add(price);

                if (isRepeat) {
                    amount++;
                    totalPrice = price.multiply(BigDecimal.valueOf(amount));
                    serials += (serials.length() != 0 ? "\n " : "") + item.getSerialNumber();
                    if (row != null) {
                        List<XWPFTableCell> cells = row.getTableCells();
                        for (XWPFTableCell cell : cells) {
                            cell.getCTTc().setPArray(new CTP[]{CTP.Factory.newInstance()});
                        }
                    }
                } else {
                    prodId = allotment.getLot().getLotGroup().getProduct().getId();
                    if (!isFirst) {
                        row = table.createRow();
                        row.addNewTableCell();
                        row.addNewTableCell();
                        row.addNewTableCell();
                        row.addNewTableCell();
                        row.addNewTableCell();
                    } else {
                        row = table.getRow(3);
                        isFirst = false;
                    }
                    serials = "";
                    serials += (serials.length() != 0 ? "\n" : "") + item.getSerialNumber();
                    amount = 1;
                    totalPrice = price.multiply(BigDecimal.valueOf(amount));
                }
                formatText(row.getCell(2), name);
                formatText(row.getCell(3), serials);
                formatText(row.getCell(7), new DecimalFormat("#").format(amount));
                formatText(row.getCell(8), new DecimalFormat("#").format(amount));
                formatText(row.getCell(9), new DecimalFormat("###,###.00").format(price));
                formatText(row.getCell(10), new DecimalFormat("###,###.00").format(totalPrice));
                formatText(row.getCell(12), section.getContract().getCustomer().getName());
                formatText(row.getCell(13), section.getContract().getFullNumber() + " от " + getMainContractSection(section.getContract()).getCreateDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
            }
        }
        return replaceTagInDocument(document, map);
    }

    private void formatText (XWPFTableCell cell, String text) {
        CTTc ctTc = cell.getCTTc();
        CTP ctP = ctTc.sizeOfPArray() == 0 ? ctTc.addNewP() : ctTc.getPArray(0);
        XWPFParagraph par = new XWPFParagraph(ctP, cell);
        var run = par.createRun();
        run.setFontSize(7);
        run.setFontFamily("Arial");
        run.setText(text);
    }


    /**
     * Акт для отгрузки ГП
     */
    public XWPFDocument warehouseAct(ModelMap waybillMap) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "outAct.docx"));
        Map<String, String> map = new HashMap<>();
        map.put("CONTRACT", waybillMap.get("sectionName") == null ? "" : waybillMap.get("sectionName").toString());
        map.put("LETTER", waybillMap.get("transmittalLetter") == null ? "" : waybillMap.get("transmittalLetter").toString());
        map.put("DATE", waybillMap.get("date") == null ? "" : waybillMap.get("date").toString());
        map.put("FULLUSERNAME", waybillMap.get("permitUser") == null ? "" : waybillMap.get("permitUser").toString());
        map.put("PERMIT", waybillMap.get("permitUser") == null ? "" : waybillMap.get("permitUser").toString());
        map.put("CUSTOMER", waybillMap.get("payer") == null ? "" : waybillMap.get("payer").toString());
        map.put("PERSON", waybillMap.get("reciever") == null ? "" : waybillMap.get("reciever").toString());
        map.put("SHORTPERS", waybillMap.get("reciever") == null ? "" : waybillMap.get("reciever").toString());
        map.put("PROXY", waybillMap.get("letterOfAttorney") == null ? "" : waybillMap.get("letterOfAttorney").toString());

        List<XWPFTable> tables = document.getTables();
        XWPFTable table = tables.get(0);

        List<MatValue> list = (List<MatValue>) waybillMap.get("matValues");
        var amount = 0.0;
        var serials = "";
        var prodId = 0.0;
        var isRepeat = false;
        var isFirst = true;
        XWPFTableRow row = null;

        for (var item : list) {
            if (item.getPresentLogRecord() != null) {
                var allotment = item.getAllotment();
                var name = allotment.getLot().getLotGroup().getProduct().getTechSpecName();
                isRepeat = allotment.getLot().getLotGroup().getProduct().getId() == prodId;

                if (isRepeat) {
                    amount++;
                    serials += (serials.length() != 0 ? "\n " : "") + item.getSerialNumber();
                    if (row != null) {
                        List<XWPFTableCell> cells = row.getTableCells();
                        for (XWPFTableCell cell : cells) {
                            cell.getCTTc().setPArray(new CTP[]{CTP.Factory.newInstance()});
                        }
                    }
                } else {
                    prodId = allotment.getLot().getLotGroup().getProduct().getId();
                    if (!isFirst) {
                        row = table.createRow();
                        if (table.getRows().size() == 2) {
                            row.createCell();
                            row.createCell();
                        }
                    } else {
                        row = table.getRow(0);
                        isFirst = false;
                    }

                    serials = "";
                    serials += (serials.length() != 0 ? "\n" : "") + item.getSerialNumber();
                    amount = 1;
                }

                row.getCell(0).setText(name + ", зав.№ " + serials);
                row.getCell(1).setText(new DecimalFormat("#").format(amount));
            }
        }

        return replaceTagInDocument(document, map);
    }

    /**
     * Счет на оплату
     */
    public XWPFDocument invoice(ModelMap invoiceMap) throws IOException, XmlException {
        String path;
        if (invoiceMap.get("invoiceType") == InvoiceType.FINAL_INVOICE) {
            path = "invoice_final.docx";
        } else if (invoiceMap.get("invoiceType") == InvoiceType.ADVANCE) {
            path = "invoice_advance.docx";
        } else {
            path = "invoice_arbitrary.docx";
        }
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, path));

        Map<String, String> map = new HashMap<>();
        map.put("ADDRESS", invoiceMap.get("address") == null ? "" : invoiceMap.get("address").toString());
        map.put("KINN", invoiceMap.get("kinn") == null ? "" : invoiceMap.get("kinn").toString());
        map.put("KKPP", invoiceMap.get("kkpp") == null ? "" : invoiceMap.get("kkpp").toString());
        map.put("RS", invoiceMap.get("rs") == null ? "" : invoiceMap.get("rs").toString());
        map.put("BANK", invoiceMap.get("bank") == null ? "" : invoiceMap.get("bank").toString());
        map.put("LOCK", invoiceMap.get("location") == null ? "" : invoiceMap.get("location").toString());
        map.put("KS", invoiceMap.get("ks") == null ? "" : invoiceMap.get("ks").toString());
        map.put("BIK", invoiceMap.get("bik") == null ? "" : invoiceMap.get("bik").toString());
        map.put("DEADLINE", invoiceMap.get("deadline") == null ? "" : invoiceMap.get("deadline").toString());
        map.put("DELIVERY", invoiceMap.get("delivery") == null ? "" : invoiceMap.get("delivery").toString());
        var date = invoiceMap.get("contractDate") == null ? "" : invoiceMap.get("contractDate").toString();
        map.put("CONTRACT", invoiceMap.get("contract") == null ? "" : invoiceMap.get("contract").toString() + " от " + date);
        map.put("INVOICE", invoiceMap.get("invoice") == null ? "" : invoiceMap.get("invoice").toString());
        map.put("DATE", invoiceMap.get("date") == null ? "" : invoiceMap.get("date").toString());
        map.put("CUSTOMER", invoiceMap.get("customer") == null ? "" : invoiceMap.get("customer").toString());
        map.put("CUSTADDR", invoiceMap.get("custaddr") == null ? "" : invoiceMap.get("custaddr").toString());
        map.put("CINN", invoiceMap.get("cinn") == null ? "" : invoiceMap.get("cinn").toString());
        map.put("CKPP", invoiceMap.get("ckpp") == null ? "" : invoiceMap.get("ckpp").toString());
        map.put("TOTL", invoiceMap.get("total") == null ? "" : new DecimalFormat("###,###.00").format(invoiceMap.get("total")));
        map.put("PRCPAY", invoiceMap.get("priceToPay") == null ? "" : new DecimalFormat("###,###.00").format(invoiceMap.get("priceToPay")));
        map.put("VAT", invoiceMap.get("vat") == null ? "" : new DecimalFormat("###,###.00").format(invoiceMap.get("vat")));
        map.put("PAID", invoiceMap.get("paid") == null ? "" : new DecimalFormat("###,##0.00").format(invoiceMap.get("paid")));
        map.put("TOPAY", invoiceMap.get("toPay") == null ? "" : new DecimalFormat("###,###.00").format(invoiceMap.get("toPay")));
        map.put("TOTLWOVAT", invoiceMap.get("totalWOVAT") == null ? "" : new DecimalFormat("###,###.00").format(invoiceMap.get("totalWOVAT")));
        map.put("TOTLVAT", invoiceMap.get("totalVAT") == null ? "" : new DecimalFormat("###,###.00").format(invoiceMap.get("totalVAT")));
        var moneyToWords = invoiceMap.get("toPay") == null ? "" : CommonUtil.moneyToWords((BigDecimal) invoiceMap.get("toPay"));
        moneyToWords = moneyToWords.replace("(", "");
        moneyToWords = moneyToWords.replace(")", "");
        map.put("TOTLWORD", moneyToWords);
        map.put("PERC", invoiceMap.get("perc") == null ? "" : new DecimalFormat("#").format(invoiceMap.get("perc")));

        if (invoiceMap.get("invoiceType") != InvoiceType.INVOICE_FOR_AMOUNT) {
            //определяем приемку
            var accstatePZ = "";
            var accstateOTK = "";
            var accstatePZ_M = "";
            var accstatePT = "";

            //список
            List<XWPFTable> tables = document.getTables();
            XWPFTable table = tables.get(1);
            var oldRow = table.getRow(3);
            var allotmentList = (List<Allotment>) invoiceMap.get("allotmentList");

            for (int i = 0; i < allotmentList.size(); i++) {
                CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
                XWPFTableRow newRow = new XWPFTableRow(ctrow, table);
                var item = allotmentList.get(i);
                newRow.getCell(0).setText(item.getLot().getLotGroup().getProduct().getTechSpecName());
                newRow.getCell(1).setText(new DecimalFormat("#").format(item.getAmount()));
                newRow.getCell(2).setText(new DecimalFormat("###,###.00").format(item.getNeededPrice()));
                newRow.getCell(3).setText(new DecimalFormat("###,###.00").format(item.getNeededPrice().multiply(BigDecimal.valueOf(item.getAmount()))));
                table.addRow(newRow, 4 + i);

                switch (item.getLot().getAcceptType()) {
                    case OTK:
                        accstateOTK += accstateOTK.equals("") ? "по п." + (i + 1) : ", " + (i + 1);
                        break;
                    case PZ:
                        accstatePZ += accstatePZ.equals("") ? "по п." + (i + 1) : ", " + (i + 1);
                        break;
                    case PZ_MANUFACTURER:
                        accstatePZ_M += accstatePZ_M.equals("") ? "по п." + (i + 1) : ", " + (i + 1);
                        break;
                    case PERIODIC_TEST:
                        accstatePT += accstatePT.equals("") ? "по п." + (i + 1) : ", " + (i + 1);
                        break;
                }
            }
            table.removeRow(3);

            var accept = "";
            accept += accstateOTK.equals("") ? "" : accstateOTK + " - ОТК Поставщика";
            accept += accstatePZ.equals("") ? "" : accstatePZ + " - 477 ВП МО РФ";
            accept += accstatePZ_M.equals("") ? "" : accstatePZ_M + " - ПЗ предприятия-изготовителя";
            accept += accstatePT.equals("") ? "" : accstatePT + " - периодические испытания";
            map.put("ACCSTATE", accept);
        }
        return replaceTagInDocument(document, map);
    }

    public XWPFDocument warehouseResidue(List<WarehouseStateResidueDocDto> data) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        XWPFDocument document = new XWPFDocument();
        document.setZoomPercent(100);
        DocumentUtil.orientSize(document, STPageOrientation.LANDSCAPE, WordUtil.cmToDXA(29.7), WordUtil.cmToDXA(21));
        // Отступы документа
        long valueRL = WordUtil.cmToDXA(1.0), valueTB = WordUtil.cmToDXA(1), valueHF = WordUtil.cmToDXA(1.25);
        DocumentUtil.pageMargin(document, valueTB, valueRL, valueTB, valueRL, valueHF, valueHF, null);

        // Элементы стилей
        XWPFStyles styles = document.createStyles();
        // Стандартные настройки стилей абзаца
        XWPFDefaultParagraphStyle defaultParagraphStyle = styles.getDefaultParagraphStyle();
        ParagraphDefaultsUtil.propertyNode(defaultParagraphStyle).setNil();
        // Стандартные настройки для стиля текста
        XWPFDefaultRunStyle defaultRunStyle = styles.getDefaultRunStyle();
        RunDefaultsUtil.size(defaultRunStyle, null);
        RunDefaultsUtil.sizeCs(defaultRunStyle, null);

        {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            run.setBold(true);
            run.setText("Остатки на складе на " + LocalDate.now().format(BaseConstant.INSTANCE.getDATE_FORMATTER()));
            run.setFontSize(13);
            document.createParagraph();
        }

        XWPFTable table = document.createTable();
        TableUtil.width(table, TableWidthType.DXA, WordUtil.cmToDXA(27));
        TableUtil.leftIndent(table, WordUtil.cmToDXA(0.02));
        TableUtil.cellAutoFit(table, Boolean.FALSE);
        TableUtil.cellMargin(table, WordUtil.cmToDXA(0.2), WordUtil.cmToDXA(0.2), WordUtil.cmToDXA(0.2), WordUtil.cmToDXA(0.2));

        {
            XWPFTableRow row = table.getRow(0);

            XWPFTableCell cell1 = row.getCell(0);
            cell1.setText("Наименование");
            cell1.setColor("D9D9D9");
            CTR ctr1 = cell1.getCTTc().getPList().get(0).getRList().get(0);
            ctr1.addNewRPr().addNewSz().setVal(BigInteger.valueOf(24));
            ctr1.getRPr().addNewB().setVal(STOnOff.TRUE);

            for (int i = 1; i < 6; i++) {
                String text = "";
                switch (i) {
                    case 1: text = "Заводской номер"; break;
                    case 2: text = "Заказчик"; break;
                    case 3: text = "Договор"; break;
                    case 4: text = "№ ячейки"; break;
                    case 5: text = "№ акта"; break;
                }
                XWPFTableCell cell = row.createCell();
                cell.setText(text);
                cell.setColor("D9D9D9");
                CTR ctr = cell.getCTTc().getPList().get(0).getRList().get(0);
                ctr.addNewRPr().addNewSz().setVal(BigInteger.valueOf(24));
                ctr.getRPr().addNewB().setVal(STOnOff.TRUE);
            }
        }

        long amount = 0;
        for (int ind = 0; ind < data.size(); ind++) {
            var line = data.get(ind);
            amount += 1;
            XWPFTableRow row = table.createRow();

            XWPFTableCell cell1 = row.getCell(0);
            cell1.setText(line.getProductName());
            CTR ctr1 = cell1.getCTTc().getPList().get(0).getRList().get(0);
            ctr1.addNewRPr().addNewSz().setVal(BigInteger.valueOf(22));

            for (int i = 1; i < 6; i++) {
                String text = "";
                switch (i) {
                    case 1: text = line.getSerialNumber(); break;
                    case 2: text = line.getCustomer(); break;
                    case 3: text = line.getContract(); break;
                    case 4: text = line.getCell(); break;
                    case 5: text = line.getNotice(); break;
                }
                XWPFTableCell cell = row.getCell(i);
                cell.setText(text);
                CTR ctr = cell.getCTTc().getPList().get(0).getRList().get(0);
                ctr.addNewRPr().addNewSz().setVal(BigInteger.valueOf(22));
            }

            if ((ind + 1 < data.size() && data.get(ind + 1).getProductId() != line.getProductId()) || ind == data.size() - 1) {
                XWPFTableRow rowTotal = table.createRow();

                XWPFTableCell cellTotal1 = rowTotal.getCell(0);
                cellTotal1.setText("ИТОГО (" + line.getProductName() + "):");
                CTR ctrTotal1 = cellTotal1.getCTTc().getPList().get(0).getRList().get(0);
                ctrTotal1.addNewRPr().addNewSz().setVal(BigInteger.valueOf(22));

                XWPFTableCell cellTotal2 = rowTotal.getCell(1);
                cellTotal2.setText(String.valueOf(amount));
                CTP ctpTotal2 = cellTotal2.getCTTc().getPList().get(0);
                ctpTotal2.addNewPPr().addNewJc().setVal(STJc.CENTER);
                CTR ctrTotal2 = ctpTotal2.getRList().get(0);
                ctrTotal2.addNewRPr().addNewSz().setVal(BigInteger.valueOf(22));

                amount = 0;
            }
        }

        if (!data.isEmpty()) {
            XWPFTableRow row = table.createRow();
            XWPFTableCell cell1 = row.getCell(0);
            cell1.setText("Итого:");
            cell1.setColor("D9D9D9");
            CTR ctr1 = cell1.getCTTc().getPList().get(0).getRList().get(0);
            ctr1.addNewRPr().addNewSz().setVal(BigInteger.valueOf(26));
            ctr1.getRPr().addNewB().setVal(STOnOff.TRUE);

            XWPFTableCell cell2 = row.getCell(1);
            cell2.setText(String.valueOf(data.size()));
            cell2.setColor("D9D9D9");
            CTP ctp2 = cell2.getCTTc().getPList().get(0);
            CTR ctr2 = ctp2.getRList().get(0);
            ctp2.addNewPPr().addNewJc().setVal(STJc.CENTER);
            ctr2.addNewRPr().addNewSz().setVal(BigInteger.valueOf(26));
            ctr2.getRPr().addNewB().setVal(STOnOff.TRUE);

            for (int i = 2; i < 6; i++) {
                XWPFTableCell cell = row.getCell(i);
                cell.setColor("D9D9D9");
            }
        }

        return document;
    }

    public XWPFDocument warehouseReceiptShipmentProductPeriod(
        List<WarehouseStateReportFirstDocDto> data,
        Product product,
        LocalDate dateFrom,
        LocalDate dateTo
    ) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        XWPFDocument document = new XWPFDocument();
        document.setZoomPercent(100);
        DocumentUtil.orientSize(document, STPageOrientation.PORTRAIT, WordUtil.cmToDXA(21), WordUtil.cmToDXA(29.7));
        // Отступы документа
        long valueRL = WordUtil.cmToDXA(1.0), valueTB = WordUtil.cmToDXA(1), valueHF = WordUtil.cmToDXA(1);
        DocumentUtil.pageMargin(document, valueTB, valueRL, valueTB, valueRL, valueHF, valueHF, WordUtil.cmToDXA(0));

        var ctpFooter = CTP.Factory.newInstance();
        ctpFooter.addNewPPr().addNewJc().setVal(STJc.CENTER);
        ctpFooter.addNewR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        ctpFooter.addNewR().addNewInstrText().setStringValue("PAGE   \\* MERGEFORMAT");
        ctpFooter.addNewR().addNewFldChar().setFldCharType(STFldCharType.SEPARATE);
        ctpFooter.addNewR().addNewT().setStringValue("1");
        ctpFooter.addNewR().addNewFldChar().setFldCharType(STFldCharType.END);

        var footerPolicy = document.createHeaderFooterPolicy();
        footerPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, new XWPFParagraph[] { new XWPFParagraph(ctpFooter, document) });

        // Элементы стилей
        XWPFStyles styles = document.createStyles();
        // Стандартные настройки стилей абзаца
        XWPFDefaultParagraphStyle defaultParagraphStyle = styles.getDefaultParagraphStyle();
        ParagraphDefaultsUtil.propertyNode(defaultParagraphStyle).setNil();
        // Стандартные настройки для стиля текста
        XWPFDefaultRunStyle defaultRunStyle = styles.getDefaultRunStyle();
        RunDefaultsUtil.size(defaultRunStyle, null);
        RunDefaultsUtil.sizeCs(defaultRunStyle, null);

        {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            run.setBold(true);
            run.setText("Предоставляю сведения о поступлении и отгрузке изделия «" + product.getConditionalName() + "» за период с " + dateFrom.format(BaseConstant.INSTANCE.getDATE_FORMATTER()) + " по " + dateTo.format(BaseConstant.INSTANCE.getDATE_FORMATTER()));
            run.setFontSize(10);
            document.createParagraph();
        }

        XWPFTable table = document.createTable();
        TableUtil.width(table, TableWidthType.DXA, WordUtil.cmToDXA(19));
        TableUtil.leftIndent(table, WordUtil.cmToDXA(0.02));
        TableUtil.cellAutoFit(table, Boolean.FALSE);
        TableUtil.cellMargin(table, WordUtil.cmToDXA(0.1), WordUtil.cmToDXA(0.1), WordUtil.cmToDXA(0.1), WordUtil.cmToDXA(0.1));

        {
            var row = table.getRow(0);
            row.getCtRow().addNewTrPr().addNewTblHeader().setVal(STOnOff.TRUE);

            for (int i = 0; i < 8; i++) {
                String text = "";
                double width = .0;
                switch (i) {
                    case 0: text = "Договор"; width = 2.3; break;
                    case 1: text = "Дата поступления"; width = 2.2; break;
                    case 2: text = "Номер приходного документа"; width = 2; break;
                    case 3: text = "Предъявление"; width = 2.5; break;
                    case 4: text = "Заводской номер"; width = 2.3; break;
                    case 5: text = "Дата отгрузки"; width = 1.9; break;
                    case 6: text = "Номер расходного документа"; width = 2.2; break;
                    case 7: text = "Заказчик"; width = 3.6; break;
                }
                var cell = i == 0 ? row.getCell(0) : row.createCell();
                cell.setText(text);
                CTR ctr = cell.getCTTc().getPList().get(0).getRList().get(0);
                ctr.addNewRPr().addNewSz().setVal(BigInteger.valueOf(18));
                ctr.getRPr().addNewB().setVal(STOnOff.TRUE);
                cell.getCTTc().getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
                cell.getCTTc().addNewTcPr().addNewVAlign().setVal(STVerticalJc.CENTER);
                cell.setWidthType(TableWidthType.DXA);
                cell.setWidth(String.valueOf(WordUtil.cmToDXA(width)));
            }
        }

        // Функция установки итоговых значений по заказчику
        var customerRunTotal = new Object() {
            void fill(XWPFTableCell cell, boolean useExistsP, String text) {
                var pList = cell.getCTTc().getPList();
                var run = useExistsP ? pList.get(0).addNewR() : cell.getCTTc().addNewP().addNewR();
                run.addNewT().setStringValue(text);
                var pr = run.addNewRPr();
                pr.addNewSz().setVal(BigInteger.valueOf(20));
                pr.addNewB().setVal(STOnOff.TRUE);
            }
        };

        // Функция создания итоговой строки по заказчику
        var customerRowTotal = new Object() {
            void create(int acceptAmount, int shipmentAmount, String text) {
                var row = table.createRow();
                var cell = row.getCell(0);
                CellUtil.gridSpan(cell, 8L);
                for (int i = 1; i <= 7; i++) {
                    row.getCtRow().removeTc(1);
                    row.removeCell(1);
                }
                CellUtil.borderLeft(cell, CTBorderProperties.instance().val(STBorder.NIL));
                CellUtil.borderRight(cell, CTBorderProperties.instance().val(STBorder.NIL));
                customerRunTotal.fill(cell, true,"Итоговые данные по " + text);
                customerRunTotal.fill(cell, false, "Всего поступило на СГП: " + acceptAmount + "шт.");
                customerRunTotal.fill(cell, false, "Всего отгружено с СГП: " + shipmentAmount + "шт.");
                customerRunTotal.fill(cell, false, "Остаток на СГП: " + (acceptAmount - shipmentAmount) + "шт.");
            }
        };

        // Итоговые счетчики
        int acceptAmount = 0; // принятых на СГП
        int totalAcceptAmount = 0; // общее принятых на СГП
        int shipmentAmount = 0; // отгруженных
        int totalShipmentAmount = 0; // общее отгруженных
        int rowCount = 1;

        // Итого по заказчику
        String tempCustomer = data.isEmpty() ? "" : data.get(0).getCustomer();
        //int customerRow = rowCount;
        //var customerMergeList = new ArrayList<Pair<Integer, Integer>>();

        for (int di = 0; di < data.size(); di++) {
            var line = data.get(di);

            // Если в строке сменился заказчик, то нужно посчитать итоговые данные
            if (!Objects.equals(tempCustomer, line.getCustomer())) {
                customerRowTotal.create(acceptAmount, shipmentAmount, tempCustomer);
                rowCount++;
                //
                tempCustomer = line.getCustomer();
                acceptAmount = 0;
                shipmentAmount = 0;
                //var toRow = di == data.size() - 1 ? rowCount : rowCount - 1;
                //customerMergeList.add(new Pair<>(customerRow, toRow));
                //customerRow = rowCount;
            }

            // Итоговые данные
            acceptAmount++;
            totalAcceptAmount++;
            if (line.getShipmentDate() != null) {
                shipmentAmount++;
                totalShipmentAmount++;
            }

            // 1 колонка
            XWPFTableRow row = table.createRow();
            XWPFTableCell cell1 = row.getCell(0);
            cell1.setText(line.getContract());
            CTR ctr1 = cell1.getCTTc().getPList().get(0).getRList().get(0);
            ctr1.addNewRPr().addNewSz().setVal(BigInteger.valueOf(18));
            row.getCtRow().addNewTrPr().addNewCantSplit().setVal(STOnOff.TRUE);

            for (int i = 1; i < 8; i++) {
                String text = "";
                switch (i) {
                    case 1: text = Objects.requireNonNull(line.getAcceptDate()).format(BaseConstant.INSTANCE.getDATE_FORMATTER()); break;
                    case 2: text = line.getInternalWaybill(); break;
                    case 3: text = line.getPresentation(); break;
                    case 4: text = line.getSerialNumber(); break;
                    case 5: text = line.getShipmentDate() == null ? "" : line.getShipmentDate().format(BaseConstant.INSTANCE.getDATE_FORMATTER()); break;
                    case 6: text = line.getShipmentWaybill(); break;
                    case 7: text = line.getCustomer(); break;
                }
                XWPFTableCell cell = row.getCell(i);
                cell.setText(text);
                CTR ctr = cell.getCTTc().getPList().get(0).getRList().get(0);
                ctr.addNewRPr().addNewSz().setVal(BigInteger.valueOf(18));
            }

            // Если строка данных последняя то нужно вставить итоговую строку
            if (di == data.size() - 1) {
                customerRowTotal.create(acceptAmount, shipmentAmount, tempCustomer);
                rowCount++;
            }
            rowCount++;
        }

        if (!data.isEmpty()) {
            customerRowTotal.create(totalAcceptAmount, totalShipmentAmount, "«" + product.getConditionalName() + "»");
        }

        return document;
    }

    public XWPFDocument warehouseMonthlyShipmentReport(
        List<WarehouseStateReportSecondDocDto> data,
        LocalDate date,
        String customer,
        User chief
    ) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        XWPFDocument document = new XWPFDocument();
        document.setZoomPercent(100);
        DocumentUtil.orientSize(document, STPageOrientation.LANDSCAPE, WordUtil.cmToDXA(29.7), WordUtil.cmToDXA(21));
        // Отступы документа
        long valueRL = WordUtil.cmToDXA(1.0), valueTB = WordUtil.cmToDXA(1), valueHF = WordUtil.cmToDXA(1.25);
        DocumentUtil.pageMargin(document, valueTB, valueRL, valueTB, valueRL, valueHF, valueHF, null);

        var ctpFooter = CTP.Factory.newInstance();
        ctpFooter.addNewPPr().addNewJc().setVal(STJc.CENTER);
        ctpFooter.addNewR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        ctpFooter.addNewR().addNewInstrText().setStringValue("PAGE   \\* MERGEFORMAT");
        ctpFooter.addNewR().addNewFldChar().setFldCharType(STFldCharType.SEPARATE);
        ctpFooter.addNewR().addNewT().setStringValue("1");
        ctpFooter.addNewR().addNewFldChar().setFldCharType(STFldCharType.END);

        var footerPolicy = document.createHeaderFooterPolicy();
        footerPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, new XWPFParagraph[] { new XWPFParagraph(ctpFooter, document) });

        // Элементы стилей
        XWPFStyles styles = document.createStyles();
        // Стандартные настройки стилей абзаца
        XWPFDefaultParagraphStyle defaultParagraphStyle = styles.getDefaultParagraphStyle();
        ParagraphDefaultsUtil.propertyNode(defaultParagraphStyle).setNil();
        // Стандартные настройки для стиля текста
        XWPFDefaultRunStyle defaultRunStyle = styles.getDefaultRunStyle();
        RunDefaultsUtil.size(defaultRunStyle, null);
        RunDefaultsUtil.sizeCs(defaultRunStyle, null);

        {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            run.setText("Начальнику 477 ВПМО " + customer);
            run.setFontSize(15);
            document.createParagraph();
        }

        {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            run.setText("Предоставляю сведения на " + date.format(BaseConstant.INSTANCE.getMONTH_FORMATTER()));
            run.setFontSize(15);
            document.createParagraph();
        }

        XWPFTable table = document.createTable();
        TableUtil.width(table, TableWidthType.DXA, WordUtil.cmToDXA(27));
        TableUtil.leftIndent(table, WordUtil.cmToDXA(0.02));
        TableUtil.cellAutoFit(table, Boolean.FALSE);
        TableUtil.cellMargin(table, WordUtil.cmToDXA(0.2), WordUtil.cmToDXA(0.2), WordUtil.cmToDXA(0.2), WordUtil.cmToDXA(0.2));

        {
            XWPFTableRow row = table.getRow(0);
            row.getCtRow().addNewTrPr().addNewTblHeader().setVal(STOnOff.TRUE);

            XWPFTableCell cell1 = row.getCell(0);
            cell1.setText("Наименование");
            CTR ctr1 = cell1.getCTTc().getPList().get(0).getRList().get(0);
            ctr1.addNewRPr().addNewSz().setVal(BigInteger.valueOf(28));

            for (int i = 1; i < 6; i++) {
                String text = "";
                switch (i) {
                    case 1: text = "Заводской номер"; break;
                    case 2: text = "Кол-во"; break;
                    case 3: text = "№ Заказа"; break;
                    case 4: text = "Заказчик"; break;
                    case 5: text = "Дата"; break;
                }
                XWPFTableCell cell = row.createCell();
                cell.setText(text);
                CTR ctr = cell.getCTTc().getPList().get(0).getRList().get(0);
                ctr.addNewRPr().addNewSz().setVal(BigInteger.valueOf(28));
            }
        }

        int amount = 0;
        var productName = data.isEmpty() ? "" : data.get(0).getProductName();
        for (int ind = 0; ind < data.size(); ind++) {
            var line = data.get(ind);
            XWPFTableRow row = table.createRow();

            if (Objects.equals(productName, line.getProductName())) {
                amount += 1;
            } else {
                amount = 1;
                productName = line.getProductName();
            }

            XWPFTableCell cell1 = row.getCell(0);
            cell1.setText(line.getProductName());
            CTR ctr1 = cell1.getCTTc().getPList().get(0).getRList().get(0);
            ctr1.addNewRPr().addNewSz().setVal(BigInteger.valueOf(28));

            for (int i = 1; i < 6; i++) {
                String text = "";
                switch (i) {
                    case 1: text = line.getSerialNumber(); break;
                    case 2: {
                        if ((ind + 1 < data.size() && !Objects.equals(line.getProductName(), data.get(ind + 1).getProductName())) || ind == data.size() - 1) {
                            text = String.valueOf(amount);
                        }
                        break;
                    }
                    case 3: text = line.getOrderNumber(); break;
                    case 4: text = line.getCustomer(); break;
                    case 5: text = line.getShipmentDate().format(BaseConstant.INSTANCE.getDATE_FORMATTER()); break;
                }
                XWPFTableCell cell = row.getCell(i);
                cell.setText(text);
                CTR ctr = cell.getCTTc().getPList().get(0).getRList().get(0);
                ctr.addNewRPr().addNewSz().setVal(BigInteger.valueOf(28));
            }

        }

        return document;
    }

    /**
     * Паспорт изделия
     */
    public XWPFDocument presentLogTemplate(ModelMap templateMap) throws IOException {
        if (templateMap.get("path") != null) {
            String path = templateMap.get("path").toString();
            String templatePath = path.substring(path.indexOf("Passport_Templates"));
            String directory = "blank\\word\\" + templatePath;

            ClassPathResource filesResource = new ClassPathResource(directory);
            File dir = filesResource.getFile();
            var files = dir.listFiles();

            if (files != null && files.length > 0) {
                XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, templatePath + File.separator + files[0].getName()));
                Map<String, String> map = new HashMap<>();
                map.put("_St_kontroler_OTK_", templateMap.get("seniorController") == null ? "" : templateMap.get("seniorController").toString());
                map.put("_Predstavitel__OTK_", templateMap.get("qcdRepresentative") == null ? "" : templateMap.get("qcdRepresentative").toString());
                map.put("_Nachalnik_____OTK_", templateMap.get("qcdChief") == null ? "" : templateMap.get("qcdChief").toString());
                map.put("_Rukovoditel_predp_", templateMap.get("headEnterprisePassport") == null ? "" : templateMap.get("headEnterprisePassport").toString());
                map.put("<Nizd123456>", templateMap.get("serialNumberMain") == null ? "" : templateMap.get("serialNumberMain").toString());
                map.put("<NIZD000001>", templateMap.get("serialNumberMain") == null ? "" : templateMap.get("serialNumberMain").toString());

                var prodModules = (Map<String, String>) templateMap.get("modulesMap");
                if (MapUtils.isNotEmpty(prodModules)) {
                    for (var table : document.getTables()) {
                        if (tableContainsMark(table)) {
                            for (var row : table.getRows()) {
                                for (var cell : row.getTableCells()) {
                                    if (cell.getText().contains("${<NIZD00") && !cell.getText().contains("${<NIZD000001>}")) {
                                        String decimal;
                                        if (templateMap.get("decimalMain").equals("ФАПИ.466226.004")) {
                                            decimal = row.getCell(1).getText();
                                        } else {
                                            decimal = row.getCell(0).getText();
                                        }
                                        String result = prodModules.entrySet()
                                            .stream()
                                            .filter(entry -> decimal.equals(entry.getValue()))
                                            .map(Map.Entry::getKey)
                                            .collect(Collectors.joining(" "));
                                        for (int i = cell.getParagraphs().size() - 1; i != 0; i--) {
                                           cell.removeParagraph(i);
                                        }
                                        cell.setText(result);
                                    }
                                }
                            }
                        }
                    }
                    map.putAll(getSerialByDecimalNumber(prodModules, templateMap.get("decimalMain").toString()));
                }
                return replaceTagInDocument(document, map);
            }
        }
        var document = new XWPFDocument();
        document.createParagraph().createRun().setText("Шаблон не найден");
        return document;
    }

    /**
     * Письмо на производство
     */
    public XWPFDocument productionShipmentLetter(ModelMap letterMap) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "production_letter.docx"));
        Map<String, String> map = new HashMap<>();
        map.put("NUMBER", letterMap.get("number") == null ? "" : letterMap.get("number").toString());
        map.put("DATE", letterMap.get("date") == null ? "" : letterMap.get("date").toString());

        XWPFTable table = document.getTables().get(0);
        var allotmentList = (List<LetterAllotmentItemDto>) letterMap.get("allotmentList");
        for (var item : allotmentList) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(item.getName());
            row.getCell(1).setText(item.getSectionFullNumber());
            row.getCell(2).setText(item.getProductName() + " \nп." + item.getOrderIndex() + "ВП");
            row.getCell(3).setText(new DecimalFormat("#").format(item.getAmount()));
            row.getCell(4).setText(item.getAcceptTypeCode());
            row.getCell(5).setText(item.getSpecialTestTypeCode());
        }

        return replaceTagInDocument(document, map);
    }

    /**
     * Отчет полная информация об изделии
     */
    public XWPFDocument reportProductInfo(ModelMap productMap) throws IOException {
        XWPFDocument document = new XWPFDocument(KtCommonUtil.INSTANCE.sourceFile(BLANK_WORLD, "productReport.docx"));
        Map<String, String> map = new HashMap<>();
        map.put("PRODUCT", productMap.get("product") == null ? "" : productMap.get("product").toString());
        map.put("STARTDATE", productMap.get("startDate") == null ? "" : productMap.get("startDate").toString());
        map.put("ENDDATE", productMap.get("endDate") == null ? "" : productMap.get("endDate").toString());

        List<XWPFTable> tables = document.getTables();
        XWPFTable table = tables.get(0);

        List<WarehouseStateMatValueDto> list = (List<WarehouseStateMatValueDto>) productMap.get("reportItems");

        for (var item : list) {
           var row = table.createRow();
           row.getCell(0).setText(item.getContract());
           row.getCell(1).setText(item.getAcceptDate().format(DateTimeFormatter.ofPattern(BaseConstant.DATE_PATTERN)));
           row.getCell(2).setText(item.getNoticeNumber());
           row.getCell(3).setText(String.valueOf(item.getLetter()));
           row.getCell(4).setText(item.getSerialNumber());
           row.getCell(5).setText(item.getShipmentDate().format(DateTimeFormatter.ofPattern(BaseConstant.DATE_PATTERN)));
           row.getCell(6).setText(item.getInternalWaybill());
           row.getCell(7).setText(item.getCustomer());
        }
        return replaceTagInDocument(document, map);
    }

    private Map<String, String> getSerialByDecimalNumber(Map <String, String> prodModules, String mainModule) {
        Map<String, String> map = new HashMap<>();
        switch (mainModule) {
            case "ЮКСУ.466225.009-02":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "ЮКСУ.467450.008-01": map.put("<NIZD000002>", item); break;
                        case "ЮКСУ.466929.009": map.put("<NIZD000003>", item); break;
                        default: map.put("<NIZD000004>", item); break;
                    }
                }
                break;
            case "ЮКСУ.466225.008-05":
                for (var item : prodModules.values()) {
                    switch (prodModules.get(item)) {
                        case "ЮКСУ.467444.036-10": map.put("<NIZD000002>", item);  break;
                        case "ЮКСУ.467130.027-05":
                            if (map.get("<NIZD000003>") == null) {
                                map.put("<NIZD000003>", item);
                            } else {
                                map.put("<NIZD000004>", item);
                            }
                            break;
                    }
                }
                break;
            case "ЮКСУ.467449.014-02":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "КУНИ.425689.001": map.put("<NIZD000002>", item);  break;
                        case "ЛРДА.436647.013": map.put("<NIZD000003>", item); break;
                        case "КУНИ.426479.003-05": map.put("<NIZD000004>", item); break;
                        case "ЮКСУ.426479.003-01": map.put("<NIZD000005>", item); break;
                        case "ЮКСУ.467450.008": map.put("<NIZD000006>", item); break;
                    }
                }
                break;
            case "ЮКСУ.467449.014-02.06":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "КУНИ.425689.001": map.put("<NIZD000002>", item); break;
                        case "ЛРДА.436647.013": map.put("<NIZD000003>", item); break;
                        case "КУНИ.426479.003-05":  map.put("<NIZD000004>", item); break;
                        case "ЮКСУ.426479.003-01": map.put("<NIZD000005>", item); break;
                        case "ЮКСУ.467450.008":
                            if (map.get("<NIZD000006>") == null) {
                                map.put("<NIZD000006>", item);
                            } else {
                                map.put("<NIZD000007>", item);
                            }
                            break;
                        case "ЮКСУ.467221.003":
                            if (map.get("<NIZD000008>") == null) {
                                map.put("<NIZD000008>", item);
                            } else {
                                map.put("<NIZD000009>", item);
                            }
                            break;
                        case "ЮКСУ.468351.002": map.put("<NIZD000010>", item); break;
                        case "ЮКСУ.467130.027": map.put("<NIZD000011>", item); break;
                        case "ЮКСУ.467249.002": map.put("<NIZD000012>", item); break;
                        case "ЮКСУ.467256.001": map.put("<NIZD000013>", item); break;
                        case "НВИТ.467846.008ТУ": map.put("<NIZD000014>", item); break;
                    }
                }
                break;
            case "ЮКСУ.466225.003-07.05":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "ЮКСУ.467450.008": map.put("<NIZD000002>", item); break;
                        case "ЛРДА.436647.014": map.put("<NIZD000003>", item); break;
                        case "ЮКСУ.467221.003-02": map.put("<NIZD000004>", item); break;
                        case "ЮКСУ.467555.004-04": map.put("<NIZD000005>", item); break;
                        case "ЮКСУ.468351.002": map.put("<NIZD000006>", item); break;
                    }
                }
                break;
            case "ЮКСУ.466225.003-07.07":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "ЮКСУ.467450.008": map.put("<NIZD000002>", item); break;
                        case "ЛРДА.436647.014": map.put("<NIZD000003>", item); break;
                        case "ЮКСУ.467555.004-04":
                            if (map.get("<NIZD000004>") == null) {
                                map.put("<NIZD000004>", item);
                            } else {
                                map.put("<NIZD000005>", item);
                            }
                            break;
                        case "ЮКСУ.468351.002": map.put("<NIZD000006>", item); break;
                    }
                }
                break;
            case "ЮКСУ.466225.003-07.06":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "ЮКСУ.467450.008": map.put("<NIZD000002>", item); break;
                        case "ЛРДА.436647.014": map.put("<NIZD000003>", item); break;
                        case "ЮКСУ.467130.027-01": map.put("<NIZD000004>", item); break;
                        case "ЮКСУ.467555.004-04":
                            if (map.get("<NIZD000005>") == null) {
                                map.put("<NIZD000005>", item);
                            } else {
                                map.put("<NIZD000006>", item);
                            }
                            break;
                        case "ЮКСУ.468351.002": map.put("<NIZD000007>", item); break;
                    }
                }
                break;
            case "ЮКСУ.466225.001-08":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "":
                            if (map.get("<NIZD000002>") == null) {
                                map.put("<NIZD000002>", item);
                            } else if (map.get("<NIZD000003>") == null) {
                                map.put("<NIZD000003>", item);
                            } else {
                                map.put("<NIZD000004>", item);
                            }
                            break;
                        case "ЮКСУ.467444.015": map.put("<NIZD000005>", item);  break;
                        case "ЛРДА.467532.001-01": map.put("<NIZD000006>", item); break;
                        case "ЛРДА.436647.013": map.put("<NIZD000007>", item); break;
                        case "КУНИ.426479.003-05": map.put("<NIZD000008>", item); break;
                        case "ЮКСУ.426479.003-01": map.put("<NIZD000009>", item); break;
                    }
                }
                break;
            case "ЮКСУ.466225.003":
            case "ЮКСУ.466225.003-07":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "ЮКСУ.467450.008": map.put("<NIZD000002>", item); break;
                        case "ЛРДА.436647.014": map.put("<NIZD000003>", item); break;
                    }
                }
                break;
            case "ЮКСУ.466225.003-06":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "ЮКСУ.467450.008": map.put("<NIZD000002>", item); break;
                        case "ЛРДА.436647.013": map.put("<NIZD000003>", item); break;
                    }
                }
                break;
            case "ЮКСУ.466225.008-04":
                for (var item : prodModules.keySet()) {
                    if (prodModules.get(item).equals("ЮКСУ.467450.008-01")) {
                        map.put("<NIZD000002>", item);
                    }
                }
                break;
            case "ЮКСУ.466225.003-06.02":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "ЮКСУ.467444.008":
                            if (map.get("<NIZD000002>") == null) {
                                map.put("<NIZD000002>", item);
                            } else if (map.get("<NIZD000004>") == null) {
                                map.put("<NIZD000004>", item);
                            } else if (map.get("<NIZD000005>") == null) {
                                map.put("<NIZD000005>", item);
                            } else if (map.get("<NIZD000006>") == null) {
                                map.put("<NIZD000006>", item);
                            }
                            break;
                        case "ЮКСУ.468351.002":
                            if (map.get("<NIZD000013>") == null) {
                                map.put("<NIZD000013>", item);
                            } else if (map.get("<NIZD000014>") == null) {
                                map.put("<NIZD000014>", item);
                            } else {
                                map.put("<NIZD000015>", item);
                            }
                            break;
                        case "ЛРДА.436647.013":
                            if (map.get("<NIZD000007>") == null) {
                                map.put("<NIZD000007>", item);
                            } else {
                                map.put("<NIZD000008>", item);
                            }
                            break;
                        case "ЮКСУ.465610.035-01": map.put("<NIZD000009>", item); break;
                        case "ЮКСУ.436647.013": map.put("<NIZD000010>", item); break;
                        case "ЮКСУ.467130.018": map.put("<NIZD000011>", item); break;
                        case "ЮКСУ.467130.026": map.put("<NIZD000012>", item); break;
                        case "ЯАПВ.467617.009-03": map.put("<NIZD000016>", item); break;
                    }
                }
                break;
            case "ЮКСУ.436434.002":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "ЮКСУ.436434.002": map.put("<NIZD000002>", item); break;
                        case "ЮКСУ.467444.043": map.put("<NIZD000003>", item); break;
                        case "ЮКСУ.465614.002": map.put("<NIZD000004>", item); break;
                        case "ЮКСУ.465614.002-01": map.put("<NIZD000005>", item); break;
                        case "ЮКСУ.468172.001": map.put("<NIZD000015>", item); break;
                        case "ЮКСУ.468172.002": map.put("<NIZD000006>", item); break;
                        case "ЮКСУ.468157.001": map.put("<NIZD000007>", item); break;
                        case "ЮКСУ.468157.001-01": map.put("<NIZD000008>", item); break;
                        case "ЮКСУ.468158.001": map.put("<NIZD000009>", item); break;
                        case "ЮКСУ.468158.001-01": map.put("<NIZD000010>", item); break;
                        case "ЮКСУ.468157.004": map.put("<NIZD000011>", item); break;
                        case "ЮКСУ.468157.004-01": map.put("<NIZD000012>", item); break;
                        case "ЮКСУ.468172.012": map.put("<NIZD000013>", item); break;
                        case "ЮКСУ.468172.014": map.put("<NIZD000014>", item); break;
                    }
                }
                break;
            case "ЮКСУ.436434.003":
                for (var item : prodModules.keySet()) {
                    switch (prodModules.get(item)) {
                        case "ЮКСУ.436434.003": map.put("<NIZD000002>", item); break;
                        case "ЮКСУ.467444.046": map.put("<NIZD000003>", item); break;
                        case "ЮКСУ.468171.001": map.put("<NIZD000004>", item); break;
                        case "ЮКСУ.468171.001-01": map.put("<NIZD000005>", item); break;
                        case "ЮКСУ.468171.002": map.put("<NIZD000006>", item); break;
                        case "ЮКСУ.468171.002-01": map.put("<NIZD000007>", item); break;
                        case "ЮКСУ.468171.003": map.put("<NIZD000008>", item); break;
                        case "ЮКСУ.468171.003-01": map.put("<NIZD000009>", item); break;
                        case "ЮКСУ.468172.005": map.put("<NIZD000010>", item); break;
                        case "ЮКСУ.468172.006": map.put("<NIZD000011>", item); break;
                        case "ЮКСУ.468363.104":
                            if (map.get("<NIZD000012>") == null) {
                                map.put("<NIZD000012>", item);
                            } else if (map.get("<NIZD000013>") == null) {
                                map.put("<NIZD000013>", item);
                            } else if (map.get("<NIZD000014>") == null) {
                                map.put("<NIZD000014>", item);
                            } else if (map.get("<NIZD000015>") == null) {
                                map.put("<NIZD000015>", item);
                            } else if (map.get("<NIZD000016>") == null) {
                                map.put("<NIZD000016>", item);
                            } else if (map.get("<NIZD000017>") == null) {
                                map.put("<NIZD000017>", item);
                            } else if (map.get("<NIZD000018>") == null) {
                                map.put("<NIZD000018>", item);
                            } else if (map.get("<NIZD000019>") == null) {
                                map.put("<NIZD000019>", item);
                            } else if (map.get("<NIZD000020>") == null) {
                                map.put("<NIZD000020>", item);
                            } else if (map.get("<NIZD000021>") == null) {
                                map.put("<NIZD000021>", item);
                            } else if (map.get("<NIZD000022>") == null) {
                                map.put("<NIZD000022>", item);
                            } else if (map.get("<NIZD000023>") == null) {
                                map.put("<NIZD000023>", item);
                            }
                            break;
                        case "ЮКСУ.468363.105": map.put("<NIZD000024>", item); break;
                        case "ЮКСУ.468363.105-01": map.put("<NIZD000025>", item);  break;
                        case "ЮКСУ.468364.031": map.put("<NIZD000026>", item); break;
                        case "ЮКСУ.468364.031-01": map.put("<NIZD000027>", item); break;
                    }
                }
                break;
        }
        return  map;
    }

    private boolean tableContainsMark(XWPFTable table) {
        for (var row : table.getRows()) {
            for (var cell : row.getTableCells()) {
                if (cell.getText().contains("${<NIZD000002>}")) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getFaxNumber(String phone) {
        var pos = phone.indexOf("факс");
        if (pos < 0) pos = phone.indexOf("Факс");
        if (pos < 0) pos = phone.indexOf("ф");
        if (pos < 0) pos = phone.indexOf("Ф");
        return pos < 0 ? "" : phone.substring(pos, phone.length() - pos + 1);
    }

    private ContractSection getMainContractSection(Contract contract) {
        for (var item : contract.getSectionList()) {
            if (item.getNumber() == 0) {
                return item;
            }
        }
        throw new IllegalStateException("Основной договор не найден");
    }

    private XWPFDocument replaceTagInDocument(XWPFDocument document, Map<String, String> map) {
        for (var table : document.getTables()) {
            for (var row : table.getRows()) {
                for (var cell : row.getTableCells()) {
                    for (var paragraph : cell.getParagraphs()) {
                        WordUtil.replaceTagInText(paragraph, map);
                    }
                }
            }
        }
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        for (var paragraph : paragraphList) {
            WordUtil.replaceTagInText(paragraph, map);
        }
        return document;
    }
}