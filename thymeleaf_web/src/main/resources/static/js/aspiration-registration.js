document.addEventListener('DOMContentLoaded', function () {
    initializeLookupForm();
    initializeAspirationForm();
});

function initializeLookupForm() {
    var lookupForm = document.getElementById('lookupForm');
    var cccdInput = lookupForm ? lookupForm.querySelector('#cccd') : null;
    var btnSearch = document.getElementById('btn-search');
    var btnText = btnSearch ? btnSearch.querySelector('.btn-text') : null;
    var btnSpinner = document.getElementById('btn-spinner');

    if (cccdInput) {
        cccdInput.focus();
        cccdInput.addEventListener('input', function () {
            this.value = this.value.replace(/[^0-9]/g, '');
        });
    }

    if (lookupForm) {
        lookupForm.addEventListener('submit', function () {
            if (btnSearch) {
                btnSearch.disabled = true;
                if (btnText) btnText.classList.add('d-none');
                if (btnSpinner) btnSpinner.classList.remove('d-none');
            }
        });
    }
}

function initializeAspirationForm() {
    var form = document.getElementById('aspirationForm');
    if (!form) return;

    var tbody = document.getElementById('aspirationRows');
    var rowTemplate = document.getElementById('aspirationRowTemplate');
    var addButton = document.getElementById('addAspiration');
    var alertBox = document.getElementById('clientValidationAlert');
    var saveButton = document.getElementById('saveAspiration');
    var saveText = saveButton ? saveButton.querySelector('.btn-text') : null;
    var saveSpinner = document.getElementById('save-spinner');
    var maxAspirations = Number(form.getAttribute('data-max-aspirations') || 10);

    function getRows() {
        return Array.prototype.slice.call(tbody.querySelectorAll('.aspiration-row'));
    }

    function refreshRows() {
        var rows = getRows();
        rows.forEach(function (row, index) {
            row.querySelector('.preference-order').textContent = index + 1;
            row.querySelector('.major-select').setAttribute('name', 'aspirations[' + index + '].maNganh');
            row.querySelector('.move-up').disabled = index === 0;
            row.querySelector('.move-down').disabled = index === rows.length - 1;
            row.querySelector('.remove-row').disabled = rows.length === 1;
        });
        if (addButton) {
            addButton.disabled = rows.length >= maxAspirations;
        }
        validateClient(false);
    }

    function addRow() {
        var rows = getRows();
        if (rows.length >= maxAspirations || !rowTemplate) return;

        var nextIndex = rows.length;
        var html = rowTemplate.innerHTML
            .replace(/__INDEX__/g, nextIndex)
            .replace(/__ORDER__/g, nextIndex + 1);
        tbody.insertAdjacentHTML('beforeend', html);
        refreshRows();
    }

    function moveRowUp(row) {
        var previous = row.previousElementSibling;
        if (previous) {
            tbody.insertBefore(row, previous);
            refreshRows();
        }
    }

    function moveRowDown(row) {
        var next = row.nextElementSibling;
        if (next) {
            tbody.insertBefore(next, row);
            refreshRows();
        }
    }

    function removeRow(row) {
        if (getRows().length === 1) {
            row.querySelector('.major-select').value = '';
        } else {
            row.remove();
        }
        refreshRows();
    }

    function validateClient(showMessage) {
        var selects = Array.prototype.slice.call(tbody.querySelectorAll('.major-select'));
        var values = selects.map(function (select) {
            return select.value.trim();
        }).filter(Boolean);
        var seen = {};
        var duplicate = false;

        selects.forEach(function (select) {
            var value = select.value.trim();
            select.classList.remove('is-invalid');
            if (!value) return;
            var key = value.toLowerCase();
            if (seen[key]) {
                duplicate = true;
                select.classList.add('is-invalid');
                seen[key].classList.add('is-invalid');
            } else {
                seen[key] = select;
            }
        });

        if (values.length === 0) {
            return showClientError('Vui lòng chọn ít nhất 1 nguyện vọng', showMessage);
        }
        if (duplicate) {
            return showClientError('Không được đăng ký trùng ngành trong danh sách nguyện vọng', showMessage);
        }

        if (alertBox) {
            alertBox.classList.add('d-none');
            alertBox.textContent = '';
        }
        return true;
    }

    function showClientError(message, showMessage) {
        if (showMessage && alertBox) {
            alertBox.textContent = message;
            alertBox.classList.remove('d-none');
        }
        return false;
    }

    if (addButton) {
        addButton.addEventListener('click', addRow);
    }

    tbody.addEventListener('click', function (event) {
        var button = event.target.closest('button');
        if (!button) return;

        var row = button.closest('.aspiration-row');
        if (button.classList.contains('move-up')) {
            moveRowUp(row);
        } else if (button.classList.contains('move-down')) {
            moveRowDown(row);
        } else if (button.classList.contains('remove-row')) {
            removeRow(row);
        }
    });

    tbody.addEventListener('change', function (event) {
        if (event.target.classList.contains('major-select')) {
            validateClient(false);
        }
    });

    form.addEventListener('submit', function (event) {
        if (!validateClient(true)) {
            event.preventDefault();
            return;
        }
        if (saveButton) {
            saveButton.disabled = true;
            if (saveText) saveText.classList.add('d-none');
            if (saveSpinner) saveSpinner.classList.remove('d-none');
        }
    });

    refreshRows();
}
