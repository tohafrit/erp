<h3 class="detail_decipherment__period-title">
    Периоды
    <i class="icon calendar alternate outline link blue detail_decipherment__btn-select-period" title="Выбрать период"></i>
</h3>
<table class="ui tiny compact definition celled table detail_decipherment__period-table">
    <thead>
        <tr>
            <th></th>
            <th>Планируемый</th>
            <th>Отчетный</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Наименование</td>
            <td>${periodName}</td>
            <td>${prevPeriodName}</td>
        </tr>
        <tr>
            <td>Начало</td>
            <td>${periodStartDate}</td>
            <td>${prevPeriodStartDate}</td>
        </tr>
        <tr>
            <td>Окончание</td>
            <td>${periodEndDate}</td>
            <td>${prevPeriodEndDate}</td>
        </tr>
        <tr>
            <td>Цена без упаковки</td>
            <td>${periodPriceWoPack}</td>
            <td>${prevPeriodPriceWoPack}</td>
        </tr>
        <tr>
            <td>Цена с упаковкой</td>
            <td>${periodPricePack}</td>
            <td>${prevPeriodPricePack}</td>
        </tr>
        <tr>
            <td>Цена с упаковкой и СИ</td>
            <td>${periodPricePackResearch}</td>
            <td>${prevPeriodPricePackResearch}</td>
        </tr>
    </tbody>
