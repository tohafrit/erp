<div class="detail_invoices__main">
    <div class="detail_invoices__header_buttons">
        <i class="icon filter link blue detail_invoices__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="icon add link blue detail_invoices__btn-add" title="Добавить счет"></i>
        <div class="detail_invoices__div_header_title">
            <h1 class="detail_invoices__header_title">
                Счета на оплату
                <c:if test="${isAdditionalAgreement}">
                    дополнительного соглашения № ${contractSectionNumber}
                </c:if>
            </h1>
        </div>
    </div>
    <div class="detail_invoices__table-block">
        <div class="detail_invoices__table-main-block">
            <div class="detail_invoices__table-wrap">
                <form:form modelAttribute="contractInvoicesFilterForm" cssClass="ui tiny form secondary segment detail_invoices_filter__form">
                    <div class="field">
                        <div class="ui icon small buttons">
                            <div class="ui button detail_invoices_filter__btn-search" title="<fmt:message key="label.button.search"/>">
                                <i class="search blue icon"></i>
                            </div>
                            <div class="ui button detail_invoices_filter__btn-clear-all" title="<fmt:message key="label.button.clearFilter"/>">
                                <i class="times blue icon"></i>
                            </div>
                        </div>
                    </div>
                    <div class="ui two column grid">
                        <div class="column field">
                            <div class="two fields">
                                <div class="field">
                                    <label>Номер</label>
                                    <form:input path="invoiceNumber" cssClass="detail_invoices_filter__field-invoiceNumber" type="search"/>
                                </div>
                                <div class="field">
                                    <label>Статус</label>
                                    <form:select cssClass="ui dropdown search label std-select" path="invoiceStatusList" multiple="multiple">
                                        <c:forEach items="${invoiceStatusList}" var="invoiceStatus">
                                            <form:option value="${invoiceStatus.id}">${invoiceStatus.value}</form:option>
                                        </c:forEach>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="column field">
                            <div class="two fields">
                                <div class="field">
                                    <label>Дата счета с</label>
                                    <div class="ui calendar">
                                        <div class="ui input left icon">
                                            <i class="calendar icon"></i>
                                            <form:input cssClass="std-date" path="invoiceDateFrom" type="search"/>
                                        </div>
                                    </div>
                                </div>
                                <div class="field">
                                    <label><fmt:message key="label.to"/></label>
                                    <div class="ui calendar">
                                        <div class="ui input left icon">
                                            <i class="calendar icon"></i>
                                            <form:input cssClass="std-date" path="invoiceDateTo" type="search"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form:form>
                <div class="detail_invoices__table table-sm"></div>
            </div>
        </div>
    </div>

    <script>
        $(() => {
            const sectionId = '${contractSectionId}';
            //
            const $menuTree = $('ul.detail__menu_tree');
            const $btnFilter = $('i.detail_invoices__btn-filter');
            const $filterForm = $('form.detail_invoices_filter__form');
            const $btnSearch = $('div.detail_invoices_filter__btn-search');
            const $btnAddInvoice = $('i.detail_invoices__btn-add');
            const $clearAllButton = $('div.detail_invoices_filter__btn-clear-all');
            const $invoicesMenu = $menuTree.find('li.detail__menu_invoices[data-id=${contractSectionId}]');

            // Очистка полей фильтра
            $clearAllButton.on({
                'click': () => formClear($filterForm)
            });

            // Поиск по кнопки enter
            $filterForm.enter(() => $btnSearch.trigger('click'));

            // Кнопка фильтра
            $btnFilter.on({
                'click': function() {
                    $(this).toggleClass('primary');
                    $filterForm.toggle(!$filterForm.is(':visible'));
                }
            });

            $('input.detail_invoices_filter__field-invoiceNumber').inputmask({
                placeholder: '',
                regex: '[0-9]{0,6}'
            });

            const table = new Tabulator('div.detail_invoices__table', {
                ajaxURL: ACTION_PATH.DETAIL_INVOICE_LOAD,
                ajaxRequesting: (url, params) => {
                    params.filterForm = formToJson($filterForm);
                    params.contractSectionId = sectionId;
                },
                selectable: 1,
                headerSort: false,
                height: '100%',
                layout: 'fitColumns',
                columns: [
                    TABR_COL_LOCAL_ROW_NUM,
                    { title: 'Номер', field: TABR_FIELD.INVOICE_NUMBER },
                    {
                        title: 'Дата',
                        field: TABR_FIELD.DATE,
                        hozAlign: 'center',
                        formatter: 'stdDate'
                    },
                    {
                        title: 'Действителен до',
                        field: TABR_FIELD.DATE_VALID_BEFORE,
                        hozAlign: 'center',
                        formatter: 'stdDate'
                    },
                    {
                        title: 'Сумма',
                        field: TABR_FIELD.AMOUNT,
                        hozAlign: 'center',
                        formatter: 'stdMoney'
                    },
                    { title: 'Расчетный счёт', field: TABR_FIELD.ACCOUNT },
                    { title: 'Банк', field: TABR_FIELD.BANK_NAME },
                    {
                        title: 'Оплачено',
                        field: TABR_FIELD.PAID_AMOUNT,
                        hozAlign: 'center',
                        formatter: 'stdMoney'
                    },
                    {
                        title: 'Описание',
                        field: TABR_FIELD.DESCRIPTION,
                        variableHeight: true,
                        resizable: false,
                        width: 200,
                        formatter: cell => {
                            $(cell.getElement()).css({'white-space': 'pre-wrap'});
                            return cell.getValue();
                        }
                    },
                    { title: 'Статус', field: TABR_FIELD.STATUS_NAME },
                    {
                        title: 'Срок изготовления',
                        field: TABR_FIELD.PRODUCTION_FINISH_DATE,
                        hozAlign: 'center',
                        formatter: 'stdDate'
                    }
                ],
                rowContext: (e, row) => {
                    table.deselectRow();
                    row.select();
                },
                rowContextMenu: row => {
                    const menu = [];
                    const data = row.getData();
                    const id = data.id
                    menu.push({
                        label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                        action: () => deleteInvoice(id)
                    });
                    menu.push({
                        label: `<i class="file word alternate outline icon blue"></i><fmt:message key="label.menu.download"/>`,
                        action: () => window.open('/invoice-documentation-formed/download/' + id, '_blank')
                    });
                    if (data.canStatusActing) {
                        menu.push({
                            label: '<i class="file icon blue"></i>Аннулировать',
                            action: () => canceledInvoice(id)
                        });
                        menu.push({
                            label: '<i class="file alternate icon blue"></i>Закрыть',
                            action: () => closedInvoice(id)
                        });
                    }
                    if (data.canStatusClosedOrCanceled) {
                        menu.push({
                            label: '<i class="file alternate outline icon blue"></i>Сделать действующим',
                            action: () => actingInvoice(id)
                        });
                    }
                    return menu;
                }
            });

            // Функция добавления счета
            function addInvoice(sectionId) {
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_INVOICES_ADD,
                    loadData: { sectionId: sectionId },
                    submitURL: ACTION_PATH.DETAIL_INVOICE_ADD_SAVE,
                    onSubmitSuccess: resp => {
                        const invoiceForAmountDialog = resp.attributes.invoiceForAmountDialog;
                        if (invoiceForAmountDialog) {
                            confirmDialog({
                                title: 'Добавление счета',
                                message: 'При указанных параметрах счета и с учетом ранее поступивших платежей сумма' +
                                    ' счета превышает стоимость выбранных изделий поставки. Все равно добавить счет?',
                                buttonTextAccept: 'Добавить',
                                onAccept: () => {
                                    const $main = $('div.detail_invoices_add__main');
                                    const $invoiceForAmountDialog = $main.find('input[name="invoiceForAmountDialog"]');
                                    $invoiceForAmountDialog.val(true);
                                    const $btnSave = $main.find('button.detail_invoices_add__button-save');
                                    $btnSave.trigger('click');
                                    table.setData();
                                }
                            });
                        } else {
                            table.setData();
                        }
                    }
                });
            }

            // Функция удаления счета
            function deleteInvoice(id) {
                confirmDialog({
                    title: 'Удаление счета',
                    message: 'Вы уверены, что хотите удалить счет?',
                    onAccept: () => $.ajax({
                        method: 'DELETE',
                        url: ACTION_PATH.DETAIL_INVOICE_DELETE + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setData())
                });
            }

            // Аннулировать счет
            function canceledInvoice(id) {
                confirmDialog({
                    title: 'Аннулирование счета',
                    message: 'Вы действительно хотите аннулировать счёт?',
                    onAccept: () => $.post({
                        url: ACTION_PATH.DETAIL_INVOICE_CANCELED_INVOICE,
                        data: { id: id },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setData())
                })
            }

            // Закрыть счет
            function closedInvoice(id) {
                confirmDialog({
                    title: 'Закрытие счета',
                    message: 'Вы действительно хотите закрыть счёт?',
                    onAccept: () => $.post({
                        url: ACTION_PATH.DETAIL_INVOICE_CLOSED_INVOICE,
                        data: { id: id },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setData())
                })
            }

            // Сделать действующим счет
            function actingInvoice(id) {
                confirmDialog({
                    title: 'Действующий статус счёта',
                    message: 'Вы действительно хотите счёт сделать действующим?',
                    onAccept: () => $.post({
                        url: ACTION_PATH.DETAIL_INVOICE_ACTING_INVOICE,
                        data: { id: id },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setData())
                })
            }

            // Кнопка добавления счета
            $btnAddInvoice.on({
                'click': () => addInvoice(sectionId)
            });

            // Кнопка поиска
            $btnSearch.on({
                'click': () => table.setData()
            });
        })
    </script>
</div>