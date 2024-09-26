<div class="ui modal admin_detail_set-status__main">
    <div class="header">
        Изменить статус заявки
    </div>
    <div class="content">
        <div class="ui form admin_detail_set-status__form">
            <div class="two fields">
                <div class="field">
                    <label>
                        Исполнитель
                    </label>
                    <select class="ui dropdown search std-select admin_detail_set-status__executor" name="executor">
                        <c:forEach items="${ahoWorkersList}" var="user">
                            <option value="${user.id}">${user.userOfficialName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>
                        Статус заявки
                    </label>
                    <select class="ui dropdown std-select admin_detail_set-status__status" name="status">
                        <c:forEach items="${statusList}" var="status">
                            <option value="${status}">${status.property}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="field">
                <label>
                    Заметка
                </label>
                <div class="ui fluid input">
                    <textarea name="note" class="admin_detail_set-status__note" rows="3"></textarea>
                </div>
            </div>
        </div>
    </div>
    <div class="actions">
        <button class="ui small button admin_detail_set-status__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </button>
    </div>
</div>



<script>
    $(() => {
        const $modal = $('div.admin_detail_set-status__main');
        const $btnSelect = $('button.admin_detail_set-status__btn-select');
        const $executor = $('div.admin_detail_set-status__executor');
        const $status = $('div.admin_detail_set-status__status');
        const $note = $('textarea.admin_detail_set-status__note');
        const detailTabulator = Tabulator.prototype.findTable('div.admin_detail__table')[0];
        const mainTabulator = Tabulator.prototype.findTable('div.admin__table')[0];

        // Кнопка выбора
        $btnSelect.on({
            'click': () => {
                let executorId = $executor.find('option:selected').val();
                let status = $status.find('option:selected').val();
                let note = $note.val();
                $.post({
                    url: '/api/action/corp/administration-office-demand/admin/detail/set-status/save',
                    data: {
                        demandId: ${demandId},
                        executorId: executorId,
                        status: status,
                        note: note,
                        stepId: '${stepId}'
                    }
                }).done(() => {
                    detailTabulator.setData();
                    mainTabulator.setPage(mainTabulator.getPage());
                    $modal.modal('hide');
                });
            }
        });
    });
</script>