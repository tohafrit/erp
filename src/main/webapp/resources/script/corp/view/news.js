const SECTION_SCHEME = 'news';
const PATH_SECTION_SCHEME = '/news';
const ROUTE = {
    list: '/list',
    listId: '/list/:id'
};
const VIEW_PATH = {
    list: '/list',
    listId: '/list/:id'
};
$(() => {
    const $content = $('div.content__news-block');
    const appendContent = html => $content.html(html);
    const route = () => {
        page(ROUTE.empty, () => page.redirect(ROUTE.list));
        page(ROUTE.list, () => $.get({ url: VIEW_PATH.list }).done(appendContent));
        page(ROUTE.listId, ctx => $.get({ url: `${VIEW_PATH.list}/${ctx.params.id}`}).done(appendContent));
    };
    initPageView({ route: route });
});