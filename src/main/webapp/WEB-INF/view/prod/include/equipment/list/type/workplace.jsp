<div class="field">
    <div class="ui toggle checkbox">
        <input type="checkbox" name="archive" <c:if test="${form.archive}">checked</c:if>>
        <label><fmt:message key="equipment.field.archive"/></label>
    </div>
</div>
<div class="field required">
    <input type="hidden" data-list-form-attribute="equipmentUnitList.id" value="${form.equipmentUnitList[0].id}">
    <label><fmt:message key="equipment.field.areaName"/></label>
    <select id="equipmentUnitList[0].areaId" name="equipmentUnitList[0].areaId">
        <option value=""><fmt:message key="text.notSpecified"/></option>
        <c:forEach items="${productionAreaList}" var="productionArea">
            <option value="${productionArea.id}"
                <c:if test="${form.equipmentUnitList[0].areaId eq productionArea.id}">
                    selected
                </c:if>
            >
                    ${productionArea.formatCode} ${productionArea.name}
            </option>
        </c:forEach>
    </select>
    <div class="ui compact message error" data-field="productionArea"></div>
</div>
<c:if test="${not empty form.id}">
    <div class="field">
        <label><fmt:message key="equipment.field.code"/></label>
        ${form.equipmentUnitList[0].code}
    </div>
</c:if>
<div class="field required">
    <label><fmt:message key="equipment.field.name"/></label>
    <input type="text" name="name" id="name" value="${form.name}" />
    <div class="ui compact message error" data-field="name"></div>
</div>
<div class="field">
    <label><fmt:message key="equipment.field.user"/></label>
    <select class="ui dropdown search label" name="employee.id">
        <option value=""><fmt:message key="text.notSpecified"/></option>
        <c:forEach items="${userList}" var="user">
            <option value="${user.id}"
                <c:if test="${form.employee.id eq user.id}">
                    selected
                </c:if>
            >${user.userOfficialName}</option>
        </c:forEach>
    </select>
</div>
<div class="field">
    <label><fmt:message key="equipment.field.shift"/></label>
    <input type="text" name="shift" id="shift" value="${form.shift}" data-inputmask-regex="[1-9]*" />
    <div class="ui compact message error" data-field="shift"></div>
</div>