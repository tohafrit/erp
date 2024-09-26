<div class="list_users__header-container">
    <h1 class="list_users__header_title">Пользователи</h1>
</div>
<div class="list_users__table-wrap">
    <div class="list_users__table"></div>
</div>

<script>
    $(() => {
        const entityId = '${entityId}';
        const datatable = new Tabulator('div.list_users__table', {
            selectable: false,
            layout: 'fitColumns',
            pagination: 'local',
            paginationSize: 10,
            paginationSizeSelector: [10, 20, 30],
            ajaxURL: '/api/action/prod/printer/list/users/load',
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
                    formatter: cell => {
                        $(cell.getElement()).addClass('row-number-cell');
                        return (datatable.getPageSize() * (datatable.getPage() - 1)) + cell.getRow().getPosition() + 1;
                    }
                },
                { title: 'Имя', field: 'firstName' },
                { title: 'Отчество', field: 'middleName' },
                { title: 'Фамилия', field: 'lastName' }
            ]
        });
    })
</script>