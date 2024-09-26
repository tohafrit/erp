<div class="detail_specification_info_position__header">
    <h1 class="detail_specification_info_position__header_title">Позиционные обозначения</h1>
</div>
<div class="detail_specification_info_position__table-wrap">
    <div class="detail_specification_info_position__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const bomItemId = '${bomItemId}';
        const isUnit = '${isUnit}' === 'true';
        const $specDatatable = $('div.detail_specification_info__spec-table');

        const datatable = new Tabulator('div.detail_specification_info_position__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/product/detail/specification/info/position/list-load',
            ajaxRequesting: (url, params) => {
                params.bomItemId = bomItemId;
            },
            height: '100%',
            layout: 'fitData',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Количество', field: 'quantity', headerSort: false, visible: !isUnit },
                { title: 'Позиционное обозначение', field: 'designation', headerSort: false, visible: isUnit },
                { title: 'Прошивка', field: 'firmware', headerSort: false, visible: isUnit }
            ],
            rowContextMenu: row => {
                datatable.deselectRow();
                row.select();
                const menu = [];
                const data = row.getData();
                if (isUnit) {
                    menu.push({
                        label: `<i class="copy outline icon blue"></i><fmt:message key="label.menu.edit"/>`,
                        action: () => editPosition(data.id)
                    });
                }
                return menu;
            }
        });

        function editPosition(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/product/detail/specification/info/position/edit',
                loadData: {
                    id: id,
                    bomItemId: bomItemId
                },
                submitURL: '/api/action/prod/product/detail/specification/info/position/edit/save',
                onSubmitSuccess: () => {
                    datatable.setData();
                    $specDatatable.trigger('reloadSelectRow', [bomItemId]);
                }
            });
        }
    })
</script>