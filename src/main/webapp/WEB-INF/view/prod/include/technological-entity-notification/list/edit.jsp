<div class="ui modal list_edit__modal">
    <div class="header">
        ${empty id ? 'Добавление извещения' : 'Редактирование извещения'}
    </div>
    <div class="content">
        <form class="ui small form">
            <input type="hidden" name="id" value="${id}"/>
            <input type="hidden" name="entityIdList"/>
            <input type="hidden" name="notificationIdList"/>
            <div class="field required">
                <label>Номер</label>
                <input type="text" name="docNumber" value="${docNumber}"/>
                <div class="ui compact message error" data-field="docNumber"></div>
            </div>
            <div class="field required">
                <label>Дата выпуска</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-date" name="releaseOn" value="${releaseOn}"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="releaseOn"></div>
            </div>
            <div class="field required">
                <label>Срок изменения</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-date" name="termChangeOn" value="${termChangeOn}"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="termChangeOn"></div>
            </div>
            <div class="field required">
                <label>Причина</label>
                <div class="ui textarea">
                    <select class="ui dropdown search std-select" name="reason">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <c:forEach items="${reasonTypeList}" var="reasonType">
                            <option value="${reasonType.id}" <c:if test="${reasonType.id eq reasonId}">selected</c:if>>${reasonType.value}</option>
                        </c:forEach>
                    </select>
                    <div class="ui compact message error" data-field="reason"></div>
                </div>
            </div>
            <div class="field required">
                <label>Текст извещения</label>
                <div class="ui textarea">
                    <textarea name="text" rows="10">${text}</textarea>
                    <div class="ui compact message error" data-field="text"></div>
                </div>
            </div>
            <div class="field">
                <label>Указание о заделе</label>
                <select class="ui dropdown search std-select" name="reserveIndication">
                    <option value="0" <c:if test="${not reserveIndication}">selected</c:if>>Не использовать</option>
                    <option value="1" <c:if test="${reserveIndication}">selected</c:if>>Использовать</option>
                </select>
            </div>
            <div class="field required">
                <label>Указание о внедрении</label>
                <input type="text" name="introductionIndication" value="${introductionIndication}"/>
                <div class="ui compact message error" data-field="introductionIndication"></div>
            </div>
            <div class="field inline required">
                <label>Технологическая документация</label>
                <i class="add link blue icon list_edit__btn-add-entity" title="<fmt:message key="label.button.add"/>"></i>
                <i class="icon trash link blue list_edit__btn-delete-entity" title="<fmt:message key="label.button.delete"/>"></i>
                <div class="list_edit__entity-table table-sm table-striped"></div>
                <div class="ui compact message small error" data-field="entityIdList"></div>
            </div>
            <div class="field inline required">
                <label>Извещение об изменении КД</label>
                <i class="add link blue icon list_edit__btn-add-notification" title="<fmt:message key="label.button.add"/>"></i>
                <i class="icon trash link blue list_edit__btn-delete-notification" title="<fmt:message key="label.button.delete"/>"></i>
                <div class="list_edit__notification-table table-sm table-striped"></div>
                <div class="ui compact message small error" data-field="notificationIdList"></div>
            </div>
            <div class="field">
                <label>Технолог</label>
                <select class="ui dropdown search std-select" name="techUserId">
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq techUserId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message error" data-field="techUser"></div>
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
        const $modal = $('div.list_edit__modal');
        const $btnAddEntity = $('i.list_edit__btn-add-entity');
        const $btnDeleteEntity = $('i.list_edit__btn-delete-entity');
        const $btnAddNotification = $('i.list_edit__btn-add-notification');
        const $btnDeleteNotification = $('i.list_edit__btn-delete-notification');

        const technologicalEntityTable = new Tabulator('div.list_edit__entity-table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(technologicalEntityList)}'),
            height: 'calc(100vh * 0.1)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер документации', field: TABR_FIELD.ENTITY_NUMBER },
                { title: 'Номер комплекта', field: TABR_FIELD.SET_NUMBER },
                { title: 'Кем разработана', field: TABR_FIELD.DESIGNED_BY },
                { title: 'Дата разработки', field: TABR_FIELD.DESIGNED_ON }
            ]
        });

        // Удаление технологической документации
        $btnDeleteEntity.on({
            'click': () => technologicalEntityTable.deleteRow(technologicalEntityTable.getSelectedData().map(el => el.id))
        });

        // Добавление технологической документации
        $btnAddEntity.on({
            'click': () => {
                const data = technologicalEntityTable.getData().map(el => el.id);
                $.modalWindow({
                    loadURL: VIEW_PATH.LIST_EDIT_ADD_TECHNOLOGICAL_ENTITY,
                    loadData: {
                        entityIdList: data.join()
                    }
                });
            }
        });

        const notificationTable = new Tabulator('div.list_edit__notification-table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(notificationList)}'),
            height: 'calc(100vh * 0.1)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Номер', field: TABR_FIELD.DOC_NUMBER },
                { title: 'Изделие применяемости', field: TABR_FIELD.PRODUCT }
            ]
        });

        // Удаление извещение
        $btnDeleteNotification.on({
            'click': () => notificationTable.deleteRow(notificationTable.getSelectedData().map(el => el.id))
        });

        // Добавление извещения
        $btnAddNotification.on({
            'click': () => {
                const data = notificationTable.getData().map(el => el.id);
                $.modalWindow({
                    loadURL: VIEW_PATH.LIST_EDIT_ADD_NOTIFICATION,
                    loadData: {
                        notificationIdList: data.join()
                    }
                });
            }
        });

        // Добавление списка изделий в сабмит форму
        $modal.on({
            'cb.onInitSubmit' : () => {
                const entityData = technologicalEntityTable.getData().map(el => el.id);
                $modal.find('input[name="entityIdList"]').val(entityData.join());
                const notificationData = notificationTable.getData().map(el => el.id);
                $modal.find('input[name="notificationIdList"]').val(notificationData.join());
            }
        });
    });
</script>