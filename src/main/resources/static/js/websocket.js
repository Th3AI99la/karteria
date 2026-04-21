document.addEventListener("DOMContentLoaded", function () {
    // 1. Pega o ID do usuário escondido no HTML
    const userIdMeta = document.querySelector('meta[name="user-id"]');
    const userId = userIdMeta ? userIdMeta.getAttribute('content') : null;

    if (userId) {
        // 2. Conecta no servidor Spring Boot
        const socket = new SockJS('/ws');
        const stompClient = Stomp.over(socket);
        
        // Esconde os logs técnicos do STOMP no console do navegador
        stompClient.debug = null;

        stompClient.connect({}, function (frame) {
            console.log('✅ Conectado ao WebSocket! Escutando o canal do usuário: ' + userId);

            // 3. Fica "ouvindo" apenas as notificações deste usuário
            stompClient.subscribe('/topic/notificacoes/' + userId, function (mensagem) {
                const notificacao = JSON.parse(mensagem.body);
                
                // Atualiza o sininho
                atualizarSininho();

                // Exibe a notificação flutuante na tela (Estilo "Ifood" / "Uber")
                mostrarToastFlutuante(notificacao.mensagem);
            });
        });
    }

    function atualizarSininho() {
        let bolinha = document.querySelector('.notification-badge');
        if (bolinha) {
            let count = parseInt(bolinha.innerText) || 0;
            bolinha.innerText = count + 1;
        } else {
            // Se a bolinha não existia, recarrega a página silenciosamente
            // ou cria o elemento dinamicamente.
        }
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
        
        // Formata a mensagem para destacar o nome do usuário igual você fez no HTML
        let mensagemFormatada = mensagem;
        if (mensagemFormatada.includes(' se candidatou')) {
            mensagemFormatada = mensagemFormatada.replace(/^(.*?)( se candidatou para sua vaga )/, '<strong class="brand-text">$1</strong>$2');
        }
        mensagemFormatada = mensagemFormatada.replace(/'(.*?)'/g, '<strong class="brand-text">($1)</strong>');

        // Gera o HTML do Toast (Card verde do Bootstrap)
        const toastId = 'toast-' + Date.now();
        const toastHTML = `
            <div id="${toastId}" class="toast align-items-center text-bg-success border-0 shadow-lg mb-2" role="alert" aria-live="assertive" aria-atomic="true" data-bs-delay="6000">
              <div class="d-flex">
                <div class="toast-body" style="color: white !important;">
                  <i class="fas fa-bell me-2"></i> ${mensagemFormatada}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
              </div>
            </div>
        `;
        
        toastContainer.insertAdjacentHTML('beforeend', toastHTML);
        
        // Ativa e exibe o Toast
        const toastElement = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastElement);
        toast.show();
        
        // Remove do DOM após sumir para não acumular lixo
        toastElement.addEventListener('hidden.bs.toast', () => toastElement.remove());
    }
});