<div class="b-left-section">
    <ul class="js-menu-tree">
        <li class="tlv__disable-selection-node tlv__expanded-node">
            <span><fmt:message key="purchase.menu.obligation"/></span>
            <ul>
                <li><span class="js-obligation-current-launch"><fmt:message key="purchase.menu.obligation.currentLaunch"/></span></li>
                <li><span class="js-obligation-previous-launch"><fmt:message key="purchase.menu.obligation.previousLaunch"/></span></li>
            </ul>
        </li>
        <li class="tlv__disable-selection-node">
            <span><fmt:message key="purchase.menu.need"/></span>
            <ul>
                <li><span class="js-need-current-launch"><fmt:message key="purchase.menu.need.currentLaunch"/></span></li>
                <li><span class="js-need-previous-launch"><fmt:message key="purchase.menu.need.previousLaunch"/></span></li>
                <li><span class="js-need-current-launch-tolling"><fmt:message key="purchase.menu.need.currentLaunch.tolling"/></span></li>
                <li><span class="js-need-previous-launch-tolling"><fmt:message key="purchase.menu.need.previousLaunch.tolling"/></span></li>
            </ul>
        </li>
        <li class="tlv__disable-selection-node">
            <span><fmt:message key="purchase.menu.income"/></span>
            <ul>
                <li><span class="js-income-free"><fmt:message key="purchase.menu.income.free"/></span></li>
                <li><span class="js-income-safe"><fmt:message key="purchase.menu.income.safe"/></span></li>
                <li><span class="js-income-component"><fmt:message key="purchase.menu.income.component"/></span></li>
                <li><span class="js-income-way"><fmt:message key="purchase.menu.income.way"/></span></li>
            </ul>
        </li>
        <li class="tlv__disable-selection-node">
            <span><fmt:message key="purchase.menu.payment"/></span>
            <ul>
                <li><span class="js-payment-nomenclature"><fmt:message key="purchase.menu.payment.nomenclature"/></span></li>
                <li><span class="js-payment-component"><fmt:message key="purchase.menu.payment.component"/></span></li>
                <li><span class="js-payment-product"><fmt:message key="purchase.menu.payment.product"/></span></li>
            </ul>
        </li>
    </ul>
</div>
<div class="b-right-section">
    <div class="b-right-section_content js-section-container"></div>
</div>

<script>
    $(() => {
        let
            $windowContainer = $('.purchase-information-modal-window'),
            $windowPreloader = $('.purchase-information-modal-window-preloader'),
            $sectionContainer = $windowContainer.find('.js-section-container'),
            $menuTree = $windowContainer.find('.js-menu-tree');

        $menuTree.treeListView({ nodeChoiceStyle: 'underline' });

        // Данные для загрузки по смене разделов секции
        let loadMenuData = [
            { menu: '.js-obligation-current-launch', url: '/purchase/information/obligation/currentLaunch' },
            { menu: '.js-obligation-previous-launch', url: '/purchase/information/obligation/previousLaunch' },
            { menu: '.js-need-current-launch', url: '/purchase/information/need/currentLaunch' },
            { menu: '.js-need-previous-launch', url: '/purchase/information/need/previousLaunch' },
            { menu: '.js-need-current-launch-tolling', url: '/purchase/information/need/currentLaunchTolling' },
            { menu: '.js-need-previous-launch-tolling', url: '/purchase/information/need/previousLaunchTolling' },
            { menu: '.js-income-free', url: '/purchase/information/income/free' },
            { menu: '.js-income-safe', url: '/purchase/information/income/safe' },
            { menu: '.js-income-component', url: '/purchase/information/income/component' },
            { menu: '.js-income-way', url: '/purchase/information/income/way' },
            { menu: '.js-payment-nomenclature', url: '/purchase/information/payment/nomenclature' },
            { menu: '.js-payment-component', url: '/purchase/information/payment/component' },
            { menu: '.js-payment-product', url: '/purchase/information/payment/product' }
        ];
        // Листенеры загрузки меню
        $.each(loadMenuData, (inx, item) => {
            $menuTree.find(item.menu).on({
                'click' : () => {
                    $.get({
                        url: item.url,
                        data: $.extend(true, {}, { purchaseId: '${purchaseId}' }, $menuTree.find(item.menu).data('attributes')),
                        beforeSend: () => $windowPreloader.show(),
                        complete: () => $windowPreloader.hide()
                    }).done(html => {
                        $sectionContainer.html(html);
                        // Стилизация select
                        $sectionContainer.find('select').addClass('search').dropdown({ fullTextSearch: true });
                    }).fail(() => {
                        globalMessage({ message: 'Ошибка загрузки раздела меню' });
                    }).then(() => {});
                }
            });
        });
        $menuTree.find(loadMenuData[0].menu).trigger('click');
    });
</script>