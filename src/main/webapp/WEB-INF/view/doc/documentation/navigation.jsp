<div>
    <div class="documentation__navigation">
        <div class="documentation__navigation-left">
            <div class="documentation__navigation_element">
                <span class="documentation__navigation_text-span">Навигация</span>
            </div>
            <div class="documentation__navigation_element">
                <button class="ui icon button
                    <c:if test="${empty prevDocId}">
                        disabled documentation__navigation_inactive-button
                    </c:if>
                    <c:if test="${not empty prevDocId}">
                        documentation__navigation_left-angle-button documentation__navigation_button
                    </c:if>
                ">
                    <i class="angle left icon"></i>
                </button>
            </div>
            <div class="documentation__navigation_element">
                <button class="ui icon button
                    <c:if test="${empty nextDocId}">
                        disabled documentation__navigation_inactive-button
                    </c:if>
                    <c:if test="${not empty nextDocId}">
                        documentation__navigation_right-angle-button documentation__navigation_button
                    </c:if>
                ">
                    <i class="angle right icon"></i>
                </button>
            </div>
        </div>
        <div class="documentation__navigation-right">
            <div class="documentation__navigation_element">
                <div class="ui action small input documentation__navigation_search-content_div">
                    <input type="text" placeholder="Поиск по документации..." class="documentation__navigation_search-content_input" value="${searchText}">
                    <button class="ui icon blue button documentation__navigation_search-content_button">
                        <i class="search icon"></i>
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script>
        $(() => {
            const $searchInput = $('input.documentation__navigation_search-content_input');
            const $searchButton = $('button.documentation__navigation_search-content_button');
            const $leftAngleButton = $('button.documentation__navigation_left-angle-button');
            const $rightAngleButton = $('button.documentation__navigation_right-angle-button');

            if ($searchInput.val()) {
                $searchInput.trigger('focus');
            }

            $leftAngleButton.on({
                'click': () => document.location.href = '${'/documentation/'.concat(prevDocId)}'
            });

            $rightAngleButton.on({
                'click': () => document.location.href = '${'/documentation/'.concat(nextDocId)}'
            });

            $leftAngleButton.add($rightAngleButton).on({
                'mouseenter mouseleave': e => $(e.currentTarget).toggleClass('documentation__navigation_button documentation__navigation_blue-button'),
            });

            $searchButton.on({
                'click': () => {
                    const val = $searchInput.val();
                    if (val) {
                        document.location.href = '/documentation/search?text=' + encodeURIComponent(val);
                    }
                }
            });

            $searchInput.on({
                'keyup': e => e.key === 'Enter' ? $searchButton.trigger('click') : null
            });
        })
    </script>
</div>