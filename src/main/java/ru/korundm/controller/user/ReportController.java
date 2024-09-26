package ru.korundm.controller.user;

import eco.dao.EcoCompanyConstProtocolService;
import eco.dao.EcoContractService;
import eco.dao.EcoProductChargesProtocolService;
import eco.entity.EcoCompanyConstProtocol;
import eco.entity.EcoContract;
import eco.entity.EcoLot;
import eco.entity.EcoProductChargesProtocol;
import kotlin.Pair;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.ContractService;
import ru.korundm.dao.ReportContractService;
import ru.korundm.entity.*;
import ru.korundm.enumeration.ContractType;
import ru.korundm.enumeration.Performer;
import ru.korundm.enumeration.PriceKindType;
import ru.korundm.helper.AttachmentMediaType;
import ru.korundm.report.excel.document.military.Acceptance;
import ru.korundm.report.excel.document.military.report.StateDefenceOrderErrorExcel;
import ru.korundm.report.xml.helper.*;
import ru.korundm.util.KtCommonUtil;
import ru.korundm.report.xml.util.MarshalUtil;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@JBossLog
@RequestMapping(RequestPath.PROD)
public class ReportController {

    private static final int HALF_CENTURY = 50;

    private final MessageSource messageSource;
    private final EcoContractService ecoContractService;
    private final ContractService contractService;
    private final EcoProductChargesProtocolService productChargesProtocolService;
    private final EcoCompanyConstProtocolService companyConstProtocolService;
    private final ReportContractService reportContractService;

    public ReportController(
        MessageSource messageSource,
        EcoContractService ecoContractService,
        ContractService contractService,
        EcoProductChargesProtocolService productChargesProtocolService,
        EcoCompanyConstProtocolService companyConstProtocolService,
        ReportContractService reportContractService
    ) {
        this.messageSource = messageSource;
        this.ecoContractService = ecoContractService;
        this.contractService = contractService;
        this.productChargesProtocolService = productChargesProtocolService;
        this.companyConstProtocolService = companyConstProtocolService;
        this.reportContractService = reportContractService;
    }

