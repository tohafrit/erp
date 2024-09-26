const SECTION_SCHEME = 'presentLogRecord';
const PATH_SECTION_SCHEME = '/present-log-record';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list',
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    LIST_CREATE_PACKAGE: '/list/create-package',
    LIST_LOG_RECORD_INFO: '/list/log-record-info',
    LIST_EDIT_LETTER: '/list/edit/letter',
    LIST_EDIT_CONFORMITY_STATEMENT: '/list/edit/conformity-statement',
    LIST_EDIT_SERIAL_NUMBER: '/list/edit/serial-number',
    LIST_EDIT_LETTER_FILTER: '/list/edit/letter/filter',
    LIST_EDIT_LETTER_INFO: '/list/edit/letter/info'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_LOG_RECORD_INFO_LOAD: '/list/log-record-info/load',
    LIST_EDIT_SERIAL_NUMBER_LOAD: '/list/edit/serial-number/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/',
    LIST_EDIT_DELETE: '/list/edit/delete/',
    LIST_PACK: '/list/pack',
    LIST_LOG_RECORD_INFO_DELETE: '/list/log-record-info/delete/',
    LIST_EDIT_LETTER_LOAD: '/list/edit/letter/load',
    LIST_EDIT_LETTER_INFO_LOAD: '/list/edit/letter/info/load'
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