</table>
<div class="detail_decipherment__table-wrap">
    <div class="detail_decipherment__table_buttons">
        <i class="icon add link blue detail_decipherment__btn-add" title="<fmt:message key="label.button.add"/>"></i>
        <i class="icon file excel link blue detail_decipherment__btn-download-excel-forms" title="Выгрузить утвержденные формы"></i>
        <i class="icon table link blue detail_decipherment__btn-analysis-price" title="Анализ цены"></i>
    </div>
    <div class="detail_decipherment__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const productId = '${productId}';
        const periodId = '${periodId}';
        const $periodInfo = $('div.detail_decipherment__period-info');
        const $periodTable = $('table.detail_decipherment__period-table');
        //
        const $addBtn = $('i.detail_decipherment__btn-add');
        const $downloadExcelFormsBtn = $('i.detail_decipherment__btn-download-excel-forms');
        const $analysisPriceBtn = $('i.detail_decipherment__btn-analysis-price');
        const $selectPeriodBtn = $('i.detail_decipherment__btn-select-period');

        // Видимость информации о периоде
        $periodTable.toggle(periodId !== '');
        $periodInfo.toggle(periodId !== '');
        $addBtn.toggle(periodId !== '');
        $downloadExcelFormsBtn.toggle(periodId !== '');
        $analysisPriceBtn.toggle(periodId !== '');

        // Добавление расшифровки
        $addBtn.on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_ADD,
                loadData: { periodId: periodId }
            })
        });

        // Загрузка окна выбора периодов
        $selectPeriodBtn.on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_PERIOD,
                loadData: { productId: productId, periodId: periodId }
            })
        });

        // Загрузка окна внесения данных
        $analysisPriceBtn.on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_ANALYSIS,
                loadData: { productId: productId, periodId: periodId },
                submitAsJson: true,
                submitURL: ACTION_PATH.DETAIL_DECIPHERMENT_ANALYSIS_SAVE,
            })
        });

        const table = new Tabulator('div.detail_decipherment__table', {
            selectable: 1,
            headerSort: false,
            ajaxURL: ACTION_PATH.DETAIL_DECIPHERMENT_LOAD,
            ajaxRequesting: (url, params) => {
                params.periodId = periodId;
            },
            height: 'calc(100vh - 450px)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Форма', field: TABR_FIELD.NAME },
                {
                    title: 'В работе',
                    field: TABR_FIELD.READINESS,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark'
                },
                {
                    title: 'Утверждено',
                    field: TABR_FIELD.APPROVED,
                    resizable: false,
                    hozAlign: 'center',
                    formatter: 'lightMark'
                },
                {
                    title: 'Дата создания',
                    field: TABR_FIELD.CREATE_DATE,
                    hozAlign: 'center',
                    width: 150,
                    resizable: false,
                    formatter: 'stdDate'
                },
                { title: 'Создано', field: TABR_FIELD.CREATED_BY, hozAlign: 'center', minWidth: 120 },
                {
                    title: 'Файл',
                    width: 100,
                    hozAlign: 'center',
                    resizable: false,
                    field: 'fileHash',
                    formatter: 'fileLink'
                },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    headerSort: false,
                    minWidth: 300,
                    formatter: 'textarea'
                }
            ],
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                const id = data.id;
                const type = data.type;
                if (['FORM_4', 'FORM_6_1', 'FORM_6_2'].includes(type)) {
                    if (data.canEdit) {
                        menu.push({
                            label: '<i class="file alternate icon blue"></i>Накладные',
                            action: () => $.modalWindow({
                                loadURL: '/decipherment/product-invoice',
                                loadData: { deciphermentId: id }
                            })
                        });
                    }
                    menu.push({
                        label: '<i class="file archive outline icon blue"></i>Выгрузить накладные',
                        action: () => $.get({
                            url : '/decipherment/download-invoices-check',
                            data : { deciphermentId: id },
                            beforeSend: () => togglePreloader(true),
                            complete: () => togglePreloader(false)
                        }).done(() => window.open('/decipherment/download-invoices?deciphermentId=' + id, '_blank'))
                    });
                }
                menu.push({
                    label: '<i class="file excel blue icon"></i>Выгрузить форму',
                    action: () => window.open(ACTION_PATH.DETAIL_DECIPHERMENT_DOWNLOAD_FORM + '?id=' + id, '_blank')
                });
                if (data.canApprove) {
                    menu.push({
                        label: '<i class="check icon blue"></i>Утвердить',
                        action: () => approve(id, true)
                    });
                }
                if (data.canUnApprove) {
                    menu.push({
                        label: '<i class="times icon blue"></i>Отменить утверждение',
                        action: () => approve(id, false)
                    });
                }
                menu.push({ separator: true });
                if (data.canEdit) {
                    if (['FORM_4', 'FORM_6_1', 'FORM_6_2', 'FORM_6_3'].includes(type)) {
                        menu.push({
                            label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                            action: () => $.modalWindow({
                                loadURL: '/decipherment/edit',
                                loadData: { id: id },
                                submitURL: '/decipherment/edit',
                                onSubmitSuccess: () => table.setData()
                            })
                        });
                    } else {
                        menu.push({
                            label: '<i class="edit blue icon"></i><fmt:message key="label.menu.edit"/>',
                            action: () => $.modalWindow({
                                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_EDIT,
                                loadData: { id: id },
                                submitURL: ACTION_PATH.DETAIL_DECIPHERMENT_EDIT_SAVE,
                                submitAsJson: true,
                                onSubmitSuccess: () => table.setData()
                            })
                        });
                    }
                }
                menu.push({
                    label: '<i class="trash alternate outline blue icon"></i><fmt:message key="label.menu.delete"/>',
                    action: () => confirmDialog({
                        title: 'Удаление формы',
                        message: 'Вы действительно хотите удалить форму?',
                        onAccept: () => $.ajax({
                            method: 'DELETE',
                            url: ACTION_PATH.DETAIL_DECIPHERMENT_DELETE + id,
                            beforeSend: () => togglePreloader(true),
                            complete: () => togglePreloader(false)
                        }).done(() => table.setData())
                    })
                });
                return menu;
            }
        });

        // Утверждение/снятие утверждения расшифровки
        function approve(id, toApprove) {
            confirmDialog({
                title: toApprove ? 'Утверждение формы' : 'Отмена утверждения формы',
                message: toApprove ? 'Вы действительно хотите утвердить форму?' : 'Вы действительно хотите отменить утверждение формы?',
                onAccept: () => $.post({
                    url: ACTION_PATH.DETAIL_DECIPHERMENT_APPROVE,
                    data: { id: id, toApprove: toApprove },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData()).then(() => table.selectRow(id))
            });
        }

        // Функия выгрузки утвержденных форм
        $downloadExcelFormsBtn.on({
            'click': () => window.open(ACTION_PATH.DETAIL_DECIPHERMENT_DOWNLOAD_APPROVED_FORMS + '?id=' + periodId, '_blank')
        });
    })
</script>