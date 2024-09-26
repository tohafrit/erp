const SECTION_SCHEME = 'internalWaybill';
const PATH_SECTION_SCHEME = '/internal-waybill';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list'
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    LIST_ACCEPT: '/list/accept',
    LIST_MAT_VALUE: '/list/mat-value',
    LIST_MAT_VALUE_ADD: '/list/mat-value/add',
    LIST_MAT_VALUE_ADD_FILTER: '/list/mat-value/add/filter'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/',
    LIST_UNACCEPT: '/list/unaccept',
    LIST_ACCEPT_APPLY: '/list/accept/apply',
    LIST_MAT_VALUE_LOAD: '/list/mat-value/load',
    LIST_MAT_VALUE_DELETE: '/list/mat-value/delete/',
    LIST_MAT_VALUE_ADD_LOAD: '/list/mat-value/add/load',
    LIST_MAT_VALUE_ADD_APPLY: '/list/mat-value/add/apply'
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