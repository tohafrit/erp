<div class="ui modal list_edit__main">
    <div class="header">
        ${empty form.id ? 'Добавление договора' : 'Редактирование договора'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="customerId"/>
            <c:if test="${not empty form.id}">
                <div class="field inline">
                    <label>Номер:</label>
                    ${contractNumber}
                </div>
            </c:if>
            <div class="inline fields">
                <div class="field">
                    <label>Тип заказчика</label>
                </div>
                <div class="field">
                    <div class="ui radio checkbox ${form.classDisable}">
                        <form:radiobutton path="customerTypeId" value="${externalCustomerTypeId}"/>
                        <label>Внешний</label>
                    </div>
                </div>
                <div class="field">
                    <div class="ui radio checkbox ${form.classDisable}">
                        <form:radiobutton path="customerTypeId" value="${internalCustomerTypeId}"/>
                        <label>Внутренний</label>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Тип договора</label>
                <form:select cssClass="ui dropdown label std-select list_edit__select-external ${form.classDisable}" path="contractType">
                    <c:forEach items="${contractTypeExternalList}" var="contractType">
                        <form:option value="${contractType}"><fmt:message key="${contractType.property}"/></form:option>
                    </c:forEach>
                </form:select>
                <form:select class="ui dropdown label std-select list_edit__select-internal ${form.classDisable}" path="contractType">
                    <form:option value="${internalApplication}"><fmt:message key="${internalApplication.property}"/></form:option>
                </form:select>
            </div>
            <div class="inline fields required">
                <label>Заказчик</label>
                <div class="list_edit__buttons">
                    <i class="icon add link blue list_edit__btn-add" title="<fmt:message key="label.button.add"/>"></i>
                </div>
            </div>
            <div class="field">
                <div class="list_edit_selected-customer_table table-sm table-striped"></div>
                <div class="ui compact message error" data-field="customerId"></div>
            </div>
            <div class="field">
                <label>Дата создания</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="createDate"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="createDate"></div>
            </div>
            <div class="field">
                <label>Активный</label>
                <std:trueOrFalse name="archive" value="${form.archiveDate eq null}"/>
            </div>
            <div class="field">
                <label>Идентификатор</label>
                <form:input path="identifier"/>
                <div class="ui compact message error" data-field="identifier"></div>
            </div>
            <div class="field">
                <label>Внешний номер</label>
                <form:input  path="externalNumber"/>
            </div>
            <div class="field required">
                <label>Ведущий</label>
                <form:select cssClass="ui dropdown label search std-select" path="manager.id">
                    <c:forEach items="${userList}" var="user">
                        <form:option value="${user.id}">${user.value}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field">
                <label>Дата передачи в ПЗ</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="sendToClientDate"/>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <form:textarea path="comment" rows="3"/>
            </div>
        </form:form>
    </div>
    <div class="actions">
        <button class="ui small button" type="submit">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const $main = $('div.list_edit__main');
        const $customerType = $main.find('[name="customerTypeId"]');
        const $contractTypeExternalList = $('div.list_edit__select-external');
        const $contractTypeInternalApplication = $('div.list_edit__select-internal');
        const $addBtn = $main.find('i.list_edit__btn-add');
        const $customerId = $main.find('input#customerId');
        const externalCustomerTypeId = '${externalCustomerTypeId}';
        const internalCustomerTypeId = '${internalCustomerTypeId}';
        const isSendToClient = '${isSendToClient}' === 'true'

        $addBtn.toggle(${empty form.customerId});

        $customerType.on({
            'change': e => {
                let typeValue = $(e.currentTarget).val();
                $contractTypeInternalApplication.toggle(typeValue === internalCustomerTypeId);
                $contractTypeExternalList.toggle(typeValue === externalCustomerTypeId);
            }
        }).filter(':checked').trigger('change');

        // Функция добавления/редактирования заказчика
        function editCustomer(id) {
            $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT_CUSTOMER,
                loadData: { id: id }
            });
        }

        // Редактирование/добавление заказчика
        $addBtn.on({
            'click' : () => editCustomer()
        });

        new Tabulator('div.list_edit_selected-customer_table', {
            ajaxURL: ACTION_PATH.LIST_EDIT_LOAD,
            ajaxRequesting: (url, params) => {
                params.customerId = $customerId.val();
            },
            selectable: 1,
            headerSort: false,
            layout: 'fitColumns',
            columns: [
                { title: 'Название', field: TABR_FIELD.NAME },
                { title: 'Адрес', field: TABR_FIELD.ADDRESS }
            ],
            rowContextMenu: component => {
                const menu = [];
                const id = component.getData().id;
                if (!isSendToClient) {
                    menu.push({
                        label:`<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                        action: () => editCustomer(id)
                    });
                    return menu;
                }
            }
        });
    });
</script>