const SECTION_SCHEME = 'launchNote';
const PATH_SECTION_SCHEME = '/launch-note';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list',
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    LIST_PRODUCT: '/list/product',
    LIST_PRODUCT_FILTER: '/list/product/filter'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/',
    LIST_AGREEMENT: '/list/agreement',
    LIST_PRODUCT_LOAD: '/list/product/load'
};
const L_STORAGE = {};
const S_STORAGE = {
    LIST_QUERY: 'list.query'
};
$(() => {
    const $content = $('div.root__content');
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
            $.get({ url: VIEW_PATH.LIST }).done(updContent);
        });
    };
    initPageView({ route: route });
});