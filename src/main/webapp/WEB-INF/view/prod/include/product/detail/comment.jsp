<div class="detail_comment__buttons">
    <i class="icon filter link detail_comment__btn-filter" title="<fmt:message key="label.button.filter"/>"></i>
    <i class="icon add link blue detail_comment__btn-add-comment" title="<fmt:message key="label.button.add"/>"></i>
</div>
<div class="detail_comment__table-block">
    <jsp:include page="/api/view/prod/product/detail/comment/filter"/>
    <div class="detail_comment__table-wrap">
        <div class="detail_comment__table table-sm table-striped"></div>
    </div>
</div>

<script>
    $(() => {
        const productId = '${productId}';
        const $filterForm = $('form.detail_comment_filter__form');
        const $filter = $('div.detail_comment_filter__main');
        const $btnSearch = $('div.detail_comment_filter__btn-search');
        const $btnAddComment = $('i.detail_comment__btn-add-comment');
        const $commentLink = $('a.detail__menu_comment');

        // Кнопка фильтра
        $('i.detail_comment__btn-filter').on({
            'click': function() {
                $(this).toggleClass('blue');
                $filter.toggle(!$filter.is(':visible'));
            }
        });

        const table = new Tabulator('div.detail_comment__table', {
            ajaxURL: '/api/action/prod/product/detail/comment/load',
            ajaxRequesting: (url, params) => {
                params.productId = productId;
                params.filterForm = formToJson($filterForm);
            },
            ajaxSorting: true,
            height: '100%',
            layout: 'fitColumns',
            columns: [
                TABR_COL_LOCAL_ROW_NUM,
                {
                    title: 'Комментарий',
                    field: TABR_FIELD.COMMENT,
                    variableHeight: true,
                    minWidth: 200,
                    width: 300,
                    formatter: 'textarea'
                },
                {
                    title: 'Дата и время создания',
                    field: TABR_FIELD.CREATE_DATE,
                    formatter: 'stdDatetime'
                },
                { title: 'Автор', field: TABR_FIELD.CREATED_BY }
            ],
            rowClick: (e, row) => {
                if (row.isSelected()) {
                    table.deselectRow();
                    sessionStorage.removeItem(ssProduct_selectedComment + productId);
                } else {
                    table.deselectRow();
                    row.select();
                    sessionStorage.setItem(ssProduct_selectedComment + productId, row.getData().id);
                }
            },
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
                sessionStorage.setItem(ssProduct_selectedComment + productId, row.getData().id);
            },
            dataLoaded: () => {
                const ssCommentSelectedId = sessionStorage.getItem(ssProduct_selectedComment + productId);
                let row = table.searchRows("id", "=", ssCommentSelectedId)[0];
                if (row !== undefined) {
                    row.select();
                    row.scrollTo();
                }
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                menu.push({
                    label: `<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>`,
                    action: () => editProductComment(data.id)
                });
                menu.push({
                    label: `<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>`,
                    action: () => deleteProductComment(data.id)
                });
                return menu;
            }
        });

        // Функция добавления/редактирования комментария
        function editProductComment(commentId) {
            $.modalWindow({
                loadURL: '/api/view/prod/product/detail/comment/edit',
                loadData: {
                    productId: productId,
                    commentId: commentId
                },
                submitURL: '/api/action/prod/product/detail/comment/edit/save',
                onSubmitSuccess: response => {
                    if (!commentId) {
                        sessionStorage.setItem(ssProduct_selectedComment + productId, response.attributes.addedCommentId);
                    }
                    $commentLink.trigger('click');
                }
            });
        }

        // Функция удаления пользовательского комментария
        function deleteProductComment(id) {
            confirmDialog({
                title: '<fmt:message key="product.detail.comment.delete.title"/>',
                message: '<fmt:message key="product.detail.comment.confirm"/>',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/product/detail/comment/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => {
                    sessionStorage.removeItem(ssProduct_selectedComment + productId);
                    table.setData();
                })
            });
        }

        // Поиск по фильтру
        $btnSearch.on({
            'click': () => table.setData()
        });

        // Кнопка добавления комментария к изделию
        $btnAddComment.on({
            'click': () => editProductComment()
        });

        $filter.enter(() => $btnSearch.trigger('click'));
    });
</script>