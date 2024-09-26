<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/prod/view/component-appointment.css"/>">

<div class="root__header">
    <h1 class="root__header_title"><fmt:message key="componentAppointment.title"/></h1>
    <div class="root__header_buttons">
        <div class="ui icon small buttons">
            <div class="ui button basic root__btn-add" title="Добавить">
                <i class="add icon"></i>
            </div>
        </div>
    </div>
</div>
<div class="root__table-container">
    <div class="root__table"></div>
</div>

<script>
    $(() => {
        const $addComponentAppointment = $('div.root__btn-add');

        const table = new Tabulator('div.root__table', {
            ajaxURL: '/api/action/prod/component-appointment/load',
            headerSort: false,
            layout: 'fitColumns',
            pagination: 'local',
            paginationSize: 30,
            paginationSizeSelector: [30, 40, 50],
            height: '100%',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                { title: '<fmt:message key="componentAppointment.table.field.name"/>', field: 'name' },
                { title: '<fmt:message key="componentAppointment.table.field.comment"/>', field: 'comment' }
            ],
            rowContextMenu: () => {
                return [
                    {
                        label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                        action: (e, row) => editComponentAppointment(row.getData().id)
                    },
                    {
                        label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                        action: (e, row) => deleteComponentAppointment(row.getData().id)
                    }
                ];
            }
        });

        // Добавление назначения компонента
        $addComponentAppointment.on({
            'click': () => editComponentAppointment()
        });

        // Функция добавления назначения компонента
        function editComponentAppointment(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/component-appointment/edit',
                loadData: { id: id },
                submitURL: '/api/action/prod/component-appointment/edit/save',
                onSubmitSuccess: () => table.setData()
            });
        }

        // Функция удаления назначения компонента
        function deleteComponentAppointment(id) {
            confirmDialog({
                title: '<fmt:message key="componentAppointment.delete.title"/>',
                message: '<fmt:message key="componentAppointment.delete.confirm"/>',
                onAccept: () => {
                    $.ajax({
                        method: 'DELETE',
                        url: '/api/action/prod/component-appointment/delete/' + id,
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(() => table.setData());
                }
            });
        }
    });
</script>