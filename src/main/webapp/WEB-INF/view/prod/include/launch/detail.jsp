<div class="detail__container">
    <div class="ui button basic detail__btn-launch-list" title="Вернуться к запускам">
        <i class="long arrow alternate left icon"></i>
        Запуски
    </div>
    <div class="detail__title">
        <h3>Запуск ${launchName}</h3>
    </div>
    <div class="detail__filter">
        <div class="ui input small icon std-div-input-search detail__product-name-input">
            <input placeholder="Наименование изделия">
        </div>
        <div class="ui checkbox detail__type-dropdown">
            <select class="ui dropdown tiny">
                <option value="0">Все</option>
                <option value="1">Претенденты</option>
                <option value="2">Запускаемые</option>
            </select>
        </div>
    </div>
    <div class="detail__tables-block">
        <div class="detail__table table-sm table-striped"></div>
        <div class="detail__sub-block">
            <i class="close link blue icon detail__btn-close-sub-block"></i>
            <div class="detail__sub-block-content"></div>
        </div>
    </div>

    <script>
        $(() => {
            const id = '${id}';
            const $productNameInput = $('div.detail__product-name-input > input');
            const $typeDropdown = $('div.detail__type-dropdown > select');
            const $btnLaunchList = $('div.detail__btn-launch-list');
            const $subBlock = $('div.detail__sub-block');
            const $subContent = $('div.detail__sub-block-content');
            const $btnCloseSubBlock = $('i.detail__btn-close-sub-block');
            const tableData = tableDataFromUrlQuery(window.location.search);
            let filterData = tableData.filterData;
            if (filterData && filterData.productName) $productNameInput.val(filterData.productName);
            if (filterData && filterData.type) {
                $typeDropdown.val(filterData.type);
                $typeDropdown.trigger('clearIconStateRefresh');
            }

            // Возврат к списку запусков
            $btnLaunchList.on({
                'click': () => page(ROUTE.list())
            });

            let notInitLoad = false;
            const table = new Tabulator('div.detail__table', {
                pagination: 'remote',
                paginationInitialPage: tableData.page,
                paginationSize: tableData.size,
                initialSort: tableData.sort.length ? tableData.sort : false,
                ajaxURL: ACTION_PATH.DETAIL_LOAD,
                ajaxRequesting: (url, params) => {
                    params.launchId = id;
                    params.filterData = JSON.stringify(filterData);
                    if (notInitLoad) {
                        const query = urlQueryFromTableParams(window.location.search, params);
                        page.show(ROUTE.detail(id, query), undefined, false);
                    }
                    $subBlock.hide();
                    notInitLoad = true;
                },
                headerSort: false,
                ajaxSorting: true,
                height: '100%',
                layout: 'fitDataFill',
                columns: [
                    TABR_COL_REMOTE_ROW_NUM,
                    TABR_COL_ID,
                    { title: 'Изделие', field: TABR_FIELD.PRODUCT_NAME, headerSort: true },
                    { title: 'Версия', field: TABR_FIELD.VERSION, minWidth: 80, resizable: false, hozAlign: 'center' },
                    { title: 'Остаток заделов предыдущих запусков по договору', field: TABR_FIELD.RESIDUE_RESERVE_CONTRACT, hozAlign: 'center' },
                    { title: 'Остаток заделов предыдущих запусков', field: TABR_FIELD.RESIDUE_RESERVE, hozAlign: 'center' },
                    { title: 'В составе заделов предыдущих запусков', field: TABR_FIELD.IN_STRUCT_RESERVE, hozAlign: 'center' },
                    { title: 'Претенденты', field: TABR_FIELD.PRETENDERS, hozAlign: 'center' },
                    { title: 'По договору', field: TABR_FIELD.FOR_CONTRACT, hozAlign: 'center' },
                    { title: 'Задел по договору', field: TABR_FIELD.RESERVE_CONTRACT, hozAlign: 'center' },
                    { title: 'Задел', field: TABR_FIELD.RESERVE, hozAlign: 'center' },
                    { title: 'Итого', field: TABR_FIELD.TOTAL, hozAlign: 'center' },
                    { title: 'Запускается в составе других изделий', field: TABR_FIELD.LAUNCH_IN_STRUCT_OTHER, hozAlign: 'center' },
                    { title: 'Итого к запуску', field: TABR_FIELD.TOTAL_BEFORE_USED_RESERVE, hozAlign: 'center' },
                    { title: 'Использовано заделов предыдущих запусков по договору', field: TABR_FIELD.USED_RESERVE_CONTRACT, hozAlign: 'center' },
                    { title: 'В составе использованных заделов других изделий предыдущих запусков', field: TABR_FIELD.IN_STRUCT_USED_RESERVE, hozAlign: 'center' },
                    { title: 'Использовано заделов предыдущих запусков для сборки других изделий', field: TABR_FIELD.USED_RESERVE_ASSEMBLE, hozAlign: 'center' },
                    { title: 'Итого использовано заделов предыдущих запусков', field: TABR_FIELD.TOTAL_USED_RESERVE, hozAlign: 'center' },
                    { title: 'Итого к запуску', field: TABR_FIELD.TOTAL_AFTER_USED_RESERVE, hozAlign: 'center' }
                ],
                rowClick: (e, row) => {
                    const data = row.getData();
                    const id = data.id;
                    table.deselectRow();
                    row.select();
                    showSub(id);
                },
                rowContext: (e, row) => {
                    table.deselectRow();
                    row.select();
                },
                rowContextMenu: row => {
                    const menu = [];
                    const data = row.getData();
                    return menu;
                }
            });

            // Скрытие области доп. контейнера
            $btnCloseSubBlock.on({
                'click': () => $subBlock.hide()
            });

            // Отображение доп. контейнера
            function showSub(id) {
                $subContent.html('');
                $subBlock.show();
                setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
            }

            // Поиск по изделию
            let productNameTimer;
            $productNameInput.on({
                'input': () => productNameSearch(),
                'change': () => productNameSearch()
            });
            function productNameSearch() {
                clearTimeout(productNameTimer);
                productNameTimer = setTimeout(() => {
                    filterData.productName = $productNameInput.val();
                    table.setPage(1);
                }, 600);
            }

            // Выбор типа изделий
            $typeDropdown.dropdown({
                clearable: false,
                onChange: value => {
                    clearTimeout(productNameTimer);
                    filterData.productName = $productNameInput.val();
                    filterData.type = value;
                    table.setPage(1);
                }
            });
        })
    </script>
</div>