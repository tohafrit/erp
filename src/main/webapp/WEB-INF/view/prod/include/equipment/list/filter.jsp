<div class="list_filter__main">
    <form:form modelAttribute="equipmentListFilterForm" cssClass="ui tiny form secondary segment list_filter__form">
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
                <label>
                    <fmt:message key="equipment.field.name"/>
                    <i class="times link blue icon list_filter__btn-clear"></i>
                </label>
                <form:input path="name"/>
            </div>
            <div class="column field">
                <label>
                    <fmt:message key="equipment.field.type"/>
                </label>
                <form:select cssClass="ui dropdown label" path="equipmentTypeId">
                    <c:forEach items="${equipmentTypeList}" var="equipmentType">
                        <form:option value="${equipmentType.id}">${equipmentType.description}</form:option>
                    </c:forEach>
                </form:select>
            </div>
        </div>
        <div class="ui three column grid">
            <div class="column field">
                <label>
                    <fmt:message key="equipment.field.archive"/>
                    <i class="times link blue icon list_filter__btn-clear"></i>
                </label>
                <form:select cssClass="ui dropdown search label" path="archive">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <form:option value="false"><fmt:message key="text.no"/></form:option>
                    <form:option value="true"><fmt:message key="text.yes"/></form:option>
                </form:select>
            </div>
            <div class="column field">
                <label>
                    <fmt:message key="equipment.field.areaName"/>
                    <i class="times link blue icon list_filter__btn-clear"></i>
                </label>
                <form:select cssClass="ui dropdown search label" path="productionAreaIdList" multiple="true">
                    <c:forEach items="${productionAreaList}" var="productionArea">
                        <form:option value="${productionArea.id}">${productionArea.formatCode} ${productionArea.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>
        </div>
    </form:form>

    <script>
        $(() => {
            const $main = $('div.list_filter__main');
            const $btnClearList = $('i.list_filter__btn-clear');
            const $btnClearListAll = $('div.list_filter__btn-clear-all');
            const $inputPosition = $('input.list_filter__field-position');

            $inputPosition.inputmask({
                placeholder: '',
                regex: '[0-9]{0,6}'
            });

            $main.find('select').dropdown({
                fullTextSearch: true,
                maxSelections: 5
            });

            $btnClearList.on({
                'click': function() {
                    const $field = $(this).closest('div.field').find('input[type="text"], select');
                    if ($field.is('input')) {
                        $field.val('');
                    } else if ($field.is('select')) {
                        $field.dropdown('clear');
                    }
                }
            });

            $btnClearListAll.on({
                 'click': () => $btnClearList.trigger('click')
            });
        });
    </script>
</div>