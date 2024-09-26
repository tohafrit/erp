<div class="ui modal detail_delivery-statement_distribution_split__main">
    <div class="header">Разделение части поставки</div>
    <div class="detail_delivery-statement_distribution_split__container">
        <div class="detail_delivery-statement_distribution_split_header">
            <div class="detail_delivery-statement_distribution_split__div-product-name">
                ${conditionalName}, ${allotmentAmount} шт.
            </div>
        </div>
        <div class="ui form detail_delivery-statement_distribution_split__div-number-split">
            <div class="inline fields">
                <div class="field">
                    <label>Разделить на две группы по</label>
                    <input type="number" id="quantityOne" name="quantityOne" max="${inputNumberMax}" min="1" value="1"/>
                    и
                    <input type="number" id="quantityTwo" name="quantityTwo" max="${inputNumberMax}" min="1"/>
                </div>
            </div>
        </div>
    </div>
    <div class="actions">
        <button class="ui small button" type="button">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const allotmentId = '${allotmentId}';
        //
        const $divInputNumber = $('div.detail_delivery-statement_distribution_split__div-number-split');
        const $inputNumberOne = $divInputNumber.find('[name="quantityOne"]');
        const $inputNumberTwo = $divInputNumber.find('[name="quantityTwo"]');
        const $submit = $('button[type="button"]');
        const $main = $('div.detail_delivery-statement_distribution_split__main');
        const specTabulator = Tabulator.prototype.findTable('div.detail_delivery-statement_distribution__table')[0];

        // Запрет на ввод с клавиатуры, только использование стрелок для input type="number"
        $inputNumberOne.add($inputNumberTwo).on({
            'keypress': e => e.preventDefault()
        });

        $inputNumberOne.on({
           'click': e => $.post({
               url:  ACTION_PATH.DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_SPLIT_CHANGE_QUANTITY,
               data: {
                   allotmentId: allotmentId,
                   quantity: $(e.currentTarget).val()
               }
           }).done(data => $inputNumberTwo.val(data))
        }).trigger('click');

        $inputNumberTwo.on({
            'click': e => $.post({
                url:  ACTION_PATH.DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_SPLIT_CHANGE_QUANTITY,
                data: {
                    allotmentId: allotmentId,
                    quantity: $(e.currentTarget).val()
                }
            }).done(data => $inputNumberOne.val(data))
        });

        $submit.on({
            'click': () => {
                const quantityOne = $main.find('input[name="quantityOne"]').val();
                const quantityTwo = $main.find('input[name="quantityTwo"]').val();
                $.post({
                    url: ACTION_PATH.DETAIL_DELIVERY_STATEMENT_DISTRIBUTION_SPLIT_SAVE,
                    data: {
                        allotmentId: allotmentId,
                        quantityOne: quantityOne,
                        quantityTwo: quantityTwo
                    },
                    beforeSend: () => togglePreloader(true),
                    complete: () => togglePreloader(false)
                }).done(() => {
                    $main.modal('hide');
                    specTabulator.setData();
                }).fail(() => globalMessage({message: 'Ошибка сохранения разделения части поставки'}));
            }
        });
    });
</script>
