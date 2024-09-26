const SECTION_SCHEME = 'productionShipmentLetter';
const PATH_SECTION_SCHEME = '/production-shipment-letter';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list',
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    LIST_LETTER_INFO: '/list/letter-info',
    LIST_EDIT_CONTRACT: '/list/edit/contract',
    LIST_EDIT_CONTRACT_FILTER: '/list/edit/contract/filter',
    LIST_EDIT_CONTRACT_PRODUCT: '/list/edit/contract/product',
    LIST_EDIT_CONTRACT_PRODUCT_FILTER: '/list/edit/contract/product/filter'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_DELETE: '/list/delete/',
    LIST_EDIT_DELETE: '/list/edit/delete',
    LIST_EDIT_CONTRACT_LOAD: '/list/edit/contract/load',
    LIST_EDIT_CONTRACT_PRODUCT_LOAD: '/list/edit/contract/product/load',
    LIST_EDIT_CONTRACT_PRODUCT_SELECT: '/list/edit/contract/product/select',
    LIST_EDIT_PRODUCTS_LOAD: '/list/edit/products/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DISTRIBUTION_LOAD: '/list/distribution/load',

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