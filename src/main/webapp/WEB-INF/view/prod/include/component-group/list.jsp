<div class="list__header">
    <h1 class="list__header_title">Группы</h1>
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

        // Таблица назначений
        const table = new Tabulator('div.list__table', {
            selectable: 1,
            layout: 'fitColumns',
            ajaxURL: '/api/action/prod/component-group/list/load',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Номер', field: 'number' },
                { title: 'Наименование', field: 'name' }
            ],
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

        // Функция добавления/редактирования группы
        function editGroup(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/component-group/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/component-group/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления группы
        function deleteGroup(id) {
            confirmDialog({
                title: 'Удаление группы',
                message: 'Вы уверены, что хотите удалить группу?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/component-group/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления группы
        $btnAdd.on({
            'click': () => editGroup()
        });
    });
</script>