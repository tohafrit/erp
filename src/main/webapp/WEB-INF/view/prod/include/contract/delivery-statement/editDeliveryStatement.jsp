<div class="editDeliveryStatement-dialog" title="<fmt:message key="deliveryStatement.edit"/>">
    <button style="position:fixed;z-index:99;" class="dt-button js-button-add-product b-common-margin10" type="button" title="<fmt:message key="deliveryStatement.product.add"/>">
        <span><i class="fa fa-plus fa-lg dt-add-button"></i></span>
    </button>
    <form:form method="POST" modelAttribute="form" cssClass="ui small form">
        <div class="js-table-statement-container" style="padding-left:50px;">
            <form:hidden path="id"/>
            <c:forEach items="${lotGroupList}" var="lotGroup">
                <div class="b-common-margin10">
                    <table class="b-table b-full-width dialog-list-form-container js-delivery-statement"
                           data-product-id="${lotGroup.productId}"
                           data-product-price="${lotGroup.productPrice}"
                    >
                        <thead>
                            <tr>
                                <th class="b-table__th" style="width: 150px;"><fmt:message key="contract.mainWindow.deliveryStatement.name"/></th>
                                <th class="b-table__th" style="width: 85px;"><fmt:message key="contract.mainWindow.deliveryStatement.count"/></th>
                                <th class="b-table__th" style="width: 85px;"><fmt:message key="contract.mainWindow.deliveryStatement.type"/></th>
                                <th class="b-table__th" style="width: 80px;"><fmt:message key="contract.mainWindow.deliveryStatement.specialTest"/></th>
                                <th class="b-table__th" style="width: 120px"><fmt:message key="contract.mainWindow.deliveryStatement.deliveryDate"/></th>
                                <th class="b-table__th" style="width: 120px"><fmt:message key="contract.mainWindow.deliveryStatement.price"/></th>
                                <th class="b-table__th b-align-mid-center" style="width: 300px;"><fmt:message key="contract.mainWindow.deliveryStatement.typePrice"/></th>
                                <th class="b-table__th b-align-mid-center" style="width: 80px;">
                                    <button class="compact ui button b-btn b-btn-add js-button-add" type="button"></button>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${lotGroup.lotList}" var="lot" varStatus="status">
                                <tr>
                                    <c:if test="${status.first}">
                                        <td class="b-table__td" style="width:150px;" rowspan="${fn:length(lotGroup.lotList)}">
                                            <input type="hidden" class="js-product-id"
                                                   data-list-form-attribute="deliveryStatementLotFormList.productId"
                                                   value="${lotGroup.productId}"/>
                                                ${lotGroup.productName}
                                        </td>
                                    </c:if>
                                    <td class="b-table__td b-align-mid-right" style="width: 85px;">
                                        <input type="hidden" data-list-form-attribute="deliveryStatementLotFormList.id" value="${lot.id}">
                                        <div class="ui fluid input">
                                            <input type="text" data-list-form-attribute="deliveryStatementLotFormList.amount" value="${lot.amount}"/>
                                        </div>
                                    </td>
                                    <td class="b-table__td b-align-mid-center" style="width: 85px;">
                                        <div class="ui fluid input">
                                            <select class="ui dropdown" data-list-form-attribute="deliveryStatementLotFormList.currentAcceptTypeId">
                                                <c:forEach items="${acceptTypeList}" var="acceptType">
                                                    <option value="${acceptType.id}"
                                                            <c:if test="${acceptType.id eq lot.acceptTypeId}">
                                                                selected
                                                            </c:if>
                                                    >${acceptType.code}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </td>
                                    <td class="b-table__td b-align-mid-center" style="width: 80px;">
                                        <div class="ui fluid input">
                                            <select class="ui dropdown" data-list-form-attribute="deliveryStatementLotFormList.specialTestTypeId">
                                                <c:forEach items="${specialTestTypeList}" var="specialTestType">
                                                    <option value="${specialTestType.id}"
                                                            <c:if test="${specialTestType.id eq lot.specialTestTypeId}">
                                                                selected
                                                            </c:if>
                                                    ><fmt:message key="${specialTestType.property}"/>
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </td>
                                    <td class="b-table__td b-align-mid-center" style="width: 120px;">
                                        <div class="ui fluid input">
                                            <input type="text" class="erp-date"
                                                   data-list-form-attribute="deliveryStatementLotFormList.deliveryDate"
                                                   value="<javatime:format value="${lot.deliveryDate}" pattern="dd.MM.yyyy"/>"/>
                                        </div>
                                    </td>
                                    <td class="b-table__td b-align-mid-center" style="width: 120px;">
                                        <div class="ui fluid input">
                                            <input type="text" class="js-price"
                                                   data-list-form-attribute="deliveryStatementLotFormList.price"
                                                   value="<fmt:formatNumber value="${lot.neededPrice}" maxFractionDigits="2" minFractionDigits="2"/>"/>
                                        </div>
                                    </td>
                                    <td class="b-table__td b-align-mid-center" style="width: 300px;">
                                        <input type="hidden" class="js-price-kind-id" data-list-form-attribute="deliveryStatementLotFormList.priceKindId" value="${lot.priceKindId}"/>
                                        <input type="hidden" class="js-protocol-id" data-list-form-attribute="deliveryStatementLotFormList.protocolId" value="${lot.protocolId}"/>
                                        <a class="b-link js-price-kind">${lot.priceKind}</a>
                                    </td>
                                    <td class="b-table__td b-align-mid-center"><i class="js-remove-option b-icon-remove"></i></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:forEach>
        </div>
        <div class="b-common-margin20 b-common-fl-right">
            <button class="ui small button b-btn b-btn-save" type="submit"><fmt:message key="label.button.save"/></button>
        </div>
    </form:form>
