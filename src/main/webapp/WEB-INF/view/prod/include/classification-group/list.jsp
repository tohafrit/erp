<div class="list__header">
    <h1 class="list__header_title">Классификационные группы изделий</h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add-group" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $btnAdd = $('i.list__btn-add-group');

        // Таблица классификационных групп
        const table = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/prod/classification-group/list/load',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Номер', field: TABR_FIELD.NUMBER },
                { title: 'Характеристика', field: TABR_FIELD.CHARACTERISTIC },
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 400,
                    width: 500,
                    formatter: 'textarea'
                }
            ],
            rowClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: component => {
                const menu = [];
                const data = component.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editGroup(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteGroup(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования
        function editGroup(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/classification-group/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/classification-group/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления
        function deleteGroup(id) {
            confirmDialog({
                title: 'Удаление группы',
                message: 'Вы уверены, что хотите удалить группу?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/classification-group/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления
        $btnAdd.on({
            'click': () => editGroup()
        });
    });
</script>