<div class="ui modal decipherment_search_version__modal">
    <div class="header">Поиск версии ЗС</div>
    <div class="content">
        <div class="decipherment_search_version__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled decipherment_search_version__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        // Текущий диалог
        const $modal = $('div.decipherment_search_version__modal');
        const $btnSelect = $('div.decipherment_search_version__btn-select');
        const mode = '${mode}';
        const productId = '${productId}';
        const selectedId = '${selectedId}';
        const productNumber = '${productNumber}';
        // Диалог расшифровки
        const $edModal = $('div.decipherment_edit__modal');
        const $edVersionTable = $edModal.find('.js-version-table');
        const $edVersionBtnAdd = $edModal.find('.js-btn-add-version');
        const $edVersionHidden = $edModal.find('.js-version');
        const $compositionTree = $edModal.find('.js-composition-tree');
        // Таблица
        const table = new Tabulator('div.decipherment_search_version__table', {
            selectable: 1,
            ajaxURL: '/decipherment/edit/search-version/load',
            ajaxRequesting: (url, params) => {
                params.productId = productId;
            },
            height: '100%',
            layout: 'fitColumns',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Версия', field: TABR_FIELD.VERSION, headerSort: false, maxWidth: 100 },
                {
                    title: 'Запуски',
                    headerSort: false,
                    field: TABR_FIELD.LAUNCHES,
                    variableHeight: true,
                    maxWidth: 800,
                    minWidth: 100,
                    formatter: 'textarea'
                }
            ],
            rowClick: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
            },
            dataLoaded: () => {
                if (selectedId) table.selectRow(selectedId);
                $btnSelect.toggleClass('disabled', !table.getSelectedRows().length)
            }
        });
        // Выбор версии
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    const id = data[0].id;
                    const version = data[0].version;
                    if (mode === 'compositionVersion') {
                        const $versionSelect = $compositionTree.find('li[data-full-hierarchy-number=' + productNumber + ']').find('.js-version-select');
                        $versionSelect.data('value', id);
                        $versionSelect.html(version);
                        $versionSelect.trigger('change');
                    } else if (mode === 'version') {
                        $edVersionHidden.val(id);
                        $edVersionTable.find('tr:eq(1)').find('td').html(version);
                        $edVersionTable.show();
                        $edVersionBtnAdd.hide();
                    }
                    $modal.modal('hide');
                }
            }
        });
    });
</script>