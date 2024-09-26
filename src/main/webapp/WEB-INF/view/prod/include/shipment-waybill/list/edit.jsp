<div class="ui modal">
    <div class="ui small header">
        ${empty id ? 'Добавление накладной' : 'Редактирование накладной'}
    </div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="version" value="${version}">
            <c:if test="${not empty number}">
                <div class="field inline">
                    <label>Номер</label>
                    <span class="ui text">${number}</span>
                </div>
            </c:if>
            <c:if test="${not empty createDate}">
                <div class="field inline">
                    <label>Дата создания</label>
                    <span class="ui text">${createDate}</span>
                </div>
            </c:if>
            <c:if test="${not empty shipmentDate}">
                <div class="field inline">
                    <label>Дата отгрузки</label>
                    <span class="ui text">${shipmentDate}</span>
                </div>
            </c:if>
            <c:if test="${empty id}">
                <div class="field inline required">
                    <label>Договор</label>
                    <input type="hidden" class="list_edit__section-id" name="sectionId">
                    <i class="add link blue icon list_edit__btn-add-section" title="<fmt:message key="label.button.add"/>"></i>
                    <i class="pen link blue icon list_edit__btn-edit-section" title="<fmt:message key="label.button.edit"/>"></i>
                    <i class="times link red icon list_edit__btn-remove-section" title="<fmt:message key="label.button.delete"/>"></i>
                    <span class="ui text list_edit__section-name"></span>
                    <div class="ui compact message small error" data-field="sectionId"></div>
                </div>
            </c:if>
            <c:if test="${not empty id}">
                <div class="field inline">
                    <label>Договор</label>
                    <span class="ui text">${sectionName}</span>
                </div>
            </c:if>
            <div class="field">
                <label>Расчетный счет</label>
                <select name="accountId" class="ui dropdown search std-select list_edit__account-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${accountList}" var="opt">
                        <option value="${opt.id}" <c:if test="${opt.id eq accountId}">selected</c:if>>${opt.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field inline list_edit__payer-name">
                <label>Плательщик</label>
                <span class="ui text">${payer}</span>
            </div>
            <div class="field">
                <label>Грузополучатель</label>
                <select name="consigneeId" class="ui dropdown search std-select list_edit__consignee-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${consigneeList}" var="opt">
                        <option value="${opt.id}" <c:if test="${opt.id eq consigneeId}">selected</c:if>>${opt.value}</option>
                    </c:forEach>
                </select>
            </div>
            <c:if test="${not empty vat}">
                <div class="field inline">
                    <label>НДС</label>
                    <span class="ui text">${vat}</span>
                </div>
            </c:if>
            <c:if test="${not empty id}">
                <div class="field inline">
                    <label>Всего</label>
                    <span class="ui text">${totalWoVat}</span>
                </div>
                <div class="field inline">
                    <label>Всего с учетом НДС</label>
                    <span class="ui text">${totalVat}</span>
                </div>
            </c:if>
            <c:if test="${not empty giveUser}">
                <div class="field inline">
                    <label>Отпуск произвел</label>
                    <span class="ui text">${giveUser}</span>
                </div>
            </c:if>
            <c:if test="${not empty permitUser}">
                <div class="field inline">
                    <label>Отпуск разрешил</label>
                    <span class="ui text">${permitUser}</span>
                </div>
            </c:if>
            <c:if test="${not empty accountantUser}">
                <div class="field inline">
                    <label>Главный бухгалтер</label>
                    <span class="ui text">${accountantUser}</span>
                </div>
            </c:if>
            <div class="field">
                <label>Сопроводительное письмо</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="transmittalLetter" value="${transmittalLetter}"/>
                </div>
                <div class="ui compact message small error" data-field="transmittalLetter"></div>
            </div>
            <div class="field">
                <label>Получил</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="receiver" value="${receiveUser}"/>
                </div>
<%--                <select name="receiverId" class="ui dropdown search std-select">--%>
<%--                    <option value=""><fmt:message key="text.notSpecified"/></option>--%>
<%--                    <c:forEach items="${receiverList}" var="opt">--%>
<%--                        <option value="${opt.id}" <c:if test="${opt.id eq receiverId}">selected</c:if>>${opt.value}</option>--%>
<%--                    </c:forEach>--%>
<%--                </select>--%>
            </div>
            <div class="field">
                <label>Доверенность</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="letterOfAttorney" value="${letterOfAttorney}"/>
                </div>
                <div class="ui compact message small error" data-field="letterOfAttorney"></div>
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
        const $sectionId = $('input.list_edit__section-id');
        const $sectionName = $('span.list_edit__section-name');
        const $btnAddSection = $('i.list_edit__btn-add-section');
        const $btnEditSection = $('i.list_edit__btn-edit-section');
        const $btnDeleteSection = $('i.list_edit__btn-remove-section');
        const $accountSelect = $('div.list_edit__account-select');
        const $payerNameDiv = $('div.list_edit__payer-name');

        // Начальная видимость элементов
        $btnAddSection.show();
        $btnEditSection.hide();
        $btnDeleteSection.hide();
        if (!$payerNameDiv.find('span').text()) $payerNameDiv.hide();

        // Изменение поля договора
        $sectionId.on({
            'change': () => {
                const val = $sectionId.val();
                $btnAddSection.toggle(val === '');
                $btnEditSection.toggle(val !== '');
                $btnDeleteSection.toggle(val !== '');
                const $payerName = $payerNameDiv.find('span');
                const $select = $accountSelect.find('select');
                $select.find('option[value!=""]').remove();
                $accountSelect.dropdown('clear');
                $accountSelect.dropdown('refresh');
                if (val) $.get({
                    url: ACTION_PATH.LIST_EDIT_LOAD_ACCOUNT_DATA,
                    data: { companyId: $payerName.data('id') },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(data => {
                    data.forEach(el => $select.append('<option value="' + el.id + '">' + el.value + '</option>'))
                    $accountSelect.dropdown('refresh');
                });
                else $payerName.text('');
                $payerNameDiv.toggle($payerName.text() !== '');
            }
        });

        // Выбор договора
        $btnAddSection.add($btnEditSection).on({
            'click': () => $.modalWindow({
                loadURL: VIEW_PATH.LIST_EDIT_CONTRACT
            })
        });
        $btnDeleteSection.on({
            'click': () => {
                $sectionId.val('');
                $sectionName.text('');
                $sectionId.trigger('change');
            }
        });
    })
</script>