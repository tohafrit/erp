const SECTION_SCHEME = 'warehouseState';
const PATH_SECTION_SCHEME = '/warehouse-state';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list'
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_MAT_VALUE: '/list/mat-value',
    LIST_MAT_VALUE_HISTORY: '/list/mat-value/history',
    LIST_REPORT_FIRST: '/list/report-first',
    LIST_REPORT_SECOND: '/list/report-second',
    LIST_REPORT_THIRD: '/list/report-third',
    LIST_REPORT_FIRST_PRODUCT: '/list/report-first/product',
    LIST_REPORT_FIRST_PRODUCT_FILTER: '/list/report-first/product/filter'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_GENERATE_REPORT: '/list/generate-report',
    LIST_MAT_VALUE_LOAD: '/list/mat-value/load',
    LIST_MAT_VALUE_HISTORY_LOAD: '/list/mat-value/history/load',
    LIST_REPORT_FIRST_EXEC: '/list/report-first/exec',
    LIST_REPORT_FIRST_PRODUCT_LOAD: '/list/report-first/product/load'
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