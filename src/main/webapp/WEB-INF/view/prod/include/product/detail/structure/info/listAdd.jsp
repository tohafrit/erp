<div class="ui modal detail_structure_info_list-add__main">
    <div class="ui small header">Добавление изделия в состав</div>
    <div class="content">
        <form class="ui form">
            <div class="field inline">
                <label>Изделие</label>
                <span class="ui text">${productName}</span>
            </div>
        </form>
        <i class="icon filter blue link detail_structure_info_list-add__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
        <div class="detail_structure_info_list-add__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <div class="ui small button disabled detail_structure_info_list-add__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const bomId = '${bomId}';
        const $modal = $('div.detail_structure_info_list-add__main');
        const $btnFilter = $('i.detail_structure_info_list-add__btn-filter');
        const $btnSelect = $('div.detail_structure_info_list-add__btn-select');
        const structTabulator = Tabulator.prototype.findTable('div.detail_structure_info__tree-table')[0];
        let filterData = {};

        const table = new Tabulator('div.detail_structure_info_list-add__table', {
            selectable: 1,
            ajaxSorting: true,
            pagination: 'remote',
            layout: 'fitDataFill',
            height: 'calc(100vh * 0.6)',
            ajaxURL: '/api/action/prod/product/detail/structure/info/list-add/list-load',
            ajaxRequesting: (url, params) => {
                params.bomId = bomId;
                params.filterData = JSON.stringify(filterData);
            },
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                { title: 'Условное наименование', field: TABR_FIELD.CONDITIONAL_NAME },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 200,
                    width: 300,
                    headerSort: false,
                    formatter: 'textarea'
                }
            ],
            rowSelected: () => $btnSelect.toggleClass('disabled', !table.getSelectedRows().length),
            rowDblClick: (e, row) => {
                table.deselectRow();
                row.select();
                $btnSelect.trigger('click');
            }
        });

        // Добавление выбранного изделия
        $btnSelect.on({
            'click': () => {
                const data = table.getSelectedData();
                if (data.length === 1) {
                    $.post({
                        url: '/api/action/prod/product/detail/structure/info/list-add/save',
                        data: { bomId: bomId, productId: data[0].id },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        $modal.modal('hide');
                        structTabulator.setData();
                    });
                }
            }
        });

        // Фильтр
        $.modalFilter({
            url: '/api/view/prod/product/detail/structure/info/list-add/filter',
            button: $btnFilter,
            filterData: () => filterData,
            onApply: data => {
                filterData = data;
                table.setData();
            }
        });
    })
</script>