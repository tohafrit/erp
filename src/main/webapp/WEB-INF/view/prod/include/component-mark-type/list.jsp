<div class="list__header">
    <h1 class="list__header_title">Перечень комплектующих изделий, подлежащих входному контролю</h1>
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

        // Таблица обозначений
        const datatable = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/component-mark-type/list/load',
            layout: 'fitColumns',
            resizableColumns: false,
            maxHeight: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Обозначение', field: 'mark' }
            ],
            rowContext: (e, row) => {
                datatable.deselectRow();
                row.select();
            },
            rowContextMenu: component => {
                const menu = [];
                const data = component.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editType(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteType(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования обозначения
        function editType(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/component-mark-type/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/component-mark-type/list/edit/save',
                onSubmitSuccess: () => datatable.setData()
            });
        }

        // Функция удаления обозначения
        function deleteType(id) {
            confirmDialog({
                title: 'Удаление обозначения',
                message: 'Вы уверены, что хотите удалить обозначение?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/component-mark-type/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => datatable.setData())
            });
        }

        // Кнопка добавления обозначения
        $btnAdd.on({
            'click': () => editType()
        });
    });
</script>