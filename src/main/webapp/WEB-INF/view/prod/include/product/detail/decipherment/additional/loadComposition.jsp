<c:forEach items="${requestScope.bomMap[param.bomCount].bomSpecItemList}" var="bomSpec">
  <li
      data-full-hierarchy-number="${''.concat(bomSpec.id).concat('-').concat(bomSpec.product.id).concat('-').concat(bomSpec.bom.id)}"
      data-specification-id="${bomSpec.id}"
      data-product-id="${bomSpec.product.id}"
      data-version-id="${bomSpec.bom.id}"
      data-version-name="${bomSpec.bom.version}"
      class="b-composition-tree__product" style="display: none;"
  >
    <c:set var="lastApprovedVersion" value="${lastApprovedProductMap[bomSpec.product.id]}"/>
      <%-- Кнопка скрытия/раскрытия --%>
    <div class="b-composition-tree__product_toggle-item">
      <div class="ui icon mini js-item-toggle button"><i class="icon"></i></div>
    </div>
      <%-- Изделие --%>
    <div class="b-composition-tree__product_item" style="width: ${param.nameFieldWidth}px;">
        ${bomSpec.product.productName}
    </div>
      <%-- Количество изделий вхождения --%>
    <div class="b-composition-tree__product_item b-align-center" style="width: 60px;">
        ${bomSpec.subProductCount}
    </div>
      <%-- Версия --%>
    <div class="b-composition-tree__product_item b-align-center" style="width: 70px;">
        <%-- По умолчанию берется последняя подтвержденная версия --%>
      <a class="b-link js-version-select" data-value="${lastApprovedVersion.id}">
          ${lastApprovedVersion.version ne null ? lastApprovedVersion.version : "-"}
      </a>
    </div>
      <%-- Выбор изделия в состав --%>
    <div class="b-composition-tree__product_item" style="width: 30px;">
      <div class="ui checkbox">
        <input type="checkbox" class="js-mark-product"/>
      </div>
    </div>
  </li>
  <ul class="b-composition-tree__block" style="display: none;">
    <c:forEach items="${bomSpec.product.ecoBomList}" var="subBom">
      <c:set target="${bomMap}" property="${param.bomCount + 1}" value="${subBom}"/>
      <jsp:include page="product/detail/decipherment/additional/loadComposition.jsp">
        <jsp:param name="bomCount" value="${param.bomCount + 1}"/>
        <jsp:param name="nameFieldWidth" value="${param.nameFieldWidth - 20}"/>
      </jsp:include>
    </c:forEach>
  </ul>
</c:forEach>