/**
 * Funções reutilizáveis para máscaras e validações de formulário
 */

// Função para aplicar máscara de CPF (###.###.###-##)
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

// Função para aplicar máscara de Telefone ((XX) XXXXX-XXXX ou (XX) XXXX-XXXX)
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

// Função para permitir apenas números em um input
function allowOnlyNumbers(inputElement) {
    if (!inputElement) return;
    inputElement.addEventListener('input', (e) => {
        e.target.value = e.target.value.replace(/\D/g, '');
    });
}

// Adiciona um listener global para aplicar as máscaras quando o DOM carregar
// Para isso funcionar, os inputs precisam ter os IDs corretos
document.addEventListener('DOMContentLoaded', () => {
    // Máscaras (existentes)
    applyCpfMask(document.getElementById('cpf'));
    applyPhoneMask(document.getElementById('telefone'));
    applyPhoneMask(document.getElementById('telefone2'));

    // Validações de Tipo (existentes + novas)
    allowOnlyNumbers(document.getElementById('numeroInput'));
    allowOnlyLettersAndSpaces(document.getElementById('nome'));
    allowOnlyLettersAndSpaces(document.getElementById('sobrenome'));

    // Capitalização (novas)
    capitalizeWords(document.getElementById('nome'));
    capitalizeWords(document.getElementById('sobrenome'));
    capitalizeFirstLetter(document.getElementById('ruaInput')); // Capitaliza só a primeira da rua
    capitalizeFirstLetter(document.getElementById('complementoInput')); // Capitaliza só a primeira do complemento
    capitalizeFirstLetter(document.getElementById('bairroInput')); // Capitaliza só a primeira do bairro
    // Cidade e Estado já vêm capitalizados das APIs

    console.log("Máscaras e validações aplicadas."); // Log de confirmação
});

// Função para permitir apenas letras e espaços (remove números e caracteres especiais)
function allowOnlyLettersAndSpaces(inputElement) {
    if (!inputElement) return;
    inputElement.addEventListener('input', (e) => {
        // Permite letras (incluindo acentuadas) e espaços
        e.target.value = e.target.value.replace(/[^a-zA-ZÀ-ÿ\s]/g, '');
    });
}

// Função para capitalizar a primeira letra de cada palavra (Title Case)
// Usaremos no 'change' para não atrapalhar a digitação
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

// Função para capitalizar apenas a primeira letra da string inteira
// Usaremos no 'change'
function capitalizeFirstLetter(inputElement) {
    if (!inputElement) return;
    inputElement.addEventListener('change', (e) => {
        let value = e.target.value.trim(); // Mantém case original exceto a primeira
        if (value) {
            e.target.value = value.charAt(0).toUpperCase() + value.slice(1);
        }
    });
}
