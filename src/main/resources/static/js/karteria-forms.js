/* ========================================
   KARTERIA - JAVASCRIPT PARA FORMULÁRIOS (REFATORADO)
   ======================================== */

(function () {
    'use strict';

    // Roda o script quando o DOM estiver pronto
    document.addEventListener('DOMContentLoaded', () => {
        // Inicializa todas as funcionalidades em todos os formulários da página
        const forms = document.querySelectorAll('form');
        forms.forEach((form) => {
            initThemeToggle();
            initPasswordToggle(form);
            initFormValidation(form);
            initSubmissionHandler(form);
            if (form.id === 'registerForm') {
                initPasswordStrength(form);
            }
        });
    });

    // --- FUNCIONALIDADES ---

    /**
     * Controla a troca de tema (claro/escuro)
     */
    function initThemeToggle() {
        const themeToggle = document.getElementById('themeToggle');
        if (!themeToggle) return;

        const applyTheme = (theme) => {
            document.documentElement.setAttribute('data-theme', theme);
            localStorage.setItem('karteria-theme', theme);
            const icon = document.getElementById('themeIcon');
            if (icon) {
                icon.className = theme === 'light' ? 'fas fa-moon' : 'fas fa-sun';
            }
        };

        // Aplica tema salvo ou do sistema
        const savedTheme =
            localStorage.getItem('karteria-theme') ||
            (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
        applyTheme(savedTheme);

        themeToggle.addEventListener('click', () => {
            const newTheme = document.documentElement.getAttribute('data-theme') === 'light' ? 'dark' : 'light';
            applyTheme(newTheme);
        });
    }

    /**
     * Controla a visibilidade da senha (ícone de olho)
     */
    function initPasswordToggle(form) {
        const toggleButton = form.querySelector('#togglePassword');
        if (!toggleButton) return;

        toggleButton.addEventListener('click', function () {
            const passwordInput = this.previousElementSibling;
            const icon = this.querySelector('i');
            const isPassword = passwordInput.type === 'password';

            passwordInput.type = isPassword ? 'text' : 'password';
            icon.classList.toggle('fa-eye', !isPassword);
            icon.classList.toggle('fa-eye-slash', isPassword);
            this.setAttribute('aria-label', isPassword ? 'Ocultar senha' : 'Mostrar senha');
        });
    }

    /**
     * Medidor de força da senha para o formulário de registro
     */
    function initPasswordStrength(form) {
        const passwordInput = form.querySelector('#senha');
        const strengthBar = form.querySelector('#passwordStrength');
        const strengthText = form.querySelector('#passwordStrengthText');
        if (!passwordInput || !strengthBar || !strengthText) return;

        const bars = strengthBar.querySelectorAll('div');
        const strengthLevels = {
            0: { text: 'Muito fraca', color: '#dc2626' },
            1: { text: 'Fraca', color: '#f97316' },
            2: { text: 'Regular', color: '#f59e0b' },
            3: { text: 'Forte', color: '#22c55e' }
        };

        passwordInput.addEventListener('input', () => {
            const password = passwordInput.value;
            let score = 0;
            if (password.length >= 6) score++;
            if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score++;
            if (/\d/.test(password)) score++;
            if (/[^a-zA-Z0-9]/.test(password)) score++;

            score = Math.min(score, 3);

            bars.forEach((bar, index) => {
                bar.style.backgroundColor = index < score + 1 ? strengthLevels[score].color : 'var(--bg-tertiary)';
            });

            if (password.length === 0) {
                strengthText.textContent = 'Digite uma senha';
                strengthText.style.color = 'var(--text-muted)';
            } else {
                strengthText.textContent = strengthLevels[score].text;
                strengthText.style.color = strengthLevels[score].color;
            }
        });
    }

    /**
     * Validação de formulário em tempo real e no envio
     */
    function initFormValidation(form) {
        const inputs = form.querySelectorAll('.form-input[required]');

        inputs.forEach((input) => {
            input.addEventListener('input', () => validateInput(input)); // Valida ao digitar
            input.addEventListener('blur', () => validateInput(input)); // Valida ao sair do campo
        });

        form.addEventListener('submit', (event) => {
            const isFormValid = Array.from(inputs).every(validateInput);
            const terms = form.querySelector('#terms');
            let termsValid = true;
            if (terms && !terms.checked) {
                alert('Você deve concordar com os Termos de Uso e Política de Privacidade.');
                termsValid = false;
            }

            if (!isFormValid || !termsValid) {
                event.preventDefault(); // Impede o envio se inválido
                // Foca no primeiro campo com erro
                const firstError = form.querySelector('.input-group.error .form-input');
                if (firstError) firstError.focus();
            }
        });
    }

    /**
     * Lógica de validação para um único campo
     */
    function validateInput(input) {
        const group = input.closest('.input-group');
        const errorElement = group.querySelector('.error-message');
        let isValid = true;
        let errorMessage = '';

        // Validação de campo obrigatório
        if (input.required && input.value.trim() === '') {
            isValid = false;
            errorMessage = 'Este campo é obrigatório.';
        }
        // Validação de e-mail
        else if (input.type === 'email' && !/^[\S+@]+\.[\S+@]+$/.test(input.value)) {
            isValid = false;
            errorMessage = 'Por favor, insira um e-mail válido.';
        }
        // Validação de tamanho mínimo
        else if (input.minLength > 0 && input.value.length < input.minLength) {
            isValid = false;
            errorMessage = `Deve ter no mínimo ${input.minLength} caracteres.`;
        }

        // Atualiza a UI com base na validação
        group.classList.toggle('error', !isValid);
        group.classList.toggle('success', isValid);
        if (errorElement) {
            errorElement.textContent = errorMessage;
        }

        return isValid;
    }

    /**
     * Controla o estado de "loading" do botão de envio
     */
    function initSubmissionHandler(form) {
        form.addEventListener('submit', function () {
            const submitButton = form.querySelector('button[type="submit"]');
            if (!submitButton) return;

            // A validação já terá prevenido o envio se o form for inválido.
            setTimeout(() => {
                // Dupla verificação de validade
                if (!form.checkValidity()) return;

                submitButton.disabled = true;

                // Salva o conteúdo original para restaurar se o usuário voltar
                const originalContent = submitButton.innerHTML;
                submitButton.setAttribute('data-original-content', originalContent);

                // Adiciona um listener para o caso de o usuário navegar "Voltar"
                // e encontrar o botão travado
                window.addEventListener('pageshow', () => {
                    submitButton.disabled = false;
                    if (submitButton.getAttribute('data-original-content')) {
                        submitButton.innerHTML = originalContent;
                    }
                });

                // Substitui o conteúdo do botão pelo seu novo loader
                submitButton.innerHTML = '<span class="loader"></span>';
            }, 0);
        });
    }
})();
