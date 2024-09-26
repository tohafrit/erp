const SECTION_SCHEME = 'launch';
const PATH_SECTION_SCHEME = '/launch';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list',
    detail: (id, query) => id ? `/detail/${id}${query ? `?${query}` : ''}` : '/detail/:id'
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    LIST_ADDITIONAL: '/list/additional',
    LIST_ADDITIONAL_EDIT: '/list/additional/edit',
    DETAIL: '/detail'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/',
    LIST_APPROVE: '/list/approve',
    LIST_ADDITIONAL_LOAD: '/list/additional/load',
    LIST_ADDITIONAL_EDIT_SAVE: '/list/additional/edit/save',
    LIST_ADDITIONAL_DELETE: '/list/additional/delete/',
    LIST_ADDITIONAL_APPROVE: '/list/additional/approve',
    DETAIL_LOAD: '/detail/load'
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