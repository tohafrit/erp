<div class="ui modal">
    <div class="ui small header">Редактирование изделия в составе</div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="lockVersion"/>
            <div class="field inline">
                <label>Изделие</label>
                <span class="ui text">${productName}</span>
            </div>
            <div class="field inline">
                <label>Входящее изделие</label>
                <span class="ui text">${subProductName}</span>
            </div>
            <div class="field required">
                <label>Количество</label>
                <form:input path="quantity" cssClass="detail_structure_info_list-edit__position"/>
                <div class="ui compact message error" data-field="quantity"></div>
            </div>
            <div class="field">
                <label>Изготовитель</label>
                <form:select cssClass="ui dropdown search std-select" path="producer.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${producerList}" var="producer">
                        <form:option value="${producer.id}">${producer.name}</form:option>
                    </c:forEach>
                </form:select>
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
        $('input.detail_structure_info_list-edit__position').inputmask({
            placeholder: '',
            regex: '[0-9]{0,3}'
        });
    })
</script>