<div class="product-dialog" title="<fmt:message key="contract.products"/>">
    <div class="b-common-margin20">
        <table class="b-full-width js-product-type">
            <tr>
                <th class="b-table-edit__th" style="width: 100px;"><fmt:message key="contract.product.typeOfWork"/></th>
                <td class="b-table-edit__td">
                    <div class="ui radio checkbox">
                        <input type="radio" name="productType" value="1" id="manufacturing" checked="checked"/><label for="manufacturing"><fmt:message key="contract.product.manufacturing"/></label>
                    </div>
                    <div class="ui radio checkbox">
                        <input type="radio" name="productType" value="2" id="repairs"/><label  for="repairs"><fmt:message key="contract.product.repairs"/></label>
                    </div>
                    <div class="ui radio checkbox">
                        <input type="radio" name="productType" value="4" id="revision"/><label  for="revision"><fmt:message key="contract.product.revision"/></label>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div class="js-contract-product-filter b-content-filter b-display_none">
        <i class="fas fa-times js-close-toggle"></i><br>
        <jsp:include page="/contract/searchProductForm"/>
    </div>
    <div class="b-common-margin20">
        <table class="erp-product-search-datatable hide-add-btn hide-excel-btn hide-print-btn display compact cell-border"
               data-search-btn-filter=".js-contract-product-filter"
        >
            <thead>
                <tr>
                    <th></th>
                    <th><fmt:message key="contract.product.product.name"/></th>
                    <th><fmt:message key="contract.product.product.number"/></th>
                    <th><fmt:message key="contract.product.product.type"/></th>
                    <th><fmt:message key="contract.product.product.protocol.number"/></th>
                    <th><fmt:message key="contract.product.product.comment"/></th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    </div>
    <div class="b-common-margin20 b-common-fl-right">
        <button class="ui small button b-btn b-btn-select js-select-btn" type="button"><fmt:message key="label.button.select"/></button>
    </div>
</div>

