<div class="list__header">
    <h1 class="list__header_title">Служебные символы</h1>
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

        // Таблица служебных символов
        const table = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/service-symbol/list/load',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Обозначение', field: 'name' },
                {
                    title: 'Описание',
                    field: 'description',
                    variableHeight: true,
                    minWidth: 400,
                    width: 500,
                    formatter: cell => {
                        $(cell.getElement()).css({'white-space': 'pre-wrap'});
                        return cell.getValue();
                    }
                },
                {
                    title: 'Техпроцесс',
                    field: 'technologicalProcess',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue(), { onTrue: 'Да', onFalse: 'Нет' })
                },
                {
                    title: 'Операционная карта',
                    field: 'operationCard',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue(), { onTrue: 'Да', onFalse: 'Нет' })
                },
                {
                    title: 'Маршрутная карта',
                    field: 'routeMap',
                    resizable: false,
                    headerSort: false,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue(), { onTrue: 'Да', onFalse: 'Нет' })
                }
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
                    action: () => editServiceSymbol(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteServiceSymbol(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования служебного символа
        function editServiceSymbol(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/service-symbol/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/service-symbol/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления служебного символа
        function deleteServiceSymbol(id) {
            confirmDialog({
                title: 'Удаление служебного символа',
                message: 'Вы уверены, что хотите удалить служебный символ?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/service-symbol/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления служебного символа
        $btnAdd.on({
            'click': () => editServiceSymbol()
        });
    });
</script>