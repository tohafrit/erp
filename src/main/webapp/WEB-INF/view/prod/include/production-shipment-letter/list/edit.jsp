<div class="ui modal list_edit__main">
    <div class="header">
        ${empty form.id ? 'Добавление письма на производство' : 'Редактирование письма на производство'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <form:hidden path="allotmentIdList"/>
            <div class="field inline">
                <label>Номер письма: </label>
                СЛ-КБ-${form.number}
                <form:hidden path="number"/>
            </div>
            <div class="column field">
                <div class="three fields">
                    <div class="field">
                        <label>Дата подписания</label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-date" path="createDate"/>
                            </div>
                        </div>
                        <div class="ui compact message error" data-field="createDate"></div>
                    </div>
                    <div class="field">
                        <label>Дата отправки в ОТК</label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-date" path="sendToProductionDate"/>
                            </div>
                        </div>
                        <div class="ui compact message error" data-field="sendToProductionDate"></div>
                    </div>
                    <div class="field">
                        <label>Дата отгрузки</label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <form:input cssClass="std-date" path="sendToWarehouseDate"/>
                            </div>
                        </div>
                        <div class="ui compact message error" data-field="sendToWarehouseDate"></div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <form:textarea path="comment" rows="3"/>
                </div>
                <div class="ui compact message error" data-field="comment"></div>
            </div>
            <div class="field inline required">
                <label>Изделия</label>
                <i class="icon add link blue list_edit__btn-add-product" title="<fmt:message key="label.button.add"/>"></i>
                <i class="icon trash link blue list_edit__btn-delete" title="<fmt:message key="label.button.delete"/>"></i>
                <i class="icon expand alternate link blue list_edit__btn-expand" title="Развернуть"></i>
                <i class="icon compress alternate link blue list_edit__btn-compress" title="Свернуть"></i>
                <div class="list_edit__product-table table-sm table-striped"></div>
                <div class="ui compact message error" data-field="allotmentIdList"></div>
            </div>
        </form:form>
    </div>
    <div class="actions">
        <button class="ui small button" type="submit">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const letterId = '${form.id}';
        const dateSent = '${form.sendToProductionDate}';
        const shipmentDate = '${form.sendToWarehouseDate}';
        const $btnDelete = $('i.list_edit__btn-delete');
        //
        const $modal = $('div.list_edit__main');
        const $contractSectionBtnAdd = $('i.list_edit__btn-add-product');
        const $table = $('div.list_edit__product-table');
        const $btnExpand = $('i.list_edit__btn-expand');
        const $btnCompress = $('i.list_edit__btn-compress');
        //
        let $allotmentIdList = $modal.find('input#allotmentIdList');
        let dateSentShipmentDateNotExists = dateSent === '' && shipmentDate === '';

        // TODO уточнить возможность добавления
        // $contractSectionBtnAdd.toggle(dateSentShipmentDateNotExists);
        <%--$contractSectionBtnAdd.toggle(${empty form.allotmentIdList});--%>

        // Редактирование/добавление договора
        $contractSectionBtnAdd.on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT_CONTRACT
            })
        });

        // Таблица изделий письма на производство
        const table = new Tabulator('div.list_edit__product-table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(form.allotmentIdList)}'),
            headerSort: false,
            height: 'calc(100vh * 0.3)',
            groupBy: [ TABR_FIELD.GROUP_MAIN, TABR_FIELD.PRODUCT_NAME, TABR_FIELD.GROUP_SUB_MAIN ],
            groupStartOpen: [ true, true, true ],
            groupToggleElement: 'header',
            layout: 'fitDataFill',
            groupHeader: [
                function(value) {
                    return '<span style="color:#315c83;">' + value + '</span>';
                },
                function(value) {
                    return value;
                },
                function(value) {
                    return value;
                }
            ],
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Кол-во',
                    field: TABR_FIELD.ALLOTMENT_AMOUNT,
                    hozAlign: 'center',
                    width: 100,
                    resizable: false
                },
                {
                    title: 'Стоимость',
                    field: TABR_FIELD.COST,
                    hozAlign: 'center',
                    width: 120,
                    resizable: false,
                    formatter: 'stdMoney'
                },
                {
                    title: 'Оплачено',
                    columns: [
                        {
                            title: 'руб.',
                            field: TABR_FIELD.PAID,
                            hozAlign: 'center',
                            width: 120,
                            resizable: false,
                            formatter: 'stdMoney'
                        },
                        {
                            title: '%',
                            field: TABR_FIELD.PERCENT_PAID,
                            hozAlign: 'center',
                            width: 50,
                            resizable: false
                        },
                    ]
                },
                {
                    title: 'Счет на ОО',
                    field: TABR_FIELD.FINAL_PRICE,
                    resizable: false,
                    width: 130,
                    hozAlign: 'center',
                    formatter: cell => {
                        return booleanToLight(cell.getValue() > 0);
                    }
                },
                {
                    title: 'Запущено',
                    field: TABR_FIELD.LAUNCH_AMOUNT,
                    hozAlign: 'center',
                    width: 100,
                    resizable: false
                },
                {
                    title: 'Запуск',
                    field: TABR_FIELD.LAUNCH_NUMBER,
                    hozAlign: 'center',
                    width: 80,
                    resizable: false
                }
            ]
        });

        // Выравнивание заголовков по центру
        $table.find('.tabulator-col').css({
            'text-align': 'center'
        });

        // Кнопка свернуть группы
        $btnCompress.on({
            'click': () => table.getGroups().forEach(group => group.hide())
        });
        // Кнопка развернуть группы
        $btnExpand.on({
            'click': () => table.getGroups().forEach(group => group.show())
        });

        // Удаление
        $btnDelete.on({
            'click': () => {
                const data = [];
                table.getSelectedData().forEach(el => data.push(el.id));
                table.deleteRow(data);
            }
        });

        // Добавление списка allotment-ов в сабмит форму
        $modal.on({
            'cb.onInitSubmit': () => {
                const data = [];
                table.getData().forEach(el => data.push(el.id));
                $modal.find('input[name="allotmentIdList"]').val(JSON.stringify(data));
            }
        });
    });
</script>