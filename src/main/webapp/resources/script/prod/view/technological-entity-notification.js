const SECTION_SCHEME = 'technologicalEntityNotification';
const PATH_SECTION_SCHEME = '/technological-entity-notification';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list',
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    LIST_EDIT_ADD_TECHNOLOGICAL_ENTITY: '/list/edit/add-technological-entity',
    LIST_EDIT_ADD_TECHNOLOGICAL_ENTITY_FILTER: '/list/edit/add-technological-entity/filter',
    LIST_EDIT_ADD_NOTIFICATION: '/list/edit/add-notification',
    LIST_EDIT_ADD_NOTIFICATION_FILTER: '/list/edit/add-notification/filter'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/',
    LIST_EDIT_ADD_TECHNOLOGICAL_ENTITY_LOAD: '/list/edit/add-technological-entity/load',
    LIST_EDIT_ADD_NOTIFICATION_LOAD: '/list/edit/add-notification/load'
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