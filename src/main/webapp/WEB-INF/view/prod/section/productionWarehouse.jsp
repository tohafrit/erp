<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/prod/view/production-warehouse.css"/>">

<div class="root__header">
    <h1 class="root__header_title"><fmt:message key="productionWarehouse.title"/></h1>
    <div class="root__header_buttons">
        <i class="icon add link blue root__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="root__table-container">
    <div class="root__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $addBtn = $('i.root__btn-add');
        const table = new Tabulator('div.root__table', {
            ajaxURL: '/api/action/prod/production-warehouse/load',
            layout: 'fitDataStretch',
            pagination: 'local',
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: '<fmt:message key="productionWarehouse.code"/>', field: 'code' },
                { title: '<fmt:message key="productionWarehouse.name"/>', field: 'name' }
            ],
            rowClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: () => {
                return [
                    {
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: (e, row) => editWarehouse(row.getData().id)
                    },
                    {
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: (e, row) => deleteWarehouse(row.getData().id)
                    }
                ];
            }
        });

        // Добавление склада
        $addBtn.on({
            'click': () => editWarehouse()
        });

        // Функция добавления/редактирования склада
        function editWarehouse(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/production-warehouse/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/production-warehouse/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления склада
        function deleteWarehouse(id) {
            confirmDialog({
                title: '<fmt:message key="productionWarehouse.delete.title"/>',
                message: '<fmt:message key="productionWarehouse.delete.confirm"/>',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/production-warehouse/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }
    });
</script>