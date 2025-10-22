/* ========================================
   KARTERIA - JAVASCRIPT PRINCIPAL (v2)
   ======================================== */

(function () {
    'use strict';

    // === INICIALIZAÇÃO === //
    document.addEventListener('DOMContentLoaded', function () {
        // Separa a lógica de aplicação do tema da configuração do botão
        applySavedTheme(); // 1. Aplica o tema salvo (roda em todas as páginas)
        initThemeToggle(); // 2. Configura o botão de troca (só roda se o botão existir)

        // Funções existentes que continuam funcionando normalmente
        initNavigation();
        initScrollEffects();
        initAnimations();
        initInteractions();
        initAccessibility();

        console.log('🚀 Karteria initialized successfully!');
    });

    // === SISTEMA DE TEMA (REESTRUTURADO) === //

    /**
     * Esta função é independente e sempre executa.
     * Ela lê o tema do localStorage ou a preferência do sistema e aplica na página.
     */
    function applySavedTheme() {
        // Se um tema já foi salvo pelo usuário, usa ele.
        // Senão, verifica a preferência do sistema operacional.
        // Se nada for definido, usa 'light' como padrão.
        const savedTheme =
            localStorage.getItem('karteria-theme') ||
            (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
        applyTheme(savedTheme);
    }

    /**
     * Esta função agora SÓ se preocupa em configurar o botão de troca de tema.
     */
    function initThemeToggle() {
        const themeToggle = document.getElementById('themeToggle');

        // Ponto crucial: Se a página não tem o botão, a função para aqui.
        if (!themeToggle) {
            return;
        }

        // Se o botão existe, adiciona o evento de clique.
        themeToggle.addEventListener('click', () => {
            const currentTheme = document.documentElement.getAttribute('data-theme');
            const newTheme = currentTheme === 'light' ? 'dark' : 'light';
            applyTheme(newTheme);

            // Animação do ícone
            const icon = document.getElementById('themeIcon');
            if (icon) {
                icon.style.transform = 'rotate(360deg)';
                setTimeout(() => {
                    icon.style.transform = 'rotate(0deg)';
                }, 300);
            }
        });
    }

    /**
     * Função auxiliar que efetivamente aplica o tema e atualiza o ícone.
     * (sem alterações, mas agora chamada pelas duas funções acima)
     */
    function applyTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('karteria-theme', theme);

        const themeIcon = document.getElementById('themeIcon');
        if (themeIcon) {
            themeIcon.className = theme === 'light' ? 'fas fa-moon' : 'fas fa-sun';
        }

        document.dispatchEvent(new CustomEvent('themeChanged', { detail: { theme } }));
    }

    // === NAVEGAÇÃO === //
    function initNavigation() {
        const navbar = document.querySelector('.navbar-modern');
        if (!navbar) return;

        // Scroll effect na navbar
        let lastScrollY = window.scrollY;

        window.addEventListener(
            'scroll',
            throttle(() => {
                const currentScrollY = window.scrollY;

                if (currentScrollY > 100) {
                    navbar.classList.add('scrolled');
                } else {
                    navbar.classList.remove('scrolled');
                }

                // Auto-hide navbar on scroll down
                if (currentScrollY > lastScrollY && currentScrollY > 200) {
                    navbar.style.transform = 'translateY(-100%)';
                } else {
                    navbar.style.transform = 'translateY(0)';
                }

                lastScrollY = currentScrollY;
            }, 100)
        );

        // Smooth scroll para links internos
        document.querySelectorAll('a[href^="#"]').forEach((link) => {
            link.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start',
                    });
                }
            });
        });

        // Mobile menu toggle
        const navbarToggler = document.querySelector('.navbar-toggler');
        const navbarCollapse = document.querySelector('.navbar-collapse');

        if (navbarToggler && navbarCollapse) {
            navbarToggler.addEventListener('click', function () {
                navbarCollapse.classList.toggle('show');
            });

            // Fechar menu ao clicar em link
            navbarCollapse.querySelectorAll('a').forEach((link) => {
                link.addEventListener('click', () => {
                    navbarCollapse.classList.remove('show');
                });
            });
        }
    }

    // === EFEITOS DE SCROLL === //
    function initScrollEffects() {
        // Parallax effect para elementos flutuantes
        const floatingElements = document.querySelectorAll('.animate-float');

        window.addEventListener(
            'scroll',
            throttle(() => {
                const scrolled = window.pageYOffset;
                const rate = scrolled * -0.5;

                floatingElements.forEach((element, index) => {
                    const speed = 0.5 + index * 0.1;
                    element.style.transform = `translateY(${rate * speed}px)`;
                });
            }, 16)
        );

        // Reveal animations on scroll
        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px',
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('animate-fade-in-up');
                    observer.unobserve(entry.target);
                }
            });
        }, observerOptions);

        document.querySelectorAll('.card-modern, .feature-icon').forEach((el) => {
            observer.observe(el);
        });
    }

    // === ANIMAÇÕES === //
    function initAnimations() {
        // Hover effects para cards
        document.querySelectorAll('.card-modern').forEach((card) => {
            card.addEventListener('mouseenter', function () {
                this.style.transform = 'translateY(-10px) scale(1.02)';
            });

            card.addEventListener('mouseleave', function () {
                this.style.transform = 'translateY(0) scale(1)';
            });
        });

        // Ripple effect para botões
        document.querySelectorAll('.btn-primary-karteria, .form-button').forEach((button) => {
            button.addEventListener('click', createRipple);
        });

        // Loading states
        document.querySelectorAll('form').forEach((form) => {
            form.addEventListener('submit', function () {
                const submitButton = this.querySelector('button[type="submit"]');
                if (submitButton && !submitButton.classList.contains('loading')) {
                    addLoadingState(submitButton);
                }
            });
        });
    }

    function createRipple(event) {
        const button = event.currentTarget;
        const circle = document.createElement('span');
        const diameter = Math.max(button.clientWidth, button.clientHeight);
        const radius = diameter / 2;

        circle.style.width = circle.style.height = `${diameter}px`;
        circle.style.left = `${event.clientX - button.offsetLeft - radius}px`;
        circle.style.top = `${event.clientY - button.offsetTop - radius}px`;
        circle.classList.add('ripple');

        const ripple = button.getElementsByClassName('ripple')[0];
        if (ripple) {
            ripple.remove();
        }

        button.appendChild(circle);

        setTimeout(() => {
            circle.remove();
        }, 600);
    }

    function addLoadingState(button) {
        const originalText = button.innerHTML;
        button.classList.add('loading');
        button.disabled = true;

        // Simular loading (remover em produção)
        setTimeout(() => {
            button.classList.remove('loading');
            button.disabled = false;
            button.innerHTML = originalText;
        }, 2000);
    }

    // === INTERAÇÕES === //
    function initInteractions() {
        // Tooltip initialization
        initTooltips();

        // Form enhancements
        initFormEnhancements();

        // Keyboard navigation
        initKeyboardNavigation();

        // Performance monitoring
        initPerformanceMonitoring();
    }

    function initTooltips() {
        document.querySelectorAll('[data-tooltip]').forEach((element) => {
            element.addEventListener('mouseenter', showTooltip);
            element.addEventListener('mouseleave', hideTooltip);
        });
    }

    function showTooltip(event) {
        const element = event.target;
        const text = element.getAttribute('data-tooltip');

        const tooltip = document.createElement('div');
        tooltip.className = 'tooltip-custom';
        tooltip.textContent = text;

        document.body.appendChild(tooltip);

        const rect = element.getBoundingClientRect();
        tooltip.style.left = `${rect.left + rect.width / 2}px`;
        tooltip.style.top = `${rect.top - tooltip.offsetHeight - 10}px`;

        element._tooltip = tooltip;
    }

    function hideTooltip(event) {
        const element = event.target;
        if (element._tooltip) {
            element._tooltip.remove();
            delete element._tooltip;
        }
    }

    function initFormEnhancements() {
        // Auto-resize textareas
        document.querySelectorAll('textarea').forEach((textarea) => {
            textarea.addEventListener('input', function () {
                this.style.height = 'auto';
                this.style.height = this.scrollHeight + 'px';
            });
        });

        // Input masks (se necessário)
        document.querySelectorAll('input[data-mask]').forEach((input) => {
            input.addEventListener('input', applyMask);
        });

        // Real-time validation
        document.querySelectorAll('.form-input').forEach((input) => {
            input.addEventListener('input', debounce(validateInput, 300));
        });
    }

    function validateInput(event) {
        const input = event.target;
        const value = input.value;
        const type = input.type;

        let isValid = true;
        let message = '';

        switch (type) {
            case 'email':
                isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
                message = 'Digite um e-mail válido';
                break;
            case 'password':
                isValid = value.length >= 6;
                message = 'Senha deve ter pelo menos 6 caracteres';
                break;
            default:
                isValid = value.trim().length > 0;
                message = 'Este campo é obrigatório';
        }

        const inputGroup = input.closest('.input-group');
        if (inputGroup) {
            inputGroup.classList.toggle('error', !isValid && value.length > 0);
            inputGroup.classList.toggle('success', isValid && value.length > 0);
        }
    }

    function applyMask(event) {
        const input = event.target;
        const mask = input.getAttribute('data-mask');
        let value = input.value.replace(/\D/g, '');

        switch (mask) {
            case 'phone':
                value = value.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
                break;
            case 'cpf':
                value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
                break;
        }

        input.value = value;
    }

    // === ACESSIBILIDADE === //
    function initAccessibility() {
        // Focus management
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Tab') {
                document.body.classList.add('keyboard-navigation');
            }
        });

        document.addEventListener('mousedown', function () {
            document.body.classList.remove('keyboard-navigation');
        });

        // Skip links
        const skipLink = document.createElement('a');
        skipLink.href = '#main-content';
        skipLink.className = 'skip-link';
        document.body.insertBefore(skipLink, document.body.firstChild);

        // ARIA live regions
        const liveRegion = document.createElement('div');
        liveRegion.setAttribute('aria-live', 'polite');
        liveRegion.setAttribute('aria-atomic', 'true');
        liveRegion.className = 'sr-only';
        liveRegion.id = 'live-region';
        document.body.appendChild(liveRegion);
    }

    function initKeyboardNavigation() {
        document.addEventListener('keydown', function (e) {
            // ESC para fechar modais/menus
            if (e.key === 'Escape') {
                document.querySelectorAll('.navbar-collapse.show').forEach((menu) => {
                    menu.classList.remove('show');
                });
            }

            // Enter/Space para elementos clicáveis
            if ((e.key === 'Enter' || e.key === ' ') && e.target.hasAttribute('data-clickable')) {
                e.preventDefault();
                e.target.click();
            }
        });
    }

    // === MONITORAMENTO DE PERFORMANCE === //
    function initPerformanceMonitoring() {
        // Web Vitals (simplificado)
        if ('PerformanceObserver' in window) {
            const observer = new PerformanceObserver((list) => {
                list.getEntries().forEach((entry) => {
                    if (entry.entryType === 'largest-contentful-paint') {
                        console.log('LCP:', entry.startTime);
                    }
                });
            });

            observer.observe({ entryTypes: ['largest-contentful-paint'] });
        }

        // Error tracking
        window.addEventListener('error', function (e) {
            console.error('JavaScript Error:', e.error);
        });

        window.addEventListener('unhandledrejection', function (e) {
            console.error('Unhandled Promise Rejection:', e.reason);
        });
    }

    // === UTILITÁRIOS === //
    function throttle(func, limit) {
        let inThrottle;
        return function () {
            const args = arguments;
            const context = this;
            if (!inThrottle) {
                func.apply(context, args);
                inThrottle = true;
                setTimeout(() => (inThrottle = false), limit);
            }
        };
    }

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
        const liveRegion = document.getElementById('live-region');
        if (liveRegion) {
            liveRegion.textContent = message;
            setTimeout(() => {
                liveRegion.textContent = '';
            }, 1000);
        }
    }

    // === API PÚBLICA === //
    window.Karteria = {
        theme: {
            toggle: toggleTheme,
            set: applyTheme,
            get: () => KARTERIA.theme.current,
        },
        utils: {
            throttle,
            debounce,
            announceToScreenReader,
        },
        version: '1.0.0',
    };
})();

