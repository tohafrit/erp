<div class="list__header">
    <h1 class="list__header_title"><fmt:message key="producer.title"/></h1>
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

        // Таблица производителей
        const table = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/prod/producer/list/load',
            layout: 'fitColumns',
            pagination: 'local',
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: '<fmt:message key="producer.result.name"/>', field: 'name' }
            ],
            rowClick: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: producer => {
                const menu = [];
                const data = producer.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editProducer(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteProducer(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования группы
        function editProducer(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/producer/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/producer/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления группы
        function deleteProducer(id) {
            confirmDialog({
                title: 'Удаление производителя',
                message: 'Вы уверены, что хотите удалить производителя?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/producer/list/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setData())
            });
        }

        // Кнопка добавления группы
        $btnAdd.on({
            'click': () => editProducer()
        });
    });
</script>