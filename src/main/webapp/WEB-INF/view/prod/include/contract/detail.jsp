<div class="detail__container">
    <div class="ui button basic detail__btn-contract-list" title="Вернуться к договорам">
        <i class="long arrow alternate left icon"></i>
        Договоры
    </div>
    <div class="detail__header">
        <h1 class="detail__header_title">Договор № ${contractFullNumber}</h1>
    </div>
    <div class="detail__container-content">
        <div class="ui icon small button basic detail__btn-menu">
            <i class="align justify icon"></i>
        </div>
        <i class="close link blue icon detail__btn-menu-close"></i>
        <div class="ui small vertical menu detail__menu">
            <ul class="detail__menu_tree">
                <c:forEach items="${sectionList}" var="section" varStatus="status">
                    <li ${status.first ? 'class="tlv__expanded-node"' : ''}>
                        <span data-id="${section.id}" class="detail__menu_general">
                            <c:choose>
                                <c:when test="${status.first}">
                                    ${section.fullNumber}
                                </c:when>
                                <c:otherwise>
                                    Дополнение №${section.number}
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <ul>
                            <li data-id="${section.id}" class="detail__menu_delivery-statement"><span>Ведомость поставки</span></li>
                            <li data-id="${section.id}" class="detail__menu_documentation"><span>Документация</span></li>
                            <li data-id="${section.id}" class="detail__menu_invoices"><span>Счета</span></li>
                            <li data-id="${section.id}" class="detail__menu_payments"><span>Платежи</span></li>
                        </ul>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="detail__content"></div>
    </div>

    <script>
        $(() => {
            const isSection = '${isSection}';
            //
            const $menu = $('div.detail__menu');
            const $btnMenu = $('div.detail__btn-menu');
            const $btnMenuClose = $('i.detail__btn-menu-close');
            const $btnContractList = $('div.detail__btn-contract-list');
            const $parentContent = $('div.root__content');
            const $menuTree = $('ul.detail__menu_tree');
            const $detailContent = $('div.detail__content');

            $menuTree.treeListView({nodeChoiceStyle: 'underline'});

            // Кнопка переключения на список договоров
            $btnContractList.on({
                'click': () => page('/list')
            });

        // Данные для загрузки по смене разделов секции
        let loadMenuData = [
            { menu: 'span.detail__menu_general', url: VIEW_PATH.DETAIL_GENERAL, available: true },
            { menu: 'li.detail__menu_delivery-statement', url: VIEW_PATH.DETAIL_DELIVERY_STATEMENT, available: true },
            { menu: 'li.detail__menu_documentation', url: VIEW_PATH.DETAIL_DOCUMENTATION, available: true },
            { menu: 'li.detail__menu_invoices', url: VIEW_PATH.DETAIL_INVOICES, available: true },
            { menu: 'li.detail__menu_payments', url: VIEW_PATH.DETAIL_PAYMENTS, available: true }
        ];

            // Листенеры загрузки меню
            $.each(loadMenuData, (inx, item) => {
                $menuTree.find(item.menu).on({
                    'click': e => {
                        if (item.available) {
                            $.get({
                                url: item.url,
                                data: { sectionId: $(e.currentTarget).data('id') }
                            }).done(html => {
                                $detailContent.html(html);
                            }).fail(() => {
                                globalMessage({message: 'Ошибка загрузки раздела меню'});
                            });
                        } else {
                            globalMessage({message: 'Функционал раздела не доступен'});
                        }
                    }
                });
            });
            if (isSection) {
                let menuTreeGeneral = $menuTree.find('span.detail__menu_general[data-id=${sectionId}]');
                menuTreeGeneral.parent().addClass('tlv__expanded-node');
                menuTreeGeneral.trigger('click');
            } else {
                $menuTree.find(loadMenuData[0].menu).filter('span:first').trigger('click');
            }

            // Состояние сайдбара
            const menuState = localStorage.getItem(lsContract_detailSidebarState);
            if (menuState == null || menuState === '1') {
                $btnMenu.hide();
                $btnMenuClose.show();
                $menu.show();
            } else {
                $btnMenu.show();
                $btnMenuClose.hide();
                $menu.hide();
            }

            // Открытие/закрытие бокового меню
            $btnMenu.on({
                'click': () => {
                    $btnMenu.hide();
                    $btnMenuClose.show();
                    $menu.show();
                    localStorage.setItem(lsContract_detailSidebarState, '1');
                }
            });
            $btnMenuClose.on({
                'click': () => {
                    $btnMenu.show();
                    $btnMenuClose.hide();
                    $menu.hide();
                    localStorage.setItem(lsContract_detailSidebarState, '0');
                }
            });
        })
    </script>
</div>