<div class="ui modal">
    <div class="header">
        Командировки сотрудников
    </div>
    <div class="scrolling content">
        <div class="list_users__table table-sm table-striped"></div>
    </div>
    <div class="actions">
        <button class="ui small button list_users__confirm-btn">
            <i class="check icon blue"></i>
            Принять
        </button>
        <button class="ui small button list_users__reject-btn">
            <i class="times icon blue"></i>
            Отклонить
        </button>
    </div>
</div>

<script>
    $(() => {
        const $confirmBtn = $('button.list_users__confirm-btn');
        const $rejectBtn = $('button.list_users__reject-btn');

        const table = new Tabulator('div.list_users__table', {
            selectable: true,
            ajaxURL: '/api/action/corp/trip/list/users/load',
            layout: 'fitColumns',
            pagination: 'local',
            paginationSize: 30,
            paginationSizeSelector: [30, 40, 50],
            columns: [
                {
                    title: '#',
                    resizable: false,
                    headerSort: false,
                    frozen: true,
                    width: 70,
                    formatter: 'rownum'
                },
                { title: 'Сотрудник', field: 'employee' },
                { title: 'Причина', field: 'name' },
                {
                    title: 'Дата',
                    field: 'date',
                    hozAlign: 'center',
                    formatter: cell => dateStdToString(cell.getValue())
                },
                {
                    title: 'Дата окончания',
                    field: 'dateTo',
                    hozAlign: 'center',
                    formatter: cell => dateStdToString(cell.getValue())
                },
                {
                    title: 'Время с',
                    field: 'timeFrom',
                    hozAlign: 'center',
                    formatter: cell => timeStdToString(cell.getValue())
                },
                {
                    title: 'Время по',
                    field: 'timeTo',
                    hozAlign: 'center',
                    formatter: cell => timeStdToString(cell.getValue())
                },
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
            ]
        });

        // Функция принятия/отклонения командировки
        function setStatus(status) {
            let selectedRows = table.getSelectedRows();
            let tripIdList = selectedRows.map(row => row.getData().id);
            $.post({
                url: '/api/action/corp/trip/list/users/set-status',
                data: {
                    tripIdList: tripIdList.join(','),
                    status: status
                },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(() => table.setData());
        }

        // Кнопка одобрения командировки
        $confirmBtn.on({
            'click': () => setStatus('CONFIRMED')
        });

        // Кнопка отклонения командировки
        $rejectBtn.on({
            'click': () => setStatus('REJECTED')
        });
    });
</script>