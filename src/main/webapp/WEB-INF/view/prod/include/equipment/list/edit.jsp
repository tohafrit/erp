<div class="ui modal list_edit__main">
    <div class="header">
        <fmt:message key="equipment.list.edit.title.${empty form.id ? 'add' : 'edit'}"/>
    </div>
    <div class="content dialog-list-form-container">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field">
                <label><fmt:message key="equipment.field.type"/></label>
                <form:select cssClass="ui dropdown search label std-select list_edit__equipment-type ${not empty form.id ? 'disabled' : ''}" path="equipmentType.id">
                    <c:forEach items="${equipmentTypeList}" var="equipmentType">
                        <form:option value="${equipmentType.id}">${equipmentType.description}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="list_edit__page"></div>
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
        const $equipmentDialog = $('div.list_edit__main');
        const $equipmentType = $('div.list_edit__equipment-type');
        const $attributeContainer = $('div.list_edit__page');
        const buttonRemoveUnit = '.js-remove-unit';
        const buttonEditLink = '.js-btn-link-edit';
        const equipmentId = '${form.id}';
        const row =
            `<tr>
                <td>
                    <input type="hidden" data-list-form-attribute="equipmentUnitList.id">
                    <select data-list-form-attribute="equipmentUnitList.areaId">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${productionAreaList}" var="productionArea">
                            <option value="${productionArea.id}">${productionArea.formatCode} ${productionArea.name}</option>
                        </c:forEach>
                    </select>
                </td>
                <td class="center aligned">
                    <div class="ui fluid input">
                        <input data-list-form-attribute="equipmentUnitList.serialNumber" type="text" />
                    </div>
                </td>
                <td class="center aligned">
                    <div class="ui fluid input">
                        <input data-list-form-attribute="equipmentUnitList.inventoryNumber" type="text" />
                    </div>
                </td>
                <c:if test="${empty form.id}">
                    <td class="center aligned"></td>
                </c:if>
                <td class="center aligned">
                    <i class="icon times link red js-remove-unit"></i>
                </td>
            </tr>`;

        $equipmentType.on({
            'change' : event => {
                $.get({
                    url : '/api/view/prod/equipment/list/edit/type',
                    data : { typeId: $(event.currentTarget).find('option:selected').val(), equipmentId: equipmentId }
                }).done(html => {
                    $attributeContainer.html(html);
                    $attributeContainer.find('select').addClass('search fluid').dropdown({ clearable: true, fullTextSearch: true });
                    $attributeContainer.find(':input').inputmask();
                    $attributeContainer.find('.ui.checkbox').checkbox();

                    const $buttonAddUnit = $equipmentDialog.find('.js-add-unit');
                    const $unitTable = $equipmentDialog.find('.js-unit-table');

                    /*$attributeContainer.find(buttonEditLink).on({
                        'click' : () => {
                            $.modalDialog({
                                dialogName : 'selectedTechnologicalFile',
                                url : '/technologicalTool/selectedTechnologicalFile'
                            });
                        }
                    });*/

                    // Добавление строки единицы оборудования
                    $buttonAddUnit.on({
                        'click' : () => {
                            $(row).appendTo($unitTable.find('tbody')).find('select')
                                .addClass('search fluid').dropdown({ clearable: true, fullTextSearch: true });
                            $unitTable.trigger('table.recalculate');
                        }
                    });

                    // Функция рекалькуляции таблицы для перепривязки листенеров
                    $unitTable.on({
                        'table.recalculate' : () => {
                            $unitTable.find(buttonRemoveUnit).off().on({
                                'click' : e => $(e.currentTarget).closest('tr').remove()
                            });
                            $unitTable.find(':input').inputmask();
                        }
                    });
                    $unitTable.trigger('table.recalculate');

                    // Добавление строки при загрузке страницы добавления оборудования
                    if ($unitTable.find('tbody > tr').length === 0) {
                        $buttonAddUnit.trigger('click');
                    }
                });
            }
        }).trigger('change');
    });
</script>