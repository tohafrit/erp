<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/prod/view/product.css"/>">
<script type="text/javascript" src="<c:url value="/resources/script/prod/view/product.js"/>"></script>
<div class="root__content"></div>

<script>
    $(() => {
        const $content = $('div.root__content');
        const listContainerSel = 'div.list__container';
        const detailContainerSel = 'div.detail__main';
        const updContent = html => $content.html(html);

        const detailMenuMap = new Map();
        detailMenuMap.set('general', { selector: 'a.detail__menu_general', url: '/api/view/prod/product/detail/general' });
        detailMenuMap.set('structure', { selector: 'a.detail__menu_structure', url: '/api/view/prod/product/detail/structure' });
        detailMenuMap.set('specification', { selector: 'a.detail__menu_specification', url: '/api/view/prod/product/detail/specification' });
        detailMenuMap.set('documentation', { selector: 'a.detail__menu_documentation', url: '/api/view/prod/product/detail/documentation' });
        detailMenuMap.set('occurrence', { selector: 'a.detail__menu_occurrence', url: '/api/view/prod/product/detail/occurrence' });
        detailMenuMap.set('comment', { selector: 'a.detail__menu_comment', url: '/api/view/prod/product/detail/comment' });
        detailMenuMap.set('decipherment', { selector: 'a.detail__menu_decipherment', url: '/api/view/prod/product/detail/decipherment' });
        // Роуты
        page.base('/prod/product');
        page('/', () => page.redirect('/list'));
        // Детализация по изделию с выбранной секцией
        page('/detail/:id/:section', ctx => {
            const id = ctx.params.id;
            const section = ctx.params.section;
            if (!detailMenuMap.has(section)) {
                document.location.href = '/404';
                return;
            }
            // Редиректы, если имеем параметры для загрузки страницы
            const usp = new URLSearchParams(ctx.querystring);
            // Спецификация
            if (section === 'specification') {
                const specUrl = '/detail/' + id + '/specification';
                const loadLastAcceptOrApproved = usp.get('loadLastAcceptOrApproved');
                if (loadLastAcceptOrApproved) {
                    page(specUrl, { loadLastAcceptOrApproved: true });
                    return;
                }
                const selectedBomId = usp.get('selectedBomId');
                if (selectedBomId) {
                    page(specUrl, { selectedBomId: selectedBomId });
                    return;
                }
            }
            const $listContainer = $(listContainerSel);
            // Загрузка окна детализации по изделию
            $.get({
                url: VIEW_PATH.DETAIL,
                data: { id: id }
            }).done(html => {
                $listContainer.hide();
                $(detailContainerSel).remove();
                $content.append(html);
                const menuData = detailMenuMap.get(section);
                // Переключение меню
                const $menu = $('div.detail__menu');
                $menu.find('a').removeClass('active');
                const $sectionLink = $(menuData.selector);
                $sectionLink.addClass('active');
                // Сборка параметров запроса
                const reqData = { productId: id };
                if (section === 'specification') {
                    reqData.loadLastAcceptOrApproved = ctx.state.loadLastAcceptOrApproved;
                    reqData.selectedBomId = ctx.state.selectedBomId;
                }
                if (section === 'decipherment') {
                    const periodId = usp.get('periodId');
                    if (periodId) reqData.periodId = periodId;
                }
                // Загрузка секции
                $.get({ url: menuData.url, data: reqData })
                .done(html => $('div.detail__content').html(html))
                .fail(() => document.location.href = '/404');
            })
            .fail(() => document.location.href = '/404');
        });
        // Список изделий
        page(ROUTE.list(), ctx => {
            let query = ctx.querystring;
            if (query) {
                sessionStorage.setItem(S_STORAGE.LIST_QUERY, query);
            } else {
                query = sessionStorage.getItem(S_STORAGE.LIST_QUERY);
                if (query) {
                    page.redirect(ROUTE.list(query));
                    return;
                }
            }
            // Загружаем кэшированную страницу или обновляем контейнер
            const $listContainer = $(listContainerSel);
            const $detailContainer = $(detailContainerSel);
            if ($listContainer.length && $listContainer.is(':hidden')) {
                const table = Tabulator.prototype.findTable('div.list__table')[0];
                $listContainer.show();
                $detailContainer.remove();
                tabrScrollToRow(table, 'top');
            } else {
                $.get({ url: VIEW_PATH.LIST }).done(updContent);
            }
        });
        page('*', () => document.location.href = '/404');
        page({
            click: false,
            decodeURLComponents: false
        });
    })
</script>