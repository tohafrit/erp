<div class="list__header">
    <h1 class="list__header_title">Типы</h1>
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

        // Таблица типов
        const table = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/component-kind/list/load',
            layout: 'fitColumns',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: 'name' }
            ],
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editKind(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteKind(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования типа
        function editKind(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/component-kind/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/component-kind/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления типа
        function deleteKind(id) {
            confirmDialog({
                title: 'Удаление типа',
                message: 'Вы уверены, что хотите удалить тип компонента?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/component-kind/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления типа
        $btnAdd.on({
            'click': () => editKind()
        });
    });
</script>