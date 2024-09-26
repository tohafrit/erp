<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/prod/view/equipment-unit-event-type.css"/>">

<div class="root__header">
    <h1 class="root__header_title"><fmt:message key="equipmentUnitEventType.title"/></h1>
    <div class="root__header_buttons">
        <i class="icon add link blue root__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="root__table table-sm table-striped"></div>

<script>
    $(() => {
        const $addBtn = $('i.root__btn-add');
        const table = new Tabulator('div.root__table', {
            ajaxURL: '/api/action/prod/equipment-unit-event-type/load',
            headerSort: false,
            layout: 'fitColumns',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: '<fmt:message key="equipmentUnitEventType.field.name"/>', field: 'name' }
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
                        action: (e, row) => editType(row.getData().id)
                    },
                    {
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: (e, row) => deleteType(row.getData().id)
                    }
                ];
            }
        });

        // Добавление типа события
        $addBtn.on({
            'click': () => editType()
        });

        // Функция добавления/редактирования типа события
        function editType(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/equipment-unit-event-type/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/equipment-unit-event-type/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления типа события
        function deleteType(id) {
            confirmDialog({
                title: '<fmt:message key="equipmentUnitEventType.delete.title"/>',
                message: '<fmt:message key="equipmentUnitEventType.delete.confirm"/>',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/equipment-unit-event-type/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }
    });
</script>