const mode = document.getElementById('mode_icon');

mode.addEventListener('click', () => {
    // Esta linha agora encontra o <form> mais próximo do ícone, tornando o script reutilizável
    const form = mode.closest('form');

    if (mode.classList.contains('fa-moon')) {
        mode.classList.remove('fa-moon');
        mode.classList.add('fa-sun');
    
        form.classList.add('dark');
        return;
    }  
    
    mode.classList.add('fa-moon');
    mode.classList.remove('fa-sun');
    form.classList.remove('dark');
});
