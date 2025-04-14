$(document).ready(function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    });
    
    // Initialize datepickers if jQuery UI is available
    if ($.fn.datepicker) {
        $('.datepicker').datepicker({
            format: 'yyyy-mm-dd',
            autoclose: true
        });
    }
    
    // Handle WhatsApp notification button
    $('.whatsapp-notify').on('click', function(e) {
        e.preventDefault();
        var dueId = $(this).data('due-id');
        
        $.get('/dues/whatsapp-link/' + dueId, function(link) {
            if (link) {
                window.open(link, '_blank');
            } else {
                alert('Telefon numarası bulunamadı veya geçersiz.');
            }
        });
    });
    
    // Confirm delete actions
    $('.confirm-delete').on('click', function(e) {
        if (!confirm('Bu kaydı silmek istediğinizden emin misiniz?')) {
            e.preventDefault();
        }
    });
    
    // Toggle password visibility
    $('.toggle-password').on('click', function() {
        var input = $($(this).data('toggle'));
        if (input.attr('type') === 'password') {
            input.attr('type', 'text');
            $(this).find('i').removeClass('fa-eye').addClass('fa-eye-slash');
        } else {
            input.attr('type', 'password');
            $(this).find('i').removeClass('fa-eye-slash').addClass('fa-eye');
        }
    });
});