<script>
    $(() => {
        const
            $dialog = $('.product-dialog'),
            $productType = $dialog.find('.js-product-type'),
            $productDataTable = $dialog.find('.erp-product-search-datatable'),
            $selectBtn = $dialog.find('.js-select-btn'),
            $filter = $dialog.find('.js-contract-product-filter'),
            $closeFilterBtn = $filter.find('.js-close-toggle'),
            $searchBtn = $filter.find('.b-search__btn'),
            removeRowBtnSelector = '.js-remove-option',
            addRowBtnSelector = '.js-button-add',
            tableSelector = '.js-delivery-statement',
            priceKindInfoSelector = '.js-price-kind';

        let $datatable = $productDataTable.DataTable({
            processing: true,
            serverSide: true,
            paging: true,
            pageLength: 200,
            searching: false,
            scrollY: '500px',
            select: 'single',
            columns: [
                { defaultContent: '' },
                { data: 'conditionalName', name: 'conditionalName', defaultContent: '' },
                { data: 'decimalNumber', name: 'decimalNumber', defaultContent: '' },
                { data: 'type.name', name: 'type', defaultContent: '' },
                {
                    name: 'protocolNumber',
                    defaultContent: '',
                    render: (data, type, row) => {
                        if (row.productChargesProtocolList.length > 0) {
                            return row.productChargesProtocolList[0].protocolNumber;
                        }
                    }
                },
                { data: 'note', name: 'note', defaultContent: '' }
            ],
            ajax: {
                type: 'POST',
                data: (data) => {
                    let searchData = {};
                    searchData.search = $filter.find('input[name="search"]').val();
                    searchData.searchType = $filter.find('select[name="searchType"] > option:selected:first').val();
                    searchData.typeIdList = $filter.find('select[name="typeIdList"] > option:selected').map((k, v) => $(v).val()).get();
                    searchData.active = $filter.find('input[name="active"]').is(':checked');
                    searchData.archive = $filter.find('input[name="archive"]').is(':checked');
                    searchData.excludeProductIdList = $parentDialog.find('.js-product-id').map((k, v) => $(v).val()).get();
                    data.searchData = JSON.stringify(searchData);
                },
                url: '/contract/searchProduct',
            },
            createdRow: (row, data) => {
                $(row).attr('data-id', data.id);
                $(row).attr('data-price', data.price);
                $(row).attr('data-export-price', data.exportPrice);
                $(row).attr('data-name', data.conditionalName);
            }
        });

        // Перезагрузка таблицы при поиске
        $searchBtn.on({
            'click': (event) => {
                event.preventDefault();
                $datatable.ajax.reload();
                $closeFilterBtn.trigger('click');
            }
        });

        // Выбор изделия
        const
            $parentDialog = $('.editDeliveryStatement-dialog'),
            $container = $parentDialog.find('.js-table-statement-container'),
            currentDate = dateStdToString(new Date());

        $selectBtn.on({
            'click' : () => {
                let
                    $row = $productDataTable.find('tbody > tr.selected'),
                    $checkedProductType = $productType.find('[name="productType"]:checked'),
                    $checkedProductTypeId = $checkedProductType.val(),
                    $checkedProductTypeText = $checkedProductType.next().text(),
                    productId = $row.data('id'),
                    productName = $checkedProductTypeId === '1' ? $row.data('conditionalName') : $checkedProductTypeText + ' ' + $row.data('conditionalName'),
                    productPrice = '${isExport}' === 'true' ? $row.data('exportPrice') : $row.data('price'),
                    div =
                        `<div class="b-common-margin10">
                            <table class="b-table b-full-width dialog-list-form-container js-delivery-statement"
                                   data-product-id="` + productId + `"
                                   data-product-price="` + productPrice + `"
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
                                    <tr>
                                        <td class="b-table__td" style="width:150px;" rowspan="1">
                                            <input type="hidden" class="js-product-id"
                                                   data-list-form-attribute="deliveryStatementLotFormList.productId"
                                                   value="` + productId + `"/>` + productName + `
                                        </td>
                                        <td class="b-table__td b-align-mid-right" style="width: 85px;">
                                            <input type="hidden" data-list-form-attribute="deliveryStatementLotFormList.id" value="">
                                            <div class="ui fluid input">
                                                <input type="text" data-list-form-attribute="deliveryStatementLotFormList.amount" value="1"/>
                                            </div>
                                        </td>
                                        <td class="b-table__td b-align-mid-center" style="width: 85px;">
                                            <div class="ui fluid input">
                                                <select class="ui dropdown" data-list-form-attribute="deliveryStatementLotFormList.currentAcceptTypeId">
                                                    <c:forEach items="${acceptTypeList}" var="acceptType">
                                                        <option value="${acceptType.id}">${acceptType.code}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </td>
                                        <td class="b-table__td b-align-mid-center" style="width: 80px;">
                                            <div class="ui fluid input">
                                                <select class="ui dropdown" data-list-form-attribute="deliveryStatementLotFormList.specialTestTypeId">
                                                    <c:forEach items="${specialTestTypeList}" var="specialTestType">
                                                        <option value="${specialTestType.id}"><fmt:message key="${specialTestType.property}"/></option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </td>
                                        <td class="b-table__td b-align-mid-center" style="width: 120px;">
                                            <div class="ui fluid input">
                                                <input type="text" class="erp-date"
                                                       data-list-form-attribute="deliveryStatementLotFormList.deliveryDate"
                                                       value="` + currentDate + `"/>
                                            </div>
                                        </td>
                                        <td class="b-table__td b-align-mid-center" style="width: 120px;">
                                            <div class="ui fluid input">
                                                <input type="text"
                                                       data-list-form-attribute="deliveryStatementLotFormList.price"
                                                       value="` + productPrice + `"/>
                                            </div>
                                        </td>
                                        <td class="b-table__td b-align-mid-center" style="width: 300px;">
                                            <input type="hidden" class="js-price-kind-id" data-list-form-attribute="deliveryStatementLotFormList.priceKindId" value="1"/>
                                            <input type="hidden" class="js-protocol-id" data-list-form-attribute="deliveryStatementLotFormList.protocolId" value=""/>
                                            <a class="b-link js-price-kind"><fmt:message key="priceKind.preliminary"/></a>
                                        </td>
                                        <td class="b-table__td b-align-mid-center"><i class="js-remove-option b-icon-remove"></i></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>`;

                $(div).appendTo($container).find(tableSelector + ' select').addClass('search').dropdown({ fullTextSearch: true });
                $container.find(tableSelector + ':last').on({
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
                                    <input type="text" data-list-form-attribute="deliveryStatementLotFormList.price" value="` + $table.data('productPrice') + `" />
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
                                        sectionId: '${sectionId}',
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
                $dialog.dialog('close');
            }
        });
    });
</script>
