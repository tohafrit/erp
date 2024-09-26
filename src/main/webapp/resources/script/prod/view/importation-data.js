const SECTION_SCHEME = 'importationData';
const PATH_SECTION_SCHEME = '/importation-data';
const ROUTE = {
    list: '/list'
};
const VIEW_PATH = {
    LIST: '/list'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_START: '/list/start',
    LIST_STOP: '/list/stop'
};
const L_STORAGE = {};
const S_STORAGE = {};
$(() => {
    const $content = $('div.root__content');
    const updContent = html => $content.html(html);
    const route = () => {
        page(ROUTE.empty, () => page.redirect(ROUTE.list()));
        page(ROUTE.list(), () => $.get({ url: VIEW_PATH.LIST }).done(updContent));
    };
    initPageView({ route: route });
});