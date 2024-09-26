<div class="general-section">
    <h1 class="b-heading">
        <fmt:message key="contract.title">
            <fmt:param value="${contractSection.fullNumber}"/>
        </fmt:message>
    </h1>

    <div class="ui tiny icon buttons b-common-margin10">
        <button title="<fmt:message key="label.button.add"/>" class="ui button js-add-addition" type="button"><i class="plus blue icon"></i></button>
        <button title="<fmt:message key="label.button.edit"/>" class="ui button js-general-edit" type="button"><i class="blue edit icon"></i></button>
    </div>

    <table class="b-table js-general">
        <tr>
            <th class="b-table__th"><fmt:message key="contract.customerName"/></th>
            <td class="b-table__td">${contractSection.contract.customer.name}</td>
        </tr>
        <tr>
            <th class="b-table__th"><fmt:message key="contract.date"/></th>
            <td class="b-table__td"><javatime:format value="${contractSection.date}" pattern="dd.MM.yyyy"/></td>
        </tr>
        <tr>
            <th class="b-table__th"><fmt:message key="contract.externalNumber"/></th>
            <td class="b-table__td">${contractSection.externalName}</td>
        </tr>
        <tr>
            <th class="b-table__th"><fmt:message key="contract.active"/></th>
            <td class="b-table__td js-toggle-status">
                <std:boolean value="${contractSection.archiveDate eq null}" image="true" tbound="contract.active" fbound="contract.archive"/>
            </td>
        </tr>
        <tr>
            <th class="b-table__th"><fmt:message key="contract.comment"/></th>
            <td class="b-table__td">${contractSection.note}</td>
        </tr>
        <tr>
            <th class="b-table__th"><fmt:message key="contract.pzCopyDate"/></th>
            <td class="b-table__td">
                <c:choose>
                    <c:when test="${contractSection.pzCopyDate ne null}">
                        <javatime:format value="${contractSection.pzCopyDate}" pattern="dd.MM.yyy"/>
                    </c:when>
                    <c:otherwise>
                        <i class="fas fa-times-circle fa-lg b-color-red" title="<fmt:message key="contract.pzCopyDate.not.transferred"/>"></i>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th class="b-table__th"><fmt:message key="contract.identifier"/></th>
            <td class="b-table__td">${contractSection.identifier}</td>
        </tr>
        <tr>
            <th class="b-table__th"><fmt:message key="contract.lead.contract"/></th>
            <td class="b-table__td">${contractSection.contract.manager.userOfficialName}</td>
        </tr>
    </table>
</div>

<script>
    $(() => {
        const
            $general = $('.general-section'),
            /*$table = $general.find('.js-general'),
            $status = $table.find('.js-toggle-status > i'),*/
            $editBtn = $general.find('.js-general-edit'),
            sectionId = '${contractSection.id}';

        $editBtn.toggle(${contractSection.archiveDate eq null});

        /*
        TODO решено редактировать архивность из общей формы редактирования
        $status.addClass('b-cursor-pointer');

        $status.on({
            'click' : () => {
                let isActive = $status.hasClass('b-color-green');
                if (confirm(isActive ? 'Сделать договор архивным?' : 'Сделать договор активным?')) {
                    $.post('/toggleStatus', { sectionId: sectionId }, () => {
                        $status.toggleClass('fa-check-circle b-color-green fa-times-circle b-color-red');
                        $status.attr('title', isActive ? 'Договор добавлен в архив. Изменения не возможны' : 'Активный');
                        $editBtn.toggle(!isActive);
                    }).then(() => {});
                }
            }
        });*/

        // Редактирование договора
        $editBtn.on({
            'click': () => {
                $.modalDialog({
                    dialogName: 'contract',
                    url: '/editContract',
                    parameters: {
                        docType: 'edit',
                        entityId: sectionId
                    }
                });
            }
        });
    });
</script>