<div class="detail_general__main">
    <div class="detail_general__header_buttons">
        <c:if test="${isSectionNumberZero}">
            <i class="icon pencil alternate link blue detail_general__edit-contract" title="Редактировать договор"></i>
        </c:if>
        <c:if test="${isAdditionalAgreement}">
            <i class="icon pencil alternate link blue detail_general__edit-section" title="Редактировать дополнительное соглашение"></i>
        </c:if>
        <c:if test="${isActive and isSectionNumberZero}">
            <i class="icon add link blue detail_general__btn-add-section" title="Добавить доп. соглашение"></i>
        </c:if>
    </div>
    <table class="ui tiny definition table detail_general__table">
        <tbody>
        <c:if test="${isAdditionalAgreement}">
            <tr>
                <td>Дополнительное соглашение</td>
                <td>№ ${sectionNumber}</td>
            </tr>
        </c:if>
        <tr>
            <td>Заказчик</td>
            <td>${customerName}</td>
        </tr>
        <tr>
            <td>Дата создания</td>
            <td>${creationDate}</td>
        </tr>
        <tr>
            <td>Внешний номер</td>
            <td>${externalName}</td>
        </tr>
        <tr>
            <td>Статус</td>
            <td>${status}</td>
        </tr>
        <tr>
            <td>Дата передачи в ПЗ</td>
            <td>${sendToClient}</td>
        </tr>
        <tr>
            <td>Идентификатор</td>
            <td>${identifier}</td>
        </tr>
        <tr>
            <td>ОБС</td>
            <td>${separateAccount}</td>
        </tr>
        <tr>
            <td>Ведущий</td>
            <td>${leadContract}</td>
        </tr>
        <tr>
            <td>Комментарий</td>
            <td>${comment}</td>
        </tr>
        </tbody>
    </table>

    <script>
        $(() => {
            const contractId = '${contractId}';
            const sectionId = '${sectionId}';
            const isSectionNumberZero = '${isSectionNumberZero}' === 'true';
            //
            const $menuTree = $('ul.detail__menu_tree');
            const $btnContractEdit = $('i.detail_general__edit-contract');
            const $btnSectionEdit = $('i.detail_general__edit-section');
            const $btnSectionAdd = $('i.detail_general__btn-add-section');
            const $btnSend = $('i.detail_general__btn-send-section');
            const $btnApprove = $('i.detail_general__btn-approve-section');
            const $generalMenu = $menuTree.find('span.detail__menu_general[data-id=${sectionId}]');

            // Кнопка редактирования договора
            $btnContractEdit.on({
                'click': () => {
                    $.modalWindow({
                        loadURL: VIEW_PATH.LIST_EDIT,
                        loadData: { id: contractId },
                        submitURL: ACTION_PATH.LIST_EDIT_SAVE,
                        onSubmitSuccess: () => $generalMenu.trigger('click')
                    });
                }
            });

            // Кнопка редактирования/добавления дополнительного соглашения
            $btnSectionEdit.add($btnSectionAdd).on({
                'click': () => {
                    $.modalWindow({
                        loadURL: VIEW_PATH.DETAIL_GENERAL_EDIT,
                        loadData: { id: sectionId },
                        submitURL: ACTION_PATH.DETAIL_GENERAL_EDIT_SAVE,
                        onSubmitSuccess: resp => {
                            const table = Tabulator.prototype.findTable('div.list_structure__table')[0];
                            table.setData();
                            if (isSectionNumberZero) {
                                page.redirect(ROUTE.list(contractId));
                                page(ROUTE.detail(contractId), { sectionId: resp.attributes.id })
                            } else {
                                $generalMenu.trigger('click');
                            }
                        }
                    });
                }
            });
        });
    </script>
</div>