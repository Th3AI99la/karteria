/**
 * Lógica específica para a página profile.html
 */
document.addEventListener('DOMContentLoaded', () => {

    // === 1. APLICA MÁSCARAS NO MODAL 1 (EDITAR PERFIL) ===
    // (Chama as funções do form-masks-validations.js)
    try {
        if (typeof applyPhoneMask === 'function') {
            applyPhoneMask(document.getElementById('editTelefone'));
            applyPhoneMask(document.getElementById('editTelefone2'));
        }
        if (typeof allowOnlyLettersAndSpaces === 'function') {
            allowOnlyLettersAndSpaces(document.getElementById('editNome'));
            allowOnlyLettersAndSpaces(document.getElementById('editSobrenome'));
        }
    } catch (e) {
        console.error("Erro ao aplicar máscaras/validações no Modal 1:", e.message);
    }

    // === 2. LÓGICA DO MODAL 2 (EDITAR ENDEREÇO) ===
    const modalEndereco = document.getElementById('modalEditarEndereco');
    if (modalEndereco) {
        // Seletores para campos DENTRO do modal 2
        const cepInput = modalEndereco.querySelector('#edit-cepInput');
        const ruaInput = modalEndereco.querySelector('#edit-ruaInput');
        const bairroInput = modalEndereco.querySelector('#edit-bairroInput');
        const cidadeSelect = modalEndereco.querySelector('#edit-cidadeSelect');
        const estadoSelect = modalEndereco.querySelector('#edit-estadoSelect');
        const numeroInput = modalEndereco.querySelector('#edit-numeroInput');
        const complementoInput = modalEndereco.querySelector('#edit-complementoInput');
        const cepErrorDiv = modalEndereco.querySelector('#edit-cepError');
        const btnConfirmar = modalEndereco.querySelector('#btnConfirmarEndereco');

        // Seletores para campos NO MODAL 1 (os alvos)
        const enderecoDisplayInput = document.getElementById('editEnderecoDisplay');
        const enderecoHiddenInput = document.getElementById('editEnderecoHidden');

        // Pega a instância do Modal Bootstrap 2
        const bootstrapModalEndereco = new bootstrap.Modal(modalEndereco);

        // --- Populando Estados (Lista Completa) ---
        const estados = [
            { sigla: 'AC', nome: 'Acre' }, { sigla: 'AL', nome: 'Alagoas' }, { sigla: 'AP', nome: 'Amapá' },
            { sigla: 'AM', nome: 'Amazonas' }, { sigla: 'BA', nome: 'Bahia' }, { sigla: 'CE', nome: 'Ceará' },
            { sigla: 'DF', nome: 'Distrito Federal' }, { sigla: 'ES', nome: 'Espírito Santo' }, { sigla: 'GO', nome: 'Goiás' },
            { sigla: 'MA', nome: 'Maranhão' }, { sigla: 'MT', nome: 'Mato Grosso' }, { sigla: 'MS', nome: 'Mato Grosso do Sul' },
            { sigla: 'MG', nome: 'Minas Gerais' }, { sigla: 'PA', nome: 'Pará' }, { sigla: 'PB', nome: 'Paraíba' },
            { sigla: 'PR', nome: 'Paraná' }, { sigla: 'PE', nome: 'Pernambuco' }, { sigla: 'PI', nome: 'Piauí' },
            { sigla: 'RJ', nome: 'Rio de Janeiro' }, { sigla: 'RN', nome: 'Rio Grande do Norte' }, { sigla: 'RS', nome: 'Rio Grande do Sul' },
            { sigla: 'RO', nome: 'Rondônia' }, { sigla: 'RR', nome: 'Roraima' }, { sigla: 'SC', nome: 'Santa Catarina' },
            { sigla: 'SP', nome: 'São Paulo' }, { sigla: 'SE', nome: 'Sergipe' }, { sigla: 'TO', nome: 'Tocantins' }
        ];
        if(estadoSelect){
            estadoSelect.innerHTML = '<option value="" disabled selected>UF</option>';
            estados.sort((a, b) => a.sigla.localeCompare(b.sigla));
            estados.forEach(uf => {
                const option = document.createElement('option');
                option.value = uf.sigla;
                option.textContent = uf.sigla;
                estadoSelect.appendChild(option);
            });
        }

        // --- Populando Cidades (API IBGE) ---
        const carregarCidades = async (uf) => {
            if (!uf || !cidadeSelect) {
                cidadeSelect.innerHTML = '<option value="" disabled selected>Selecione o Estado</option>';
                cidadeSelect.disabled = true;
                return;
            }
            cidadeSelect.disabled = true;
            cidadeSelect.innerHTML = '<option value="" disabled selected>Carregando...</option>';
            const url = `https://servicodados.ibge.gov.br/api/v1/localidades/estados/${uf}/municipios`;
            try {
                const response = await fetch(url);
                if (!response.ok) throw new Error('Erro ao buscar cidades');
                const cidades = await response.json();
                cidadeSelect.innerHTML = '<option value="" disabled selected>Selecione a Cidade</option>';
                cidades.sort((a, b) => a.nome.localeCompare(b.nome));
                cidades.forEach(cidade => {
                    const option = document.createElement('option');
                    option.value = cidade.nome;
                    option.textContent = cidade.nome;
                    cidadeSelect.appendChild(option);
                });
                cidadeSelect.disabled = false;
            } catch (error) {
                 cidadeSelect.innerHTML = '<option value="" disabled selected>Erro ao carregar</option>';
            }
        };
        estadoSelect.addEventListener('change', (e) => carregarCidades(e.target.value));

        // --- Lógica do CEP (ViaCEP) ---
        const limparCamposEndereco = () => {
            ruaInput.value = ''; bairroInput.value = ''; cidadeSelect.value = ''; estadoSelect.value = '';
            cepErrorDiv.textContent = ''; cepInput.closest('.input-group').classList.remove('error');
            ruaInput.readOnly = false; bairroInput.readOnly = false; estadoSelect.disabled = false;
            cidadeSelect.disabled = true; cidadeSelect.innerHTML = '<option value="" disabled selected>Selecione o Estado</option>';
        };

        const preencherCamposViaCep = (dados) => {
            if (dados.erro) {
                cepErrorDiv.textContent = 'CEP não encontrado. Preencha manualmente.';
                cepInput.closest('.input-group').classList.add('error');
                ruaInput.readOnly = false; bairroInput.readOnly = false; estadoSelect.disabled = false;
                cidadeSelect.disabled = true; estadoSelect.value = '';
                cidadeSelect.innerHTML = '<option value="" disabled selected>Selecione o Estado</option>';
                ruaInput.focus(); return;
            }
            ruaInput.value = dados.logouro || ''; ruaInput.readOnly = !!dados.logouro;
            bairroInput.value = dados.bairro || ''; bairroInput.readOnly = !!dados.bairro;
            estadoSelect.value = dados.uf || ''; estadoSelect.disabled = !!dados.uf;
            cepErrorDiv.textContent = ''; cepInput.closest('.input-group').classList.remove('error');
            if (dados.uf) {
                carregarCidades(dados.uf).then(() => {
                     if(dados.localidade) { cidadeSelect.value = dados.localidade; cidadeSelect.disabled = true; }
                     else { cidadeSelect.disabled = false; }
                     numeroInput.focus();
                });
            } else {
                estadoSelect.disabled = false; cidadeSelect.disabled = true;
                cidadeSelect.innerHTML = '<option value="" disabled selected>Selecione o Estado</option>';
                numeroInput.focus();
            }
        };

        const buscarCep = async (cep) => {
            const cepLimpo = cep.replace(/\D/g, ''); if (cepLimpo.length !== 8) { return; }
            const url = `https://viacep.com.br/ws/${cepLimpo}/json/`;
            try {
                const response = await fetch(url); if (!response.ok) { throw new Error('Erro'); }
                const dados = await response.json(); preencherCamposViaCep(dados);
            } catch (error) {
                cepErrorDiv.textContent = 'Erro ao buscar CEP. Preencha manualmente.';
                cepInput.closest('.input-group').classList.add('error');
                ruaInput.readOnly = false; bairroInput.readOnly = false; estadoSelect.disabled = false;
                cidadeSelect.disabled = true; cidadeSelect.innerHTML = '<option value="" disabled selected>Selecione o Estado</option>';
                ruaInput.focus();
            }
        };

        let debounceTimer;
        cepInput.addEventListener('input', (event) => {
            let value = event.target.value.replace(/\D/g, '');
            value = value.replace(/^(\d{5})(\d)/, '$1-$2');
            event.target.value = value.substring(0, 9);
            clearTimeout(debounceTimer);
            if (event.target.value.replace(/\D/g, '').length === 8) {
                debounceTimer = setTimeout(() => buscarCep(event.target.value), 500);
            }
        });

        // --- Confirmação do Modal 2 ---
        btnConfirmar.addEventListener('click', () => {
            // Monta a string de endereço (lógica copiada)
            const partes = [
                ruaInput.value,
                numeroInput.value ? `, ${numeroInput.value}` : '',
                complementoInput.value ? ` - ${complementoInput.value}` : '',
                bairroInput.value ? ` - ${bairroInput.value}` : '',
                cidadeSelect.value || '',
                estadoSelect.value ? `/${estadoSelect.value}` : '',
                cepInput.value ? ` (CEP: ${cepInput.value})` : ''
            ];
            const enderecoCompleto = partes.filter(p => p && p.trim() !== '').join('');

            // Atualiza os inputs do Modal 1
            if (enderecoDisplayInput) enderecoDisplayInput.value = enderecoCompleto;
            if (enderecoHiddenInput) enderecoHiddenInput.value = enderecoCompleto;

            // Fecha o Modal 2
             bootstrapModalEndereco.hide();
             // A reabertura do Modal 1 é tratada pelos atributos data-bs-target dos botões "Cancelar" e "X"
        });
    }
});