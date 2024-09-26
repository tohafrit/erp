<div class="editJustification-dialog" title="<fmt:message key="justification.${form.docType}"/>">
    <form:form method="POST" action="/editJustification" modelAttribute="form">
        <form:hidden path="id"/>
        <form:hidden path="typeId"/>
        <table class="b-full-width">
            <tr>
                <th class="b-table-edit__th"><fmt:message key="justification.name"/></th>
                <td class="b-table-edit__td">
                    <div class="ui fluid input">
                        <form:input path="name" />
                    </div>
                    <div class="name b-error"></div>
                </td>
            </tr>
            <tr>
                <th class="b-table-edit__th"><fmt:message key="justification.date"/></th>
                <td class="b-table-edit__td">
                    <div class="ui fluid input">
                        <form:input cssClass="erp-date" path="date" />
                    </div>
                    <div class="date b-error"></div>
                </td>
            </tr>
            <c:if test="${isCompany}">
                <tr>
                    <th class="b-table-edit__th"><fmt:message key="justification.company"/></th>
                    <td class="b-table-edit__td">
                        <button class="js-btn-add compact ui button b-btn b-btn-add" type="button"></button>
                        <table class="b-table js-company-table">
                            <thead>
                                <tr>
                                    <th class="b-table__th b-text-right" colspan="2">
                                        <form:hidden path="company.id" cssClass="js-company-id"/>
                                        <button class="js-btn-edit ui small button b-btn b-btn-edit" type="button"></button>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <th class="b-table__th"><fmt:message key="company.field.name"/></th>
                                    <td class="b-table__td">${form.company.name}</td>
                                </tr>
                                <tr>
                                    <th class="b-table__th"><fmt:message key="company.field.contactPerson"/></th>
                                    <td class="b-table__td">${form.company.contactPerson}</td>
                                </tr>
                            </tbody>
                        </table>
                        <div class="company b-error"></div>
                    </td>
                </tr>
            </c:if>
            <tr>
                <th class="b-table-edit__th"><fmt:message key="justification.note"/></th>
                <td class="b-table-edit__td">
                    <form:textarea path="note" rows="3"/>
                </td>
            </tr>
            <tr>
                <th class="b-table-edit__th"><fmt:message key="justification.document"/></th>
                <td class="b-table-edit__td">
                    <div class="erp-file-input ui action input">
                        <form:hidden path="fileStorage.id"/>
                        <input type="file" name="file"/>
                        <span>
                            ${form.fileStorage.name}
                            <a target="_blank" href="<c:url value="/download-file/${form.fileStorage.storeHash}"/>"><fmt:message key="text.downloadFile"/></a>
                        </span>
                    </div>
                    <div class="file b-error"></div>
                </td>
            </tr>
            <tr>
                <th>&nbsp;</th><td class="b-table-edit__td b-text-right"><button class="ui small button b-btn b-btn-save" type="submit"><fmt:message key="label.button.save"/></button></td>
            </tr>
        </table>
    </form:form>
</div>

<script>
    $(() => {
        const
            $dialog = $('.editJustification-dialog'),
            $companyTable = $dialog.find('.js-company-table'),
            $buttonAdd = $dialog.find('.js-btn-add'),
            $buttonEdit = $dialog.find('.js-btn-edit');
        let
            companyExists = ${form.company.id ne null};

        $buttonAdd.toggle(!companyExists);
        $companyTable.toggle(companyExists);

        // Редактирование/добавление компании
        $buttonAdd.add($buttonEdit).on({
            'click' : () => {
                $.modalDialog({
                    dialogName : 'company',
                    url : '/justification/searchCompany',
                    dialogWidth: 1300
                });
            }
        });
    });
</script>