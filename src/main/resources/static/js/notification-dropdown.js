document.addEventListener('DOMContentLoaded', () => {
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
    const dropdown = document.querySelector('.notification-dropdown');
    
    if (dropdown) {
        dropdown.addEventListener('click', async (event) => {
            const button = event.target.closest('.btn-marcar-lida');
            if (button) {
                event.preventDefault();
                event.stopPropagation();

                const notificationId = button.dataset.id;
                await marcarComoLida(notificationId, button);
                return;
            }

            const link = event.target.closest('.notification-link');
            if (link) {
                event.preventDefault();
                const notificationId = link.dataset.id;
                const destinationUrl = link.href;

                await marcarComoLida(notificationId, null);
                window.location.href = destinationUrl;
            }
        });
    }

    async function marcarComoLida(id, buttonElement) {
        const headers = {
            'Content-Type': 'application/json'
        };

        if (csrfHeader && csrfToken) {
            headers[csrfHeader] = csrfToken;
        }

        try {
            const response = await fetch(`/notificacoes/marcar-lida/${id}`, {
                method: 'POST',
                headers
            });

            if (response.ok) {
                if (buttonElement) {
                    const itemWrapper = document.getElementById(`notificacao-item-${id}`);
                    if (itemWrapper) {
                        itemWrapper.style.opacity = '0';
                        itemWrapper.style.transform = 'translateX(20px)';
                        setTimeout(() => itemWrapper.remove(), 300);
                    }
                    atualizarContagemBadge();
                }
            } else {
                if (buttonElement) alert('Erro ao marcar notificação como lida.');
            }
        } catch (error) {
            console.error('Erro no fetch:', error);
            if (buttonElement) alert('Erro de conexão ao marcar notificação.');
        }
    }

    function atualizarContagemBadge() {
        const badges = document.querySelectorAll('.notification-badge, [data-notification-badge]');
        
        badges.forEach(badge => {
            let contagemAtual = parseInt(badge.textContent, 10);
            if (contagemAtual > 0) {
                contagemAtual--;
                setBadgeCount(badge, contagemAtual);
                
                if (contagemAtual === 0) {
                    badge.style.display = 'none';
                }
            }
        });
    }

    function setBadgeCount(badge, count) {
        badge.textContent = String(count);

        const srText = document.createElement('span');
        srText.className = 'visually-hidden';
        srText.textContent = 'notificações não lidas';
        badge.appendChild(srText);
    }
});
