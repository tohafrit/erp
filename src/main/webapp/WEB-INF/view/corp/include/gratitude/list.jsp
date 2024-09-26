<div class="list__header">
    <h1 class="list__header_title">Благодарности</h1>
    <div class="list__header_buttons">
        <i class="icon add link blue list__btn-add" title="Добавить"></i>
    </div>
</div>
<div class="list__table-container">
    <div class="list__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const $addGratitude = $('i.list__btn-add');

        const table = new Tabulator('div.list__table', {
            ajaxURL: '/api/action/corp/gratitude/list/load',
            layout: 'fitColumns',
            pagination: 'local',
            height: '100%',
            columns: [
                {
                    title: '#',
                    resizable: false,
                    headerSort: false,
                    frozen: true,
                    width: 40,
                    formatter: 'rownum'
                },
                {
                    title: 'Дата',
                    field: 'date',
                    width: 100,
                    formatter: 'stdDate',
                    sorter: (a, b) => sortDate(a, b)
                },
                { title: 'Компания', field: 'company' },
                {
                    title: 'Благодарность',
                    field: 'fileUrlHash',
                    headerSort: false,
                    formatter: cell => {
                        if (cell.getValue() != null) {
                            return '<a href="/download-file/' + cell.getValue() + '"><fmt:message key="text.downloadFile"/></a>';
                        }
                    }
                }
            ],
            rowContextMenu: row => {
                let id = row.getData().id;
                return [
                    {
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: () => editGratitude(id)
                    },
                    {
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: () => deleteGratitude(id)
                    }
                ];
            }
        });

        // Добавление благодарности
        $addGratitude.on({
            'click': () => editGratitude()
        });

        // Функция добавления/редактирования благодарности
        function editGratitude(id) {
            $.modalWindow({
                loadURL: '/api/view/corp/gratitude/list/edit',
                loadData: { id: id },
                submitURL: '/api/action/corp/gratitude/list/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления благодарности
        function deleteGratitude(id) {
            confirmDialog({
                title: 'Удаление благодарности',
                message: 'Вы действительно хотите удалить благодарность?',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/corp/gratitude/list/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setData());
                }
            });
        }
    });
</script>