<div class="list__header">
    <h1 class="list__header_title">Типы установки</h1>
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
        const datatable = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/component-installation-type/list/load',
            layout: 'fitColumns',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: 'name' }
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
                    action: () => editInstallationType(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteInstallationType(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования типа установки
        function editInstallationType(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/component-installation-type/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/component-installation-type/list/edit/save',
                onSubmitSuccess: () => datatable.setData()
            });
        }

        // Функция удаления типа установки
        function deleteInstallationType(id) {
            confirmDialog({
                title: 'Удаление типа установки',
                message: 'Вы уверены, что хотите удалить тип установки?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/component-installation-type/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => datatable.setData())
            });
        }

        // Кнопка добавления типа назначения
        $btnAdd.on({
            'click': () => editInstallationType()
        });
    });
</script>