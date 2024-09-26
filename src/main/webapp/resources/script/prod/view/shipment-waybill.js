const SECTION_SCHEME = 'shipmentWaybill';
const PATH_SECTION_SCHEME = '/shipment-waybill';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list'
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    LIST_EDIT_CONTRACT: '/list/edit/contract',
    LIST_EDIT_CONTRACT_FILTER: '/list/edit/contract/filter',
    LIST_SHIPMENT: '/list/shipment',
    LIST_CHECK_SHIPMENT: '/list/check-shipment',
    LIST_MAT_VALUE: '/list/mat-value',
    LIST_MAT_VALUE_ADD: '/list/mat-value/add',
    LIST_MAT_VALUE_ADD_FILTER: '/list/mat-value/add/filter'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_EDIT_LOAD_ACCOUNT_DATA: '/list/edit/load-account-data',
    LIST_EDIT_CONTRACT_LOAD: '/list/edit/contract/load',
    LIST_CHECK_SHIPMENT_LOAD: '/list/check-shipment/load',
    LIST_CHECK_SHIPMENT_SAVE: '/list/check-shipment/save',
    LIST_UNSHIPMENT: '/list/unshipment',
    LIST_SHIPMENT_APPLY: '/list/shipment/apply',
    LIST_DELETE: '/list/delete/',
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