<div class="list_filter__main">
    <form:form modelAttribute="componentListFilterForm" cssClass="ui tiny form secondary segment list_filter__form">
        <div class="field">
            <div class="ui icon small buttons">
                <div class="ui button list_filter__btn-search" title="Поиск">
                    <i class="search blue icon"></i>
                </div>
                <div class="ui button list_filter__btn-clear-all" title="Очистить фильтр">
                    <i class="times blue icon"></i>
                </div>
            </div>
        </div>
        <div class="ui three column grid">
            <div class="column field">
                <label>Наименование по ТС</label>
                <div class="ui input std-div-input-search icon">
                    <form:input path="name"/>
                </div>
            </div>
            <div class="column field">
                <label>Позиция</label>
                <div class="ui input std-div-input-search icon">
                    <form:input path="position" cssClass="list_filter__field-position"/>
                </div>
            </div>
            <div class="column field">
                <label>Категория</label>
                <form:select cssClass="ui dropdown search std-select" path="categoryIdList" multiple="multiple">
                    <c:forEach items="${categoryList}" var="category">
                        <form:option value="${category.id}">${category.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="column field">
                <label>Описание</label>
                <div class="ui input std-div-input-search icon">
                    <form:input path="description"/>
                </div>
            </div>
            <div class="column field">
                <label>Производитель</label>
                <form:select cssClass="ui dropdown search std-select" path="producerIdList" multiple="multiple">
                    <c:forEach items="${producerList}" var="producer">
                        <form:option value="${producer.id}">${producer.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <c:if test="${isNew}">
                <div class="column field">
                    <label>Запуск</label>
                    <form:select cssClass="ui dropdown search std-select" path="launchId">
                        <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                        <c:forEach items="${launchList}" var="launch">
                            <option value="${launch.id}">${launch.numberInYear}</option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="column field">
                    <label>Изделие</label>
                    <div class="ui input std-div-input-search icon">
                        <form:input path="product"/>
                    </div>
                </div>
            </c:if>
        </div>
    </form:form>

    <script>
        $(() => {
            $('input.list_filter__field-position').inputmask({
                placeholder: '',
                regex: '[0-9]{0,6}'
            });

            $('div.list_filter__btn-clear-all').on({
                'click': () => formClear('form.list_filter__form')
            });
        })
    </script>
</div>