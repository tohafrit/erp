<div class="list__header">
    <h1 class="list__header_title">Командировки</h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add" title="<fmt:message key="label.button.add"/>"></i>
        <c:if test="${isEmployeeTrip}">
            <i class="icon users link blue list__btn-users" title="Просмотреть командировки пользователей"></i>
        </c:if>
    </div>
</div>
<div class="list__table-block">
    <div class="list__table-main-block">
        <div class="list__table-wrap">
            <div class="list__table table-sm table-striped"></div>
        </div>
    </div>
</div>

<script>
    $(() => {
        const $btnAdd = $('i.list__btn-add');
        const $btnUsers = $('i.list__btn-users');

        // Таблица командировок
        const table = new Tabulator('div.list__table', {
            resizableColumns: false,
            layout: 'fitColumns',
            ajaxURL: '/api/action/corp/trip/list/load',
            pagination: 'local',
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
                { title: 'Причина', field: 'name' },
                {
                    title: 'Дата',
                    field: 'date',
                    hozAlign: 'center',
                    formatter: cell => dateStdToString(cell.getValue()),
                    sorter: (a, b) => sortDate(a, b)
                },
                {
                    title: 'Дата окончания',
                    field: 'dateTo',
                    hozAlign: 'center',
                    formatter: cell => dateStdToString(cell.getValue()),
                    sorter: (a, b) => sortDate(a, b)
                },
                {
                    title: 'Время с',
                    field: 'timeFrom',
                    hozAlign: 'center',
                    formatter: cell => timeStdToString(cell.getValue()),
                    sorter: (a, b) => sortDate(a, b)
                },
                {
                    title: 'Время по',
                    field: 'timeTo',
                    hozAlign: 'center',
                    formatter: cell => timeStdToString(cell.getValue()),
                    sorter: (a, b) => sortDate(a, b)
                },
                { title: 'Кто отпустил', field: 'chief' },
                {
                    title: 'Статус',
                    field: 'status',
                    resizable: false,
                    headerSort: false,
                    width: 100,
                    hozAlign: 'center',
                    formatter: cell => booleanToLight(cell.getValue())
                },
                { title: 'Тип', field: 'type' }
            ],
            rowClick: (e, row) => {
                if (row.isSelected()) {
                    table.deselectRow();
                    sessionStorage.removeItem(ssTrip_selectedId);
                } else {
                    table.deselectRow();
                    row.select();
                    sessionStorage.setItem(ssTrip_selectedId, row.getData().id);
                }
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
                sessionStorage.setItem(ssTrip_selectedId, row.getData().id);
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editTrip(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteTrip(data.id)
                });
                return menu;
            },
            dataLoaded: () => {
                const ssTripSelectedId = sessionStorage.getItem(ssTrip_selectedId);
                let row = table.searchRows("id", "=", ssTripSelectedId)[0];
                if (row !== undefined) {
                    row.pageTo();
                    row.select();
                    row.scrollTo();
                }
            }
        });

        // Функция добавления/редактирования командировки
        function editTrip(id) {
            $.modalWindow({
                loadURL: '/api/view/corp/trip/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/corp/trip/list/edit/save',
                onSubmitSuccess: response => {
                    if (!id) {
                        sessionStorage.setItem(ssTrip_selectedId, response.attributes.addedTripId);
                    }
                    table.setData();
                }
            });
        }

        // Функция удаления командировки
        function deleteTrip(id) {
            confirmDialog({
                title: 'Удаление командировки',
                message: 'Вы уверены, что хотите удалить командировку?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/corp/trip/list/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => {
                        sessionStorage.removeItem(ssTrip_selectedId);
                        table.setData();
                    });
                }
            });
        }

        // Кнопка добавления командировки
        $btnAdd.on({
            'click': () => editTrip()
        });

        // Кнопка командировок подчинённых
        $btnUsers.on({
            'click': () => {
                $.modalWindow({
                    loadURL: '/api/view/corp/trip/list/users'
                });
            }
        });
    });
</script>