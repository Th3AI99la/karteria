/**
 * Lógica JavaScript específica para a página anuncio-detalhes.html
 */
document.addEventListener('DOMContentLoaded', () => {

    // Garante que o botão "Você já se candidatou" esteja cinza e desabilitado
    const jaCandidatadoBtn = document.querySelector('.btn-ja-candidatado');

    if (jaCandidatadoBtn) {
        console.log("Botão 'Já se candidatou' encontrado. Verificando classes..."); // Log para Debug

        // Garante as classes corretas
        jaCandidatadoBtn.classList.add('btn', 'btn-secondary', 'disabled');
        // Remove classes potencialmente conflitantes (ex: se algo adicionou btn-primary por engano)
        jaCandidatadoBtn.classList.remove('btn-primary-karteria', 'btn-success');

        // Garante o atributo disabled
        jaCandidatadoBtn.disabled = true;

        console.log("Classes e atributo 'disabled' para o botão 'Já se candidatou' foram garantidos."); // Log para Debug
    } else {
         // Isso não deve acontecer se a lógica do Thymeleaf estiver correta,
         // mas é bom ter um log caso o botão não seja renderizado quando deveria.
         // console.log("Botão 'Já se candidatou' não encontrado na página.");
    }

});