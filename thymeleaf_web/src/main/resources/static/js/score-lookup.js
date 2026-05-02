document.addEventListener('DOMContentLoaded', function () {
    const cccdInput = document.getElementById('cccd');
    const form = document.getElementById('lookupForm');
    const btnSearch = document.getElementById('btn-search');
    const btnText = btnSearch ? btnSearch.querySelector('.btn-text') : null;
    const btnSpinner = document.getElementById('btn-spinner');

    // Auto-focus CCCD input
    if (cccdInput) {
        cccdInput.focus();
    }

    // Enforce numeric-only input
    if (cccdInput) {
        cccdInput.addEventListener('input', function () {
            this.value = this.value.replace(/[^0-9]/g, '');
        });
    }

    // Form submission: show loading state
    if (form) {
        form.addEventListener('submit', function () {
            if (btnSearch) {
                btnSearch.disabled = true;
                if (btnText) btnText.classList.add('d-none');
                if (btnSpinner) btnSpinner.classList.remove('d-none');
            }
        });
    }
});
