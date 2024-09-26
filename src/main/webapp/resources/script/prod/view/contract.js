const SECTION_SCHEME = 'contract';
const PATH_SECTION_SCHEME = '/contract';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list',
    detail: query => query ? `/detail?${query}` : '/detail'
};
const VIEW_PATH = {
    LIST: '/list',
    LIST_FILTER: '/list/filter',
    LIST_STRUCTURE: '/list/structure',
    LIST_EDIT: '/list/edit',
    LIST_EDIT_CUSTOMER: '/list/edit/customer',
    LIST_EDIT_CUSTOMER_FILTER: '/list/edit/customer/filter',
    DETAIL: '/detail',
    DETAIL_GENERAL: '/detail/general',
    DETAIL_GENERAL_EDIT: '/detail/general/edit',
    DETAIL_DELIVERY_STATEMENT: '/detail/delivery-statement',
    DETAIL_DELIVERY_STATEMENT_FILTER: '/detail/delivery-statement/filter',
    DETAIL_DELIVERY_STATEMENT_ADD: '/detail/delivery-statement/add',
    DETAIL_DELIVERY_STATEMENT_ADD_FILTER: '/detail/delivery-statement/add/filter',
    DETAIL_DELIVERY_STATEMENT_EDIT: '/detail/delivery-statement/edit',
    DETAIL_DELIVERY_STATEMENT_STRUCTURE: '/detail/delivery-statement/distribution',
    DETAIL_DELIVERY_STATEMENT_DISTRIBUTE_SPLIT: '/detail/delivery-statement/distribution/split',
    DETAIL_DOCUMENTATION: '/detail/documentation',
    DETAIL_DETAIL_DOCUMENTATION_EDIT: '/detail/documentation/edit',
    DETAIL_PAYMENTS: '/detail/payments',
    DETAIL_PAYMENTS_FILTER: '/detail/payments/filter',
    DETAIL_PAYMENTS_EDIT: '/detail/payment/edit',
    DETAIL_PAYMENTS_EDIT_ACCOUNTS: '/detail/payments/edit/accounts',
    DETAIL_PAYMENTS_EDIT_FILTER: '/detail/payments/edit/filter',
    DETAIL_PAYMENTS_DISTRIBUTE: '/detail/payment/distribute',
    DETAIL_DELIVERY_STATEMENT_DISTRIBUTE_EDIT_PRODUCTS_LAUNCH: '/detail/delivery-statement/distribution/edit-products-launch',
    DETAIL_INVOICES: '/detail/invoices',
    DETAIL_INVOICES_ADD: '/detail/invoices/add',
    DETAIL_INVOICES_ADD_FILTER: '/detail/invoices/add/filter',
    DETAIL_INVOICES_ADD_ACCOUNTS: '/detail/invoices/add/accounts',
    DETAIL_INVOICES_EDIT_ACCOUNTS: '/detail/invoices/edit/accounts',
    DETAIL_INVOICES_EDIT_FILTER: '/detail/invoices/edit/filter',
    DETAIL_DOCUMENTATION_FORMED_EDIT: '/detail/documentation-formed/edit'

};
const ACTION_PATH = {
    LIST_LOAD: '/list/load',
    LIST_STRUCTURE_LOAD: '/list/structure/load',
    LIST_EDIT_CUSTOMER_LOAD: '/list/edit/customer/load',
    LIST_EDIT_LOAD: '/list/edit/load',
    LIST_EDIT_SAVE: '/list/edit/save',
    LIST_DELETE: '/list/delete/',
    LIST_SECTION_DELETE: '/list/section-delete/',
    LIST_APPROVE: '/list/approve',
    LIST_SEND: '/list/send',
    DETAIL_GENERAL_SEND: '/detail/general/send',
    DETAIL_GENERAL_APPROVE: '/detail/general/approve',
    DETAIL_GENERAL_EDIT_SAVE: '/detail/general/edit/save',
    DETAIL_DELIVERY_STATEMENT_LOAD: '/detail/delivery-statement/load',
    DETAIL_DELIVERY_STATEMENT_RESULT_LOAD: '/detail/delivery-statement/result-load',
    DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_LOAD: '/detail/delivery-statement/distribution-load',
    DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_SPLIT_CHANGE_QUANTITY: '/detail/delivery-statement/distribution/split/change-quantity',
    DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_SPLIT_SAVE: '/detail/delivery-statement/distribution/split/save',
    DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_EDIT_PRODUCTS_LAUNCH_LOAD: '/detail/delivery-statement/distribution/edit-products-launch-load',
    DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_EDIT_PRODUCTS_LAUNCH_SAVE: '/detail/delivery-statement/distribution/edit-products-launch/save',
    DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_EDIT_PRODUCTS_LAUNCH_DELETE: '/detail/delivery-statement/distribution/edit-products-launch/delete',
    DETAIL_DELIVERY_STATEMENT__DISTRIBUTE_UNITE: '/detail/delivery-statement/distribution/unite',
    DETAIL_DELIVERY_STATEMENT_DELETE: '/detail/delivery-statement/delete/',
    DETAIL_DELIVERY_STATEMENT_PRODUCTS_LOAD: '/detail/delivery-statement/products/load',
    DETAIL_DELIVERY_STATEMENT_EDIT_NEEDED_PRICE: '/detail/delivery-statement/edit/needed-price',
    DETAIL_DELIVERY_STATEMENT_EDIT_SAVE: '/detail/delivery-statement/edit/save',
    DETAIL_DELIVERY_STATEMENT_ADD_NEEDED_PREFIX: '/detail/delivery-statement/add/needed-prefix',
    DETAIL_PAYMENTS_LOAD: '/detail/payments/load',
    DETAIL_PAYMENTS_RESULT_LOAD: '/detail/payments/result-load',
    DETAIL_PAYMENTS_EDIT_ACCOUNTS_LOAD: '/detail/payments/edit/accounts-load',
    DETAIL_INVOICE_LOAD: '/detail/invoices/load',
    DETAIL_INVOICE_CANCELED_INVOICE: '/detail/invoices/canceled-invoice',
    DETAIL_INVOICE_CLOSED_INVOICE: '/detail/invoices/closed-invoice',
    DETAIL_INVOICE_ACTING_INVOICE: '/detail/invoices/acting-invoice',
    DETAIL_INVOICE_ADD_ACCOUNTS_LOAD: '/detail/invoices/add/accounts-load',
    DETAIL_INVOICE_EDIT_ACCOUNTS_LOAD: '/detail/invoices/edit/accounts-load',
    DETAIL_INVOICE_ADD_SELECTED_ACCOUNT_LOAD: '/detail/invoices/add/selected-account-load',
    DETAIL_INVOICE_EDIT_SELECTED_ACCOUNT_LOAD: '/detail/invoices/edit/selected-account-load',
    DETAIL_INVOICE_ADD_PRODUCTS_LOAD: '/detail/invoices/add/products-load',
    DETAIL_INVOICE_ADD_SAVE: '/detail/invoices/add/save',
    DETAIL_PAYMENT_EDIT_SAVE: '/detail/payments/edit/save',
    DETAIL_PAYMENT_EDIT_PRODUCTS_LOAD: '/detail/payment/edit/products-load',
    DETAIL_PAYMENT_EDIT_SELECTED_ACCOUNT_LOAD: '/detail/payments/edit/selected-account-load',
    DETAIL_PAYMENTS_DISTRIBUTE_CALCULATE: '/detail/payments/distribute/calculate',
    DETAIL_PAYMENTS_DISTRIBUTE_NULLIFY: '/detail/payments/distribute/Nullify',
    DETAIL_PAYMENTS_DISTRIBUTE_SAVE: '/detail/payments/distribute/save',
    DETAIL_INVOICE_DELETE: '/detail/invoice/delete/',
    DETAIL_PAYMENT_DELETE: '/detail/payment/delete/',
    DETAIL_DOCUMENTATION_LOAD: '/detail/documentation/load',
    DETAIL_DOCUMENTATION_FORMED_LOAD: '/detail/documentation-formed/load',
    DETAIL_DOCUMENTATION_EDIT_SAVE: '/detail/documentation/edit/save',
    DETAIL_DOCUMENTATION_DELETE: '/detail/documentation/delete/',
    DETAIL_DOCUMENTATION_FORMED_WITH_INVOICE_INFO_LOAD: '/detail/documentation-formed/with-invoice-info/load',
    DETAIL_DOCUMENTATION_FORMED_WITH_INVOICE_INFO_SAVE: '/detail/documentation-formed/with-invoice-info/save',
    DETAIL_DOCUMENTATION_FORMED_WITH_PAYMENT_INFO_LOAD: '/detail/documentation-formed/with-payment-info/load',
    DETAIL_DOCUMENTATION_FORMED_WITH_PAYMENT_INFO_SAVE: '/detail/documentation-formed/with-payment-info/save',
    DETAIL_DOCUMENTATION_FORMED_WITH_ACCOUNT_INFO_LOAD: '/detail/documentation-formed/with-account-info/load',
    DETAIL_DOCUMENTATION_FORMED_WITH_ACCOUNT_INFO_SAVE: '/detail/documentation-formed/with-account-info/save',
    DETAIL_DOCUMENTATION_FORMED_WITH_ALLOTMENT_INFO_LOAD: '/detail/documentation-formed/with-allotment-info/load',
    DETAIL_DOCUMENTATION_FORMED_WITH_ALLOTMENT_INFO_SAVE: '/detail/documentation-formed/with-allotment-info/save',
    DETAIL_DOCUMENTATION_FORMED_SAVE: '/detail/documentation-formed/save',
    DETAIL_DOCUMENTATION_FORMED_DELETE: '/detail/documentation-formed/delete/'
};
const L_STORAGE = {};
const S_STORAGE = {
    LIST_QUERY: 'list.query'
};
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
                $.get({ url: VIEW_PATH.LIST }).done(updContent);
            }
        });
        page(ROUTE.detail(), ctx => {
            const usp = new URLSearchParams(ctx.querystring);
            const contractId = usp.get('contractId');
            const sectionId = usp.get('sectionId');
            if (isNaN(parseInt(contractId)) || isNaN(parseInt(sectionId))) {
                page.redirect(ROUTE.unknown);
            } else {
                const $detailContainer = $(detailContainerSel);
                $detailContainer.remove();
                $.get({ url: VIEW_PATH.DETAIL, data: { contractId: contractId, sectionId: sectionId } }).done(html => {
                    const $listContainer = $(listContainerSel);
                    $listContainer.hide();
                    $content.append(html);
                });
            }
        });
    };
    initPageView({ route: route });
});