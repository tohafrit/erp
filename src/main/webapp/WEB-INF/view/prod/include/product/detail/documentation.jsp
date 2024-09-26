<div class="detail_documentation__table-wrap">
    <i class="icon add link blue detail_documentation__btn-add" title="<fmt:message key="label.button.add"/>"></i>
    <div class="detail_documentation__table table-sm table-striped"></div>
</div>

<script>
    $(() => {
        const productId = '${product.id}';
        const $btnAdd = $('i.detail_documentation__btn-add');

        const table = new Tabulator('div.detail_documentation__table', {
            selectable: 1,
            pagination: 'remote',
            ajaxURL: '/api/action/prod/product/detail/documentation/load',
            ajaxRequesting: (url, params) => {
                params.productId = productId;
            },
            ajaxSorting: true,
            height: 'calc(100vh - 170px)',
            columns: [
                TABR_COL_REMOTE_ROW_NUM,
                TABR_COL_ID,
                { title: 'Наименование', field: TABR_FIELD.NAME },
                {
                    title: '',
                    field: 'fileHash',
                    headerSort: false,
                    formatter: 'fileLink'
                },
                { title: 'Комментарий', field: TABR_FIELD.COMMENT, formatter: 'textarea' }
            ],
            rowContext: (e, row) => {
                table.deselectRow();
                row.select();
            },
            rowContextMenu: row => {
                const menu = [];
                const data = row.getData();
                const id = data.id;
                menu.push({
                    label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
                    action: () => editDoc(id)
                });
                menu.push({
                    label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
                    action: () => deleteDoc(id)
                });
                return menu;
            }
        });

        // Скролл до строки и ее выбор по id
        function rowScrollSelect(id) {
            table.selectRow(id);
            table.scrollToRow(id, 'middle', false);
        }

        // Редактирование/добавление
        function editDoc(id) {
            $.modalWindow({
                loadURL: '/api/view/prod/product/detail/documentation/edit',
                loadData: { id: id, productId: productId },
                submitURL: '/api/action/prod/product/detail/documentation/edit/save',
                onSubmitSuccess: resp => {
                    if (id) {
                        table.setPage(table.getPage()).then(() => rowScrollSelect(id));
                    } else {
                        const id = resp.attributes.id;
                        if (id) {
                            table.setSort(TABR_SORT_ID_DESC);
                            table.setPage(1).then(() => rowScrollSelect(id));
                        }
                    }
                }
            });
        }

        // Добавление
        $btnAdd.on({
            'click': () => editDoc()
        });

        // Удаление
        function deleteDoc(id) {
            confirmDialog({
                title: 'Удаление документации',
                message: 'Вы действительно хотите удалить документацию?',
                onAccept: () => $.ajax({
                    method: 'DELETE',
                    url: '/api/action/prod/product/detail/documentation/delete/' + id,
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => table.setPage(table.getPage()))
            });
        }
    });
</script>