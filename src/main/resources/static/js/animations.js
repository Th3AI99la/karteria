// Aguarda o conteúdo completo da página ser carregado antes de executar o script
document.addEventListener('DOMContentLoaded', function() {

    // Seleciona todos os elementos que têm a classe 'fade-in-on-load'
    const elementsToFadeIn = document.querySelectorAll('.fade-in-on-load');

    // Itera sobre cada elemento encontrado
    elementsToFadeIn.forEach((element, index) => {
        // Define um pequeno atraso (delay) para cada elemento, criando um efeito escalonado
        element.style.transitionDelay = `${index * 100}ms`;
        
        // Adiciona a classe 'visible' para iniciar a animação de fade-in e subida
        element.classList.add('visible');
    });

});