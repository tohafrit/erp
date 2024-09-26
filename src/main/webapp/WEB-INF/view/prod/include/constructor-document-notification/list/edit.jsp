<div class="ui modal list_edit__modal">
    <div class="header">
        ${empty id ? 'Добавление извещения' : 'Редактирование извещения'}
    </div>
    <div class="content">
        <form class="ui small form">
            <input type="hidden" name="id" value="${id}"/>
            <input type="hidden" name="productApplicabilityIdList"/>
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
                    <textarea name="reason" rows="10">${reason}</textarea>
                    <div class="ui compact message error" data-field="reason"></div>
                </div>
            </div>
            <div class="field inline required">
                <label>Изделие применяемости</label>
                <i class="add link blue icon list_edit__btn-add-product" title="<fmt:message key="label.button.add"/>"></i>
                <i class="icon trash link blue list_edit__btn-delete-product" title="<fmt:message key="label.button.delete"/>"></i>
                <div class="list_edit__product-table table-sm table-striped"></div>
                <div class="ui compact message small error" data-field="productApplicabilityIdList"></div>
            </div>
            <div class="field">
                <label>Ведущий изделия</label>
                <select class="ui dropdown search std-select" name="leadProductUser">
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq leadProductUser}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message error" data-field="leadProductUser"></div>
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
                <label>Родительское извещение</label>
                <i class="add link blue icon list_edit__btn-add-notification" title="<fmt:message key="label.button.add"/>"></i>
                <i class="icon trash link blue list_edit__btn-delete-notification" title="<fmt:message key="label.button.delete"/>"></i>
                <div class="list_edit__notification-table table-sm table-striped"></div>
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
        const $btnAddProduct = $('i.list_edit__btn-add-product');
        const $btnDeleteProduct = $('i.list_edit__btn-delete-product');
        const $btnAddNotification = $('i.list_edit__btn-add-notification');
        const $btnDeleteNotification = $('i.list_edit__btn-delete-notification');
        const id = '${id}';

        const productTable = new Tabulator('div.list_edit__product-table', {
            selectable: true,
            data: JSON.parse('${std:escapeJS(productApplicabilityList)}'),
            height: 'calc(100vh * 0.1)',
            layout: 'fitDataFill',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.CONDITIONAL_NAME },
                { title: 'Децимальный номер', field: TABR_FIELD.DECIMAL_NUMBER }
            ]
        });

        // Удаление изделий
        $btnDeleteProduct.on({
            'click': () => productTable.deleteRow(productTable.getSelectedData().map(el => el.id))
        });

        // Добавление изделий
        $btnAddProduct.on({
            'click': () => {
                const data = productTable.getData().map(el => el.id);
                $.modalWindow({
                    loadURL: VIEW_PATH.LIST_EDIT_ADD_PRODUCT,
                    loadData: {
                        productApplicabilityIdList: data.join()
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
                        notificationIdList: data.join(),
                        currentNotificationId: id
                    }
                });
            }
        });

        // Добавление списка изделий в сабмит форму
        $modal.on({
            'cb.onInitSubmit' : () => {
                const productData = productTable.getData().map(el => el.id);
                $modal.find('input[name="productApplicabilityIdList"]').val(productData.join());
                const notificationData = notificationTable.getData().map(el => el.id);
                $modal.find('input[name="notificationIdList"]').val(notificationData.join());
            }
        });
    });
</script>