<div class="ui modal">
  <div class="header">Фильтр ведомости поставки</div>
  <div class="content">
    <form class="ui small form">
      <div class="field">
        <label>Изделие</label>
        <input name="productName" type="search"/>
      </div>
      <div class="column field">
        <div class="two fields">
          <div class="field">
            <label>Дата поставки с</label>
            <div class="ui calendar">
              <div class="ui input left icon">
                <i class="calendar icon"></i>
                <input class="std-date" name="deliveryDateFrom" type="search"/>
              </div>
            </div>
          </div>
          <div class="field">
            <label>
              <fmt:message key="label.to"/>
            </label>
            <div class="ui calendar">
              <div class="ui input left icon">
                <i class="calendar icon"></i>
                <input class="std-date" name="deliveryDateTo" type="search"/>
              </div>
            </div>
          </div>
        </div>
      </div>
    </form>
  </div>
</div>