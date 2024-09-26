<div class="detail_delivery-statement__main">
        <div class="detail_delivery-statement__header_buttons">
            <i class="icon filter link blue detail_delivery-statement__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
            <i class="icon add link blue detail_delivery-statement__btn-add" title="Добавить изделие"></i>
            <div class="detail_delivery-statement__header_buttons-expand">
                <i class="icon expand alternate link blue detail_delivery-statement__btn-expand" title="Развернуть"></i>
                <i class="icon compress alternate link blue detail_delivery-statement__btn-compress" title="Свернуть"></i>
            </div>
            <div class="detail_delivery-statement__div_header_title">
                <h1 class="detail_delivery-statement__header_title">
                    Ведомость поставки
                    <c:if test="${isAdditionalAgreement}">
                        дополнительного соглашения № ${sectionNumber}
                    </c:if>
                </h1>
            </div>
        </div>
    <div class="detail_delivery-statement__table-result table-sm"></div>
    <div class="detail_delivery-statement__tables-block">
        <div class="detail_delivery-statement__table-block">
            <div class="detail_delivery-statement__table-wrap">
                <div class="detail_delivery-statement__table table-sm"></div>
            </div>
        </div>
        <div class="detail_delivery-statement__sub-block">
            <i class="close link blue icon detail_delivery-statement__btn-close-sub-block"></i>
            <div class="detail_delivery-statement__sub-block-content"></div>
        </div>
    </div>

    <script>
        $(() => {
            const sectionId = '${sectionId}';
            //
            const $menuTree = $('ul.detail__menu_tree');
            const $btnFilter = $('i.detail_delivery-statement__btn-filter');
            const $btnExpand = $('i.detail_delivery-statement__btn-expand');
            const $btnCompress = $('i.detail_delivery-statement__btn-compress');
            const $btnAddProduct = $('i.detail_delivery-statement__btn-add');
            const $subBlock = $('div.detail_delivery-statement__sub-block');
            const $subContent = $('div.detail_delivery-statement__sub-block-content');
            const $btnCloseSubBlock = $('i.detail_delivery-statement__btn-close-sub-block');
            const $deliveryStatement = $menuTree.find('li.detail__menu_delivery-statement[data-id=${sectionId}]');

            // Параметры фильтра таблицы
            const tableData = tableDataFromUrlQuery(window.location.search);
            let filterData = tableData.filterData;

            const table = new Tabulator('div.detail_delivery-statement__table', {
                ajaxURL: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_LOAD,
                ajaxRequesting: (url, params) => {
                    params.filterData = JSON.stringify(filterData);
                    params.sectionId = sectionId;
                },
                headerSort: false,
                groupBy: 'groupMain',
                groupToggleElement: 'header',
                height: '100%',
                layout: 'fitColumns',
                groupHeader: [
                    function(value) {
                        return '<span style="color:#315c83;">' + value + '</span>';
                    }
                ],
                columns: [
                    TABR_COL_LOCAL_ROW_NUM,
                    { title: 'Кол-во', field: TABR_FIELD.AMOUNT, hozAlign: 'center' },
                    { title: 'Тип приемки', field: TABR_FIELD.ACCEPT_TYPE, hozAlign: 'center' },
                    { title: 'Спец. проверка', field: TABR_FIELD.SPECIAL_TEST_TYPE, hozAlign: 'center' },
                    {
                        title: 'Дата поставки',
                        field: TABR_FIELD.DELIVERY_DATE,
                        hozAlign: 'center',
                        formatter: 'stdDate'
                    },
                    { title: 'Запущено', field: TABR_FIELD.LAUNCH_AMOUNT, hozAlign: 'center '},
                    { title: 'Отгружено', field: TABR_FIELD.SHIPPED_AMOUNT, hozAlign: 'center' },
                    {
                        title: 'Цена',
                        field: TABR_FIELD.PRICE,
                        hozAlign: 'center',
                        formatter: 'stdMoney'
                    },
                    {
                        title: 'Вид цены',
                        field: TABR_FIELD.PRICE_KIND,
                        hozAlign: 'center',
                        variableHeight: true,
                        resizable: false,
                        width: 200,
                        formatter: cell => {
                            $(cell.getElement()).css({'white-space': 'pre-wrap'});
                            return cell.getValue();
                        }
                    },
                    {
                        title: 'Стоимость',
                        field: TABR_FIELD.COST,
                        hozAlign: 'center',
                        formatter: 'stdMoney'
                    }
                ],
                rowClick: (e, row) => {
                    const data = row.getData();
                    table.deselectRow();
                    row.select();
                    showDistribution(data.lotId);
                },
                rowContext: (e, row) => {
                    table.deselectRow();
                    row.select();
                },
                rowContextMenu: row => {
                    const menu = [];
                    const lotId = row.getData().lotId;
                    menu.push({
                        label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                        action: () => editDelivery(lotId)
                    });
                    menu.push({
                        label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                        action: () => deleteDelivery(lotId)
                    });
                    return menu;
                }
            });

            // Фильтр
            $.modalFilter({
                url: VIEW_PATH.DETAIL_DELIVERY_STATEMENT_FILTER,
                button: $btnFilter,
                filterData: () => filterData,
                onApply: data => {
                    filterData = data;
                    table.setData();
                }
            });

            // Кнопка свернуть группу
            $btnCompress.on({
                'click': () => table.getGroups().forEach(group => group.hide())
            });
            // Кнопка развернуть группу
            $btnExpand.on({
                'click': () => table.getGroups().forEach(group => group.show())
            });

            const resultTable = new Tabulator('div.detail_delivery-statement__table-result', {
                ajaxURL: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_RESULT_LOAD,
                ajaxRequesting: (url, params) => {
                    params.contractSectionId = sectionId;
                },
                selectable: false,
                headerSort: false,
                layout: 'fitDataTable',
                columns: [
                    {
                        title: 'Итого',
                        field: 'total',
                        formatter: 'stdMoney'
                    },
                    {
                        title: 'НДС',
                        field: 'vat',
                        formatter: 'stdMoney'
                    },
                    {
                        title: 'Итого с НДС',
                        field: 'totalWithVat',
                        formatter: 'stdMoney'
                    }
                ]
            });

            // Функция добавления изделия в ведомость поставки
            function addProduct(id) {
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_DELIVERY_STATEMENT_ADD,
                    loadData: { sectionId: id }
                });
            }

            // Функция удаления поставки (lot-a)
            function deleteDelivery(id) {
                confirmDialog({
                    title: 'Удаление поставки',
                    message: 'Вы уверены, что хотите удалить поставку?',
                    onAccept: () => $.ajax({
                        method: 'DELETE',
                        url: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_DELETE + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => $deliveryStatement.trigger('click'))
                });
            }

            // Функция редактирования поставки
            function editDelivery(lotId) {
                $.modalWindow({
                    loadURL: VIEW_PATH.DETAIL_DELIVERY_STATEMENT_EDIT,
                    loadData: { lotId: lotId },
                    submitURL: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_EDIT_SAVE,
                    onSubmitSuccess: () => {
                        // $deliveryStatement.trigger('click');
                        table.setData();
                        table.selectRow(lotId);
                        showDistribution(lotId);
                    }
                });
            }

            // Отображение распределения поставки в доп. контейнере
            function showDistribution(lotId) {
                $subContent.html('');
                $subBlock.show();
                $.get({
                    url: VIEW_PATH.DETAIL_DELIVERY_STATEMENT_STRUCTURE,
                    data: { lotId: lotId }
                }).done(html => $subContent.html(html));
            }

            // Скрытие области вспомогательного контейнера
            $btnCloseSubBlock.on({
                'click': () => $subBlock.hide()
            });

            // Кнопка добавление изделия
            $btnAddProduct.on({
                'click': () => addProduct(sectionId)
            });
        })
    </script>
</div>