</div>

<script>
    $(() => {
        const
            $dialog = $('.editDeliveryStatement-dialog'),
            $container = $dialog.find('.js-table-statement-container'),
            $addProductBtn = $dialog.find('.js-button-add-product'),
            removeRowBtnSelector = '.js-remove-option',
            addRowBtnSelector = '.js-button-add',
            tableSelector = '.js-delivery-statement',
            priceKindInfoSelector = '.js-price-kind',
            currentDate = dateStdToString(new Date()),
            sectionId = '${form.id}';

        $dialog.find(tableSelector).on({
            'delivery.statement.events' : e => {
                let
                    $table = $(e.currentTarget),
                    $removeBtn = $table.find(removeRowBtnSelector),
                    $addBtn = $table.find(addRowBtnSelector),
                    $priceKindInfo = $table.find(priceKindInfoSelector),
                    $erpDate = $table.find('.erp-date'),
                    $productTd = $table.find('tbody tr:first td:first'),
                    row =
                        `<tr>
                            <td class="b-table__td b-align-mid-right" style="width: 85px;" rowspan="1">
                                <input type="hidden" data-list-form-attribute="deliveryStatementLotFormList.id" />
                                <div class="ui fluid input">
                                    <input type="text" data-list-form-attribute="deliveryStatementLotFormList.amount" value="1"/>
                                </div>
                            </td>
                            <td class="b-table__td b-align-mid-center" style="width: 85px;">
                                <div class="ui fluid input">
                                    <select class="ui dropdown search" data-list-form-attribute="deliveryStatementLotFormList.currentAcceptTypeId">
                                        <option value=""><fmt:message key="text.notSpecified"/></option>
                                        <c:forEach items="${acceptTypeList}" var="acceptType">
                                            <option value="${acceptType.id}">${acceptType.code}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </td>
                            <td class="b-table__td b-align-mid-center" style="width: 80px;">
                                <div class="ui fluid input">
                                    <select class="ui dropdown search" data-list-form-attribute="deliveryStatementLotFormList.specialTestType">
                                        <c:forEach items="${specialTestTypeList}" var="specialTestType">
                                            <option value="${specialTestType.id}"><fmt:message key="${specialTestType.property}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </td>
                            <td class="b-table__td b-align-mid-center" style="width: 120px;">
                                <div class="ui fluid input">
                                    <input type="text" class="erp-date" data-list-form-attribute="deliveryStatementLotFormList.deliveryDate" value="` + currentDate + `"/>
                                </div>
                            </td>
                            <td class="b-table__td b-align-mid-center" style="width: 120px;">
                                <div class="ui fluid input">
                                    <input type="text" class="js-price" data-list-form-attribute="deliveryStatementLotFormList.price" value="` + $table.data('productPrice') + `" />
                                </div>
                            </td>
                            <td class="b-table__td b-align-mid-center" style="width: 300px;">
                                <input type="hidden" class="js-price-kind-id" data-list-form-attribute="deliveryStatementLotFormList.priceKindId" value="1"/>
                                <input type="hidden" class="js-protocol-id" data-list-form-attribute="deliveryStatementLotFormList.protocolId" />
                                <a class="b-link js-price-kind"><fmt:message key="priceKind.preliminary"/></a>
                            </td>
                            <td class="b-table__td b-align-mid-center"><i class="js-remove-option b-icon-remove"></i></td>
                        </tr>`;

                $erpDate.removeAttr('id').datepicker();
                $erpDate.inputmask({
                    alias: 'datetime',
                    inputFormat: 'dd.mm.yyyy',
                    placeholder: '__.__.____',
                    clearIncomplete: true
                });

                $removeBtn.off().on({
                    'click' : e => {
                        let $tr = $(e.currentTarget).closest('tr');
                        if ($removeBtn.length > 1) {
                            if ($tr.index() === 0) {
                                $productTd = $productTd.clone();
                                $productTd.prependTo($tr.next());
                            }
                            $tr.remove();
                            $productTd.attr('rowspan', +$productTd.attr('rowspan') - 1);
                            $table.trigger('delivery.statement.events');
                        } else {
                            $table.remove();
                        }
                    }
                });

                $addBtn.off().on({
                    'click' : () => {
                        $(row).appendTo($table.find('tbody')).find('select').dropdown({ fullTextSearch: true });
                        $productTd.attr('rowspan', +$productTd.attr('rowspan') + 1);
                        $table.trigger('delivery.statement.events');
                    }
                });

                $priceKindInfo.off().on({
                    'click': e => {
                        let $this = $(e.currentTarget),
                            $td = $this.closest('td');
                        $.modalDialog({
                            dialogName: 'priceKindInfo',
                            url: '/contract/priceKindInfo',
                            parameters: {
                                sectionId: sectionId,
                                productId: $table.data('productId'),
                                priceKindId: $td.find('.js-price-kind-id').val(),
                                protocolId: $td.find('.js-protocol-id').val(),
                                index: $container.find(priceKindInfoSelector).index($this)
                            }
                        });
                    }
                });
            }
        }).trigger('delivery.statement.events');

        // Добавление изделия
        $addProductBtn.on({
            'click' : () => {
                $.modalDialog({
                   dialogName: 'product',
                   url: '/contract/searchProduct',
                   dialogWidth: 900,
                   parameters: {
                       sectionId: sectionId
                   }
                });
            }
        });
    });
</script>