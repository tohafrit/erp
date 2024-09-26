<div class="ui modal detail_specification_info_edit-spec-item__modal">
    <div class="ui small header">Редактирование компонента</div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="lockVersion"/>
            <form:hidden path="unit"/>
            <form:hidden path="approvedOrAccepted"/>
            <div class="field inline">
                <label>Изделие</label>
                <span class="ui text">${productInfo}</span>
            </div>
            <div class="field inline">
                <label>Компонент</label>
                <span class="ui text">${componentInfo}</span>
            </div>
            <div class="field">
                <div class="ui checkbox">
                    <form:checkbox path="givenRawMaterial" cssClass="detail_specification_info_edit-spec-item__given-material-checkbox"/>
                    <label>Давальческое сырье</label>
                </div>
            </div>
            <div class="field required">
                <label>Изготовитель</label>
                <form:select cssClass="ui dropdown label search std-select detail_specification_info_edit-spec-item__producer-select" path="producerIdList" multiple="multiple">
                    <c:forEach items="${producerList}" var="producer">
                        <form:option value="${producer.id}">${producer.name}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="producerIdList"></div>
            </div>
            <c:choose>
                <c:when test="${form.approvedOrAccepted}">
                    <div class="ui compact message error visible">
                        Утвержденные (принятые) спецификации можно редактировать только в части замен и давальческого сырья
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="field">
                        <label>Количество</label>
                        <form:input path="quantity" cssClass="detail_specification_info_edit-spec-item__quantity-field"/>
                        <div class="ui compact message error" data-field="quantity"></div>
                    </div>
                    <c:if test="${form.unit}">
                        <div class="field">
                            <div class="ui primary small tertiary button detail_specification_info_edit-spec-item__btn-add-position">Добавить позиционное обозначение</div>
                            <div class="ui compact message error" data-field="positionList"></div>
                            <table class="detail_specification_info_edit-spec-item__position-table dialog-list-form-container">
                                <tbody>
                                    <c:forEach items="${form.positionList}" var="position">
                                        <tr>
                                            <td class="detail_specification_info_edit-spec-item__cell-letter">
                                                <div class="ui fluid input">
                                                    <input type="text" value="${position.letter}">
                                                </div>
                                            </td>
                                            <td class="detail_specification_info_edit-spec-item__cell-value">
                                                <div class="ui fluid input">
                                                    <input type="text" value="${position.value}">
                                                </div>
                                            </td>
                                            <td class="detail_specification_info_edit-spec-item__cell-remove">
                                                <div class="ui button tiny basic icon">
                                                    <i class="times link red icon"></i>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
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
        const $modal = $('div.detail_specification_info_edit-spec-item__modal');
        const $producerSelect = $('div.detail_specification_info_edit-spec-item__producer-select > select');
        const $givenMaterialCheckbox = $('input.detail_specification_info_edit-spec-item__given-material-checkbox');
        const $btnAddPosition = $('div.detail_specification_info_edit-spec-item__btn-add-position');
        const $positionTable = $('table.detail_specification_info_edit-spec-item__position-table');
        const blankPosition =
            `<tr>
                 <td class="detail_specification_info_edit-spec-item__cell-letter">
                     <div class="ui fluid input">
                         <input type="text">
                     </div>
                 </td>
                 <td class="detail_specification_info_edit-spec-item__cell-value">
                     <div class="ui fluid input">
                         <input type="text">
                     </div>
                 </td>
                 <td class="detail_specification_info_edit-spec-item__cell-remove">
                     <div class="ui button tiny basic icon">
                         <i class="times link red icon"></i>
                     </div>
                 </td>
             </tr>`;

        // Инициализация строк таблицы
        $positionTable.find('tr').each((inx, elem) => initPositionRow($(elem)));

        // Инициализация строки таблицы позиции
        function initPositionRow($row) {
            // Литера
            const $letterInput = $row.find('td.detail_specification_info_edit-spec-item__cell-letter input');
            $letterInput.attr('data-list-form-attribute', 'positionList.letter');
            $letterInput.attr('placeholder', 'Введите литеру позиции');
            $letterInput.inputmask({ regex: '^[a-zA-Z]{0,3}$' });
            $letterInput.on({
                'input': () => $letterInput.val($letterInput.val().toUpperCase())
            });
            // Значение литеры
            const $valueInput = $row.find('td.detail_specification_info_edit-spec-item__cell-value input');
            $valueInput.attr('data-list-form-attribute', 'positionList.value');
            $valueInput.attr('placeholder', 'Введите позиционную нумерацию');
            /*$valueInput.inputmask({
                regex: '^((([1-9][0-9]*)|([1-9][0-9]*[-][1-9][0-9]*))([,](([1-9][0-9]*)|([1-9][0-9]*[-][1-9][0-9]*)))*)$',
                clearIncomplete: true
            });*/
            // Кнопка удаления
            const $btnDelete = $row.find('td.detail_specification_info_edit-spec-item__cell-remove div.button');
            $btnDelete.attr('title', 'Удалить позицию');
        }

        // Добавление позиции
        $btnAddPosition.on({
            'click': () => {
                const $row = $(blankPosition).appendTo($positionTable);
                initPositionRow($row);
                $positionTable.trigger('table.recalculate');
            }
        });

        // Функция переназначения кнопок удаления строки
        $positionTable.on({
            'table.recalculate': () => {
                $positionTable.find('td.detail_specification_info_edit-spec-item__cell-remove div.button').off().on({
                    'click': function () {
                        $(this).closest('tr').remove();
                    }
                });
            }
        });
        $positionTable.trigger('table.recalculate');

        // Маска для ввода чисел с плавающей точкой в количество
        $('input.detail_specification_info_edit-spec-item__quantity-field').inputmask('decimal', {
            rightAlign: false,
            placeholder: ''
        });

        // Отключение списка изготовителей и его очистка если убираем давальческое сырье
        $givenMaterialCheckbox.on({
            'change': e => {
                if ($(e.currentTarget).is(':checked')) {
                    $producerSelect.closest('div.ui.dropdown').removeClass('disabled');
                } else {
                    $producerSelect.dropdown('clear');
                    $producerSelect.closest('div.ui.dropdown').addClass('disabled');
                }
            }
        });
        $givenMaterialCheckbox.trigger('change');
    })
</script>