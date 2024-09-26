<div class="ui modal detail_specification_info_excel-import-detail__main">
    <div class="header">
        Информация по выгрузке спецификации из excel
    </div>
    <div class="scrolling content">
        <table class="ui celled table">
            <thead>
                <tr>
                    <th>Номер строки в excel</th>
                    <th>Позиция</th>
                    <th>Наименование</th>
                    <th>Комментарий</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${ignoreList}" var="n">
                    <tr class="negative">
                        <td>${n.rowNumber}</td>
                        <td>${n.component.position}</td>
                        <td>${n.component.name}</td>
                        <td>${n.description}</td>
                    </tr>
                </c:forEach>
                <c:forEach items="${newList}" var="n">
                    <tr class="positive">
                        <td>${n.rowNumber}</td>
                        <td>${n.component.position}</td>
                        <td>${n.component.name}</td>
                        <td>${n.description}</td>
                    </tr>
                </c:forEach>
                <c:forEach items="${existList}" var="e">
                    <tr>
                        <td>${e.rowNumber}</td>
                        <td>${e.component.position}</td>
                        <td>${e.component.name}</td>
                        <td>${e.description}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="actions">
        <button class="ui small button detail_specification_info_excel-import-detail__btn-clear" type="button">
            <i class="blue broom icon"></i>
            <fmt:message key="label.button.clear"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.detail_specification_info_excel-import-detail__main');
        const $parentContent = $('div.detail_specification__content');
        const $btnClear = $('button.detail_specification_info_excel-import-detail__btn-clear');

        $btnClear.on({
            'click': () => $.ajax({
                method: 'DELETE',
                url: '/api/action/prod/product/detail/specification/info/excel-import-detail/clear/' + '${bomId}',
                beforeSend: () => togglePreloader(true),
                complete: () => togglePreloader(false)
            }).done(() => {
                $parentContent.trigger('load-list');
                $modal.modal('hide');
            })
        });
    });
</script>