document.addEventListener("DOMContentLoaded", function () {
    const userIdMeta = document.querySelector('meta[name="user-id"]');
    const userIdFromNav = document.querySelector('[data-user-id]')?.dataset.userId;
    const userId = userIdMeta?.getAttribute('content') || userIdFromNav;

    if (!userId || userId === 'null' || typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
        return;
    }

    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/notificacoes/' + userId, function (mensagem) {
            let notificacao;
            try {
                notificacao = JSON.parse(mensagem.body);
            } catch (error) {
                return;
            }

            atualizarSininho();
            mostrarToastFlutuante(notificacao.mensagem);
        });
    });

    function atualizarSininho() { 
        let bolinha = document.querySelector('.notification-badge');
        if (bolinha) {
            const count = parseInt(bolinha.innerText, 10) || 0;
            setBadgeCount(bolinha, count + 1);
            bolinha.style.display = '';
            return;
        }

        const notificationButton = document.getElementById('notificationDropdown');
        if (notificationButton) {
            bolinha = document.createElement('span');
            bolinha.className = 'position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger notification-badge';
            setBadgeCount(bolinha, 1);
            notificationButton.appendChild(bolinha);
        }
    }

    function setBadgeCount(badge, count) {
        badge.textContent = String(count);

        const srText = document.createElement('span');
        srText.className = 'visually-hidden';
        srText.textContent = 'notificações não lidas';
        badge.appendChild(srText);
    }

    function mostrarToastFlutuante(mensagem) {
        // Cria um container para o Toast se ele não existir
        let toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.id = 'toast-container';
            toastContainer.className = 'toast-container position-fixed bottom-0 end-0 p-3';
            toastContainer.style.zIndex = '1055';
            document.body.appendChild(toastContainer);
        }
        
        const toastId = 'toast-' + Date.now();
        const toastElement = document.createElement('div');
        toastElement.id = toastId;
        toastElement.className = 'toast align-items-center text-bg-success border-0 shadow-lg mb-2';
        toastElement.setAttribute('role', 'alert');
        toastElement.setAttribute('aria-live', 'assertive');
        toastElement.setAttribute('aria-atomic', 'true');
        toastElement.setAttribute('data-bs-delay', '6000');

        const contentWrapper = document.createElement('div');
        contentWrapper.className = 'd-flex';

        const body = document.createElement('div');
        body.className = 'toast-body';
        body.style.color = 'white';

        const icon = document.createElement('i');
        icon.className = 'fas fa-bell me-2';
        body.appendChild(icon);
        appendMensagemFormatada(body, mensagem);

        const closeButton = document.createElement('button');
        closeButton.type = 'button';
        closeButton.className = 'btn-close btn-close-white me-2 m-auto';
        closeButton.setAttribute('data-bs-dismiss', 'toast');
        closeButton.setAttribute('aria-label', 'Fechar');

        contentWrapper.appendChild(body);
        contentWrapper.appendChild(closeButton);
        toastElement.appendChild(contentWrapper);
        toastContainer.appendChild(toastElement);

        if (typeof bootstrap === 'undefined') return;

        const toast = new bootstrap.Toast(toastElement);
        toast.show();

        toastElement.addEventListener('hidden.bs.toast', () => toastElement.remove());
    }

    function appendMensagemFormatada(container, mensagem) {
        const marker = ' se candidatou para sua vaga ';
        const markerIndex = mensagem.indexOf(marker);

        if (markerIndex === -1) {
            container.appendChild(document.createTextNode(mensagem));
            return;
        }

        const nome = mensagem.slice(0, markerIndex);
        const restante = mensagem.slice(markerIndex);
        const strong = document.createElement('strong');
        strong.className = 'brand-text';
        strong.textContent = nome;

        container.appendChild(strong);
        container.appendChild(document.createTextNode(restante));
    }
});
