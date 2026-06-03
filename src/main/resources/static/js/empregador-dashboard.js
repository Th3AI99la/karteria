/**
 * Lógica específica do Dashboard do Empregador.
 */
document.addEventListener('DOMContentLoaded', () => {
    // Seletores (garantir que estão corretos)
    const searchInput = document.getElementById('searchInput');
    const vagasCountInfo = document.getElementById('vagasCountInfo');
    const tabContent = document.getElementById('vagasTabContent');
    const tabs = document.querySelectorAll('#vagasTab .nav-link');

    // Verifica se os elementos essenciais existem
    if (!searchInput || !vagasCountInfo || !tabContent || tabs.length === 0) {
        console.error(
            'Erro: Elementos essenciais do dashboard não encontrados (searchInput, vagasCountInfo, tabContent, tabs).'
        );
        return; // Interrompe a execução se algo estiver faltando
    }

    // --- Lógica de Busca ---
    let debounceTimer;
    searchInput.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(filterCards, 300);   // Debounce de 300ms
    });

    // --- Lógica de Atualização da Contagem ao Mudar de Aba ---
    tabs.forEach((tabButton) => {
        tabButton.addEventListener('shown.bs.tab', (event) => {
            const activeTabPaneId = event.target.getAttribute('data-bs-target');
            const activeTabPane = document.querySelector(activeTabPaneId);
            if (activeTabPane) {
                // Ao mudar de aba, sempre re-filtramos E atualizamos a contagem
                filterCards(); // filterCards agora lida com a aba ativa correta e chama updateVagasCountInfo
            } else {
                console.error('Erro: Não foi possível encontrar o painel da aba ativa:', activeTabPaneId);
            }
        });
    });

    // --- Função Principal de Filtragem ---
    function filterCards() {
        const searchTerm = searchInput.value.toLowerCase().trim();
        // ** Pega a aba ativa DENTRO desta função **
        const activeTabPane = tabContent.querySelector('.tab-pane.active');

        if (!activeTabPane) {
            console.error('Erro em filterCards: Aba ativa não encontrada.');
            return;
        }
        const cardColumns = activeTabPane.querySelectorAll('.vaga-card-column');
        const emptyState = activeTabPane.querySelector('.empty-state');
        let visibleCount = 0;

        cardColumns.forEach((column) => {
            const card = column.querySelector('.anuncio-card');
            if (!card) return;

            const titulo = card.querySelector('.card-titulo')?.textContent.toLowerCase() || '';
            const valorText =
                card.querySelector('.card-valor')?.textContent.toLowerCase().replace('r$', '').trim() || '';
            const localizacao = card.querySelector('.card-localizacao span')?.textContent.toLowerCase() || '';

            const isMatch =
                searchTerm === '' ||
                titulo.includes(searchTerm) ||
                valorText.includes(searchTerm) ||
                localizacao.includes(searchTerm);

            column.style.display = isMatch ? '' : 'none';
            if (isMatch) {
                visibleCount++;
            }
        });

        if (emptyState) {
            emptyState.style.display = visibleCount === 0 ? '' : 'none';
        }

        // Chama a atualização da contagem com os dados corretos da aba atual
        updateVagasCountInfo(activeTabPane, visibleCount);
    }

    // --- Função para Atualizar o Texto de Contagem ---
    function updateVagasCountInfo(activeTabPane, visibleCount) {
        if (!vagasCountInfo || !activeTabPane) return;

        // ** Recalcula o total DENTRO da aba ativa AQUI **
        const totalNaAba = activeTabPane.querySelectorAll('.vaga-card-column').length;
        const tabId = activeTabPane.id;
        let statusText = 'vagas';

        if (tabId === 'ativas-content') {
            statusText = 'ativas';
        } else if (tabId === 'pausadas-content') {
            statusText = 'pausadas';
        } else if (tabId === 'arquivadas-content') {
            statusText = 'arquivadas';
        } else if (tabId === 'concluidas-content') { 
            statusText = 'concluídas';
        }

        const searchTerm = searchInput.value.trim();

        if (searchTerm === '') {
            vagasCountInfo.textContent = `Exibindo ${totalNaAba} vagas ${statusText} no total.`;
        } else {
            vagasCountInfo.textContent = `Exibindo ${visibleCount} de ${totalNaAba} vagas ${statusText} correspondentes à busca.`;
        }
    }

    const initialActiveTabPane = tabContent.querySelector('.tab-pane.active');
    if (initialActiveTabPane) {
        filterCards(); // Chama filterCards que por sua vez chama updateVagasCountInfo
    } else {
        console.error('Erro: Nenhuma aba ativa encontrada na carga inicial.');
    }
});
