<form class="ui tiny form detail_specification_info_comparison_filter__comparison-form">
    <table class="ui definition celled table blue">
        <thead>
            <tr>
                <th></th>
                <th colspan="2">Изделие</th>
                <th>Версия</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td class="center aligned">
                    A
                    <input type="hidden" name="productAId" value="${productA.id}"/>
                </td>
                <td class="product-a-name">${productA.conditionalName}</td>
                <td>
                    <div class="ui icon small buttons">
                        <div data-letter="A" class="ui button basic detail_specification_info_comparison_filter__select-product" title="Выбрать изделие">
                            <i class="pencil alternate icon"></i>
                        </div>
                    </div>
                </td>
                <td>
                    <select name="versionA" class="ui small fluid dropdown search std-select detail_specification_info_comparison_filter__select-version-a">
                        <c:forEach items="${versionListA}" var="version">
                            <option value="${version.id}">${version.value}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="center aligned">
                    B
                    <input type="hidden" name="productBId" value="${productB.id}"/>
                </td>
                <td class="product-b-name">${productB.conditionalName}</td>
                <td>
                    <div class="ui icon small buttons">
                        <div data-letter="B" class="ui button basic detail_specification_info_comparison_filter__select-product" title="Выбрать изделие">
                            <i class="pencil alternate icon"></i>
                        </div>
                    </div>
                </td>
                <td>
                    <select name="versionB" class="ui small fluid dropdown search std-select detail_specification_info_comparison_filter__select-version-b">
                        <c:forEach items="${versionListB}" var="version">
                            <option value="${version.id}">${version.value}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr class="right aligned">
                <td colspan="4">
                    <div class="ui small button detail_specification_info_comparison_filter__btn-compare">
                        <i class="icon blue check"></i>
                        Сравнить
                    </div>
                </td>
            </tr>
        </tbody>
    </table>
</form>