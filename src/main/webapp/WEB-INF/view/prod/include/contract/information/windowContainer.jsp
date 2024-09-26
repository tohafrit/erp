<div class="b-left-section">
    <ul class="js-menu-tree">
        <c:forEach items="${contract.sectionList}" var="section" varStatus="status">
                <li ${status.first ? 'class="tlv__expanded-node"' : ''}>
                    <span data-attributes='{"sectionId":${section.id}}' class="js-general">
                        <c:choose>
                            <c:when test="${status.first}">
                                ${section.fullNumber}
                            </c:when>
                            <c:otherwise>
                                <fmt:message key="contract.menuWindow.addition"/>${section.number}
                            </c:otherwise>
                        </c:choose>
                    </span>
                <ul>
                    <li><span data-attributes='{"sectionId":${section.id}}' class="js-delivery-statement"><fmt:message key="contract.menuWindow.deliveryStatement"/></span></li>
                    <li><span data-attributes='{"sectionId":${section.id}}' class="js-document"><fmt:message key="contract.menuWindow.document"/></span></li>
                    <li><span data-attributes='{"sectionId":${section.id}}' class="js-invoice"><fmt:message key="contract.menuWindow.invoice"/></span></li>
                    <li><span data-attributes='{"sectionId":${section.id}}' class="js-payment"><fmt:message key="contract.menuWindow.payment"/></span></li>
                    <li><span data-attributes='{"sectionId":${section.id}}' class="js-product"><fmt:message key="contract.menuWindow.production"/></span></li>
                </ul>
            </li>
        </c:forEach>
    </ul>
</div>
<div class="b-right-section">
    <div class="b-right-section_content js-section-container"></div>
</div>

<script>
    $(() => {
        let
            $windowContainer = $('.contract-information-modal-window'),
            $preloader = $('.contract-information-modal-window-preloader'),
            $sectionContainer = $windowContainer.find('.js-section-container'),
            $menuTree = $windowContainer.find('.js-menu-tree');

        $menuTree.treeListView({nodeChoiceStyle: 'underline'});

        // Данные для загрузки по смене разделов секции
        let loadMenuData = [
            { menu: '.js-general', url: '/contract/information/general', available: true },
            { menu: '.js-delivery-statement', url: '/contract/deliveryStatement', available: true },
            { menu: '.js-document', url: '/contract/document', available: false },
            { menu: '.js-invoice', url: '/contract/invoice', available: true },
            { menu: '.js-payment', url: '/contract/payment', available: true },
            { menu: '.js-product', url: '/contract/product', available: true }
        ];
        // Листенеры загрузки меню
        $.each(loadMenuData, (inx, item) => {
            $menuTree.find(item.menu).on({
                'click' : e => {
                    if (item.available) {
                        $.get({
                            url: item.url,
                            data: $.extend(true, {}, $(e.currentTarget).data('attributes')),
                            beforeSend: () => $preloader.show(),
                            complete: () => $preloader.hide()
                        }).done((html) => {
                            $sectionContainer.html(html);
                            $sectionContainer.find('.ui.checkbox').checkbox();
                        }).fail(() => {
                            globalMessage({message: 'Ошибка загрузки раздела меню'});
                        }).then(() => {});
                    } else {
                        globalMessage({message: 'Функционал раздела не доступен'});
                    }
                }
            });
        });
        $menuTree.find(loadMenuData[0].menu).filter(':first').trigger('click');
    });
</script>