// === CSS DINÂMICO === //
const dynamicStyles = `
    .ripple {
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.6);
        transform: scale(0);
        animation: ripple-animation 0.6s linear;
        pointer-events: none;
    }
    
    @keyframes ripple-animation {
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
    
    .tooltip-custom {
        position: absolute;
        background: var(--bg-secondary);
        color: var(--text-primary);
        padding: 0.5rem 0.75rem;
        border-radius: var(--radius-md);
        font-size: 0.875rem;
        box-shadow: var(--shadow-lg);
        z-index: 1000;
        pointer-events: none;
        transform: translateX(-50%);
        border: 1px solid var(--border-light);
    }
    
    .skip-link {
        position: absolute;
        top: -40px;
        left: 6px;
        background: var(--brand-primary);
        color: white;
        padding: 8px;
        text-decoration: none;
        border-radius: 4px;
        z-index: 1000;
        transition: top 0.3s;
    }
    
    .skip-link:focus {
        top: 6px;
    }
    
    .sr-only {
        position: absolute;
        width: 1px;
        height: 1px;
        padding: 0;
        margin: -1px;
        overflow: hidden;
        clip: rect(0, 0, 0, 0);
        white-space: nowrap;
        border: 0;
    }
    
    .keyboard-navigation *:focus {
        outline: 2px solid var(--brand-primary);
        outline-offset: 2px;
    }
    
    .navbar-modern.scrolled {
        background: rgba(255, 255, 255, 0.98);
        backdrop-filter: blur(20px);
    }
    
    [data-theme="dark"] .navbar-modern.scrolled {
        background: rgba(17, 24, 39, 0.98);
    }
    
    @media (prefers-reduced-motion: reduce) {
        .ripple {
            animation: none;
        }
        
        .animate-float {
            animation: none;
        }
    }
`;

// Injetar estilos dinâmicos
const styleSheet = document.createElement('style');
styleSheet.textContent = dynamicStyles;
document.head.appendChild(styleSheet);
