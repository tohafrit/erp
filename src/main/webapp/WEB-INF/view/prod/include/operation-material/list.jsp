<div class="list__header">
    <h1 class="list__header_title">Материалы операций</h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $btnAdd = $('i.list__btn-add');

        // Таблица материалов
        const table = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/prod/operation-material/list/load',
            resizableColumns: false,
            pagination: 'local',
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: 'name' },
                { title: 'Операции', field: 'operation' }
            ],
            rowClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editOperation(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteOperation(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования материала
        function editOperation(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/operation-material/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/operation-material/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления материала
        function deleteOperation(id) {
            confirmDialog({
                title: 'Удаление материала',
                message: 'Вы уверены, что хотите удалить материал?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/operation-material/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления материала
        $btnAdd.on({
            'click': () => editOperation()
        });
    });
</script>