<div class="detail_child__header">
    <h1 class="detail_child__header_title">Функциональности операции</h1>
    <div class="list_child__table_buttons">
        <i class="icon add link blue list_child__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="detail_child__table-wrap">
    <div class="detail_child__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const parentId = '${parentId}';
        const entityId = '${entityId}';
        const $btnAdd = $('i.list_child__btn-add');

        const table = new Tabulator('div.detail_child__table', {
            ajaxURL: ACTION_PATH.DETAIL_CHILD_LOAD,
            movableRows: true,
            ajaxRequesting: (url, params) => params.parentId = parentId,
            headerSort: false,
            height: '100%',
            layout: 'fitDataStretch',
            columns: [
                {
                    rowHandle: true,
                    formatter: 'handle',
                    frozen: true,
                    width: 30,
                    minWidth: 30
                },
                TABR_COL_ID,
                { title: 'Символ', field: TABR_FIELD.SYMBOL },
                { title: 'Оборудование', field: TABR_FIELD.EQUIPMENT },
                { title: 'Содержание операции', field: TABR_FIELD.CONTENT },
                { title: 'Режим проведения операции', field: TABR_FIELD.MODE },
                {
                    title: 'Технологическая оснастка',
                    field: TABR_FIELD.TECHNOLOGICAL_TOOL,
                    variableHeight: true,
                    minWidth: 300,
                    formatter: 'textarea'
                },
                { title: 'Материалы', field: TABR_FIELD.OPERATION_MATERIAL },
                { title: 'КОИД', field: TABR_FIELD.KOID },
                { title: 'Кшт', field: TABR_FIELD.KSHT },
                { title: 'Тпз', field: TABR_FIELD.TPZ },
                { title: 'Тшт', field: TABR_FIELD.TSHT }
            ],
            rowContextMenu: row => {
                const data = row.getData();
                const id = data.id;
                const menu = [];
                menu.push({
                    label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                    action: () => editRecord(id)
                });
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => deleteRecord(id)
                });
                return menu;
            },
        });

        // Редактирование/добавление
        function editRecord(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_EDIT,
                loadData: { entityId: entityId, parentId: parentId, id: id },
                submitURL: ACTION_PATH.DETAIL_EDIT_SAVE,
                submitAsJson: true,
                onSubmitSuccess: () => table.setData()
            });
        }

        // Удаление
        function deleteRecord(id) {
            confirmDialog({
                title: 'Удаление операции',
                message: 'Вы действительно хотите удалить операцию?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: ACTION_PATH.DETAIL_DELETE + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }

        // Добавление
        $btnAdd.on({
            'click': () => editRecord()
        });
    });
</script>