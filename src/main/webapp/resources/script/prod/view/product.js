const SECTION_SCHEME = 'product';
const PATH_SECTION_SCHEME = '/product';
const ROUTE = {
    list: query => query ? `/list?${query}` : '/list'
};
const VIEW_PATH = {
    LIST: '/api/view/prod/product/list',
    LIST_FILTER: '/api/view/prod/product/list/filter',
    LIST_REPORT: '/api/view/prod/product/list/report',
    LIST_EDIT: '/api/view/prod/product/list/edit',
    DETAIL: '/api/view/prod/product/detail',
    DETAIL_DECIPHERMENT_ADD: '/api/view/prod/product/detail/decipherment/add',
    DETAIL_DECIPHERMENT_EDIT: '/api/view/prod/product/detail/decipherment/edit',
    DETAIL_DECIPHERMENT_EDIT_FORM9_LABOUR_INTENSITY: '/api/view/prod/product/detail/decipherment/edit-form9/labour-intensity',
    DETAIL_DECIPHERMENT_EDIT_FORM9_LABOUR_INTENSITY_FILTER: '/api/view/prod/product/detail/decipherment/edit-form9/labour-intensity/filter',
    DETAIL_DECIPHERMENT_EDIT_FORM9_COST_JUSTIFICATION: '/api/view/prod/product/detail/decipherment/edit-form9/cost-justification',
    DETAIL_DECIPHERMENT_EDIT_FORM9_COST_JUSTIFICATION_FILTER: '/api/view/prod/product/detail/decipherment/edit-form9/cost-justification/filter',
    DETAIL_DECIPHERMENT_PERIOD: '/api/view/prod/product/detail/decipherment/period',
    DETAIL_DECIPHERMENT_PERIOD_EDIT: '/api/view/prod/product/detail/decipherment/period/edit',
    DETAIL_DECIPHERMENT_PERIOD_EDIT_REPORT_PERIOD: '/api/view/prod/product/detail/decipherment/period/edit/report-period',
    DETAIL_DECIPHERMENT_PERIOD_EDIT_PLAN_PERIOD: '/api/view/prod/product/detail/decipherment/period/edit/plan-period',
    DETAIL_DECIPHERMENT_PERIOD_EDIT_PLAN_PERIOD_FILTER: '/api/view/prod/product/detail/decipherment/period/edit/plan-period/filter',
    DETAIL_DECIPHERMENT_EDIT_FORM18_REVIEW_JUSTIFICATION: '/api/view/prod/product/detail/decipherment/edit-form18/review-justification',
    DETAIL_DECIPHERMENT_EDIT_FORM18_REVIEW_JUSTIFICATION_FILTER: '/api/view/prod/product/detail/decipherment/edit-form18/review-justification/filter',
    DETAIL_DECIPHERMENT_EDIT_FORM18_RESEARCH_JUSTIFICATION: '/api/view/prod/product/detail/decipherment/edit-form18/research-justification',
    DETAIL_DECIPHERMENT_EDIT_FORM18_RESEARCH_JUSTIFICATION_FILTER: '/api/view/prod/product/detail/decipherment/edit-form18/research-justification/filter',
    DETAIL_DECIPHERMENT_EDIT_FORM2_ECO_INDICATOR: '/api/view/prod/product/detail/decipherment/edit-form2/eco-indicator',
    DETAIL_DECIPHERMENT_EDIT_FORM1_CUSTOMER_FILTER: '/api/view/prod/product/detail/decipherment/edit-form1/customer/filter',
    DETAIL_DECIPHERMENT_EDIT_FORM1_CUSTOMER: '/api/view/prod/product/detail/decipherment/edit-form1/customer',
    DETAIL_DECIPHERMENT_ANALYSIS: '/api/view/prod/product/detail/decipherment/analysis'
};
const ACTION_PATH = {
    LIST_LOAD: '/api/action/prod/product/list/load',
    LIST_EDIT_SAVE: '/api/action/prod/product/list/edit/save',
    LIST_DELETE: '/api/action/prod/product/list/delete/',
    DETAIL_DECIPHERMENT_DELETE: '/api/action/prod/product/detail/decipherment/delete/',
    DETAIL_DECIPHERMENT_LOAD: '/api/action/prod/product/detail/decipherment/load',
    DETAIL_DECIPHERMENT_EDIT_SAVE: '/api/action/prod/product/detail/decipherment/edit/save',
    DETAIL_DECIPHERMENT_ADD_SELECT: '/api/action/prod/product/detail/decipherment/add/select',
    DETAIL_DECIPHERMENT_PERIOD_LOAD: '/api/action/prod/product/detail/decipherment/period/load',
    DETAIL_DECIPHERMENT_PERIOD_EDIT_SAVE: '/api/action/prod/product/detail/decipherment/period/edit/save',
    DETAIL_DECIPHERMENT_PERIOD_DELETE: '/api/action/prod/product/detail/decipherment/period/delete/',
    DETAIL_DECIPHERMENT_PERIOD_EDIT_REPORT_PERIOD_LOAD: '/api/action/prod/product/detail/decipherment/period/edit/report-period/load',
    DETAIL_DECIPHERMENT_PERIOD_EDIT_PLAN_PERIOD_LOAD: '/api/action/prod/product/detail/decipherment/period/edit/plan-period/load',
    DETAIL_DECIPHERMENT_DOWNLOAD_FORM: '/api/action/prod/product/detail/decipherment/download-form',
    DETAIL_DECIPHERMENT_EDIT_FORM9_LABOUR_INTENSITY_LOAD: '/api/action/prod/product/detail/decipherment/edit-form9/labour-intensity/load',
    DETAIL_DECIPHERMENT_EDIT_FORM9_COST_JUSTIFICATION_LOAD: '/api/action/prod/product/detail/decipherment/edit-form9/cost-justification/load',
    DETAIL_DECIPHERMENT_EDIT_FORM9_WORK_COST: '/api/action/prod/product/detail/decipherment/edit-form9/work-cost',
    DETAIL_DECIPHERMENT_EDIT_FORM18_REVIEW_JUSTIFICATION_LOAD: '/api/action/prod/product/detail/decipherment/edit-form18/review-justification/load',
    DETAIL_DECIPHERMENT_EDIT_FORM18_RESEARCH_JUSTIFICATION_LOAD: '/api/action/prod/product/detail/decipherment/edit-form18/research-justification/load',
    DETAIL_DECIPHERMENT_EDIT_FORM2_ECO_INDICATOR_LOAD: '/api/action/prod/product/detail/decipherment/edit-form2/eco-indicator/load',
    DETAIL_DECIPHERMENT_EDIT_FORM1_CUSTOMER_LOAD: '/api/action/prod/product/detail/decipherment/edit-form1/customer/load',
    DETAIL_DECIPHERMENT_ANALYSIS_CREATE: '/api/action/prod/product/detail/decipherment/analysis/create',
    DETAIL_DECIPHERMENT_ANALYSIS_LOAD: '/api/action/prod/product/detail/decipherment/analysis/load',
    DETAIL_DECIPHERMENT_ANALYSIS_SAVE: '/api/action/prod/product/detail/decipherment/analysis/save',
    DETAIL_DECIPHERMENT_APPROVE: '/api/action/prod/product/detail/decipherment/approve',
    DETAIL_DECIPHERMENT_DOWNLOAD_APPROVED_FORMS: '/api/action/prod/product/detail/decipherment/download-approved-forms'
};
const L_STORAGE = {};
const S_STORAGE = {
    LIST_QUERY: 'list.query'
};