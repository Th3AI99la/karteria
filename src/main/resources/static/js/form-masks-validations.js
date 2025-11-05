/**
 * Função para validar CPF
 *
 */
function validaCPF(cpf) {
    cpf = String(cpf).replace(/\D/g, ''); // Remove todos os caracteres não numéricos

    if (cpf.length !== 11) return false;

    // Verifica se todos os dígitos são iguais (ex: "111.111.111-11"), o que é inválido
    if (/^(\d)\1{10}$/.test(cpf)) return false;

    let soma = 0;
    let resto;

    // Valida o primeiro dígito verificador (DV)
    for (let i = 1; i <= 9; i++) {
        soma += parseInt(cpf.substring(i - 1, i)) * (11 - i);
    }
    resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpf.substring(9, 10))) return false;

    soma = 0;
    // Valida o segundo dígito verificador (DV)
    for (let i = 1; i <= 10; i++) {
        soma += parseInt(cpf.substring(i - 1, i)) * (12 - i);
    }
    resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpf.substring(10, 11))) return false;

    return true; // CPF é válido
}

/**
 * Função para aplicar máscara de CPF (###.###.###-##)
 *
 */
function applyCpfMask(inputElement) {
    if (!inputElement) return;
    inputElement.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\D/g, ''); // Remove tudo que não for dígito
        value = value.substring(0, 11); // Limita a 11 dígitos

        value = value.replace(/(\d{3})(\d)/, '$1.$2'); // Coloca ponto após 3 dígitos
        value = value.replace(/(\d{3})(\d)/, '$1.$2'); // Coloca ponto após 6 dígitos
        value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2'); // Coloca hífen antes dos últimos 2 dígitos

        e.target.value = value;
    });
}

/**
 * Função para aplicar máscara de Telefone ((XX) XXXXX-XXXX ou (XX) XXXX-XXXX)
 *
 */
function applyPhoneMask(inputElement) {
    if (!inputElement) return;
    inputElement.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\D/g, ''); // Remove não dígitos
        value = value.substring(0, 11); // Limita a 11 dígitos (incluindo DDD)

        value = value.replace(/^(\d{2})(\d)/g, '($1) $2'); // Coloca parênteses em volta dos dois primeiros dígitos

        if (value.length > 13) {
            // Se for celular (9 dígitos + DDD + espaços/parênteses/hífen)
            value = value.replace(/(\d{5})(\d)/, '$1-$2'); // Coloca hífen depois do quinto dígito do número
        } else if (value.length > 9 && value.length <= 13) {
            // Se for telefone fixo ou celular com 8 dígitos + ddd
            value = value.replace(/(\d{4})(\d)/, '$1-$2'); // Coloca hífen depois do quarto dígito
        }

        e.target.value = value;
    });
}

/**
 * Função para permitir apenas números em um input
 *
 */
function allowOnlyNumbers(inputElement) {
    if (!inputElement) return;
    inputElement.addEventListener('input', (e) => {
        e.target.value = e.target.value.replace(/\D/g, '');
    });
}

/**
 * Função para permitir apenas letras e espaços (remove números e caracteres especiais)
 *
 */
function allowOnlyLettersAndSpaces(inputElement) {
    if (!inputElement) return;
    inputElement.addEventListener('input', (e) => {
        // Permite letras (incluindo acentuadas) e espaços
        e.target.value = e.target.value.replace(/[^a-zA-ZÀ-ÿ\s]/g, '');
    });
}

/**
 * Função para capitalizar a primeira letra de cada palavra (Title Case)
 *
 */
function capitalizeWords(inputElement) {
    if (!inputElement) return;
    inputElement.addEventListener('change', (e) => {
        // 'change' é disparado quando o campo perde o foco
        let value = e.target.value.toLowerCase().trim();
        if (value) {
            // Capitaliza a primeira letra de cada palavra separada por espaço
            e.target.value = value
                .split(' ')
                .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
                .join(' ');
        }
    });
}

/**
 * Função para capitalizar apenas a primeira letra da string inteira
 *
 */
function capitalizeFirstLetter(inputElement) {
    if (!inputElement) return;
    inputElement.addEventListener('change', (e) => {
        let value = e.target.value.trim(); // Mantém case original exceto a primeira
        if (value) {
            e.target.value = value.charAt(0).toUpperCase() + value.slice(1);
        }
    });
}

// =================================================================
// === 2. EXECUÇÃO (QUANDO O DOM ESTIVER PRONTO)
// =================================================================

document.addEventListener('DOMContentLoaded', () => {
    // === ANEXA MÁSCARAS ===
    applyCpfMask(document.getElementById('cpf'));
    applyPhoneMask(document.getElementById('telefone'));
    applyPhoneMask(document.getElementById('telefone2'));

    // === ANEXA VALIDAÇÕES DE TIPO ===
    allowOnlyNumbers(document.getElementById('numeroInput'));
    allowOnlyLettersAndSpaces(document.getElementById('nome'));
    allowOnlyLettersAndSpaces(document.getElementById('sobrenome'));

    // === ANEXA CAPITALIZAÇÃO ===
    capitalizeWords(document.getElementById('nome'));
    capitalizeWords(document.getElementById('sobrenome'));
    capitalizeFirstLetter(document.getElementById('ruaInput'));
    capitalizeFirstLetter(document.getElementById('complementoInput'));
    capitalizeFirstLetter(document.getElementById('bairroInput'));

    console.log('Máscaras e validações de tipo aplicadas.');

    // === ANEXA VALIDAÇÃO DE CPF EM TEMPO REAL ===
    // (Este bloco estava fora do DOMContentLoaded no arquivo original)

    const cpfInput = document.getElementById('cpf');
    const cpfErrorDiv = document.getElementById('cpfError'); // Div de erro do CPF

    if (cpfInput && cpfErrorDiv) {
        // Gatilho: quando o usuário sai do campo
        cpfInput.addEventListener('blur', () => {
            const cpf = cpfInput.value;

            // Não valida se estiver vazio (o 'required' cuidará disso no submit)
            if (cpf.trim() === '') {
                cpfErrorDiv.textContent = ''; // Limpa erro JS
                cpfInput.closest('.input-group').classList.remove('error', 'success');
                return;
            }

            // Se não estiver vazio, valida o formato
            if (validaCPF(cpf)) {
                // Válido
                cpfErrorDiv.textContent = ''; // Limpa qualquer mensagem de erro
                cpfInput.closest('.input-group').classList.remove('error');
                // (Opcional) Adiciona classe de sucesso
                // cpfInput.closest('.input-group').classList.add('success');
            } else {
                // Inválido
                cpfErrorDiv.textContent = 'CPF inválido.';
                cpfInput.closest('.input-group').classList.add('error');
                cpfInput.closest('.input-group').classList.remove('success');
            }
        });

        // Limpa o erro de JS assim que o usuário começar a corrigir
        cpfInput.addEventListener('input', () => {
            if (cpfErrorDiv.textContent === 'CPF inválido.') {
                cpfErrorDiv.textContent = '';
                cpfInput.closest('.input-group').classList.remove('error', 'success');
            }
        });

        console.log('Validação de CPF em tempo real anexada com sucesso.');
    } else {
        console.warn('Campos de CPF (cpf ou cpfError) não encontrados para anexar validação.');
    }

    // === ANEXA CAPITALIZAÇÃO (CAMPOS DO ANUNCIO-FORM) ===
    capitalizeFirstLetter(document.getElementById('tituloVaga'));
    capitalizeFirstLetter(document.getElementById('descricaoVaga'));
});
