<div class="float-thead-container">
    <h1 class="b-heading">
        <fmt:message key="contract.mainWindow.deliveryStatement.title"/> ${contractSection.fullNumber}
    </h1>

    <c:if test="${contractSection.archiveDate eq null}">
        <div class="ui tiny icon buttons b-common-margin10">
            <button title="<fmt:message key="label.button.edit"/>" class="ui button js-delivery-edit" type="button"><i class="blue edit icon"></i></button>
            <button title="<fmt:message key="label.button.refresh"/>" class="ui button js-delivery-refresh" type="button"><i class="blue sync alternate icon"></i></button>
        </div>
    </c:if>
    <div class="b-common-margin20 b-float-thead-container">
        <table class="b-table b-full-width erp-float-thead">
            <thead>
                <tr>
                    <th class="b-table__th"></th>
                    <th class="b-table__th" style="width:400px;"><fmt:message key="contract.mainWindow.deliveryStatement.name"/></th>
                    <th class="b-table__th"><fmt:message key="contract.mainWindow.deliveryStatement.count"/></th>
                    <th class="b-table__th"><fmt:message key="contract.mainWindow.deliveryStatement.type"/></th>
                    <th class="b-table__th"><fmt:message key="contract.mainWindow.deliveryStatement.specialTest"/></th>
                    <th class="b-table__th"><fmt:message key="contract.mainWindow.deliveryStatement.deliveryDate"/></th>
                    <th class="b-table__th"><fmt:message key="contract.mainWindow.deliveryStatement.price"/></th>
                    <th class="b-table__th"><fmt:message key="contract.mainWindow.deliveryStatement.typePrice"/></th>
                    <th class="b-table__th"><fmt:message key="contract.mainWindow.deliveryStatement.amount"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${lotGroupList}" var="lotGroup" varStatus="status" >
                    <tr>
                        <td class="b-table__td b-table__td_blue" rowspan="${fn:length(lotGroup.lotList) + 1}">${status.count}</td>
                        <td class="b-table__td b-table__td_blue" rowspan="${fn:length(lotGroup.lotList) + 1}">${lotGroup.productName}</td>
                        <td class="b-table__td b-table__td_blue">${lotGroup.lotAmountCount}</td>
                        <td class="b-table__td b-table__td_blue"></td>
                        <td class="b-table__td b-table__td_blue"></td>
                        <td class="b-table__td b-table__td_blue"></td>
                        <td class="b-table__td b-table__td_blue"></td>
                        <td class="b-table__td b-table__td_blue"></td>
                        <td class="b-table__td b-table__td_blue b-text-nowrap">
                            <fmt:formatNumber value="${lotGroup.productTotalCost}" maxFractionDigits="2" minFractionDigits="2"/>
                        </td>
                    </tr>
                    <c:forEach items="${lotGroup.lotList}" var="lot" >
                        <tr>
                            <td class="b-table__td b-align-mid-right">${lot.amount}</td>
                            <td class="b-table__td b-align-mid-center">${lot.acceptType}</td>
                            <td class="b-table__td b-align-mid-center">${lot.specialTestType}</td>
                            <td class="b-table__td b-align-mid-center"><javatime:format value="${lot.deliveryDate}" pattern="dd.MM.yyyy"/></td>
                            <td class="b-table__td b-align-mid-center b-text-nowrap">
                                <fmt:formatNumber value="${lot.neededPrice}" maxFractionDigits="2" minFractionDigits="2"/>
                            </td>
                            <td class="b-table__td b-align-mid-right">${lot.priceKind}</td>
                            <td class="b-table__td b-align-mid-right">
                                <fmt:formatNumber value="${lot.totalCost}" maxFractionDigits="2" minFractionDigits="2"/>
                            </td>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <table class="b-table b-table__result b-full-width">
        <tr>
            <th class="b-table__th"><fmt:message key="contract.mainWindow.deliveryStatement.total"/></th>
            <td class="b-table__td b-text-nowrap">
                <fmt:formatNumber value="${sumContractSectionCost}" maxFractionDigits="2" minFractionDigits="2"/>
            </td>
        </tr>
        <tr>
            <th class="b-table__th"><fmt:message key="contract.mainWindow.deliveryStatement.vat"/></th>
            <td class="b-table__td b-text-nowrap">
                <fmt:formatNumber value="${lotTotalVAT}" maxFractionDigits="2" minFractionDigits="2"/>
            </td>
        </tr>
        <tr>
            <th class="b-table__th"><fmt:message key="contract.mainWindow.deliveryStatement.totalWithVAT"/></th>
            <td class="b-table__td b-text-nowrap">
                <fmt:formatNumber value="${sumContractSectionCost + lotTotalVAT}" maxFractionDigits="2" minFractionDigits="2"/>
            </td>
        </tr>
    </table>
</div>

<script>
    $(() => {
        const
            $deliveryEditBtn = $('.js-delivery-edit'),
            $deliveryRefreshBtn = $('.js-delivery-refresh'),
            $floatThead = $('.erp-float-thead');

        $deliveryEditBtn.on({
            'click': () => {
                $.modalDialog({
                    dialogName: 'editDeliveryStatement',
                    url: '/contract/editDeliveryStatement',
                    parameters: {
                        docType: 'edit',
                        sectionId: '${contractSection.id}'
                    }
                });
            }
        });

        $deliveryRefreshBtn.on({
            'click': () => $('.js-delivery-statement').trigger('click')
        });

        $floatThead.floatThead({
            scrollContainer: $table => $table.closest('div'),
        });
    });
</script>