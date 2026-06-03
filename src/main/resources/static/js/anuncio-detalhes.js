/**
 * Lógica JavaScript específica para a página anuncio-detalhes.html
 */
document.addEventListener('DOMContentLoaded', () => {

    // Garante que o botão "Você já se candidatou" esteja cinza e desabilitado
    const jaCandidatadoBtn = document.querySelector('.btn-ja-candidatado');

    if (jaCandidatadoBtn) {
        // Garante as classes corretas
        jaCandidatadoBtn.classList.add('btn', 'btn-secondary', 'disabled');
        // Remove classes potencialmente conflitantes (ex: se algo adicionou btn-primary por engano)
        jaCandidatadoBtn.classList.remove('btn-primary-karteria', 'btn-success');

        // Garante o atributo disabled
        jaCandidatadoBtn.disabled = true;
    }

});
