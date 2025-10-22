/**
 * Lógica específica do Dashboard do Colaborador
 */
document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('colabSearchInput');
    const vagasCountInfo = document.getElementById('colabVagasCountInfo');
    const cardContainer = document.getElementById('colabCardContainer'); // Container dos cards

    // Verifica se os elementos essenciais existem
    if (!searchInput || !vagasCountInfo || !cardContainer) {
        console.warn("Aviso: Elementos de busca/contagem do dashboard do colaborador não encontrados.");
        return; // Não executa a lógica de busca se algo estiver faltando
    }

    // --- Lógica de Busca ---
    let debounceTimer;
    searchInput.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(filterCards, 300);
    });

    function filterCards() {
        const searchTerm = searchInput.value.toLowerCase().trim();
        const cardColumns = cardContainer.querySelectorAll('.colab-vaga-card-column'); // Seleciona as colunas dos cards
        const emptyState = document.querySelector('#colabCardContainer ~ .empty-state'); // Encontra empty-state (se houver)
        let visibleCount = 0;

        cardColumns.forEach(column => {
            const card = column.querySelector('.anuncio-card-colab');
            if (!card) return;

            // Pega os textos dos campos relevantes
            const titulo = card.querySelector('.card-titulo')?.textContent.toLowerCase() || '';
            const valorText = card.querySelector('.card-valor')?.textContent.toLowerCase().replace('r$', '').trim() || '';
            const localizacao = card.querySelector('.card-localizacao span')?.textContent.toLowerCase() || '';

            // Verifica match
            const isMatch = searchTerm === '' ||
                            titulo.includes(searchTerm) ||
                            valorText.includes(searchTerm) ||
                            localizacao.includes(searchTerm);

            // Mostra/esconde coluna
            column.style.display = isMatch ? '' : 'none';
            if (isMatch) {
                visibleCount++;
            }
        });

        // Mostra/esconde mensagem de "nenhuma vaga" (se aplicável)
        if (emptyState) {
            emptyState.style.display = visibleCount === 0 && cardColumns.length > 0 ? '' : 'none'; // Mostra só se HÁ cards mas NENHUM corresponde
        }

        updateVagasCountInfo(visibleCount, cardColumns.length);
    }

    // --- Função para Atualizar o Texto de Contagem ---
    function updateVagasCountInfo(visibleCount, totalNaPagina) {
        if (!vagasCountInfo) return;

        const searchTerm = searchInput.value.trim();

        if (searchTerm === '') {
             vagasCountInfo.textContent = `Exibindo ${totalNaPagina} vagas disponíveis.`;
        } else {
             vagasCountInfo.textContent = `Exibindo ${visibleCount} de ${totalNaPagina} vagas correspondentes à busca.`;
        }
    }

     // --- Chamada Inicial ---
     // Atualiza a contagem inicial (o texto já vem do Thymeleaf, mas podemos garantir)
     const initialCards = cardContainer.querySelectorAll('.colab-vaga-card-column').length;
     updateVagasCountInfo(initialCards, initialCards);

});