<div class="list__header">
    <h1 class="list__header_title">Административно-хозяйственные услуги</h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    </div>
</div>
<div class="list__table-container">
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $addDemand = $('i.list__btn-add');

        const table = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/corp/administration-office-demand/list/load',
            headerSort: false,
            layout: 'fitColumns',
            pagination: 'local',
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Дата заявки',
                    field: 'requestDate',
                    formatter: cell => dateTimeStdToString(cell.getValue())
                },
                { title: 'Заявитель', field: 'user' },
                { title: 'Номер комнаты', field: 'roomNumber' },
                { title: 'Описание проблемы', field: 'reason' },
                { title: 'Статус', field: 'statusText' }
            ],
            rowClick: (e, row) => {
                if (row.isSelected()) {
                    table.deselectRow();
                    sessionStorage.removeItem(ssAdministrationOfficeDemand_selectedId);
                } else {
                    table.deselectRow();
                    row.select();
                    sessionStorage.setItem(ssAdministrationOfficeDemand_selectedId, row.getData().id);
                }
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
                sessionStorage.setItem(ssAdministrationOfficeDemand_selectedId, row.getData().id);
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                if (data.status === 'NEW') {
                    menu.push({
                        label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                        action: () => editDemand(data.id)
                    });
                    menu.push({
                        label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                        action: () => deleteDemand(data.id)
                    });
                }
                return menu;
            },
            dataLoaded: () => {
                const ssDemandSelectedId = sessionStorage.getItem(ssAdministrationOfficeDemand_selectedId);
                let row = table.searchRows("id", "=", ssDemandSelectedId)[0];
                if (row !== undefined) {
                    row.pageTo();
                    row.select();
                    row.scrollTo();
                }
            }
        });

        // Добавление заявки
        $addDemand.on({
            'click': () => editDemand()
        });

        // Функция добавления/редактирования заявки
        function editDemand(id) {
            $.modalWindow({
                loadURL: '/api/view/corp/administration-office-demand/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/corp/administration-office-demand/list/edit/save',
                onSubmitSuccess: response => {
                    if (!id) {
                        sessionStorage.setItem(ssAdministrationOfficeDemand_selectedId, response.attributes.addedDemandId);
                    }
                    table.setData();
                }
            });
        }

        // Функция удаления заявки
        function deleteDemand(id) {
            confirmDialog({
                title: 'Удаление заявки',
                message: 'Вы действительно хотите удалить заявку?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/corp/administration-office-demand/list/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        sessionStorage.removeItem(ssAdministrationOfficeDemand_selectedId);
                        table.setData();
                    });
                }
            });
        }
    });
</script>