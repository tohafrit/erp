<div class="ui modal">
    <div class="ui small header">Редактирование - форма ${formName}</div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="version" value="${version}">
            <div class="field">
                <label>Классификационная группа изделия</label>
                <span class="ui text">${classGroupName}</span>
            </div>
            <div class="field required inline">
                <label>Цена СП</label>
                <i class="add link blue icon decipherment_edit_form18__btn-add-review-justification" title="<fmt:message key="label.button.add"/>"></i>
                <i class="pen link blue icon decipherment_edit_form18__btn-edit-review-justification" title="<fmt:message key="label.button.edit"/>"></i>
                <i class="times link red icon decipherment_edit_form18__btn-remove-review-justification" title="<fmt:message key="label.button.delete"/>"></i>
                <input type="hidden" class="decipherment_edit_form18__review-justification-id" name="reviewJustificationId" value="${reviewJustificationId}">
                <span class="ui text decipherment_edit_form18__review-justification-name">${reviewJustificationName}</span>
                <div class="ui compact message small error" data-field="reviewJustificationId"></div>
            </div>
            <div class="field inline">
                <label>Цена СИ</label>
                <input type="hidden" class="decipherment_edit_form18__research-justification-id" name="researchJustificationId" value="${researchJustificationId}">
                <i class="add link blue icon decipherment_edit_form18__btn-add-research-justification" title="<fmt:message key="label.button.add"/>"></i>
                <i class="pen link blue icon decipherment_edit_form18__btn-edit-research-justification" title="<fmt:message key="label.button.edit"/>"></i>
                <i class="times link red icon decipherment_edit_form18__btn-remove-research-justification" title="<fmt:message key="label.button.delete"/>"></i>
                <span class="ui text decipherment_edit_form18__research-justification-name">${researchJustificationName}</span>
                <div class="ui compact message small error" data-field="researchJustificationId"></div>
            </div>
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
            <div class="field required">
                <label>Начальник производства</label>
                <select name="headProdId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq headProdId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="headProdId"></div>
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
        const deciphermentId = '${id}';
        const $reviewJustificationId = $('input.decipherment_edit_form18__review-justification-id');
        const $reviewJustificationName = $('span.decipherment_edit_form18__review-justification-name');
        const $btnAddReviewJustification = $('i.decipherment_edit_form18__btn-add-review-justification');
        const $btnEditReviewJustification = $('i.decipherment_edit_form18__btn-edit-review-justification');
        const $btnDeleteReviewJustification = $('i.decipherment_edit_form18__btn-remove-review-justification');
        const $researchJustificationId = $('input.decipherment_edit_form18__research-justification-id');
        const $researchJustificationName = $('span.decipherment_edit_form18__research-justification-name');
        const $btnAddResearchJustification = $('i.decipherment_edit_form18__btn-add-research-justification');
        const $btnEditResearchJustification = $('i.decipherment_edit_form18__btn-edit-research-justification');
        const $btnDeleteResearchJustification = $('i.decipherment_edit_form18__btn-remove-research-justification');

        // Изменение цены на СП и СИ
        $reviewJustificationId.add($researchJustificationId).on({
            'change': () => {
                $btnAddReviewJustification.toggle($reviewJustificationId.val() === '');
                $btnEditReviewJustification.toggle($reviewJustificationId.val() !== '');
                $btnDeleteReviewJustification.toggle($reviewJustificationId.val() !== '');
                $btnAddResearchJustification.toggle($researchJustificationId.val() === '');
                $btnEditResearchJustification.toggle($researchJustificationId.val() !== '');
                $btnDeleteResearchJustification.toggle($researchJustificationId.val() !== '');
            }
        });
        $reviewJustificationId.trigger('change');

        // Выбор обоснования цены СП
        $btnAddReviewJustification.add($btnEditReviewJustification).on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_EDIT_FORM18_REVIEW_JUSTIFICATION,
                loadData: { deciphermentId: deciphermentId }
            })
        });
        $btnDeleteReviewJustification.on({
            'click': () => {
                $reviewJustificationId.val('');
                $reviewJustificationName.text('');
                $reviewJustificationId.trigger('change');
            }
        });

        // Выбор обоснования цены СИ
        $btnAddResearchJustification.add($btnEditResearchJustification).on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.DETAIL_DECIPHERMENT_EDIT_FORM18_RESEARCH_JUSTIFICATION,
                loadData: { deciphermentId: deciphermentId }
            })
        });
        $btnDeleteResearchJustification.on({
            'click': () => {
                $researchJustificationId.val('');
                $researchJustificationName.text('');
                $researchJustificationId.trigger('change');
            }
        });
    })
</script>