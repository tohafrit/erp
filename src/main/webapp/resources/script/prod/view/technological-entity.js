const SECTION_SCHEME = 'technologicalEntity';
const PATH_SECTION_SCHEME = '/technological-entity';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list',
    detail: (id, query) => id ? `/detail/${id}${query ? `?${query}` : ''}` : '/detail/:id'
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_EDIT: '/list/edit',
    LIST_EDIT_ADD_PRODUCT: '/list/edit/add-product',
    LIST_EDIT_ADD_PRODUCT_FILTER: '/list/edit/add-product/filter',
    LIST_APPLICABILITY: '/list/applicability',
    DETAIL: '/detail',
    DETAIL_EDIT: '/detail/edit',
    DETAIL_CHILD: '/detail/child',
    DETAIL_LABOUR: '/detail/labour',
    DETAIL_EDIT_ADD_AREA: '/detail/edit/add-area',
    DETAIL_EDIT_ADD_LABOR: '/detail/edit/add-labor',
    DETAIL_EDIT_ADD_EQUIPMENT: '/detail/edit/add-equipment',
    DETAIL_EDIT_ADD_TOOL: '/detail/edit/add-tool',
    DETAIL_EDIT_ADD_MATERIAL: '/detail/edit/add-material'
};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/',
    LIST_EDIT_ADD_PRODUCT_LOAD: '/list/edit/add-product/load',
    DETAIL_LOAD: '/detail/load',
    DETAIL_CHILD_LOAD: '/detail/child/load',
    DETAIL_LABOUR_LOAD: '/detail/labour/load',
    DETAIL_DELETE: '/detail/delete/',
    DETAIL_EDIT_SAVE: '/detail/edit/save',
    DETAIL_EDIT_ADD_AREA_LOAD: '/detail/edit/add-area/load',
    DETAIL_EDIT_ADD_LABOR_LOAD: '/detail/edit/add-labor/load',
    DETAIL_EDIT_ADD_EQUIPMENT_LOAD: '/detail/edit/add-equipment/load',
    DETAIL_EDIT_ADD_TOOL_LOAD: '/detail/edit/add-tool/load',
    DETAIL_EDIT_ADD_MATERIAL_LOAD: '/detail/edit/add-material/load',
    DETAIL_UPDATE_SORT: '/detail/update-sort'
};
const L_STORAGE = {};
const S_STORAGE = {
    LIST_QUERY: 'list.query'
};
const APPLICABILITY_TYPE = Object.freeze({
    WITHOUT: 1,
    PRIVATE: 2,
    TYPICAL_PRODUCT_GROUP: 3,
});
const SERVICE_SYMBOL_TYPE = Object.freeze({
    A: 'A',
    B: 'B',
    M: 'M',
    O: 'O',
    R: 'R',
    T: 'T'
})
$(() => {
    const $content = $('div.root__content');
    const listContainerSel = 'div.list__container';
    const detailContainerSel = 'div.detail__container';
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
            // Загружаем кэшированную страницу или обновляем контейнер
            const $listContainer = $(listContainerSel);
            const $detailContainer = $(detailContainerSel);
            if ($listContainer.length && $listContainer.is(':hidden')) {
                const table = Tabulator.prototype.findTable('div.list__table')[0];
                $listContainer.show();
                $detailContainer.remove();
                tabrScrollToRow(table);
            } else {
                $.get(VIEW_PATH.LIST).done(updContent);
            }
        });
        page(ROUTE.detail(), ctx => {
            const id = ctx.params.id;
            if (isNaN(parseInt(id))) {
                page.redirect(ROUTE.unknown);
            } else {
                const $detailContainer = $(detailContainerSel);
                $detailContainer.remove();
                $.get({ url: VIEW_PATH.DETAIL, data: { id: id } }).done(html => {
                    const $listContainer = $(listContainerSel);
                    $listContainer.hide();
                    $content.append(html);
                });
            }
        });
    };
    initPageView({ route: route });
});