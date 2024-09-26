<div class="admin_detail__header-container">
    <h1 class="admin_detail__header_title">Детали заявки</h1>
    <div class="admin_detail__header_buttons">
        <i class="icon check link blue admin_detail__btn-status" title="Изменить статус"></i>
    </div>
</div>
<div class="admin_detail__table-wrap">
    <div class="admin_detail__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const entityId = '${entityId}';
        const $buttons = $('div.admin_detail__header_buttons');
        const $changeStatus = $('i.admin_detail__btn-status');
        const mainTabulator = Tabulator.prototype.findTable('div.admin__table')[0];

        const table = new Tabulator('div.admin_detail__table', {
            selectable: false,
            layout: 'fitColumns',
            ajaxURL: '/api/action/corp/administration-office-demand/admin/detail/load',
            ajaxRequesting: (url, params) => {
                params.id = entityId;
            },
            ajaxSorting: false,
            height: '100%',
            columns: [
                {
                    title: '#',
                    resizable: false,
                    headerSort: false,
                    frozen: true,
                    width: 70,
                    formatter: 'rownum'
                },
                { title: 'Исполнитель', field: 'executor' },
                {
                    title: 'Время изменения',
                    field: 'updatedOn',
                    formatter: cell => dateTimeStdToString(cell.getValue())
                },
                { title: 'Заметка', field: 'note' },
                {
                    title: 'Статус',
                    field: 'statusText',
                    formatter: cell => {
                        const status = cell.getRow().getData().status;
                        let color = '#faa';
                        if (status === 'IN_PROGRESS') color = '#ffa';
                        if (status === 'FINISHED') color = '#afa';
                        $(cell.getElement()).css({'background-color': color});
                        return cell.getValue();
                    }
                }
            ],
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                if (data.changeable) {
                    menu.push({
                        label: `<i class="edit icon blue"></i>Уточнить статус`,
                        action: () => editStep(data.id)
                    });
                    menu.push({
                        label: `<i class="trash alternate outline icon blue"></i>Удалить статус`,
                        action: () => deleteStep(data.id)
                    });
                }
                return menu;
            },
            dataLoaded: data => {
                $buttons.toggle(data[0].status !== 'FINISHED');
            }
        });

        // Изменение статуса
        $changeStatus.on({
            'click': () => editStep()
        });

        // Функция добавления или редактирования статуса
        function editStep(stepId) {
            $.modalWindow({
                loadURL: '/api/view/corp/administration-office-demand/admin/detail/set-status',
                loadData: {
                    id: entityId,
                    stepId: stepId
                }
            });
        }

        // Функция удаления статуса
        function deleteStep(id) {
            confirmDialog({
                title: 'Удаление статуса',
                message: 'Вы действительно хотите удалить статус?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/corp/administration-office-demand/admin/detail/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        sessionStorage.removeItem(ssBank_selectedId);
                        mainTabulator.setPage(mainTabulator.getPage());
                        table.setData();
                    });
                }
            });
        }
    })
</script>