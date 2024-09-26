<div class="detail__main">
    <div class="ui button basic detail__btn-product-list" title="Вернуться к изделиям">
        <i class="long arrow alternate left icon"></i>
        Изделия
    </div>
    <div class="detail__header">
        <h1 class="detail__header_title">${productName}</h1>
    </div>
    <div class="detail__container">
        <div class="ui icon small button basic detail__btn-menu">
            <i class="align justify icon"></i>
        </div>
        <i class="close link blue icon detail__btn-menu-close"></i>
        <div class="ui mini vertical menu detail__menu">
            <div class="item">
                <a class="item detail__menu_general" data-section="general"><fmt:message key="product.detail.menu.general"/></a>
                <a class="item detail__menu_structure" data-section="structure"><fmt:message key="product.detail.menu.structure"/></a>
                <a class="item detail__menu_specification" data-section="specification"><fmt:message key="product.detail.menu.specification"/></a>
                <a class="item detail__menu_documentation" data-section="documentation"><fmt:message key="product.detail.menu.documentation"/></a>
                <a class="item detail__menu_occurrence" data-section="occurrence"><fmt:message key="product.detail.menu.occurrence"/></a>
                <a class="item detail__menu_comment" data-section="comment"><fmt:message key="product.detail.menu.comment"/></a>
                <a class="item detail__menu_decipherment" data-section="decipherment">Расчет цены</a>
            </div>
        </div>
        <div class="detail__content"></div>
    </div>

    <script>
        $(() => {
            const productId = '${productId}';
            const $menu = $('div.detail__menu');
            const $btnMenu = $('div.detail__btn-menu');
            const $btnMenuClose = $('i.detail__btn-menu-close');
            const $btnProductList = $('div.detail__btn-product-list');
            const $menuItemList = $('div.detail__menu a');

            // Загрузка списка изделий
            $btnProductList.on({
                'click': () => page(ROUTE.list())
            });

            // Загрузка раздела
            $menuItemList.on({
                'click': e => page('/detail/' + productId + '/' + $(e.currentTarget).data('section'))
            });

            // Состояние сайдбара
            const menuState = localStorage.getItem(lsProduct_detailSidebarState);
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
                    localStorage.setItem(lsProduct_detailSidebarState, '1');
                }
            });
            $btnMenuClose.on({
                'click': () => {
                    $btnMenu.show();
                    $btnMenuClose.hide();
                    $menu.hide();
                    localStorage.setItem(lsProduct_detailSidebarState, '0');
                }
            });
        })
    </script>
</div>