    @PostMapping(
        value = "/report",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody void report(
        HttpServletResponse response,
        @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file.getSize() > 0) {
            Workbook workbook = null;
            String name = file.getOriginalFilename();
            if (name != null) {
                InputStream inputStream = file.getInputStream();
                if (name.endsWith(AttachmentMediaType.XLS.getExtension())) {
                    workbook = new HSSFWorkbook(inputStream);
                } else if (name.endsWith(AttachmentMediaType.XLSX.getExtension())) {
                    workbook = new XSSFWorkbook(inputStream);
                }
            }
            if (workbook != null) {
                AccountingData accountingData = new AccountingData();
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    ru.korundm.report.xml.helper.Contract contract = new ru.korundm.report.xml.helper.Contract();
                    GroupFinancingContract groupFinancingContract = new GroupFinancingContract();
                    CustomerCash customerCash = new CustomerCash();
                    BankLoans bankLoans = new BankLoans();
                    DebtPercentageCredits debtPercentageCredits = new DebtPercentageCredits();
                    DebtSuppliers debtSuppliers = new DebtSuppliers();
                    GroupDistributionContractResources groupDistributionContractResources = new GroupDistributionContractResources();
                    GroupCash groupCash = new GroupCash();
                    CashEquitySeparateAccount cashEquitySeparateAccount = new CashEquitySeparateAccount();
                    BankDeposits bankDeposits = new BankDeposits();
                    AdvancesIssued advancesIssued = new AdvancesIssued();
                    GroupReserves groupReserves = new GroupReserves();
                    MaterialsInWarehouses materialsInWarehouses = new MaterialsInWarehouses();
                    VATOnPurchasedAssets vatOnPurchasedAssets = new VATOnPurchasedAssets();
                    PrefabricatedInStocks prefabricatedInStocks = new PrefabricatedInStocks();
                    MaterialsTransferredToRecycling materialsTransferredToRecycling = new MaterialsTransferredToRecycling();
                    FutureSpending futureSpending = new FutureSpending();
                    MeansProduction meansProduction = new MeansProduction();
                    GroupProduction groupProduction = new GroupProduction();
                    MaterialCosts materialCosts = new MaterialCosts();
                    PayrollCosts payrollCosts = new PayrollCosts();
                    OtherProductionCosts otherProductionCosts = new OtherProductionCosts();
                    OverheadCost overheadCost = new OverheadCost();
                    GeneralBusinessCosts generalBusinessCosts = new GeneralBusinessCosts();
                    SemifinishedInternalWorks semifinishedInternalWorks = new SemifinishedInternalWorks();
                    ProductionInnerProducts productionInnerProducts = new ProductionInnerProducts();
                    Output output = new Output();
                    FinishedProducts finishedProducts = new FinishedProducts();
                    GroupShippingProductsPerformanceWorks groupShippingProductsPerformanceWorks = new GroupShippingProductsPerformanceWorks();
                    CostSales costSales = new CostSales();
                    Aur aur = new Aur();
                    SellingCosts sellingCosts = new SellingCosts();
                    BankLoanInterest bankLoanInterest = new BankLoanInterest();
                    VATSales vatSales = new VATSales();
                    Profit profit = new Profit();
                    RedirectionAttraction redirectionAttraction = new RedirectionAttraction();
                    WrittenOffFunds writtenOffFunds = new WrittenOffFunds();

                    Sheet sheet = workbook.getSheetAt(i);

                    // Контракт
                    Row row = sheet.getRow(3);
                    contract.setIgk(row.getCell(13).getStringCellValue());
                    contract.setSingleAccountNumber(row.getCell(14).getStringCellValue());
                    contract.setReportDate(row.getCell(15).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    contract.setNumber(row.getCell(16).getStringCellValue());
                    contract.setContractDate(row.getCell(17).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    contract.setPlannedExecutionDate(row.getCell(18).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

                    // Финансирование контракта
                    row = sheet.getRow(4);
                    groupFinancingContract.setTargetAmountFunding(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    groupFinancingContract.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    groupFinancingContract.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));

                    // Ден.средства, полученные от заказчика (авансы)
                    row = sheet.getRow(5);
                    customerCash.setContractPrice(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    customerCash.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    customerCash.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    customerCash.setReturnedCustomer(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    customerCash.setReturnedCustomerOwnFunds(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    customerCash.setReceivedFromCustomer(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    // Кредиты банка
                    row = sheet.getRow(6);
                    bankLoans.setPlannedVolumeCrediting(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    bankLoans.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    bankLoans.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    bankLoans.setRedeemedBodyCredit(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    bankLoans.setRedeemedCreditCreditsOwnFunds(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    bankLoans.setAttractedCredits(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    // Задолженность по процентам кредитов
                    row = sheet.getRow(7);
                    debtPercentageCredits.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    debtPercentageCredits.setRedeemedPercent(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    debtPercentageCredits.setRedeemedInterestOwnFunds(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    debtPercentageCredits.setAccruedInterest(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    // Задолженность перед поставщиками
                    row = sheet.getRow(8);
                    debtSuppliers.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    debtSuppliers.setPaidSuppliers(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    debtSuppliers.setPaidSuppliersOtherContracts(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    debtSuppliers.setPaidSuppliersOwnFunds(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    debtSuppliers.setTotalDebt(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));

                    // Распределение ресурсов контракта
                    row = sheet.getRow(9);
                    groupDistributionContractResources.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));

                    // Денежные средства
                    row = sheet.getRow(10);
                    groupCash.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    groupCash.setCashAssets(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    groupCash.setCashAssetsOtherContracts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    groupCash.setCashAssetsOwnFunds(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    groupCash.setResourceUsage(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    groupCash.setUseResourcesOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    groupCash.setUseResourcesOwnFunds(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));
                    // Денежные средства на ОС
                    row = sheet.getRow(11);
                    cashEquitySeparateAccount.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    cashEquitySeparateAccount.setCreditedContractExecution(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    cashEquitySeparateAccount.setCreditedOther(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    cashEquitySeparateAccount.setWrittenOffContractExecution(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    cashEquitySeparateAccount.setWrittenOffOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    cashEquitySeparateAccount.setChargedOrganizationCosts(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));
                    // Денежные средства на депозитах в банке
                    row = sheet.getRow(12);
                    bankDeposits.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    bankDeposits.setListedDeposit(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    bankDeposits.setReturnedWithDeposit(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    // Авансы, выданные поставщикам
                    row = sheet.getRow(13);
                    advancesIssued.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    advancesIssued.setAdvancesContractExecution(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    advancesIssued.setAdvancesOtherContracts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    advancesIssued.setAdvancesOwnFunds(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    advancesIssued.setCreditAdvances(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    advancesIssued.setChargedDebtCooperation(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));

                    // Запасы
                    row = sheet.getRow(14);
                    groupReserves.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    groupReserves.setFormedStocks(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    groupReserves.setFormedStocksMeansOtherContracts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    groupReserves.setFormedStocksOwnFunds(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    groupReserves.setUsedStocks(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    groupReserves.setUsedStocksOnOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    groupReserves.setUsedStocksOrganizationNeeds(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));
                    // Материалы на складах
                    row = sheet.getRow(15);
                    materialsInWarehouses.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    materialsInWarehouses.setReceivedMaterials(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    materialsInWarehouses.setMaterialsReceivedMeansOtherContracts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    materialsInWarehouses.setReceivedMaterialsOwnFunds(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    materialsInWarehouses.setUsedMaterials(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    materialsInWarehouses.setMaterialsUsedOnOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    materialsInWarehouses.setUsedMaterialsNeedsOrganization(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));
                    // НДС входящий
                    row = sheet.getRow(16);
                    vatOnPurchasedAssets.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    vatOnPurchasedAssets.setHighlighted(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    vatOnPurchasedAssets.setIncludedInStockCost(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    vatOnPurchasedAssets.setAcceptedByDeduction(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));
                    // Полуфабрикаты на складах
                    row = sheet.getRow(17);
                    prefabricatedInStocks.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    prefabricatedInStocks.setReceivedSemiFinishedProducts(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    prefabricatedInStocks.setReceivedSemiFinishedProductsMeansOtherContracts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    prefabricatedInStocks.setReceivedSemiFinishedOwnMeans(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    prefabricatedInStocks.setUsedSemiFinishedProducts(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    prefabricatedInStocks.setUsedSemiManufacturedForOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    prefabricatedInStocks.setUsedSemiManufacturedNeedsOrganizations(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));
                    // Материалы, переданные в переработку
                    row = sheet.getRow(18);
                    materialsTransferredToRecycling.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    materialsTransferredToRecycling.setSubmittedThirdPartyContractor(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    materialsTransferredToRecycling.setAdoptedFromRecycling(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    materialsTransferredToRecycling.setAdoptedFromOrganizationRecycling(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));
                    // Расходы будущих периодов
                    row = sheet.getRow(19);
                    futureSpending.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    futureSpending.setAccruedRBP(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    futureSpending.setWrittenOffRBP(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    // Средства производства
                    row = sheet.getRow(20);
                    meansProduction.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    meansProduction.setReceivedMeansProduction(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    meansProduction.setReceivedMeansProductionMeansOtherContracts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    meansProduction.setReceivedMeansProductionOwnFunds(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    meansProduction.setRetiredProductionTools(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    meansProduction.setRetiredProductionMeansForOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    meansProduction.setRetiredProductionMeansNeedsOrganization(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));

                    // Производство
                    row = sheet.getRow(21);
                    groupProduction.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    groupProduction.setProductionCosts(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    groupProduction.setProductionCostsOtherContracts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    groupProduction.setProductionCostsOwn(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    groupProduction.setRelease(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    groupProduction.setIssueOnOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    groupProduction.setOrganizationNeedsIssue(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));
                    // Затраты на материалы
                    row = sheet.getRow(22);
                    materialCosts.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    materialCosts.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    materialCosts.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    materialCosts.setChargedCosts(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    materialCosts.setChargedOtherContractCosts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    materialCosts.setChargedOwnCost(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    materialCosts.setExcludedFromCosts(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    materialCosts.setRelatedToOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    materialCosts.setRelatedToOwnCosts(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));
                    // Затраты на оплату труда
                    row = sheet.getRow(23);
                    payrollCosts.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    payrollCosts.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    payrollCosts.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    payrollCosts.setSalariesExecutors(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    // Прочие производственные затраты
                    row = sheet.getRow(24);
                    otherProductionCosts.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    otherProductionCosts.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    otherProductionCosts.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    otherProductionCosts.setChargedCosts(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    otherProductionCosts.setChargedOtherContractCosts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    otherProductionCosts.setChargedOwnCost(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    otherProductionCosts.setExcludedFromCosts(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    otherProductionCosts.setRelatedToOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    otherProductionCosts.setRelatedToOwnCosts(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));
                    // Прочие производственные затраты
                    row = sheet.getRow(25);
                    overheadCost.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    overheadCost.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    overheadCost.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    overheadCost.setCostSize(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    // Общехозяйственные затраты
                    row = sheet.getRow(26);
                    generalBusinessCosts.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    generalBusinessCosts.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    generalBusinessCosts.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    generalBusinessCosts.setCostSize(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    // Полуфабрикаты, внутренние работы
                    row = sheet.getRow(27);
                    semifinishedInternalWorks.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    semifinishedInternalWorks.setChargedToCosts(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    // Выпуск полуфабрикатов, внутренних работ
                    row = sheet.getRow(28);
                    productionInnerProducts.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    productionInnerProducts.setReleased(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    // Выпуск продукции
                    row = sheet.getRow(29);
                    output.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    output.setReleased(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));

                    // Готовый товар на складе
                    row = sheet.getRow(30);
                    finishedProducts.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    finishedProducts.setReleased(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    finishedProducts.setUsedFromOtherContracts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    finishedProducts.setUsedOwn(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    finishedProducts.setShipped(String.format(Locale.US, "%.2f", row.getCell(10).getNumericCellValue()));
                    finishedProducts.setShippedToOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    finishedProducts.setShippedToOrganizationNeeds(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));

                    // Отгрузка товара, выполнение работ, оказание услуг
                    row = sheet.getRow(31);
                    groupShippingProductsPerformanceWorks.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    groupShippingProductsPerformanceWorks.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    groupShippingProductsPerformanceWorks.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    // Себестоимость реализованной продукции
                    row = sheet.getRow(32);
                    costSales.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    costSales.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    costSales.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    costSales.setCostContract(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    costSales.setCostNonContract(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    // Административно-управленческие расходы
                    row = sheet.getRow(33);
                    aur.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    aur.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    aur.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    aur.setCostSize(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    // Коммерческие расходы
                    row = sheet.getRow(34);
                    sellingCosts.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    sellingCosts.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    sellingCosts.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    sellingCosts.setCostSize(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    // Проценты по кредитам банка
                    row = sheet.getRow(35);
                    bankLoanInterest.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    bankLoanInterest.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    bankLoanInterest.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    bankLoanInterest.setCostSize(String.format(Locale.US, "%.2f", row.getCell(6).getNumericCellValue()));
                    // НДС с выручки от продаж
                    row = sheet.getRow(36);
                    vatSales.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    vatSales.setVatAmount(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    // Прибыль контракта
                    row = sheet.getRow(37);
                    profit.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    profit.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    profit.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));

                    // Привлечение ресурсов в контракт / Перенаправление средств контракта
                    row = sheet.getRow(38);
                    redirectionAttraction.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));
                    redirectionAttraction.setAttractedFundsOtherContracts(String.format(Locale.US, "%.2f", row.getCell(7).getNumericCellValue()));
                    redirectionAttraction.setAttractedOwnFunds(String.format(Locale.US, "%.2f", row.getCell(8).getNumericCellValue()));
                    redirectionAttraction.setUsedOnOtherContracts(String.format(Locale.US, "%.2f", row.getCell(11).getNumericCellValue()));
                    redirectionAttraction.setUsedForYourOwnNeeds(String.format(Locale.US, "%.2f", row.getCell(12).getNumericCellValue()));

                    // Списание денежных средств с ОС  Контракта
                    row = sheet.getRow(39);
                    writtenOffFunds.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                    writtenOffFunds.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                    writtenOffFunds.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));

                    groupFinancingContract.setDebtSuppliers(debtSuppliers);
                    groupFinancingContract.setDebtPercentageCredits(debtPercentageCredits);
                    groupFinancingContract.setBankLoans(bankLoans);
                    groupFinancingContract.setCustomerCash(customerCash);
                    contract.setGroupFinancingContract(groupFinancingContract);

                    groupCash.setAdvancesIssued(advancesIssued);
                    groupCash.setBankDeposits(bankDeposits);
                    groupCash.setCashEquitySeparateAccount(cashEquitySeparateAccount);
                    groupDistributionContractResources.setGroupCash(groupCash);
                    groupReserves.setMaterialsInWarehouses(materialsInWarehouses);
                    groupReserves.setVatOnPurchasedAssets(vatOnPurchasedAssets);
                    groupReserves.setPrefabricatedInStocks(prefabricatedInStocks);
                    groupReserves.setMaterialsTransferredToRecycling(materialsTransferredToRecycling);
                    groupReserves.setFutureSpending(futureSpending);
                    groupReserves.setMeansProduction(meansProduction);
                    groupDistributionContractResources.setGroupReserves(groupReserves);
                    groupProduction.setMaterialCosts(materialCosts);
                    groupProduction.setPayrollCosts(payrollCosts);
                    groupProduction.setOtherProductionCosts(otherProductionCosts);
                    groupProduction.setOverheadCost(overheadCost);
                    groupProduction.setGeneralBusinessCosts(generalBusinessCosts);
                    groupProduction.setSemifinishedInternalWorks(semifinishedInternalWorks);
                    groupProduction.setProductionInnerProducts(productionInnerProducts);
                    groupProduction.setOutput(output);
                    groupDistributionContractResources.setGroupProduction(groupProduction);
                    groupDistributionContractResources.setFinishedProducts(finishedProducts);
                    contract.setGroupDistributionContractResources(groupDistributionContractResources);
                    groupShippingProductsPerformanceWorks.setCostSales(costSales);
                    groupShippingProductsPerformanceWorks.setAur(aur);
                    groupShippingProductsPerformanceWorks.setSellingCosts(sellingCosts);
                    groupShippingProductsPerformanceWorks.setBankLoanInterest(bankLoanInterest);
                    groupShippingProductsPerformanceWorks.setVatSales(vatSales);
                    groupShippingProductsPerformanceWorks.setProfit(profit);
                    contract.setGroupShippingProductsPerformanceWorks(groupShippingProductsPerformanceWorks);
                    contract.setRedirectionAttraction(redirectionAttraction);
                    contract.setWrittenOffFunds(writtenOffFunds);

                    accountingData.getContractList().add(contract);
                }

                MarshalUtil.marshal(accountingData, response);
            }
        }
    }

    @PostMapping(
        value = "/acceptance",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody void acceptance(
        HttpServletResponse response,
        HttpServletRequest request,
        @RequestParam("file") MultipartFile file
    ) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (file.getSize() > 0) {
            Workbook workbook = null;
            String name = file.getOriginalFilename();
            if (name != null) {
                InputStream inputStream = file.getInputStream();
                if (name.endsWith(AttachmentMediaType.XLS.getExtension())) {
                    workbook = new HSSFWorkbook(inputStream);
                } else if (name.endsWith(AttachmentMediaType.XLSX.getExtension())) {
                    workbook = new XSSFWorkbook(inputStream);
                }
            }
            if (workbook != null) {
                List<Acceptance> acceptanceList = new ArrayList<>();

                Sheet sheet = workbook.getSheetAt(0);
                for (Row row : sheet) {
                    Acceptance acceptance = new Acceptance();

                    Cell contractInformationCell = row.getCell(0);
                    if (contractInformationCell != null) {
                        String contractInformation = contractInformationCell.getStringCellValue().trim().replaceAll("'", "");
                        String[] contractInformationArray = contractInformation.split("/");
                        if (contractInformationArray.length > 1) {
                            // номер контракта
                            Long contractNumber = Long.parseLong(contractInformationArray[0]);
                            String[] secondPartArray = contractInformationArray[1].split(" ");
                            String contractShortName = secondPartArray[0];
                            String[] contractShortNameArray = contractShortName.split("-");
                            String needDate = row.getCell(1).getStringCellValue().trim().replaceAll("'", "");
                            if (needDate.split("\\.").length == 3) {
                                LocalDate date = LocalDate.parse(needDate, DateTimeFormatter.ofPattern(BaseConstant.DATE_PATTERN));

                                Performer performer = Performer.Companion.getByPrefix(contractShortNameArray[0]);
                                ContractType contractType = ContractType.Companion.getByCode(
                                    contractInformationArray.length == 4 ? contractShortNameArray[1] + "-" + contractShortNameArray[2] : contractShortNameArray[1]
                                );
//                            LocalDate deliveryDate = row.getCell(2).getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                String dd = row.getCell(2).getStringCellValue().trim().replaceAll("'", "");
                                LocalDate deliveryDate = LocalDate.parse(dd, DateTimeFormatter.ofPattern(BaseConstant.DATE_PATTERN));

                                acceptance.setContractName(contractInformation);
                                acceptance.setDeliveryDate(deliveryDate);
                                List<EcoContract> contractList = ecoContractService.getByParams(performer.getId(), contractNumber, contractType.getId(), date.getYear());

                                if (!contractList.isEmpty()) {
                                    List<Acceptance.ProductInfo> productInfoList = new ArrayList<>();
                                    for (var contractSection : contractList.get(0).getSectionList()) {
                                        List<EcoLot> lotList = contractSection.getLotGroupList().stream().flatMap(lotGroup -> lotGroup.getLotList().stream()).filter(lot -> lot.getDeliveryDate().toLocalDate().equals(deliveryDate)).collect(Collectors.toList());
                                        if (!lotList.isEmpty()) {
                                            for (var lot : lotList) {
                                                Acceptance.ProductInfo info = new Acceptance.ProductInfo();
                                                info.setProductName(lot.getLotGroup().getProduct().getProductName());
                                                info.setType(lot.getLotGroup().getProduct().getProductType().getName());
                                                PriceKindType priceKindType = lot.getPriceKind();
                                                if (Objects.equals(PriceKindType.FINAL.getId(), priceKindType.getId())) {
                                                    EcoProductChargesProtocol productChargesProtocol = productChargesProtocolService.read(lot.getPriceProtocol());
                                                    info.setProtocolNumber(productChargesProtocol.getProtocolNumber().replaceAll("№", ""));
                                                    info.setProtocolPrice(productChargesProtocol.getPrice());
                                                    info.setProtocolDate(productChargesProtocol.getProtocolDate().toLocalDate());
                                                } else if (!Objects.equals(ContractType.SUPPLY_OF_EXPORTED.getId(), contractType.getId()) && Objects.equals(PriceKindType.EXPORT.getId(), lot.getPriceKind())) {
                                                    info.setProtocolNumber(messageSource.getMessage("priceKind.fixed", new Object[]{}, request.getLocale()));
                                                    info.setProtocolPrice(lot.getPrice());
                                                } else {
                                                    info.setProtocolNumber(messageSource.getMessage(priceKindType.getProperty(), new Object[]{}, request.getLocale()));
                                                    info.setProtocolPrice(lot.getPrice());
                                                }
                                                productInfoList.add(info);
                                            }
                                        }
                                    }

                                    acceptance.getProductInfoList().addAll(productInfoList);
                                    acceptanceList.add(acceptance);
                                }
                            }
                        }
                    }
                }

                Workbook book = new HSSFWorkbook();
                sheet = book.createSheet("acceptance");

                DataFormat format = book.createDataFormat();
                CellStyle dateStyle = book.createCellStyle();
                dateStyle.setDataFormat(format.getFormat(BaseConstant.DATE_PATTERN));

                int counter = 0;
                for (var acceptance : acceptanceList) {
                    for (int j = 0; j < acceptance.getProductInfoList().size(); j++) {
                        Row row = sheet.createRow(counter);

                        Cell contractName = row.createCell(0);
                        contractName.setCellValue(acceptance.getContractName());

                        Cell deliveryDate = row.createCell(1);
                        deliveryDate.setCellStyle(dateStyle);
                        deliveryDate.setCellValue(acceptance.getDeliveryDate());

                        Acceptance.ProductInfo info = acceptance.getProductInfoList().get(j);
                        Cell productName = row.createCell(2);
                        productName.setCellValue(info.getProductName());

                        Cell type = row.createCell(3);
                        type.setCellValue(info.getType());

                        Cell protocolPrice = row.createCell(4);
                        protocolPrice.setCellValue(info.getProtocolPrice().doubleValue());

                        Cell protocolNumber = row.createCell(5);
                        protocolNumber.setCellValue(info.getProtocolNumber());

                        if (info.getProtocolDate() != null) {
                            Cell protocolDate = row.createCell(6);
                            protocolDate.setCellStyle(dateStyle);
                            protocolDate.setCellValue(info.getProtocolDate());
                        }
                        counter++;
                    }
                }

                KtCommonUtil.INSTANCE.attachDocumentXLS(response, book, "acceptance");
            }
        }
    }

    @GetMapping("/state-defense-order")
    public String report_stateDefenseOrder() {
        return "prod/section/stateDefenseOrder";
    }

    @PostMapping(
        value = "/state-defense-order/generation",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @ResponseBody
    public void report_stateDefenseOrder_generation(
        HttpServletResponse response,
        @RequestParam("firstStep") MultipartFile firstStep,
        @RequestParam("secondStep") MultipartFile secondStep,
        @RequestParam(name = "db", required = false, defaultValue = "false") Boolean db
    ) throws IOException, JAXBException {
        Map<String, List<BigDecimal>> dictionary = new HashMap<>();

        // Сбор информации об ошибках
        var stateDefenceOrder = StateDefenceOrderErrorExcel.Companion.create();

        // Этапы 2 и 3
        if (secondStep.getSize() > 0) {
            Workbook workbook = null;
            String name = secondStep.getOriginalFilename();
            if (name != null) {
                InputStream inputStream = secondStep.getInputStream();
                if (name.toLowerCase().endsWith(AttachmentMediaType.XLS.getExtension())) {
                    workbook = new HSSFWorkbook(inputStream);
                } else if (name.toLowerCase().endsWith(AttachmentMediaType.XLSX.getExtension())) {
                    workbook = new XSSFWorkbook(inputStream);
                }
            }
            if (workbook != null) {
                Sheet sheet = workbook.getSheetAt(0);
                int rowCount = 0;
                for (var row : sheet) {
                    // Данные записываем начиная с 4 строки
                    if (rowCount >= 3 && row != null) {
                        List<BigDecimal> values = new ArrayList<>();
                        if (row.getCell(8) != null) {
                            values.add(BigDecimal.valueOf(row.getCell(8).getNumericCellValue()));
                        }
                        if (row.getCell(10) != null) {
                            values.add(BigDecimal.valueOf(row.getCell(10).getNumericCellValue()));
                        }
                        if (row.getCell(11) != null) {
                            values.add(BigDecimal.valueOf(row.getCell(11).getNumericCellValue()));
                        }
                        if (row.getCell(1) != null) {
                            dictionary.put(row.getCell(1).getStringCellValue(), values);
                        }
                    }
                    rowCount++;
                }
            }
        }

        // Индексы дефляции
        Map<LocalDate, HashMap<Integer, Float>> commonIndexMap = new TreeMap<>();
        commonIndexMap.put(LocalDate.of(2015, Month.NOVEMBER, 16), new HashMap<>() {{
            put(2016, 1.089f);
            put(2017, 1.052f);
            put(2018, 1.052f);
            put(2019, 1.059f);
            put(2020, 1.049f);
            put(2021, 1.043f);
            put(2022, 1.045f);
            put(2023, 1.046f);
            put(2024, 1.049f);
        }});
        commonIndexMap.put(LocalDate.of(2017, Month.JANUARY, 9), new HashMap<>() {{
            put(2016, 1.093f);
            put(2017, 1.065f);
            put(2018, 1.057f);
            put(2019, 1.059f);
            put(2020, 1.049f);
            put(2021, 1.043f);
            put(2022, 1.045f);
            put(2023, 1.046f);
            put(2024, 1.049f);
        }});
        commonIndexMap.put(LocalDate.of(2017, Month.NOVEMBER, 1), new HashMap<>() {{
            put(2016, 1.092f);
            put(2017, 1.058f);
            put(2018, 1.056f);
            put(2019, 1.051f);
            put(2020, 1.049f);
            put(2021, 1.043f);
            put(2022, 1.045f);
            put(2023, 1.046f);
            put(2024, 1.049f);
        }});
        commonIndexMap.put(LocalDate.of(2018, Month.OCTOBER, 12), new HashMap<>() {{
            put(2016, 1.092f);
            put(2017, 1.053f);
            put(2018, 1.053f);
            put(2019, 1.049f);
            put(2020, 1.046f);
            put(2021, 1.043f);
            put(2022, 1.045f);
            put(2023, 1.046f);
            put(2024, 1.049f);
        }});
        commonIndexMap.put(LocalDate.of(2019, Month.APRIL, 1), new HashMap<>() {{
            put(2016, 1.092f);
            put(2017, 1.053f);
            put(2018, 1.053f);
            put(2019, 1.049f);
            put(2020, 1.046f);
            put(2021, 1.043f);
            put(2022, 1.045f);
            put(2023, 1.046f);
            put(2024, 1.049f);
        }});
        commonIndexMap.put(LocalDate.of(2019, Month.AUGUST, 1), new HashMap<>() {{
            put(2016, 1.092f);
            put(2017, 1.053f);
            put(2018, 1.053f);
            put(2019, 1.049f);
            put(2020, 1.046f);
            put(2021, 1.043f);
            put(2022, 1.045f);
            put(2023, 1.046f);
            put(2024, 1.049f);
        }});
        commonIndexMap.put(LocalDate.of(2019, Month.OCTOBER, 10), new HashMap<>() {{
            put(2016, 1.092f);
            put(2017, 1.053f);
            put(2018, 1.049f);
            put(2019, 1.048f);
            put(2020, 1.047f);
            put(2021, 1.045f);
            put(2022, 1.048f);
            put(2023, 1.050f);
            put(2024, 1.052f);
        }});

        // Этап 1
        int count = 0;
        if (firstStep.getSize() > 0) {
            Workbook workbook = null;
            String name = firstStep.getOriginalFilename();
            if (name != null) {
                InputStream inputStream = firstStep.getInputStream();
                if (name.toLowerCase().endsWith(AttachmentMediaType.XLS.getExtension())) {
                    workbook = new HSSFWorkbook(inputStream);
                } else if (name.toLowerCase().endsWith(AttachmentMediaType.XLSX.getExtension())) {
                    workbook = new XSSFWorkbook(inputStream);
                }
            }
            if (workbook != null) {
                AccountingData accountingData = new AccountingData();
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    for (var row : sheet) {
                        // ИГК
                        String igk = row.getCell(1).getStringCellValue();
                        // полный номер контракта
                        String contractInformation = row.getCell(3).getStringCellValue();
                        String[] contractInformationArray = contractInformation.split("/");
                        String[] contractShortNameArray = contractInformationArray[1].split("-");
                        // номер контракта
                        long contractNumber = Long.parseLong(contractInformationArray[0]);
                        Performer performer = Performer.Companion.getByPrefix(contractShortNameArray[0]);
                        ContractType contractType = ContractType.Companion.getByCode(
                            contractShortNameArray.length == 4 ? contractShortNameArray[1] + "-" + contractShortNameArray[2] : contractShortNameArray[1]
                        );

                        int contractYear = contractShortNameArray.length == 4 ? Integer.parseInt(contractShortNameArray[3]) : Integer.parseInt(contractShortNameArray[2].substring(0, 2));
                        contractYear = contractYear < HALF_CENTURY ? Integer.parseInt("20" + contractYear) : Integer.parseInt("19" + contractYear);
                        List<ru.korundm.entity.Contract> erpContractList = contractService.getByParams(performer.getId(), contractNumber, contractType.getId(), contractYear);
                        if (!erpContractList.isEmpty()) {
                            ru.korundm.report.xml.helper.Contract contract = new ru.korundm.report.xml.helper.Contract();
                            GroupFinancingContract groupFinancingContract = new GroupFinancingContract();
                            CustomerCash customerCash = new CustomerCash();
                            BankLoans bankLoans = new BankLoans();
                            DebtPercentageCredits debtPercentageCredits = new DebtPercentageCredits();
                            DebtSuppliers debtSuppliers = new DebtSuppliers();
                            GroupDistributionContractResources groupDistributionContractResources = new GroupDistributionContractResources();
                            GroupCash groupCash = new GroupCash();
                            CashEquitySeparateAccount cashEquitySeparateAccount = new CashEquitySeparateAccount();
                            BankDeposits bankDeposits = new BankDeposits();
                            AdvancesIssued advancesIssued = new AdvancesIssued();
                            GroupReserves groupReserves = new GroupReserves();
                            MaterialsInWarehouses materialsInWarehouses = new MaterialsInWarehouses();
                            VATOnPurchasedAssets vatOnPurchasedAssets = new VATOnPurchasedAssets();
                            PrefabricatedInStocks prefabricatedInStocks = new PrefabricatedInStocks();
                            MaterialsTransferredToRecycling materialsTransferredToRecycling = new MaterialsTransferredToRecycling();
                            FutureSpending futureSpending = new FutureSpending();
                            MeansProduction meansProduction = new MeansProduction();
                            GroupProduction groupProduction = new GroupProduction();
                            MaterialCosts materialCosts = new MaterialCosts();
                            PayrollCosts payrollCosts = new PayrollCosts();
                            OtherProductionCosts otherProductionCosts = new OtherProductionCosts();
                            OverheadCost overheadCost = new OverheadCost();
                            GeneralBusinessCosts generalBusinessCosts = new GeneralBusinessCosts();
                            SemifinishedInternalWorks semifinishedInternalWorks = new SemifinishedInternalWorks();
                            ProductionInnerProducts productionInnerProducts = new ProductionInnerProducts();
                            Output output = new Output();
                            FinishedProducts finishedProducts = new FinishedProducts();
                            GroupShippingProductsPerformanceWorks groupShippingProductsPerformanceWorks = new GroupShippingProductsPerformanceWorks();
                            CostSales costSales = new CostSales();
                            Aur aur = new Aur();
                            SellingCosts sellingCosts = new SellingCosts();
                            BankLoanInterest bankLoanInterest = new BankLoanInterest();
                            VATSales vatSales = new VATSales();
                            Profit profit = new Profit();
                            RedirectionAttraction redirectionAttraction = new RedirectionAttraction();
                            WrittenOffFunds writtenOffFunds = new WrittenOffFunds();

                            ru.korundm.entity.Contract erpContract = erpContractList.get(0);
                            if (erpContract == null) {
                                log.info("Полный номер контракта: number -> " + contractInformation + "; Контракт не найден: igk -> " + igk);
                                stateDefenceOrder.add(new Pair<>(contractInformation, "Контракт не найден. Полный номер контракта: number -> " + contractInformation + "; Контракт не найден: igk -> " + igk));
                                continue;
                            }
                            ContractSection contractSection = erpContract.getSectionList().get(0);
                            String obs = null;
                            if (StringUtils.isNotBlank(contractSection.getComment())) {
                                String[] noteArray = contractSection.getComment().split("//");
                                if (noteArray.length > 0 && noteArray[0].matches(BaseConstant.ONLY_DIGITAL_PATTERN)) {
                                    obs = noteArray[0];
                                }
                            }

                            log.info("Договор: " + contractSection.getFullNumber());
                            log.info("ОБС: " + obs);
                            log.info("Идентификатор: " + contractSection.getIdentifier());
                            log.info("number -> " + contractInformation + "; count -> " + erpContractList.size());

                            // В этом случае договор не попадает в отчет
                            if (obs == null || contractSection.getIdentifier() == null) {
                                log.debug("CONTRACT_ID: " + contractSection.getContract().getId() + " Не заполнено одно из следующих полей: obs -> " + obs + "; identifier -> " + contractSection.getIdentifier());
                                stateDefenceOrder.add(new Pair<>(contractInformation, "Не заполнено поле. Полный номер контракта: number -> " + contractInformation + "; Не заполнено одно из следующих полей: obs -> " + obs + "; identifier -> " + contractSection.getIdentifier()));
                                continue;
                            }

                            ReportContract reportContract = reportContractService.getByContractSection(contractSection);
//                            if (reportContract != null && !reportContract.isReportUse()) {
//                                log.debug("Контракт не может быть использован, так как он архивный (isReportUse): obs -> " + obs + "; identifier -> " + contractSection.getIdentifier());
//                                stateDefenceOrder.add(new Pair<>(contractInformation, "Контракт архивный. Полный номер контракта: number -> " + contractInformation + "; Контракт не может быть использован, так как он архивный (isReportUse): obs -> " + obs + "; identifier -> " + contractSection.getIdentifier()));
//                                continue;
//                            }

                            // Заполнение контракта
                            LocalDate contractDate = contractSection.getCreateDate();
                            contract.setContractDate(contractDate);
                            contract.setIgk(contractSection.getIdentifier());
                            contract.setReportDate(LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
                            contract.setSingleAccountNumber(obs);
                            contract.setNumber(contractSection.getIdentifier() + "/" + contractInformation);

                            LocalDate plannedExecutionDate = null;
                            BigDecimal sumWithVAT = BigDecimal.ZERO;
                            BigDecimal sumWithoutVAT = BigDecimal.ZERO;
                            // 100%
                            BigDecimal hundred = BigDecimal.valueOf(100);

                            double xmlCommonMaterial = 0.0;
                            double xmlSpecialCosts = 0.0;
                            double xmlCommonSalary = 0.0;
                            double xmlOverheadCosts = 0.0;
                            double xmlWorkCosts = 0.0;
                            BigDecimal xmlCostPrice;
                            BigDecimal xmlForDiff;
                            BigDecimal xmlProfitContract = BigDecimal.ZERO;
                            long productAmount = 0; // количество изделий во всех lot
                            if (
//                                (contractSection.getArchiveDate() == null || (reportContract != null && reportContract.isReportUse())) &&
                                    contractSection.getSendToClientDate() != null
                            ) {
                                for (var lotGroup : contractSection.getLotGroupList()) {
                                    for (var lot : lotGroup.getLotList()) {
                                        productAmount += lot.getAmount();
                                        BigDecimal neededPrice = lot.getNeededPrice().setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                        // Определяем в какой период действия ставки НДС попадает дата поставки lot-a
                                        ValueAddedTax valueAddedTax = lot.getVat();
                                        // Величина процентной ставки НДС
                                        BigDecimal vat = valueAddedTax == null ? BigDecimal.ZERO : BigDecimal.valueOf(valueAddedTax.getValue());

                                        for (int j = 0; j < lot.getAmount(); j++) {
                                            sumWithoutVAT = sumWithoutVAT.add(neededPrice);
                                            sumWithVAT = sumWithVAT.add(neededPrice.multiply(vat).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP)).add(neededPrice);
                                        }

                                        LocalDate deliveryDate = lot.getDeliveryDate();
                                        if (plannedExecutionDate == null) {
                                            plannedExecutionDate = deliveryDate;
                                        } else if (plannedExecutionDate.isBefore(deliveryDate)) {
                                            plannedExecutionDate = deliveryDate;
                                        }

                                        PriceKindType priceKind = lot.getPriceKind();
                                        // Тип цены = Предварительная или Тип цены = Фиксированная
                                        if (Objects.equals(PriceKindType.PRELIMINARY, priceKind) || Objects.equals(PriceKindType.EXPORT, priceKind)) {
                                            Product product = lot.getLotGroup().getProduct();
                                            // ОЦ (ориентировочная цена)
                                            BigDecimal indicativePrice = lot.getNeededPrice();
                                            LocalDate indexDate = null;
                                            for (var localDate : commonIndexMap.keySet()) {
                                                if (localDate.isBefore(contractDate)) {
                                                    indexDate = localDate;
                                                }
                                            }
                                            Map<Integer, Float> indexMap = commonIndexMap.get(indexDate);
                                            ProductChargesProtocol needChargesProtocol = null;
                                            Float index;
                                            Float commonIndex = null;
                                            Float mathIndex;
                                            BigDecimal checkForProtocol = null;
                                            // поиск протокола разница по модулю < 300
                                            for (var protocol : KtCommonUtil.INSTANCE.getChargesProtocolList(product)) {
                                                if (protocol.getProtocolDate().getYear() >= 2015) {
                                                    BigDecimal protocolPrice = protocol.getPrice();
                                                    mathIndex = null;
                                                    for (var key : indexMap.keySet()) {
                                                        if (key > protocol.getProtocolDate().getYear()) {
                                                            index = indexMap.get(key);
                                                            protocolPrice = protocolPrice.multiply(BigDecimal.valueOf(index));
                                                            mathIndex = mathIndex == null ? index : mathIndex * index;
                                                            if (protocolPrice.subtract(indicativePrice).abs().compareTo(BigDecimal.valueOf(300)) < 0) {
                                                                if (checkForProtocol == null) {
                                                                    checkForProtocol = protocolPrice.subtract(indicativePrice).abs();
                                                                    needChargesProtocol = protocol;
                                                                    commonIndex = mathIndex;
                                                                } else if (checkForProtocol.compareTo(protocolPrice.subtract(indicativePrice).abs()) >= 0) {
                                                                    checkForProtocol = protocolPrice.subtract(indicativePrice);
                                                                    needChargesProtocol = protocol;
                                                                    commonIndex = index;
                                                                }
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            // ищем ближайший протокол
                                            if (needChargesProtocol == null) {
                                                commonIndex = null;
                                                checkForProtocol = null;
                                                for (var protocol : KtCommonUtil.INSTANCE.getChargesProtocolList(product)) {
                                                    if (protocol.getProtocolDate().getYear() >= 2015) {
                                                        BigDecimal protocolPrice = protocol.getPrice();
                                                        mathIndex = null;
                                                        for (var key : indexMap.keySet()) {
                                                            if (key > protocol.getProtocolDate().getYear()) {
                                                                index = indexMap.get(key);
                                                                protocolPrice = protocolPrice.multiply(BigDecimal.valueOf(index));
                                                                mathIndex = mathIndex == null ? index : mathIndex * index;
                                                                if (checkForProtocol == null) {
                                                                    checkForProtocol = protocolPrice.subtract(indicativePrice).abs();
                                                                    needChargesProtocol = protocol;
                                                                    commonIndex = mathIndex;
                                                                } else if (checkForProtocol.compareTo(protocolPrice.subtract(indicativePrice).abs()) >= 0) {
                                                                    checkForProtocol = protocolPrice.subtract(indicativePrice);
                                                                    needChargesProtocol = protocol;
                                                                    commonIndex = index;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            if (needChargesProtocol != null) {
                                                // Сырье и основные материалы
                                                double material = needChargesProtocol.getMaterial();
                                                // Вспомогательные материалы
                                                double addMaterial = needChargesProtocol.getAddMaterial();
                                                // Покупные полуфабрикаты
                                                double halfUnit = needChargesProtocol.getHalfUnit();
                                                // Составляющая цены : ПКИ (теперь это сумма 2-х)
                                                double componentsPurchased = needChargesProtocol.getPurchasedComponent();
                                                // Составляющая цены : Контрагентам
                                                double partner = needChargesProtocol.getPartnerCharges();
                                                // Транспортно-заготовительные расходы
                                                double transport = needChargesProtocol.getTransport();
                                                // Топливо на технологические цели
                                                double fuel = needChargesProtocol.getFuel();
                                                // Энергия на технологические цели
                                                double energy = needChargesProtocol.getEnergy();
                                                // Тара (невозвратная) и упаковка
                                                double pack = needChargesProtocol.getPack();
                                                // Затраты на материалы - всего
                                                double commonMaterial = BigDecimal.valueOf((material + addMaterial + halfUnit + componentsPurchased + partner + transport + fuel + energy + pack) * commonIndex).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).doubleValue();
                                                xmlCommonMaterial += commonMaterial * lot.getAmount();

                                                // Основная ЗП
                                                double mainSalary = 0;
                                                for (var productLabourReference : needChargesProtocol.getProductLabourReferenceList()) {
                                                    mainSalary += productLabourReference.getLabourTime() * productLabourReference.getLabourPrice().getHourlyPay();
                                                }
                                                EcoCompanyConstProtocol ecoCompanyConstProtocol = null;
                                                List<EcoCompanyConstProtocol> companyConstProtocolList = companyConstProtocolService.getAllByCompanyId(BaseConstant.ECO_MAIN_PLANT_ID);
                                                int protocolYear = needChargesProtocol.getProtocolDate().getYear();
                                                for (var companyConstProtocol : companyConstProtocolList) {
                                                    if (companyConstProtocol.getProtocolDate().getYear() == protocolYear || (protocolYear == 2020 && companyConstProtocol.getProtocolDate().getYear() == 2019)) {
                                                        ecoCompanyConstProtocol = companyConstProtocol;
                                                        break;
                                                    }
                                                }
                                                // Дополнительная ЗП
                                                double additionalSalary = ecoCompanyConstProtocol != null ?
                                                    mainSalary * ecoCompanyConstProtocol.getAdditionalWagesRate() / hundred.doubleValue() : 0;
                                                // Общая ЗП
                                                double commonSalary = (mainSalary + additionalSalary) * commonIndex;
                                                xmlCommonSalary += commonSalary * lot.getAmount();

                                                // Страховые взносы
                                                double insurancePremiums = ecoCompanyConstProtocol != null ?
                                                    commonSalary * ecoCompanyConstProtocol.getSocialInsuranceRate() / hundred.doubleValue() : 0;
                                                xmlCommonSalary += insurancePremiums * lot.getAmount();

                                                // Специальные затраты
                                                double specialCosts = needChargesProtocol.getSpecialEquipCharges() * commonIndex;
                                                xmlSpecialCosts += specialCosts * lot.getAmount();

                                                // Общепроизводственные затраты
                                                double overheadCosts = ecoCompanyConstProtocol != null ?
                                                    mainSalary * commonIndex * ecoCompanyConstProtocol.getManufacturingChargesRate() / hundred.doubleValue() : 0;
                                                xmlOverheadCosts += overheadCosts * lot.getAmount();

                                                // Общехозяйственные затраты
                                                double workCosts = ecoCompanyConstProtocol != null ?
                                                    mainSalary * commonIndex * ecoCompanyConstProtocol.getWorkshopChargesRate() / hundred.doubleValue() : 0;
                                                xmlWorkCosts += workCosts * lot.getAmount();

                                                // Прибыль
                                                BigDecimal profitContract = lot.getPrice().subtract(BigDecimal.valueOf(commonMaterial + commonSalary + insurancePremiums + specialCosts + overheadCosts + workCosts)).multiply(BigDecimal.valueOf(lot.getAmount()));
                                                xmlProfitContract = xmlProfitContract.add(profitContract);
                                            }
                                        }
                                        // Тип цены = По протоколу
                                        else if (Objects.equals(PriceKindType.FINAL, priceKind)) {
                                            // ЦП (цена по протоколу)
                                            BigDecimal price = lot.getNeededPrice();

                                            ProductChargesProtocol productChargesProtocol = lot.getProtocol();
                                            // Сырье и основные материалы
                                            double material = productChargesProtocol.getMaterial();
                                            // Вспомогательные материалы
                                            double addMaterial = productChargesProtocol.getAddMaterial();
                                            // Покупные полуфабрикаты
                                            double halfUnit = productChargesProtocol.getHalfUnit();
                                            // Составляющая цены : ПКИ (теперь это сумма 2-х)
                                            double componentsPurchased = productChargesProtocol.getPurchasedComponent();
                                            // Составляющая цены : Контрагентам
                                            double partner = productChargesProtocol.getPartnerCharges();
                                            // Транспортно-заготовительные расходы
                                            double transport = productChargesProtocol.getTransport();
                                            // Топливо на технологические цели
                                            double fuel = productChargesProtocol.getFuel();
                                            // Энергия на технологические цели
                                            double energy = productChargesProtocol.getEnergy();
                                            // Тара (невозвратная) и упаковка
                                            double pack = productChargesProtocol.getPack();
                                            // Затраты на материалы - всего
                                            double commonMaterial = BigDecimal.valueOf(material + addMaterial + halfUnit + componentsPurchased + partner + transport + fuel + energy + pack).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).doubleValue();
                                            xmlCommonMaterial += commonMaterial * lot.getAmount();

                                            // Основная ЗП
                                            double mainSalary = 0;
                                            for (var productLabourReference : productChargesProtocol.getProductLabourReferenceList()) {
                                                mainSalary += productLabourReference.getLabourTime() * productLabourReference.getLabourPrice().getHourlyPay();
                                            }
                                            EcoCompanyConstProtocol ecoCompanyConstProtocol = null;
                                            List<EcoCompanyConstProtocol> companyConstProtocolList = companyConstProtocolService.getAllByCompanyId(BaseConstant.ECO_MAIN_PLANT_ID);
                                            int protocolYear = productChargesProtocol.getProtocolDate().getYear();
                                            for (var companyConstProtocol : companyConstProtocolList) {
                                                if (companyConstProtocol.getProtocolDate().getYear() == protocolYear || (protocolYear == 2020 && companyConstProtocol.getProtocolDate().getYear() == 2019)) {
                                                    ecoCompanyConstProtocol = companyConstProtocol;
                                                    break;
                                                }
                                            }
                                            // Дополнительная ЗП
                                            double additionalSalary = ecoCompanyConstProtocol != null ?
                                                mainSalary * ecoCompanyConstProtocol.getAdditionalWagesRate() / hundred.doubleValue() : 0;
                                            // Общая ЗП
                                            double commonSalary = mainSalary + additionalSalary;
                                            xmlCommonSalary += commonSalary * lot.getAmount();

                                            // Страховые взносы
                                            double insurancePremiums = ecoCompanyConstProtocol != null ?
                                                commonSalary * ecoCompanyConstProtocol.getSocialInsuranceRate() / hundred.doubleValue() : 0;
                                            xmlCommonSalary += insurancePremiums * lot.getAmount();

                                            // Специальные затраты
                                            double specialCosts = productChargesProtocol.getSpecialEquipCharges();
                                            xmlSpecialCosts += specialCosts * lot.getAmount();

                                            // Общепроизводственные затраты
                                            double overheadCosts = ecoCompanyConstProtocol != null ?
                                                mainSalary * ecoCompanyConstProtocol.getManufacturingChargesRate() / hundred.doubleValue() : 0;
                                            xmlOverheadCosts += overheadCosts * lot.getAmount();

                                            // Общехозяйственные затраты
                                            double workCosts = ecoCompanyConstProtocol != null ?
                                                mainSalary * ecoCompanyConstProtocol.getWorkshopChargesRate() / hundred.doubleValue() : 0;
                                            xmlWorkCosts += workCosts * lot.getAmount();

                                            // Прибыль
                                            BigDecimal profitContract = price.multiply(BigDecimal.valueOf(lot.getAmount())).subtract(BigDecimal.valueOf((commonMaterial + commonSalary + insurancePremiums + specialCosts + overheadCosts + workCosts) * lot.getAmount()));
                                            xmlProfitContract = xmlProfitContract.add(profitContract);
                                        }
                                    }
                                }
                            } else {
                                stateDefenceOrder.add(new Pair<>(contractInformation, "Проверить поля. Полный номер контракта: number -> " + contractInformation + "; Необходимо проверить поля: contractSection.getArchiveDate() == null || (reportContract != null && reportContract.isReportUse())); && contractSection.getPzCopyDate() != null для contractSection с id -> " + contractSection.getId()));
                                log.debug("Необходимо проверить поля: contractSection.getArchiveDate() == null || (reportContract != null && reportContract.isReportUse())); && contractSection.getPzCopyDate() != null для contractSection с id -> " + contractSection.getId());
                            }
                            if (plannedExecutionDate != null) {
                                contract.setPlannedExecutionDate(plannedExecutionDate);
                            }

                            List<BigDecimal> dictionaryList = dictionary.get(igk);

                            log.debug("Последняя проверка -> dictionaryList != null: " + dictionaryList + "; sumWithVAT.compareTo(BigDecimal.ZERO) != 0: " + sumWithVAT.compareTo(BigDecimal.ZERO) + "; BigDecimal.valueOf(xmlCommonMaterial).compareTo(BigDecimal.ZERO) != 0: " + BigDecimal.valueOf(xmlCommonMaterial).compareTo(BigDecimal.ZERO));
                            if (dictionaryList != null && sumWithVAT.compareTo(BigDecimal.ZERO) != 0 && BigDecimal.valueOf(xmlCommonMaterial).compareTo(BigDecimal.ZERO) != 0) {
                                int size = dictionaryList.size();
                                count++;
                                log.info("count: " + count);
                                // Себестоимость реализованной продукции
                                xmlCostPrice = BigDecimal.valueOf(xmlCommonMaterial).add(BigDecimal.valueOf(xmlCommonSalary))
                                    .add(BigDecimal.valueOf(xmlSpecialCosts)).add(BigDecimal.valueOf(xmlOverheadCosts))
                                    .add(BigDecimal.valueOf(xmlWorkCosts)).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                // Находим разницу между рассчитаной суммой и sumWithoutVAT
                                xmlForDiff = xmlCostPrice.add(xmlProfitContract.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP));
                                BigDecimal diff = xmlForDiff.subtract(sumWithoutVAT).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                if (diff.abs().doubleValue() < 1.0) {
                                    xmlProfitContract = xmlProfitContract.subtract(diff);
                                }

                                if (reportContract == null) {
                                    reportContract = new ReportContract();
                                    reportContract.setContractSection(contractSection);
                                }

                                if (reportContract.getId() == null || !Objects.equals(reportContract.getProductAmount(), productAmount)) {
                                    // Установка целевых показателей
                                    reportContract.setTargetAmountFunding(sumWithVAT.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).toString());
                                    reportContract.setContractPrice(sumWithVAT.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).toString());
                                    reportContract.setMaterialTarget(BigDecimal.valueOf(xmlCommonMaterial).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).toString());
                                    reportContract.setPayrollTarget(BigDecimal.valueOf(xmlCommonSalary).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).toString());
                                    reportContract.setOtherProductionTarget(BigDecimal.valueOf(xmlSpecialCosts).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).toString());
                                    reportContract.setOverheadTarget(BigDecimal.valueOf(xmlOverheadCosts).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).toString());
                                    reportContract.setGeneralBusinessTarget(BigDecimal.valueOf(xmlWorkCosts).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).toString());
                                    reportContract.setGroupShippingTarget(sumWithVAT.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).toString());
                                    reportContract.setCostSalesTarget(xmlCostPrice.toString());
                                    reportContract.setProfitTarget(xmlProfitContract.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).toString());
                                }
                                reportContract.setProductAmount(productAmount);
                                reportContract.setReportUse(contractSection.getArchiveDate() == null);
                                reportContractService.save(reportContract);

                                // Установка целевых показателей
                                groupFinancingContract.setTargetAmountFunding(reportContract.getTargetAmountFunding());
                                customerCash.setContractPrice(reportContract.getContractPrice());
                                materialCosts.setTargetIndicator(reportContract.getMaterialTarget());
                                payrollCosts.setTargetIndicator(reportContract.getPayrollTarget());
                                otherProductionCosts.setTargetIndicator(reportContract.getOtherProductionTarget());
                                overheadCost.setTargetIndicator(reportContract.getOverheadTarget());
                                generalBusinessCosts.setTargetIndicator(reportContract.getGeneralBusinessTarget());
                                groupShippingProductsPerformanceWorks.setTargetIndicator(reportContract.getGroupShippingTarget());
                                costSales.setTargetIndicator(reportContract.getCostSalesTarget());
                                profit.setTargetIndicator(reportContract.getProfitTarget());

                                // Финансирование контракта
                                BigDecimal k101 = dictionaryList.get(0).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k102 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k103 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k104 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal g101 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g102 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g103 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g104 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal h104 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal i101 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i102 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i103 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i104 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal e101 = k101.subtract(g101).subtract(i101);
                                BigDecimal e102 = k102.subtract(g102).subtract(i102);
                                BigDecimal e103 = k103.subtract(g103).subtract(i103);
                                BigDecimal e104 = k104.subtract(g104).subtract(h104).subtract(i104);
                                BigDecimal e100 = e101.add(e102).add(e103).add(e104);

                                // customerCash.setContractPrice(sumWithVAT.setScale(SystemSetting.SCALE, RoundingMode.HALF_UP).toString());
                                BigDecimal c100 = sumWithVAT.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c101 = sumWithVAT.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c102 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c231 = BigDecimal.valueOf(xmlCommonMaterial).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c232 = BigDecimal.valueOf(xmlCommonSalary).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c233 = BigDecimal.valueOf(xmlSpecialCosts).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c234 = BigDecimal.valueOf(xmlOverheadCosts).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c235 = BigDecimal.valueOf(xmlWorkCosts).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c300 = sumWithVAT.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c301 = xmlCostPrice;
                                BigDecimal c302 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c303 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal c304 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal d101 = c101.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e101.divide(c101, BaseConstant.SCALE, RoundingMode.HALF_UP).multiply(hundred);
                                BigDecimal d102 = c102.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e102.multiply(c102).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal d100 = c102.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e100.multiply(c100).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);

                                // Денежные средства
                                BigDecimal l211 = size > 1 ? dictionaryList.get(1).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                                BigDecimal l210 = l211.add(BigDecimal.ZERO);

                                BigDecimal m211 = size > 2 ? dictionaryList.get(2).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                                BigDecimal m213 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal m210 = m211.add(m213);

                                BigDecimal g211 = k101.add(BigDecimal.ZERO);
                                BigDecimal g212 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g213 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g210 = g211.add(g212).add(g213);

                                BigDecimal i211 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i213 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i210 = i211.add(i213);

                                BigDecimal k211 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k212 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k213 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k210 = k211.add(k212).add(k213);

                                BigDecimal e211 = g211.add(i211).subtract(k211).subtract(m211).subtract(l211);
                                BigDecimal e212 = g212.subtract(k212);

                                BigDecimal h213 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal h210 = h213.add(BigDecimal.ZERO);

                                BigDecimal e213 = g213.add(h213).add(i213).subtract(k213).subtract(m213);
                                BigDecimal e210 = g210.add(h210).add(i210).subtract(k210).subtract(l210).subtract(m210);

                                // Производство
                                BigDecimal h231 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal h233 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal h230 = h231.add(h233);

                                BigDecimal i231 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i232 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i233 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i234 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i235 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i230 = i231.add(i232).add(i233).add(i234).add(i235);

                                BigDecimal k231 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k233 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k237 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k238 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k230 = k231.add(k233).add(k237).add(k238);

                                BigDecimal l231 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal l233 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal l230 = l231.add(l233);

                                BigDecimal m231 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal m233 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal m230 = m231.add(m233);

                                BigDecimal g231 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g233 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g236 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g230 = g231.add(g233).add(g236);

                                BigDecimal e231 = g231.add(h231).add(i231).subtract(k231).subtract(l231).subtract(m231);
                                BigDecimal e232 = i232.add(BigDecimal.ZERO);
                                BigDecimal e233 = g233.add(h233).add(i233).subtract(k233).subtract(l233).subtract(m233);
                                BigDecimal e234 = i234.add(BigDecimal.ZERO);
                                BigDecimal e235 = i235.add(BigDecimal.ZERO);
                                BigDecimal e236 = g236.add(BigDecimal.ZERO);
                                BigDecimal e237 = k237.add(BigDecimal.ZERO);
                                BigDecimal e238 = k238.add(BigDecimal.ZERO);
                                BigDecimal e230 = g230.add(h230).add(i230).subtract(k230).subtract(l230).subtract(m230);

                                BigDecimal d231 = c231.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e231.multiply(c231).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal d232 = c232.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e232.multiply(c232).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal d233 = c233.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e233.multiply(c233).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal d234 = c234.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e234.multiply(c234).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal d235 = c235.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e235.multiply(c235).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);

                                // Запасы
                                BigDecimal g221 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal i221 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i223 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i226 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal l221 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal l223 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal l226 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal m221 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal m222 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal m223 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal m224 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal m226 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal k221 = g231.add(BigDecimal.ZERO);
                                BigDecimal k223 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k224 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k225 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal k226 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal h221 = k221.add(BigDecimal.ZERO);
                                BigDecimal h223 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal h226 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal g222 = h221.multiply(BigDecimal.valueOf(20)).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g223 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g224 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g225 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g226 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal k222 = g222.add(BigDecimal.ZERO);

                                BigDecimal e221 = g221.add(h221).add(i221).subtract(k221).subtract(l221).subtract(m221);
                                BigDecimal e222 = g222.subtract(k222).subtract(m222);
                                BigDecimal e223 = g223.add(h223).add(i223).subtract(k223).subtract(l223).subtract(m223);
                                BigDecimal e224 = g224.subtract(k224).subtract(m224);
                                BigDecimal e225 = g225.subtract(k225);
                                BigDecimal e226 = g226.add(h226).add(i226).subtract(k226).subtract(l226).subtract(m226);

                                BigDecimal g220 = g221.add(g222).add(g223).add(g224).add(g225).add(g226);
                                BigDecimal h220 = h221.add(h223).add(h226);
                                BigDecimal i220 = i221.add(i223).add(i226);
                                BigDecimal k220 = k221.add(k222).add(k223).add(k224).add(k225).add(k226);
                                BigDecimal l220 = l221.add(l223).add(l226);
                                BigDecimal m220 = m221.add(m222).add(m223).add(m226);
                                BigDecimal e220 = g220.add(h220).add(i220).subtract(k220).subtract(l220).subtract(m220);

                                // Готовый товар на складе
                                BigDecimal g240 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal h240 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal i240 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal k240 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal l240 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal m240 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal e240 = g240.add(h240).add(i240).subtract(k240).subtract(l240).subtract(m240);

                                // Отгрузка товара
                                BigDecimal g301 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g303 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal g304 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal i301 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i302 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal i305 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);

                                BigDecimal e300 = BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal e301 = g301.add(i301);
                                BigDecimal e302 = i302.add(BigDecimal.ZERO);
                                BigDecimal e303 = g303.add(BigDecimal.ZERO);
                                BigDecimal e304 = g304.add(BigDecimal.ZERO);
                                BigDecimal e305 = i305.add(BigDecimal.ZERO);
                                BigDecimal e306 = e300.subtract(e301).subtract(e302).subtract(e303).subtract(e304).subtract(e305);

                                BigDecimal d300 = c300.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e300.multiply(c300).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal d301 = c301.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e301.multiply(c301).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal d302 = c302.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e302.multiply(c302).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal d303 = c303.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e303.multiply(c303).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);
                                BigDecimal d304 = c304.compareTo(BigDecimal.ZERO) == 0 ?
                                    BigDecimal.ZERO.setScale(BaseConstant.SCALE, RoundingMode.HALF_UP) : e304.multiply(c304).divide(hundred, BaseConstant.SCALE, RoundingMode.HALF_UP);

                                // Привлечение / перенаправление ресурсов контракта
                                BigDecimal h400 = h104.add(h210).add(h220).add(h230).add(h240);
                                BigDecimal i400 = i101.add(i102).add(i103).add(i104).add(i210).add(i220).add(i230).add(i240).add(i301).add(i302).add(i305);
                                BigDecimal l400 = l210.add(l220).add(l230).add(l240);
                                BigDecimal m400 = m210.add(m220).add(m230).add(m240);
                                BigDecimal e400 = h400.add(i400).subtract(l400).subtract(m400);

                                // Ден.средства, полученные от заказчика (авансы)
                                customerCash.setBalanceOperations(e101.toString());
                                customerCash.setPercentageCompletion(
                                    new BigDecimal(customerCash.getBalanceOperations()).divide(new BigDecimal(customerCash.getContractPrice()), BaseConstant.SCALE, RoundingMode.HALF_UP).multiply(hundred).toString()
                                );
                                customerCash.setReturnedCustomer(g101.toString());
                                customerCash.setReturnedCustomerOwnFunds(i101.toString());
                                customerCash.setReceivedFromCustomer(k101.toString());
                                // Кредиты банка
                                bankLoans.setPlannedVolumeCrediting(c102.toString());
                                bankLoans.setPercentageCompletion(d102.toString());
                                bankLoans.setBalanceOperations(e102.toString());
                                bankLoans.setRedeemedBodyCredit(g102.toString());
                                bankLoans.setRedeemedCreditCreditsOwnFunds(i102.toString());
                                bankLoans.setAttractedCredits(k102.toString());
                                // Задолженность по процентам кредитов
                                debtPercentageCredits.setBalanceOperations(e103.toString());
                                debtPercentageCredits.setRedeemedPercent(g103.toString());
                                debtPercentageCredits.setRedeemedInterestOwnFunds(i103.toString());
                                debtPercentageCredits.setAccruedInterest(k103.toString());
                                // Задолженность перед поставщиками
                                debtSuppliers.setBalanceOperations(e104.toString());
                                debtSuppliers.setPaidSuppliers(g104.toString());
                                debtSuppliers.setPaidSuppliersOtherContracts(h104.toString());
                                debtSuppliers.setPaidSuppliersOwnFunds(i104.toString());
                                debtSuppliers.setTotalDebt(k104.toString());

                                // Распределение ресурсов контракта
                                groupDistributionContractResources.setBalanceOperations(e210.toString());

                                // Денежные средства
                                groupCash.setBalanceOperations(e210.toString());
                                groupCash.setCashAssets(g210.toString());
                                groupCash.setCashAssetsOtherContracts(h210.toString());
                                groupCash.setCashAssetsOwnFunds(i210.toString());
                                groupCash.setResourceUsage(k210.toString());
                                groupCash.setUseResourcesOtherContracts(l210.toString());
                                groupCash.setUseResourcesOwnFunds(m210.toString());
                                // Денежные средства на ОС
                                cashEquitySeparateAccount.setBalanceOperations(e211.toString());
                                cashEquitySeparateAccount.setCreditedContractExecution(g211.toString());
                                cashEquitySeparateAccount.setCreditedOther(i211.toString());
                                cashEquitySeparateAccount.setWrittenOffContractExecution(k211.toString());
                                cashEquitySeparateAccount.setWrittenOffOtherContracts(l211.toString());
                                cashEquitySeparateAccount.setChargedOrganizationCosts(m211.toString());
                                // Денежные средства на депозитах в банке
                                bankDeposits.setBalanceOperations(e212.toString());
                                bankDeposits.setListedDeposit(g212.toString());
                                bankDeposits.setReturnedWithDeposit(k212.toString());
                                // Авансы, выданные поставщикам
                                advancesIssued.setBalanceOperations(e213.toString());
                                advancesIssued.setAdvancesContractExecution(g213.toString());
                                advancesIssued.setAdvancesOtherContracts(h213.toString());
                                advancesIssued.setAdvancesOwnFunds(i213.toString());
                                advancesIssued.setCreditAdvances(k213.toString());
                                advancesIssued.setChargedDebtCooperation(m213.toString());

                                // Запасы
                                groupReserves.setBalanceOperations(e220.toString());
                                groupReserves.setFormedStocks(g220.toString());
                                groupReserves.setFormedStocksMeansOtherContracts(h220.toString());
                                groupReserves.setFormedStocksOwnFunds(i220.toString());
                                groupReserves.setUsedStocks(k220.toString());
                                groupReserves.setUsedStocksOnOtherContracts(l220.toString());
                                groupReserves.setUsedStocksOrganizationNeeds(m220.toString());
                                // Материалы на складах
                                materialsInWarehouses.setBalanceOperations(e221.toString());
                                materialsInWarehouses.setReceivedMaterials(g221.toString());
                                materialsInWarehouses.setMaterialsReceivedMeansOtherContracts(h221.toString());
                                materialsInWarehouses.setReceivedMaterialsOwnFunds(i221.toString());
                                materialsInWarehouses.setUsedMaterials(k221.toString());
                                materialsInWarehouses.setMaterialsUsedOnOtherContracts(l221.toString());
                                materialsInWarehouses.setUsedMaterialsNeedsOrganization(m221.toString());
                                // НДС входящий
                                vatOnPurchasedAssets.setBalanceOperations(e222.toString());
                                vatOnPurchasedAssets.setHighlighted(g222.toString());
                                vatOnPurchasedAssets.setIncludedInStockCost(k222.toString());
                                vatOnPurchasedAssets.setAcceptedByDeduction(m222.toString());
                                // Полуфабрикаты на складах
                                prefabricatedInStocks.setBalanceOperations(e223.toString());
                                prefabricatedInStocks.setReceivedSemiFinishedProducts(g223.toString());
                                prefabricatedInStocks.setReceivedSemiFinishedProductsMeansOtherContracts(h223.toString());
                                prefabricatedInStocks.setReceivedSemiFinishedOwnMeans(i223.toString());
                                prefabricatedInStocks.setUsedSemiFinishedProducts(k223.toString());
                                prefabricatedInStocks.setUsedSemiManufacturedForOtherContracts(l223.toString());
                                prefabricatedInStocks.setUsedSemiManufacturedNeedsOrganizations(m223.toString());
                                // Материалы, переданные в переработку
                                materialsTransferredToRecycling.setBalanceOperations(e224.toString());
                                materialsTransferredToRecycling.setSubmittedThirdPartyContractor(g224.toString());
                                materialsTransferredToRecycling.setAdoptedFromRecycling(k224.toString());
                                materialsTransferredToRecycling.setAdoptedFromOrganizationRecycling(m224.toString());
                                // Расходы будущих периодов
                                futureSpending.setBalanceOperations(e225.toString());
                                futureSpending.setAccruedRBP(g225.toString());
                                futureSpending.setWrittenOffRBP(k225.toString());
                                // Средства производства
                                meansProduction.setBalanceOperations(e226.toString());
                                meansProduction.setReceivedMeansProduction(g226.toString());
                                meansProduction.setReceivedMeansProductionMeansOtherContracts(h226.toString());
                                meansProduction.setReceivedMeansProductionOwnFunds(i226.toString());
                                meansProduction.setRetiredProductionTools(k226.toString());
                                meansProduction.setRetiredProductionMeansForOtherContracts(l226.toString());
                                meansProduction.setRetiredProductionMeansNeedsOrganization(m226.toString());

                                // Производство
                                groupProduction.setBalanceOperations(e230.toString());
                                groupProduction.setProductionCosts(g230.toString());
                                groupProduction.setProductionCostsOtherContracts(h230.toString());
                                groupProduction.setProductionCostsOwn(i230.toString());
                                groupProduction.setRelease(k230.toString());
                                groupProduction.setIssueOnOtherContracts(l230.toString());
                                groupProduction.setOrganizationNeedsIssue(m230.toString());
                                // Затраты на материалы
                                materialCosts.setPercentageCompletion(d231.toString());
                                materialCosts.setBalanceOperations(e231.toString());
                                materialCosts.setChargedCosts(g231.toString());
                                materialCosts.setChargedOtherContractCosts(h231.toString());
                                materialCosts.setChargedOwnCost(i231.toString());
                                materialCosts.setExcludedFromCosts(k231.toString());
                                materialCosts.setRelatedToOtherContracts(l231.toString());
                                materialCosts.setRelatedToOwnCosts(m231.toString());
                                // Затраты на оплату труда
                                payrollCosts.setPercentageCompletion(d232.toString());
                                payrollCosts.setBalanceOperations(e232.toString());
                                payrollCosts.setSalariesExecutors(i232.toString());
                                // Прочие производственные затраты
                                otherProductionCosts.setPercentageCompletion(d233.toString());
                                otherProductionCosts.setBalanceOperations(e233.toString());
                                otherProductionCosts.setChargedCosts(g233.toString());
                                otherProductionCosts.setChargedOtherContractCosts(h233.toString());
                                otherProductionCosts.setChargedOwnCost(i233.toString());
                                otherProductionCosts.setExcludedFromCosts(k233.toString());
                                otherProductionCosts.setRelatedToOtherContracts(l233.toString());
                                otherProductionCosts.setRelatedToOwnCosts(m233.toString());
                                // Прочие общепроизводственные затраты
                                overheadCost.setPercentageCompletion(d234.toString());
                                overheadCost.setBalanceOperations(e234.toString());
                                overheadCost.setCostSize(i234.toString());
                                // Общехозяйственные затраты
                                generalBusinessCosts.setPercentageCompletion(d235.toString());
                                generalBusinessCosts.setBalanceOperations(e235.toString());
                                generalBusinessCosts.setCostSize(i235.toString());
                                // Полуфабрикаты, внутренние работы
                                semifinishedInternalWorks.setBalanceOperations(e236.toString());
                                semifinishedInternalWorks.setChargedToCosts(g236.toString());
                                // Выпуск полуфабрикатов, внутренних работ
                                productionInnerProducts.setBalanceOperations(e237.toString());
                                productionInnerProducts.setReleased(k237.toString());
                                // Выпуск продукции
                                output.setBalanceOperations(e238.toString());
                                output.setReleased(k238.toString());

                                // Готовый товар на складе
                                finishedProducts.setBalanceOperations(e240.toString());
                                finishedProducts.setReleased(g240.toString());
                                finishedProducts.setUsedFromOtherContracts(h240.toString());
                                finishedProducts.setUsedOwn(i240.toString());
                                finishedProducts.setShipped(k240.toString());
                                finishedProducts.setShippedToOtherContracts(l240.toString());
                                finishedProducts.setShippedToOrganizationNeeds(m240.toString());

                                // Отгрузка товара, выполнение работ, оказание услуг
                                groupShippingProductsPerformanceWorks.setPercentageCompletion(d300.toString());
                                groupShippingProductsPerformanceWorks.setBalanceOperations(e300.toString());
                                // Себестоимость реализованной продукции
                                costSales.setPercentageCompletion(d301.toString());
                                costSales.setBalanceOperations(e301.toString());
                                costSales.setCostContract(g301.toString());
                                costSales.setCostNonContract(i301.toString());
                                // Административно-управленческие расходы
                                aur.setTargetIndicator(c302.toString());
                                aur.setPercentageCompletion(d302.toString());
                                aur.setBalanceOperations(e302.toString());
                                aur.setCostSize(i302.toString());
                                // Коммерческие расходы
                                sellingCosts.setTargetIndicator(c303.toString());
                                sellingCosts.setPercentageCompletion(d303.toString());
                                sellingCosts.setBalanceOperations(e303.toString());
                                sellingCosts.setCostSize(g303.toString());
                                // Проценты по кредитам банка
                                bankLoanInterest.setTargetIndicator(c304.toString());
                                bankLoanInterest.setPercentageCompletion(d304.toString());
                                bankLoanInterest.setBalanceOperations(e304.toString());
                                bankLoanInterest.setCostSize(g304.toString());
                                // НДС с выручки от продаж
                                vatSales.setBalanceOperations(e305.toString());
                                vatSales.setVatAmount(i305.toString());
                                // Прибыль контракта
                                // TODO изменить в зависимости от целевых показателей
                                profit.setPercentageCompletion(e306.toString());
                                profit.setBalanceOperations(e306.toString());

                                /*// Списание денежных средств с ОС  Контракта
                                writtenOffFunds.setTargetIndicator(String.format(Locale.US, "%.2f", row.getCell(2).getNumericCellValue()));
                                writtenOffFunds.setPercentageCompletion(String.format(Locale.US, "%.2f", row.getCell(3).getNumericCellValue()));
                                writtenOffFunds.setBalanceOperations(String.format(Locale.US, "%.2f", row.getCell(4).getNumericCellValue()));*/

                                // Финансирование контракта
                                groupFinancingContract.setBalanceOperations(e100.toString());
                                groupFinancingContract.setPercentageCompletion(
//                                    e100.divide(sumWithVAT, BaseConstant.SCALE, RoundingMode.HALF_UP).multiply(hundred).toString()
                                    e100.divide(new BigDecimal(groupFinancingContract.getTargetAmountFunding()), BaseConstant.SCALE, RoundingMode.HALF_UP).multiply(hundred).toString()
                                );
                                groupFinancingContract.setDebtSuppliers(debtSuppliers);
                                groupFinancingContract.setDebtPercentageCredits(debtPercentageCredits);
                                groupFinancingContract.setBankLoans(bankLoans);
                                groupFinancingContract.setCustomerCash(customerCash);
                                contract.setGroupFinancingContract(groupFinancingContract);

                                // Привлечение ресурсов в контракт / Перенаправление средств контракта
                                redirectionAttraction.setBalanceOperations(e400.toString());
//                                redirectionAttraction.setBalanceOperations(e210.subtract(e100).setScale(BaseConstant.SCALE, RoundingMode.HALF_UP).toString());
                                redirectionAttraction.setAttractedFundsOtherContracts(h400.toString());
                                redirectionAttraction.setAttractedOwnFunds(i400.toString());
                                redirectionAttraction.setUsedOnOtherContracts(l400.toString());
                                redirectionAttraction.setUsedForYourOwnNeeds(m400.toString());

                                groupCash.setAdvancesIssued(advancesIssued);
                                groupCash.setBankDeposits(bankDeposits);
                                groupCash.setCashEquitySeparateAccount(cashEquitySeparateAccount);
                                groupDistributionContractResources.setGroupCash(groupCash);
                                groupReserves.setMaterialsInWarehouses(materialsInWarehouses);
                                groupReserves.setVatOnPurchasedAssets(vatOnPurchasedAssets);
                                groupReserves.setPrefabricatedInStocks(prefabricatedInStocks);
                                groupReserves.setMaterialsTransferredToRecycling(materialsTransferredToRecycling);
                                groupReserves.setFutureSpending(futureSpending);
                                groupReserves.setMeansProduction(meansProduction);
                                groupDistributionContractResources.setGroupReserves(groupReserves);
                                groupProduction.setMaterialCosts(materialCosts);
                                groupProduction.setPayrollCosts(payrollCosts);
                                groupProduction.setOtherProductionCosts(otherProductionCosts);
                                groupProduction.setOverheadCost(overheadCost);
                                groupProduction.setGeneralBusinessCosts(generalBusinessCosts);
                                groupProduction.setSemifinishedInternalWorks(semifinishedInternalWorks);
                                groupProduction.setProductionInnerProducts(productionInnerProducts);
                                groupProduction.setOutput(output);
                                groupDistributionContractResources.setGroupProduction(groupProduction);
                                groupDistributionContractResources.setFinishedProducts(finishedProducts);
                                contract.setGroupDistributionContractResources(groupDistributionContractResources);
                                groupShippingProductsPerformanceWorks.setCostSales(costSales);
                                groupShippingProductsPerformanceWorks.setAur(aur);
                                groupShippingProductsPerformanceWorks.setSellingCosts(sellingCosts);
                                groupShippingProductsPerformanceWorks.setBankLoanInterest(bankLoanInterest);
                                groupShippingProductsPerformanceWorks.setVatSales(vatSales);
                                groupShippingProductsPerformanceWorks.setProfit(profit);
                                contract.setGroupShippingProductsPerformanceWorks(groupShippingProductsPerformanceWorks);
                                contract.setRedirectionAttraction(redirectionAttraction);
                                contract.setWrittenOffFunds(writtenOffFunds);

                                accountingData.getContractList().add(contract);
                            }
                        } else {
                            log.debug("contractInformation -> договор не найден -> " + contractInformation);
                            stateDefenceOrder.add(new Pair<>(contractInformation, "Договор не найден. Полный номер контракта: number -> " + contractInformation));
                        }
                    }
                }

                // Формирование файлов
                ClassPathResource xmlResource = new ClassPathResource("blank" + File.separator + "xml" + File.separator + "message." + AttachmentMediaType.XML.getExtension());
                File xmlFile = xmlResource.getFile();
                JAXBContext context = JAXBContext.newInstance(accountingData.getClass());
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.displayName());
                marshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                marshaller.marshal(accountingData, xmlFile);

                Workbook workbookExcel = stateDefenceOrder.generate();
                ClassPathResource excelResource = new ClassPathResource("blank" + File.separator + "excel" + File.separator + "error." + AttachmentMediaType.XLSX.getExtension());
                File excelFile = excelResource.getFile();
                try (FileOutputStream fos = new FileOutputStream(excelFile)) {
                    workbookExcel.write(fos);
                }

                // Упаковка в zip
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(baos);
                ZipOutputStream zos = new ZipOutputStream(bos);

                String entryName = excelFile.toPath().getFileName().toString();
                zos.putNextEntry(new ZipEntry(entryName));
                FileInputStream fis = new FileInputStream(excelFile);
                IOUtils.copy(fis, zos);

                entryName = xmlFile.toPath().getFileName().toString();
                zos.putNextEntry(new ZipEntry(entryName));
                fis = new FileInputStream(xmlFile);
                IOUtils.copy(fis, zos);

                fis.close();
                zos.closeEntry();
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
                    + MimeUtility.encodeText("order." + attachmentType.getExtension(), StandardCharsets.UTF_8.displayName(), "Q") + "\"");
                response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
                response.getOutputStream().write(baos.toByteArray());
            }
        }
    }
}