<div class="ui modal list_edit_conformity-statement__main">
    <div class="header">Заявление о соответствии</div>
    <div class="content ui small form">
        <div class="field">
            <label>Номер</label>
            <input type="text" name="number" value="${number}" readonly/>
        </div>
        <div class="field required">
            <label>Дата создания</label>
            <div class="ui calendar">
                <div class="ui input left icon">
                    <i class="calendar icon"></i>
                    <input type="text" class="std-date" name="createDate" value="${createDate}"/>
                </div>
            </div>
        </div>
        <div class="field required">
            <label>Срок действия</label>
            <div class="ui calendar">
                <div class="ui input left icon">
                    <i class="calendar icon"></i>
                    <input type="text" class="std-date" name="validityDate" value="${validityDate}"/>
                </div>
            </div>
        </div>
        <div class="field required">
            <label>Дата передачи</label>
            <div class="ui calendar">
                <div class="ui input left icon">
                    <i class="calendar icon"></i>
                    <input type="text" class="std-date" name="transferDate" <c:if test="${isFormJson eq false}">value="${transferDate}"</c:if>/>
                </div>
            </div>
        </div>
        <div class="field required">
            <label>Подписал</label>
            <select class="ui dropdown label search std-select" name="managerId">
                <c:forEach items="${userList}" var="user">
                    <option value="${user.id}" <c:if test="${user.id eq managerId}">selected</c:if>>${user.value}</option>
                </c:forEach>
            </select>
        </div>
    </div>
    <div class="actions">
        <button class="ui small button list_edit_conformity-statement__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const $modal = $('div.list_edit_conformity-statement__main');
        const $editModal = $('div.list_edit__main');
        const $selectBtn = $modal.find('button.list_edit_conformity-statement__btn-select');
        //
        const $createDate = $modal.find('input[name="createDate"]');
        const $validityDate = $modal.find('input[name="validityDate"]');
        const $transferDate = $modal.find('input[name="transferDate"]');
        const $number = $modal.find('input[name="number"]');
        const $conformityStatement = $editModal.find('input[name="conformityStatement"]');
        const $btnAddConformityStatement = $editModal.find('i.list_edit__btn-add-conformity-statement');
        const $managerId = $editModal.find('input[name="managerId"]');
        //
        const specTabulator = Tabulator.prototype.findTable('div.list_edit_conformity-statement__table')[0];

        $selectBtn.on({
            'click': () => {
                const data = [];
                data.push({
                    conformityStatementNumber: $number.val(),
                    conformityStatementCreateDate: $createDate.val(),
                    conformityStatementValidity: $validityDate.val(),
                    conformityStatementTransferDate: $transferDate.val(),
                    manager: $modal.find('select option:selected').text(),
                    managerId: $modal.find('select option:selected').val()
                });
                $managerId.val($modal.find('select option:selected').val());
                specTabulator.setData(data);
                $conformityStatement.val(JSON.stringify(data));
                $btnAddConformityStatement.hide();
                $modal.modal('hide');
            }
        });
    })
</script>