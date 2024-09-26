const SECTION_SCHEME = 'account';
const PATH_SECTION_SCHEME = '/account';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list',
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    LIST_EDIT_CUSTOMER: '/list/edit/customer',
    LIST_EDIT_BANK: '/list/edit/bank',
    LIST_EDIT_CUSTOMER_FILTER: '/list/edit/customer/filter',
    LIST_EDIT_BANK_FILTER: '/list/edit/bank/filter'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_EDIT_CUSTOMER_SELECTED_LOAD: '/list/edit/customer-selected/load',
    LIST_EDIT_BANK_SELECTED_LOAD: '/list/edit/bank-selected/load',
    LIST_EDIT_CUSTOMER_LOAD: '/list/edit/customer/load',
    LIST_EDIT_BANK_LOAD: '/list/edit/bank/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/'
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