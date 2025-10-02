/* ========================================
   KARTERIA - JAVASCRIPT PARA FORMULÁRIOS
   ======================================== */

(function() {
    'use strict';
    
    // === CONFIGURAÇÕES === //
    const FORM_CONFIG = {
        validation: {
            email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
            password: {
                minLength: 6,
                requireUppercase: false,
                requireLowercase: false,
                requireNumbers: false,
                requireSpecialChars: false
            },
            name: {
                minLength: 2,
                maxLength: 50
            }
        },
        animations: {
            duration: 300,
            easing: 'ease-in-out'
        }
    };
    
    // === INICIALIZAÇÃO === //
    document.addEventListener('DOMContentLoaded', function() {
        initThemeToggle();
        initFormValidation();
        initPasswordFeatures();
        initInputEnhancements();
        initSubmissionHandling();
        initAccessibility();
        
        console.log('📝 Karteria Forms initialized successfully!');
    });
    
    // === TOGGLE DE TEMA === //
    function initThemeToggle() {
        const themeToggle = document.getElementById('themeToggle');
        const themeIcon = document.getElementById('themeIcon');
        
        if (!themeToggle || !themeIcon) return;
        
        // Carregar tema salvo
        const savedTheme = localStorage.getItem('karteria-theme') || 'light';
        applyTheme(savedTheme);
        
        // Event listener
        themeToggle.addEventListener('click', function() {
            const currentTheme = document.documentElement.getAttribute('data-theme');
            const newTheme = currentTheme === 'light' ? 'dark' : 'light';
            applyTheme(newTheme);
            
            // Animação do botão
            this.style.transform = 'scale(0.9) rotate(180deg)';
            setTimeout(() => {
                this.style.transform = 'scale(1) rotate(0deg)';
            }, 200);
        });
        
        // Detectar preferência do sistema
        const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
        mediaQuery.addEventListener('change', function(e) {
            if (!localStorage.getItem('karteria-theme')) {
                applyTheme(e.matches ? 'dark' : 'light');
            }
        });
    }
    
    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('karteria-theme', theme);
        
        const themeIcon = document.getElementById('themeIcon');
        if (themeIcon) {
            themeIcon.className = theme === 'light' ? 'fas fa-moon' : 'fas fa-sun';
        }
        
        // Anunciar mudança para leitores de tela
        announceToScreenReader(`Tema alterado para ${theme === 'light' ? 'claro' : 'escuro'}`);
    }
    
    // === VALIDAÇÃO DE FORMULÁRIOS === //
    function initFormValidation() {
        const forms = document.querySelectorAll('form');
        
        forms.forEach(form => {
            const inputs = form.querySelectorAll('.form-input');
            
            inputs.forEach(input => {
                // Validação em tempo real
                input.addEventListener('input', debounce(() => validateInput(input), 300));
                input.addEventListener('blur', () => validateInput(input));
                input.addEventListener('focus', () => clearValidationState(input));
            });
            
            // Validação no submit
            form.addEventListener('submit', function(e) {
                if (!validateForm(this)) {
                    e.preventDefault();
                    focusFirstError(this);
                }
            });
        });
    }
    
    function validateInput(input) {
        const value = input.value.trim();
        const inputGroup = input.closest('.input-group');
        const errorElement = inputGroup.querySelector('.error-message');
        
        let isValid = true;
        let errorMessage = '';
        
        // Validação por tipo
        switch (input.type) {
            case 'email':
                if (value && !FORM_CONFIG.validation.email.test(value)) {
                    isValid = false;
                    errorMessage = 'Digite um e-mail válido';
                }
                break;
                
            case 'password':
                if (value && value.length < FORM_CONFIG.validation.password.minLength) {
                    isValid = false;
                    errorMessage = `Senha deve ter pelo menos ${FORM_CONFIG.validation.password.minLength} caracteres`;
                }
                break;
                
            case 'text':
                if (input.id === 'nome' || input.id === 'name') {
                    if (value && value.length < FORM_CONFIG.validation.name.minLength) {
                        isValid = false;
                        errorMessage = `Nome deve ter pelo menos ${FORM_CONFIG.validation.name.minLength} caracteres`;
                    }
                }
                break;
        }
        
        // Validação de campo obrigatório
        if (input.hasAttribute('required') && !value) {
            isValid = false;
            errorMessage = 'Este campo é obrigatório';
        }
        
        // Aplicar estado visual
        updateValidationState(inputGroup, isValid, errorMessage, errorElement);
        
        return isValid;
    }
    
    function updateValidationState(inputGroup, isValid, errorMessage, errorElement) {
        const input = inputGroup.querySelector('.form-input');
        const hasValue = input.value.trim().length > 0;
        
        // Remover estados anteriores
        inputGroup.classList.remove('error', 'success');
        
        if (hasValue) {
            if (isValid) {
                inputGroup.classList.add('success');
            } else {
                inputGroup.classList.add('error');
            }
        }
        
        // Mostrar/ocultar mensagem de erro
        if (errorElement) {
            if (!isValid && hasValue) {
                errorElement.textContent = errorMessage;
                errorElement.style.display = 'flex';
                errorElement.setAttribute('aria-live', 'polite');
            } else {
                errorElement.style.display = 'none';
                errorElement.removeAttribute('aria-live');
            }
        }
    }
    
    function clearValidationState(input) {
        const inputGroup = input.closest('.input-group');
        const errorElement = inputGroup.querySelector('.error-message');
        
        inputGroup.classList.remove('error');
        
        if (errorElement) {
            errorElement.style.display = 'none';
        }
    }
    
    function validateForm(form) {
        const inputs = form.querySelectorAll('.form-input[required]');
        let isFormValid = true;
        
        inputs.forEach(input => {
            if (!validateInput(input)) {
                isFormValid = false;
            }
        });
        
        // Validar checkboxes obrigatórios
        const requiredCheckboxes = form.querySelectorAll('input[type="checkbox"][required]');
        requiredCheckboxes.forEach(checkbox => {
            if (!checkbox.checked) {
                isFormValid = false;
                showCheckboxError(checkbox);
            }
        });
        
        // Validar radio buttons obrigatórios
        const radioGroups = form.querySelectorAll('input[type="radio"][required]');
        const radioGroupNames = [...new Set(Array.from(radioGroups).map(radio => radio.name))];
        
        radioGroupNames.forEach(groupName => {
            const groupRadios = form.querySelectorAll(`input[type="radio"][name="${groupName}"]`);
            const isChecked = Array.from(groupRadios).some(radio => radio.checked);
            
            if (!isChecked) {
                isFormValid = false;
                showRadioError(groupRadios[0]);
            }
        });
        
        return isFormValid;
    }
    
    function showCheckboxError(checkbox) {
        const label = checkbox.closest('label') || checkbox.nextElementSibling;
        if (label) {
            label.style.color = 'var(--danger)';
            setTimeout(() => {
                label.style.color = '';
            }, 3000);
        }
    }
    
    function showRadioError(firstRadio) {
        const fieldset = firstRadio.closest('fieldset') || firstRadio.closest('.radio-group');
        if (fieldset) {
            fieldset.style.borderColor = 'var(--danger)';
            setTimeout(() => {
                fieldset.style.borderColor = '';
            }, 3000);
        }
    }
    
    function focusFirstError(form) {
        const firstError = form.querySelector('.input-group.error .form-input');
        if (firstError) {
            firstError.focus();
            firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }
    
    // === RECURSOS DE SENHA === //
    function initPasswordFeatures() {
        initPasswordToggle();
        initPasswordStrength();
    }
    
    function initPasswordToggle() {
        const toggleButtons = document.querySelectorAll('#togglePassword');
        
        toggleButtons.forEach(button => {
            button.addEventListener('click', function() {
                const passwordInput = this.closest('.input-wrapper').querySelector('input[type="password"], input[type="text"]');
                const icon = this.querySelector('i');
                
                if (passwordInput.type === 'password') {
                    passwordInput.type = 'text';
                    icon.classList.remove('fa-eye');
                    icon.classList.add('fa-eye-slash');
                    this.setAttribute('aria-label', 'Ocultar senha');
                } else {
                    passwordInput.type = 'password';
                    icon.classList.remove('fa-eye-slash');
                    icon.classList.add('fa-eye');
                    this.setAttribute('aria-label', 'Mostrar senha');
                }
                
                // Manter foco no input
                passwordInput.focus();
            });
        });
    }
    
    function initPasswordStrength() {
        const passwordInputs = document.querySelectorAll('input[type="password"]#senha, input[type="password"]#password');
        
        passwordInputs.forEach(input => {
            const strengthContainer = document.getElementById('passwordStrength');
            const strengthText = document.getElementById('passwordStrengthText');
            
            if (!strengthContainer || !strengthText) return;
            
            input.addEventListener('input', function() {
                const password = this.value;
                const strength = calculatePasswordStrength(password);
                updatePasswordStrengthUI(strengthContainer, strengthText, strength, password.length);
            });
        });
    }
    
    function calculatePasswordStrength(password) {
        let score = 0;
        const checks = {
            length: password.length >= 8,
            lowercase: /[a-z]/.test(password),
            uppercase: /[A-Z]/.test(password),
            numbers: /\d/.test(password),
            special: /[^a-zA-Z0-9]/.test(password)
        };
        
        // Pontuação baseada nos critérios
        if (password.length >= 6) score += 1;
        if (password.length >= 8) score += 1;
        if (checks.lowercase) score += 1;
        if (checks.uppercase) score += 1;
        if (checks.numbers) score += 1;
        if (checks.special) score += 1;
        
        return Math.min(score, 4);
    }
    
    function updatePasswordStrengthUI(container, textElement, strength, passwordLength) {
        const bars = container.querySelectorAll('div');
        const colors = ['#dc2626', '#f97316', '#eab308', '#22c55e'];
        const texts = ['Muito fraca', 'Fraca', 'Regular', 'Boa', 'Forte'];
        
        // Reset all bars
        bars.forEach(bar => {
            bar.style.backgroundColor = '#e5e7eb';
        });
        
        // Fill bars based on strength
        for (let i = 0; i < strength; i++) {
            bars[i].style.backgroundColor = colors[Math.min(i, colors.length - 1)];
        }
        
        // Update text
        if (passwordLength === 0) {
            textElement.textContent = 'Digite uma senha';
            textElement.style.color = 'var(--text-muted)';
        } else {
            textElement.textContent = texts[strength];
            textElement.style.color = colors[Math.min(strength - 1, colors.length - 1)];
        }
    }
    
    // === MELHORIAS DE INPUT === //
    function initInputEnhancements() {
        // Auto-focus no primeiro input
        const firstInput = document.querySelector('.form-input');
        if (firstInput && !document.querySelector('.alert')) {
            setTimeout(() => firstInput.focus(), 500);
        }
        
        // Animações de foco
        document.querySelectorAll('.form-input').forEach(input => {
            input.addEventListener('focus', function() {
                this.closest('.input-wrapper').style.transform = 'scale(1.02)';
            });
            
            input.addEventListener('blur', function() {
                this.closest('.input-wrapper').style.transform = 'scale(1)';
            });
        });
        
        // Máscaras de input (se necessário)
        initInputMasks();
        
        // Prevenção de spam
        initSpamPrevention();
    }
    
    function initInputMasks() {
        // Máscara para telefone
        const phoneInputs = document.querySelectorAll('input[data-mask="phone"]');
        phoneInputs.forEach(input => {
            input.addEventListener('input', function() {
                let value = this.value.replace(/\D/g, '');
                value = value.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
                this.value = value;
            });
        });
        
        // Máscara para CPF
        const cpfInputs = document.querySelectorAll('input[data-mask="cpf"]');
        cpfInputs.forEach(input => {
            input.addEventListener('input', function() {
                let value = this.value.replace(/\D/g, '');
                value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
                this.value = value;
            });
        });
    }
    
    function initSpamPrevention() {
        const forms = document.querySelectorAll('form');
        
        forms.forEach(form => {
            let submitCount = 0;
            const maxSubmits = 3;
            const timeWindow = 60000; // 1 minuto
            
            form.addEventListener('submit', function(e) {
                submitCount++;
                
                if (submitCount > maxSubmits) {
                    e.preventDefault();
                    showSpamWarning();
                    return false;
                }
                
                // Reset counter após time window
                setTimeout(() => {
                    submitCount = Math.max(0, submitCount - 1);
                }, timeWindow);
            });
        });
    }
    
    function showSpamWarning() {
        const alert = document.createElement('div');
        alert.className = 'alert alert-danger';
        alert.innerHTML = `
            <i class="fas fa-exclamation-triangle"></i>
            <span>Muitas tentativas. Aguarde um momento antes de tentar novamente.</span>
        `;
        
        const form = document.querySelector('form');
        form.insertBefore(alert, form.firstChild);
        
        setTimeout(() => {
            alert.remove();
        }, 5000);
    }
    
    // === MANIPULAÇÃO DE SUBMISSÃO === //
    function initSubmissionHandling() {
        const forms = document.querySelectorAll('form');
        
        forms.forEach(form => {
            form.addEventListener('submit', function(e) {
                const submitButton = this.querySelector('button[type="submit"]');
                
                if (submitButton && !submitButton.classList.contains('loading')) {
                    addLoadingState(submitButton);
                }
                
                // Desabilitar múltiplos submits
                setTimeout(() => {
                    const buttons = this.querySelectorAll('button');
                    buttons.forEach(btn => btn.disabled = true);
                }, 100);
            });
        });
    }
    
    function addLoadingState(button) {
        const originalText = button.innerHTML;
        button.classList.add('loading');
        button.disabled = true;
        
        // Criar spinner
        const spinner = document.createElement('span');
        spinner.className = 'spinner-border spinner-border-sm me-2';
        spinner.setAttribute('role', 'status');
        spinner.setAttribute('aria-hidden', 'true');
        
        button.innerHTML = '';
        button.appendChild(spinner);
        button.appendChild(document.createTextNode('Processando...'));
        
        // Armazenar texto original para possível restauração
        button._originalText = originalText;
    }
    
    // === ACESSIBILIDADE === //
    function initAccessibility() {
        // Associar labels com inputs
        document.querySelectorAll('.form-input').forEach(input => {
            const label = input.closest('.input-group').querySelector('label');
            if (label && !input.getAttribute('aria-labelledby')) {
                const labelId = `label-${Math.random().toString(36).substr(2, 9)}`;
                label.id = labelId;
                input.setAttribute('aria-labelledby', labelId);
            }
        });
        
        // Adicionar aria-describedby para mensagens de erro
        document.querySelectorAll('.error-message').forEach(errorMsg => {
            const input = errorMsg.closest('.input-group').querySelector('.form-input');
            if (input) {
                const errorId = `error-${Math.random().toString(36).substr(2, 9)}`;
                errorMsg.id = errorId;
                input.setAttribute('aria-describedby', errorId);
            }
        });
        
        // Navegação por teclado para radio buttons
        document.querySelectorAll('.radio-option').forEach(option => {
            option.addEventListener('keydown', function(e) {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    const radio = this.querySelector('input[type="radio"]');
                    radio.checked = true;
                    radio.dispatchEvent(new Event('change'));
                }
            });
        });
        
        // Anúncios para leitores de tela
        document.addEventListener('themeChanged', function(e) {
            announceToScreenReader(`Tema alterado para ${e.detail.theme === 'light' ? 'claro' : 'escuro'}`);
        });
    }
    
    // === UTILITÁRIOS === //
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
    
    function announceToScreenReader(message) {
        const announcement = document.createElement('div');
        announcement.setAttribute('aria-live', 'polite');
        announcement.setAttribute('aria-atomic', 'true');
        announcement.className = 'sr-only';
        announcement.textContent = message;
        
        document.body.appendChild(announcement);
        
        setTimeout(() => {
            document.body.removeChild(announcement);
        }, 1000);
    }
    
    // === API PÚBLICA === //
    window.KarteriaForms = {
        validateForm: validateForm,
        validateInput: validateInput,
        applyTheme: applyTheme,
        version: '1.0.0'
    };
    
})();
