<div class="ui modal list_edit_letter__main">
    <div class="header">Выбор пункта письма</div>
    <div class="content">
        <div class="list_edit_letter__header_buttons">
            <i class="icon filter link blue list_edit_letter__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        </div>
        <div class="list_edit_letter__letter-table-block">
            <div class="list_edit_letter__letter-table table-sm table-striped"></div>
        </div>
        <div class="list_edit_letter__lot_group-table-block">
            <i class="close link blue icon list_edit_letter__btn-close-sub-block"></i>
            <div class="list_edit_letter__lot_group-table table-sm table-striped"></div>
        </div>
    </div>
    <div class="actions">
        <button class="ui small disabled button list_edit_letter__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_letter__main');
        const $btnFilter = $('i.list_edit_letter__btn-filter');
        const $btnCloseSubBlock = $('i.list_edit_letter__btn-close-sub-block');
        const $btnSelect = $('button.list_edit_letter__btn-select');

        const $subBlock = $('div.list_edit_letter__lot_group-table-block');
        const $subContent = $('div.list_edit_letter__lot_group-table');

        // Параметры фильтра таблицы
        const tableData = tableDataFromUrlQuery(window.location.search);
        let filterData = tableData.filterData;

        // Фильтр
        $.modalFilter({
            url: VIEW_PATH.LIST_EDIT_LETTER_FILTER,
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });

        // Таблица писем на производство
        const table = new Tabulator('div.list_edit_letter__letter-table', {
            pagination: 'remote',
            paginationInitialPage: tableData.page,
            paginationSize: tableData.size,
            ajaxURL: ACTION_PATH.LIST_EDIT_LETTER_LOAD,
            ajaxRequesting: (url, params) => {
                params.filterData = JSON.stringify(filterData);
                $subBlock.hide();
            },
            ajaxSorting: true,
            initialSort: tableData.sort.length ? tableData.sort : [{ column: TABR_FIELD.ID, dir: SORT_DIR_DESC }],
            selectable: 1,
            maxHeight: '450px',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер', field: TABR_FIELD.NUMBER },
                {
                    title: 'Дата создания',
                    field: TABR_FIELD.CREATE_DATE,
                    hozAlign: 'center',
                    formatter: 'stdDate'
                },
                {
                    title: 'Договор',
                    field: TABR_FIELD.CONTRACT_NUMBER,
                    headerSort: false,
                    variableHeight: true,
                    minWidth: 200,
                    width: 500,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                }
            ],
            rowClick: (e, row) => {
                const data = row.getData();
                table.deselectRow();
                row.select();
                showLetterInfo(data.id);
            }
        });

        // Отображение информации о письме (пункты письма)
        function showLetterInfo(id) {
            $subContent.html('');
            $subBlock.show();
            setTimeout(() => table.scrollToRow(id, 'middle', false), 200);
            $.get({
                url: VIEW_PATH.LIST_EDIT_LETTER_INFO,
                data: { id: id }
            }).done(html => $subContent.html(html));
        }

        // Скрытие области вспомогательного контейнера
        $btnCloseSubBlock.on({
            'click': () => {
                const specTabulator = Tabulator.prototype.findTable('div.list_edit_letter_info__table')[0];
                specTabulator.deselectRow();
                $btnSelect.toggleClass('disabled', !specTabulator.getSelectedRows().length)
                $subBlock.hide();
            }
        });

        // Resize вспомогательного контейнера
        $subBlock.resizable({
            autoHide: true,
            handles: 'n',
            ghost: true,
            stop: () => $subBlock.css({
                'width': '100%',
                'top': 0
            })
        });
    });
</script>