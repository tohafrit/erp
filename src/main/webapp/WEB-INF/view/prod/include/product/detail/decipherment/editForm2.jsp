<div class="ui modal">
    <div class="ui small header">Редактирование - форма ${formName}</div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="version" value="${version}">
            <div class="field required inline">
                <label>Базовый планово-экономический показатель</label>
                <i class="add link blue icon decipherment_edit_form2__btn-add-indicator" title="<fmt:message key="label.button.add"/>"></i>
                <i class="pen link blue icon decipherment_edit_form2__btn-edit-indicator" title="<fmt:message key="label.button.edit"/>"></i>
                <i class="times link red icon decipherment_edit_form2__btn-remove-indicator" title="<fmt:message key="label.button.delete"/>"></i>
                <input class="decipherment_edit_form2__indicator-id" type="hidden" name="ecoIndicatorId" value="${ecoIndicatorId}">
                <table class="ui tiny compact definition table decipherment_edit_form2__table-indicator">
                    <tbody>
                        <tr>
                            <td class="two wide">Наименование</td>
                            <td class="ten wide">${ecoIndicatorName}</td>
                        </tr>
                        <tr>
                            <td>Дата утверждения</td>
                            <td>${ecoIndicatorApproveDate}</td>
                        </tr>
                    </tbody>
                </table>
                <div class="ui compact message small error" data-field="ecoIndicatorId"></div>
            </div>
            <c:if test="${not empty okpdCode}">
                <div class="field inline">
                    <label>Код ОКП/ОКПД2</label>
                    <span class="ui text">${okpdCode}</span>
                </div>
            </c:if>
            <c:if test="${not empty techDoc}">
                <div class="field inline">
                    <label>Техническая документация</label>
                    <span class="ui text">${techDoc}</span>
                </div>
            </c:if>
            <div class="field required">
                <label>Начальник ПЭО</label>
                <select name="headEcoId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq headEcoId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="headEcoId"></div>
            </div>
            <div class="field inline">
                <label>Дата создания</label>
                <span class="ui text">${createDate}</span>
            </div>
            <div class="field inline">
                <label>Создано</label>
                <span class="ui text">${createdBy}</span>
            </div>
            <div class="field inline">
                <label>Файл</label>
                <div class="std-file ui action input fluid">
                    <input type="hidden" name="fileId" value="${fileId}">
                    <input type="file" name="file"/>
                    <span>
                        ${fileName}
                        <a target="_blank" href="<c:url value="/download-file/${fileUrlHash}"/>">
                            <fmt:message key="text.downloadFile"/>
                        </a>
                    </span>
                </div>
                <div class="ui compact message small error" data-field="file"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <textarea name="comment" rows="3">${comment}</textarea>
                </div>
                <div class="ui compact message small error" data-field="comment"></div>
            </div>
        </form>
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
        const $indicatorId = $('input.decipherment_edit_form2__indicator-id');
        const $tableIndicator = $('table.decipherment_edit_form2__table-indicator');
        const $btnAddIndicator = $('i.decipherment_edit_form2__btn-add-indicator');
        const $btnEditIndicator = $('i.decipherment_edit_form2__btn-edit-indicator');
        const $btnDeleteIndicator = $('i.decipherment_edit_form2__btn-remove-indicator');

        // Выбор экономического показателя
        $indicatorId.on({
            'change': () => {
                const val = $indicatorId.val();
                $btnAddIndicator.toggle(val === '');
                $btnEditIndicator.toggle(val !== '');
                $btnDeleteIndicator.toggle(val !== '');
                $tableIndicator.toggle(val !== '');
            }
        });
        $indicatorId.trigger('change');

        $btnAddIndicator.add($btnEditIndicator).on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_EDIT_FORM2_ECO_INDICATOR
            })
        });
        $btnDeleteIndicator.on({
            'click': () => {
                $indicatorId.val('');
                $tableIndicator.find('tr:eq(0) > td:eq(1)').text('');
                $tableIndicator.find('tr:eq(1) > td:eq(1)').text('');
                $indicatorId.trigger('change');
            }
        });
    })
</script>