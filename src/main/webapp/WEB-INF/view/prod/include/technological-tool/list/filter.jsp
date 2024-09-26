<div class="list_filter__main">
    <form:form modelAttribute="technologicalToolListFilterForm" cssClass="ui tiny form secondary segment list_filter__form">
        <div class="field">
            <div class="ui icon small buttons">
                <div class="ui button list_filter__btn-search" title="Поиск">
                    <i class="search blue icon"></i>
                </div>
                <div class="ui button list_filter__btn-clear" title="Очистить фильтр">
                    <i class="times blue icon"></i>
                </div>
            </div>
        </div>
        <div class="ui three column grid">
            <div class="column field">
                <label>Тип</label>
                <form:select cssClass="ui dropdown std-select" path="type">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${technologicalToolTypeList}" var="type">
                        <form:option value="${type}"><fmt:message key="${type.property}"/></form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="column field">
                <label>Наименование</label>
                <form:input path="name"/>
            </div>
            <div class="column field">
                <label>Участок</label>
                <form:select cssClass="ui dropdown search std-select" path="productionAreaIdList" multiple="multiple">
                    <c:forEach items="${productionAreaList}" var="area">
                        <form:option value="${area.id}">${area.formatCode} ${area.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>
        </div>
    </form:form>

    <script>
        $(() => {
            const $btnClear = $('div.list_filter__btn-clear');
            $btnClear.on({
                'click': () => formClear('form.list_filter__form')
            });
        })
    </script>
</div>