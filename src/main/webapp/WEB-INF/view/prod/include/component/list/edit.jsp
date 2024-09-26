<div class="ui modal">
    <div class="ui small header">
        ${empty form.id ? 'Добавление компонента' : 'Редактирование компонента'}<br>
        ${form.name}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="lockVersion"/>
            <form:hidden path="addAsDesign"/>
            <div class="field">
                <label>Позиция</label>
                <form:input path="position" data-inputmask-regex="[0-9]{0,6}"/>
                <div class="ui compact message error" data-field="position"></div>
            </div>
            <c:if test="${form.purchaseComponent ne null}">
                <div class="field">
                    <label>Замена к закупке</label>
                    <table class="ui mini definition table">
                        <tbody>
                            <tr>
                                <td>Дата</td>
                                <td>${form.purchaseComponentData}</td>
                            </tr>
                            <tr>
                                <td>Позиция</td>
                                <td>${form.purchaseComponent.formattedPosition}</td>
                            </tr>
                            <tr>
                                <td>Наименование</td>
                                <td>${form.purchaseComponent.name}</td>
                            </tr>
                            <tr>
                                <td>Производитель</td>
                                <td>${form.purchaseComponent.producer.name}</td>
                            </tr>
                            <tr>
                                <td>Категория</td>
                                <td>${form.purchaseComponent.category.name}</td>
                            </tr>
                            <tr>
                                <td>Описание</td>
                                <td>${form.purchaseComponent.description}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </c:if>
            <c:if test="${form.substituteComponent ne null}">
                <div class="field">
                    <label>Заместитель</label>
                    <table class="ui mini definition table">
                        <tbody>
                        <tr>
                            <td>Позиция</td>
                            <td>${form.substituteComponent.formattedPosition}</td>
                        </tr>
                        <tr>
                            <td>Наименование</td>
                            <td>${form.substituteComponent.name}</td>
                        </tr>
                        <tr>
                            <td>Производитель</td>
                            <td>${form.substituteComponent.producer.name}</td>
                        </tr>
                        <tr>
                            <td>Категория</td>
                            <td>${form.substituteComponent.category.name}</td>
                        </tr>
                        <tr>
                            <td>Описание</td>
                            <td>${form.substituteComponent.description}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </c:if>
            <div class="field required">
                <label>Наименование по ТС</label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <label>Производитель</label>
                <form:select cssClass="std-select search" path="producer.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${producerList}" var="producer">
                        <form:option value="${producer.id}">${producer.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field required">
                <label>Категория</label>
                <form:select cssClass="std-tree-select list_edit__category-select" path="category.id">
                    <std:treeChosen hierarchyList="${categoryList}" selectedItems="${form.category}"/>
                </form:select>
                <div class="ui compact message error" data-field="category"></div>
            </div>
            <div class="field">
                <label>Описание</label>
                <div class="ui textarea">
                    <form:textarea path="description" rows="3"/>
                </div>
                <div class="ui compact message error" data-field="description"></div>
            </div>
            <div class="list_edit__attribute-content"></div>
            <div class="field">
                <label>Единица измерения</label>
                <form:select cssClass="std-select search" path="okei.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${okeiList}" var="okei">
                        <form:option value="${okei.id}">${okei.symbolNational}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field">
                <label>Назначение</label>
                <form:select cssClass="std-select search" path="purpose.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${purposeList}" var="purpose">
                        <form:option value="${purpose.id}">${purpose.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field">
                <label>Тип установки</label>
                <form:select cssClass="std-select search" path="installation.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${installationList}" var="installation">
                        <form:option value="${installation.id}">${installation.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field">
                <label>Тип</label>
                <form:select cssClass="std-select search" path="kind.id">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${kindList}" var="kind">
                        <form:option value="${kind.id}">${kind.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field">
                <label>Ориентировочная цена</label>
                <form:input path="price" cssClass="list_edit__price-input"/>
            </div>
            <div class="field">
                <label>Срок поставки</label>
                <form:input path="deliveryTime" cssClass="list_edit__delivery-input"/>
            </div>
            <div class="field inline">
                <div class="ui checkbox">
                    <form:checkbox path="processed"/>
                    <label>Обработан</label>
                </div>
            </div>
            <c:if test="${form.id ne null}">
                <div class="field">
                    <label>Дата изменения</label>
                    <javatime:format value="${form.modifiedDatetime}" pattern="dd.MM.yyyy HH:mm:ss"/>
                </div>
            </c:if>
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
        const $attrContent = $('div.list_edit__attribute-content');
        const $select = $('select.list_edit__category-select');

        // Получение техничеких атрибутов
        $select.on({
            'change': e => {
                const categoryId = $(e.currentTarget).find('option:selected').val();
                $attrContent.empty();
                if (categoryId != null) {
                    $.get({
                        url: '/api/view/prod/component/list/edit/attribute',
                        data: {
                            componentId: '${componentId}',
                            categoryId: categoryId
                        },
                        beforeSend: () => togglePreloader(true),
                        complete: () => togglePreloader(false)
                    }).done(html => $attrContent.html(html));
                }
            }
        });
        $select.trigger('change');

        // Маска для ввода чисел с плавающей для цены
        $('input.list_edit__price-input').inputmask('decimal', {
            rightAlign: false,
            placeholder: ''
        });

        $('input.list_edit__delivery-input').inputmask('integer', {
            rightAlign: false,
            placeholder: '',
            allowMinus: false
        })
    })
</script>