<c:if test="${fn:length(categoryAttributeMap) > 0}">
    <div>
        <h3 class="ui dividing header">Технические характеристики</h3>
        <c:set var="index" value="0"/>
        <c:forEach items="${categoryAttributeMap}" var="categoryAttribute">
            <c:set var="category" value="${categoryAttribute.key}"/>
            <c:set var="attributeList" value="${categoryAttribute.value}"/>
            <h4 class="ui dividing header">${category.name}</h4>
            <c:forEach items="${attributeList}" var="attribute">
                <input type="hidden" name="attributeList[${index}].id" value="${attribute.id}">
                <input type="hidden" name="attributeList[${index}].type" value="${attribute.type}">
                <c:if test="${attribute.type eq 'INPUT'}">
                    <div class="field <c:if test="${attribute.required}">required</c:if>">
                        <label>${attribute.name}</label>
                        <input type="text"
                           name="attributeList[${index}].stringValue"
                           <c:if test="${attribute.disabled}">disabled</c:if>
                           value="${attribute.stringValue}"
                        >
                        <div class="ui compact message error" data-field="attribute${attribute.id}"></div>
                    </div>
                </c:if>
                <c:if test="${attribute.type eq 'CHECKBOX'}">
                    <div class="field inline <c:if test="${attribute.required}">required</c:if>">
                        <div class="ui checkbox">
                            <input type="checkbox"
                               name="attributeList[${index}].boolValue"
                               value="true"
                               <c:if test="${attribute.disabled}">disabled</c:if>
                               <c:if test="${attribute.boolValue}">checked</c:if>
                            >
                            <label>${attribute.name}</label>
                        </div>
                        <div class="ui compact message error" data-field="attribute${attribute.id}"></div>
                    </div>
                </c:if>
                <c:if test="${attribute.type eq 'SELECT'}">
                    <div class="field <c:if test="${attribute.required}">required</c:if>">
                        <label>${attribute.name}</label>
                        <select class="ui dropdown label search list_edit_attribute__select"
                            <c:if test="${attribute.multiple}">
                                name="attributeList[${index}].selectOptionIdList"
                            </c:if>
                            <c:if test="${not attribute.multiple}">
                                name="attributeList[${index}].selectOptionId"
                            </c:if>
                            <c:if test="${attribute.disabled}">disabled</c:if>
                            <c:if test="${attribute.multiple}">multiple</c:if>
                        >
                            <c:if test="${not attribute.multiple}">
                                <option value=""><fmt:message key="text.notSpecified"/></option>
                            </c:if>
                            <c:forEach items="${attribute.selectOptionList}" var="option">
                                <option value="${option.id}"
                                    <c:forEach items="${attribute.selectOptionIdList}" var="selectedId">
                                        <c:if test="${selectedId eq option.id}">selected</c:if>
                                    </c:forEach>
                                >${option.value}</option>
                            </c:forEach>
                        </select>
                        <div class="ui compact message error" data-field="attribute${attribute.id}"></div>
                    </div>
                </c:if>
                <c:set var="index" value="${index + 1}"/>
            </c:forEach>
        </c:forEach>
        <div class="ui divider"></div>
        <script>
            $(() => {
                $('select.list_edit_attribute__select').dropdown({
                    forceSelection: false,
                    fullTextSearch: true,
                    clearable: true
                });
            })
        </script>
    </div>
</c:if>