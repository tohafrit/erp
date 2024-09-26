<div class="company-dialog" title="<fmt:message key="justification.searchCompany"/>">
    <div class="b-common-margin20">
        <table class="erp-company-datatable hide-add-btn hide-excel-btn hide-print-btn display compact cell-border">
            <thead>
                <tr>
                    <th></th>
                    <th data-visible="false"></th>
                    <th><fmt:message key="company.field.name"/></th>
                    <th><fmt:message key="company.field.shortName"/></th>
                    <th><fmt:message key="company.field.fullName"/></th>
                    <th><fmt:message key="company.field.chiefName"/></th>
                    <th><fmt:message key="company.field.chiefPosition"/></th>
                    <th><fmt:message key="company.field.phoneNumber"/></th>
                    <th><fmt:message key="company.field.contactPerson"/></th>
                    <th><fmt:message key="company.field.location"/></th>
                    <th><fmt:message key="company.field.inn"/></th>
                    <th><fmt:message key="company.field.kpp"/></th>
                    <th><fmt:message key="company.field.ogrn"/></th>
                    <th><fmt:message key="company.field.inspectorName"/></th>
                    <th><fmt:message key="company.field.inspectorHead"/></th>
                    <th><fmt:message key="company.field.note"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${companyList}" var="company" varStatus="status">
                    <tr>
                        <td>${status.count}</td>
                        <td>${company.id}</td>
                        <td>${company.name}</td>
                        <td>${company.shortName}</td>
                        <td>${company.fullName}</td>
                        <td>${company.chiefName}</td>
                        <td>${company.chiefPosition}</td>
                        <td>${company.phoneNumber}</td>
                        <td>${company.contactPerson}</td>
                        <td>${company.location}</td>
                        <td>${company.inn}</td>
                        <td>${company.kpp}</td>
                        <td>${company.ogrn}</td>
                        <td>${company.inspectorName}</td>
                        <td>${company.inspectorHead}</td>
                        <td>${company.note}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="b-common-margin20 b-common-fl-right">
        <button class="ui small button b-btn b-btn-select js-select-btn" type="button"><fmt:message key="label.button.select"/></button>
    </div>
</div>

<script>
    $(() => {
        const
            $dialog = $('.company-dialog'),
            $companyTable = $dialog.find('.erp-company-datatable'),
            $datatable = $companyTable.DataTable(),
            $selectBtn = $dialog.find('.js-select-btn'),

            $justificationDialog = $('.editJustification-dialog'),
            $companyId = $justificationDialog.find('.js-company-id'),
            $justificationCompanyTable = $justificationDialog.find('.js-company-table'),
            $justificationCompanyBtnAdd =  $justificationDialog.find('.js-btn-add');

        // Выбор компании
        $selectBtn.on({
            'click': () => {
                let data = $datatable.row('.selected').data(),
                    $justificationCompanyTableBody = $justificationCompanyTable.find('tbody');
                if (!$.isEmptyObject(data)) {
                    $companyId.val(data[1]);
                    $justificationCompanyTableBody.find('tr:eq(0) td').html(data[2]);
                    $justificationCompanyTableBody.find('tr:eq(1) td').html(data[14]);
                    $justificationCompanyTable.show();
                    $justificationCompanyBtnAdd.hide();
                    $dialog.dialog('close');
                }
            }
        });
    });
</script>