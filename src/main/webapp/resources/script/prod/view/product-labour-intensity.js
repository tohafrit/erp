const SECTION_SCHEME = 'productLabourIntensity';
const PATH_SECTION_SCHEME = '/product-labour-intensity';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list',
    detail: (id, query) => id ? `/detail/${id}${query ? `?${query}` : ''}` : '/detail/:id'
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    DETAIL: '/detail',
    DETAIL_FILTER: '/detail/filter',
    DETAIL_ADD: '/detail/add',
    DETAIL_EDIT: '/detail/edit',
    DETAIL_WORK_TYPE: '/detail/work-type'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/',
    DETAIL_LOAD: '/detail/load',
    DETAIL_DELETE: '/detail/delete/',
    DETAIL_EDIT_SAVE: '/detail/edit/save',
    DETAIL_APPROVE: '/detail/approve',
    DETAIL_ADD_IMPORT: '/detail/add/import',
    DETAIL_ADD_APPLY: '/detail/add/apply'
};
const L_STORAGE = {};
const S_STORAGE = {
    LIST_QUERY: 'list.query',
    DETAIL_QUERY: 'detail.query'
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
        page(ROUTE.detail(), ctx => {
            const id = ctx.params.id;
            if (isNaN(parseInt(id))) {
                page.redirect(ROUTE.unknown);
            } else {
                const $detailContainer = $(detailContainerSel);
                $detailContainer.remove();
                $.get({ url: VIEW_PATH.DETAIL, data: { id: id } }).done(html => {
                    const $listContainer = $(listContainerSel);
                    $listContainer.hide();
                    $content.append(html);
                });
            }
        });
    };
    initPageView({ route: route });
});