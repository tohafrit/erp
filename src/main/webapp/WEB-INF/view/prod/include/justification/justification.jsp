<div class="justification-dialog" title="<fmt:message key="justification.title"/>">
    <table class="erp-justification-dt hide-excel-btn hide-print-btn display compact">
        <thead>
            <tr>
                <th></th>
                <th><fmt:message key="justification.name"/></th>
                <th><fmt:message key="justification.date"/></th>
                <th><fmt:message key="justification.note"/></th>
                <c:if test="${isCompany}">
                    <th><fmt:message key="justification.company"/></th>
                </c:if>
                <th><fmt:message key="justification.document"/></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${justificationList}" var="justification" varStatus="key">
                <tr data-id="${justification.id}">
                    <td>${key.count}</td>
                    <td>${justification.name}</td>
                    <td><javatime:format value="${justification.date}" pattern="dd.MM.yyyy"/></td>
                    <td>${justification.note}</td>
                    <c:if test="${isCompany}">
                        <td>${justification.company.name}</td>
                    </c:if>
                    <td>
                        <c:if test="${not empty justification.file}">
                            <a target="_blank" href="<c:url value="/download-file/${justification.file.storeHash}"/>"><fmt:message key="text.downloadFile"/></a>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<script>
    $(() => {
        const
            datatableSelector = '.erp-justification-dt',
            contextMenuSelector = datatableSelector + ' > tbody > tr:has(td:not(.dataTables_empty))';

        $(datatableSelector).DataTable({
            searching: false,
            initComplete: function (settings, json) {
                $.fn.dataTable.defaults.initComplete(settings, json);
                let $table = $(settings.nTable),
                    $addButton = $table.closest('.dataTables_wrapper').find('.dt-add-button').closest('button');

                // Добавление обоснования
                $addButton.off().on({
                    'click' : () => {
                        $.modalDialog({
                            dialogName: 'editJustification',
                            url: '/editJustification',
                            parameters: {
                                docType: 'add',
                                type: '${type}'
                            }
                        });
                    }
                });

                // Контекстное меню таблицы
                $.contextMenu('destroy', contextMenuSelector);
                $.contextMenu({
                    selector: contextMenuSelector,
                    build: () => {
                        return {
                            items: {
                                edit: {
                                    name: 'Редактировать',
                                    icon: 'edit',
                                    callback: (itemKey, opt) => {
                                        $.modalDialog({
                                            dialogName: 'editJustification',
                                            url: '/editJustification',
                                            parameters: {
                                                docType: 'edit',
                                                entityId: opt.$trigger.data('id'),
                                                type: '${type}'
                                            }
                                        });
                                    }
                                },
                                delete: {
                                    name: 'Удалить',
                                    icon: 'delete',
                                    callback: (itemKey, opt) => {
                                        if (confirm('Подтвердите удаление')) {
                                            $.post('/deleteJustification', { entityId: opt.$trigger.data('id'), type: '${type}' }, (data) => {
                                                window.location.href = data.redirectHref;
                                            });
                                        }
                                    }
                                }
                            }
                        };
                    }
                });
            }
        });
    });
</script>