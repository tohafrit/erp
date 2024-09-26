const SECTION_SCHEME = 'productWorkCostJustification';
const PATH_SECTION_SCHEME = '/product-work-cost-justification';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list'
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    LIST_EDIT_ADD_WORK: '/list/edit/add-work',
    LIST_WORK_COST: '/list/work-cost'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/',
    LIST_EDIT_ADD_WORK_LOAD: '/list/edit/add-work/load'
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