<div class="list__header">
    <h1 class="list__header_title">Краткие технические харарактеристики</h1>
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

        // Таблица технических характеристик
        const table = new Tabulator('div.list__table', {
            selectable: 1,
            ajaxURL: '/api/action/prod/product-type/list/load',
            resizableColumns: false,
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: 'Наименование', field: 'name' },
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
                    action: () => editType(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteType(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования технической характеристики
        function editType(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/product-type/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/product-type/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления технической характеристики
        function deleteType(id) {
            confirmDialog({
                title: 'Удаление технической характеристики',
                message: 'Вы уверены, что хотите удалить техническую характеристику?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/product-type/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления технической характеристики
        $btnAdd.on({
            'click': () => editType()
        });
    });
</script>