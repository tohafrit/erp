<div class="ui modal detail_specification_info_replacement_edit-status__main">
    <div class="ui small header">
        Редактирование статуса<br>
        ${compName}
    </div>
    <div class="content">
        <div class="ui small form">
            <div class="field">
                <label>Статус</label>
                <select class="ui dropdown search std-select detail_specification_info_replacement_edit-status__status-select">
                    <c:forEach items="${statusList}" var="status">
                        <option value="${status}">${status.name}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
    </div>
    <div class="actions">
        <div class="ui small button detail_specification_info_replacement_edit-status__btn-save">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </div>
    </div>
</div>

<script>
    $(() => {
        const id = '${id}';
        const $modal = $('div.detail_specification_info_replacement_edit-status__main');
        const $select = $('div.detail_specification_info_replacement_edit-status__status-select > select');
        const $btnSelect = $('div.detail_specification_info_replacement_edit-status__btn-save');
        const $replacementDatatable = $('div.detail_specification_info_replacement__table');

        $btnSelect.on({
            'click': () => $.post({
                url: '/api/action/prod/product/detail/specification/info/replacement/edit-status/save',
                data: { id: id, status: $select.find(':selected').val() },
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(() => {
                $modal.modal('hide');
                $replacementDatatable.trigger('reload');
            })
        });
    })
</script>