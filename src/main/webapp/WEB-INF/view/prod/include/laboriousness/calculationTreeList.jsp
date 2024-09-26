<ul class="js-calculation-tree tree-list-view" data-main-id="${mainEntityId}"
    data-tree-list-view-options='{
            "nodeChoiceMode":"none",
            "elementClickExpandAll": ".js-expand-tree",
            "elementClickCollapseAll": ".js-collapse-tree",
            "inputSearchField": ".js-search-process",
            "elementFocusContainer": ".js-calculation-tree-container",
            "collapseAll": false,
            "sortable" : true,
            "changeURL" : "/calculationTreeList"
        }'
>

</ul>
<script>
    $(() => {
        $('.js-calculation-tree').treeListView();

        // Инициализация контестного меню
        $.contextMenu({
            selector: '.js-calculation-tree li',
            build: function () {
                return {
                    items: {
                        delete: {
                            name: "Удалить",
                            icon: "delete",
                            callback: function () {
                                if (confirm("Подтвердите удаление")) {
                                    $.post('/deleteLaboriousnessCalculation', { entityId: this.data('id') }).done(() => {
                                        $('.js-calculation-tree-container').html('').load('/calculationTreeList?mainEntityId=${mainEntityId}')
                                    }).then(() => {});
                                }
                            }
                        }
                    }
                };
            }
        });
    });
</script>