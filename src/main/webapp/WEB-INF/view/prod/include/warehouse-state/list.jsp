<div class="list__header">
    <h1 class="list__header_title">Состояние склада</h1>
    <div class="list__header_buttons">
        <i class="icon filter link blue list__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <i class="ui icon layer group blue dropdown list__btn-report" title="Отчеты">
            <div class="menu">
                <div class="item">
                    Отчет о текущих остатках на складе
                </div>
                <div class="item">
                    Поступление и отгрузка изделия за период
                </div>
                <div class="item">
                    Сведения об отгрузке за месяц для ПЗ
                </div>
            </div>
        </i>
    </div>
</div>
<div class="list__total-amount-block">
    Всего изделий:
    <span class="ui text"></span>
</div>
<div class="list__tables-block">
    <div class="list__table-block">
        <div class="list__table-wrap">
            <div class="list__table table-sm table-striped"></div>
        </div>
    </div>
    <div class="list__sub-block">
        <i class="close link blue icon list__btn-close-sub-block"></i>
        <div class="list__sub-block-content"></div>
    </div>
</div>

<script>
    $(() => {
        const tableSelector = 'div.list__table';
        const $btnFilter = $('i.list__btn-filter');
        const $btnReport = $('i.list__btn-report');
        const $subBlock = $('div.list__sub-block');
        const $subContent = $('div.list__sub-block-content');
        const $btnCloseSubBlock = $('i.list__btn-close-sub-block');
        const $totalAmountBlock = $('div.list__total-amount-block');
        const $totalAmountSpan = $totalAmountBlock.find('span');
        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        let notInitLoad = false;
        const table = new Tabulator(tableSelector, {
            ajaxURL: ACTION_PATH.LIST_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                if (notInitLoad) {
                    const query = urlQueryFromTableParams(window.location.search, params);
                    sessionStorage.setItem(S_STORAGE.LIST_QUERY, query);
                    page.show(ROUTE.list(query), undefined, false);
                }
                $subBlock.hide();
                notInitLoad = true;
            },
            headerSort: false,
            height: '100%',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Изделие', field: TABR_FIELD.PRODUCT, minWidth: 200 },
                {
                    title: 'Кол-во',
                    field: TABR_FIELD.AMOUNT,
                    width: 80,
                    resizable: false,
                    hozAlign: 'center'
                }
            ],
            dataLoaded: data => $totalAmountSpan.text(data.map(el => el.amount).reduce((a, b) => a + b, 0)),
            rowClick: (e, row) => {
                const data = row.getData();
                table.deselectRow();
                row.select();
                showMatValue(data.id);
            }
        });

        // Отображение данных накладной
        function showMatValue(id) {
            $subContent.html('');
            $subBlock.show();
            setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
            $.get({
                url: VIEW_PATH.LIST_MAT_VALUE,
                data: { id: id }
            }).done(html => $subContent.html(html));
        }

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => {
                $subContent.html('');
                $subBlock.hide();
            }
        });

        $btnReport.dropdown({ action: 'hide' });

        // Генерация отчета о остатках
        $btnReport.find('div.item:eq(0)').on({
            'click': () => window.open('/warehouse-documentation-formed/download?type=' + 32, '_blank')
        });
        // Генерация отчета о поступлении и отгрузке изделия за период
        $btnReport.find('div.item:eq(1)').on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.LIST_REPORT_FIRST
            })
        });
        // Генерация отчета об отгрузке за месяц для ПЗ
        $btnReport.find('div.item:eq(2)').on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.LIST_REPORT_SECOND
            })
        });
        $btnReport.find('div.item:eq(3)').on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.LIST_REPORT_THIRD
            })
        });

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });

        // Обновление таблицы
        tableTimerUpdate({
            selector: tableSelector,
            url: ACTION_PATH.LIST_LOAD,
            filterData: () => filterData
        });
    })
</script>