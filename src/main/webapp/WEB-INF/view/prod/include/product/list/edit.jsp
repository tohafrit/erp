<div class="ui modal">
    <div class="ui small header">
        <fmt:message key="product.list.edit.title.${empty form.id ? 'add' : 'edit'}"/><br>
        ${form.conditionalName}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="lockVersion"/>
            <div class="field">
                <label><fmt:message key="product.field.conditionalName"/></label>
                <form:input path="conditionalName"/>
                <div class="ui compact message error" data-field="conditionalName"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="product.field.techSpecName"/></label>
                <form:input path="techSpecName"/>
                <div class="ui compact message error" data-field="techSpecName"></div>
            </div>
            <div class="field inline">
                <div class="ui checkbox">
                    <form:checkbox path="archive"/>
                    <label><fmt:message key="product.field.archive"/></label>
                </div>
            </div>
            <div class="field inline">
                <div class="ui checkbox">
                    <form:checkbox path="serial"/>
                    <label>Серийное</label>
                </div>
            </div>
            <div class="field">
                <label><fmt:message key="product.field.type"/></label>
                <form:select cssClass="ui dropdown std-select" path="type.id">
                    <c:forEach items="${productTypeList}" var="productType">
                        <form:option value="${productType.id}">${productType.name}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="type"></div>
            </div>
            <div class="field">
                <label><fmt:message key="product.field.decimalNumber"/></label>
                <form:input path="decimalNumber"/>
                <div class="ui compact message error" data-field="decimalNumber"></div>
            </div>
            <div class="field">
                <label><fmt:message key="product.field.letter"/></label>
                <form:select cssClass="ui dropdown search std-select" path="letter.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${letterList}" var="letter">
                        <form:option value="${letter.id}">${letter.name}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="letter"></div>
            </div>
            <div class="field">
                <label><fmt:message key="product.field.position"/></label>
                <form:input path="position" data-inputmask-regex="[0-9]{0,6}"/>
                <div class="ui compact message error" data-field="position"></div>
            </div>
            <div class="field">
                <label><fmt:message key="product.field.lead"/></label>
                <form:select cssClass="ui dropdown search std-select" path="lead.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${leadList}" var="lead">
                        <form:option value="${lead.id}">${lead.userOfficialName}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="lead"></div>
            </div>
            <div class="field">
                <label><fmt:message key="product.field.classificationGroup"/></label>
                <form:select cssClass="ui dropdown search std-select" path="classificationGroup.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${classificationGroupList}" var="classificationGroup">
                        <form:option value="${classificationGroup.id}">${classificationGroup.number} ${classificationGroup.characteristic}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="classificationGroup"></div>
            </div>
            <div class="field">
                <label><fmt:message key="product.field.comment"/></label>
                <div class="ui textarea">
                    <form:textarea path="comment" rows="3"/>
                </div>
                <div class="ui compact message error" data-field="comment"></div>
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