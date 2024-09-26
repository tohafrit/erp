<div class="ui modal">
    <div class="header">
        <fmt:message key="equipmentUnitEvent.${empty form.id ? 'adding' : 'editing'}"/>
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field">
                <label><fmt:message key="equipmentUnitEvent.field.name"/></label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="equipmentUnitEvent.field.eventType.name"/></label>
                <form:select cssClass="ui dropdown std-select search" path="equipmentUnitEventType.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${equipmentUnitEventTypeList}" var="equipmentUnitEventType">
                        <form:option value="${equipmentUnitEventType.id}">${equipmentUnitEventType.name}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="equipmentUnitEventType"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="equipmentUnitEvent.field.equipmentUnit"/></label>
                <input class="list_edit__unit-table_input" type="hidden" name="equipmentUnit.id" value="${form.equipmentUnit.id}">
                <div class="ui icon small buttons">
                    <div class="ui button basic list_edit__unit-table_div-add" title="Добавить">
                        <i class="add icon"></i>
                    </div>
                </div>
                <table class="ui tiny padded table list_edit__unit-table">
                    <tr>
                        <th class="right aligned" colspan="2">
                            <i class="pencil alternate icon link blue list_edit__unit-table_div-edit" title="Редактировать"></i>
                        </th>
                    </tr>
                    <tr>
                        <th><fmt:message key="equipmentUnitEvent.field.equipment.name"/></th>
                        <td>${form.equipmentUnit.equipment.name}</td>
                    </tr>
                    <tr>
                        <th><fmt:message key="equipmentUnitEvent.field.producer.name"/></th>
                        <td>${form.equipmentUnit.equipment.producer.name}</td>
                    </tr>
                    <tr>
                        <th><fmt:message key="equipmentUnitEvent.field.equipment.model"/></th>
                        <td>${form.equipmentUnit.equipment.model}</td>
                    </tr>
                    <tr>
                        <th><fmt:message key="equipmentUnitEvent.field.productionArea.name"/></th>
                        <td>${form.equipmentUnit.lastEquipmentUnitProductionArea.productionArea.name}</td>
                    </tr>
                    <tr>
                        <th><fmt:message key="equipmentUnitEvent.field.equipmentUnit.serialNumber"/></th>
                        <td>${form.equipmentUnit.serialNumber}</td>
                    </tr>
                    <tr>
                        <th><fmt:message key="equipmentUnitEvent.field.equipmentUnit.inventoryNumber"/></th>
                        <td>${form.equipmentUnit.inventoryNumber}</td>
                    </tr>
                </table>
                <div class="ui compact message error" data-field="equipmentUnit"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="equipmentUnitEvent.field.eventOn"/></label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="dateEventOn" autocomplete="off"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="dateEventOn"></div>
            </div>
            <div class="field">
                <label><fmt:message key="equipmentUnitEvent.field.commentary"/></label>
                <form:textarea path="commentary" rows="3"/>
                <div class="ui compact message error" data-field="commentary"></div>
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
        const $unitTable = $('table.list_edit__unit-table');
        const $buttonAdd = $('div.list_edit__unit-table_div-add');
        const $buttonEdit = $('i.list_edit__unit-table_div-edit');
        let unitExists = ${not empty form.equipmentUnit};

        $buttonAdd.toggle(!unitExists);
        $unitTable.toggle(unitExists);

        $buttonAdd.add($buttonEdit).on({
            'click' : () => $.modalWindow({
                loadURL: '/api/view/prod/equipment-unit-event/list/edit/equipment-unit'
            })
        });
    });
</script>