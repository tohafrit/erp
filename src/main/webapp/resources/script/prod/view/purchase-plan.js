const SECTION_SCHEME = 'purchasePlan';
const PATH_SECTION_SCHEME = '/purchase-plan';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list',
    detail: query => query ? `/detail?${query}` : '/detail'
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/',
    LIST_INFO: '/list/info/',
    LIST_APPROVE: '/list/approve',
};
const L_STORAGE = {};
const S_STORAGE = {
    LIST_QUERY: 'list.query'
};
$(() => {
    const $content = $('div.root__content');
    const listContainerSel = 'div.list__container';
    const detailContainerSel = 'div.detail__container';
    const updContent = html => $content.html(html);
    //
    const route = () => {
        page(ROUTE.empty, () => page.redirect(ROUTE.list()));
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
                tabrScrollToRow(table);
            } else {
                $.get({ url: VIEW_PATH.LIST }).done(updContent);
            }
        });
    };
    initPageView({ route: route });
});