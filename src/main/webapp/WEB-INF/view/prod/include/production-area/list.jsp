<div class="list__header">
    <h1 class="list__header_title">Участки</h1>
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

        // Таблица участков
        const datatable = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/production-area/list/load',
            layout: 'fitColumns',
            resizableColumns: false,
            maxHeight: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Код', field: 'code' },
                { title: 'Наименование', field: 'name' },
                {
                    title: 'Технологическая',
                    field: 'technological',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue(), { onTrue: 'Да', onFalse: 'Нет' })
                }
            ],
            rowContext: (e, row) => {
                datatable.deselectRow();
                row.select();
            },
            rowContextMenu: component => {
                const menu = [];
                const data = component.getData();
                if (data.hasEmployee) {
                    menu.push({
                        label: `<i class="user tie icon blue"></i>Закрепленные сотрудники`,
                        action: () => productionAreaInfo(data.id, 'employee')
                    });
                }
                if (data.hasStoreroom) {
                    menu.push({
                        label: `<i class="store alternate icon blue"></i></i>Производственные кладовки`,
                        action: () => productionAreaInfo(data.id, 'storeroom')
                    });
                }
                if (data.hasDefect) {
                    menu.push({
                        label: `<i class="exclamation icon blue"></i>Производственные дефекты`,
                        action: () => productionAreaInfo(data.id, 'defect')
                    });
                }
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editProductionArea(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteProductionArea(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования участка
        function editProductionArea(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/production-area/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/production-area/list/edit/save',
                onSubmitSuccess: () => datatable.setData()
            });
        }

        // Функция удаления участка
        function deleteProductionArea(id) {
            confirmDialog({
                title: 'Удаление участка',
                message: 'Вы уверены, что хотите удалить участок?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/production-area/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => datatable.setData())
            });
        }

        // Ссылка на дополнительную информацию
        function productionAreaInfo(id, name) {
            $.modalWindow({
                loadURL: '/api/view/prod/production-area/list/info',
                loadData: { id: id, name: name }
            });
        }

        // Кнопка добавления участка
        $btnAdd.on({
            'click': () => editProductionArea()
        });
    });
</script>