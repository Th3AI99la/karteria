// Perfil.js - Lógica dos Modais de Edição de Perfil e Endereço

document.addEventListener('DOMContentLoaded', () => {
    // 1. APLICA MÁSCARAS E VALIDAÇÕES NO MODAL 1 (EDITAR PERFIL)

    try {
        // Aplica máscara de telefone, se a função existir
        if (typeof applyPhoneMask === 'function') {
            applyPhoneMask(document.getElementById('editTelefone'));
            applyPhoneMask(document.getElementById('editTelefone2'));
        }

        // Permite apenas letras e espaços em nome/sobrenome
        if (typeof allowOnlyLettersAndSpaces === 'function') {
            allowOnlyLettersAndSpaces(document.getElementById('editNome'));
            allowOnlyLettersAndSpaces(document.getElementById('editSobrenome'));
        }
    } catch (e) {
        console.error('Erro ao aplicar máscaras/validações no Modal 1:', e.message);
    }

    // 2. LÓGICA DO MODAL 2 (EDITAR ENDEREÇO)

    const modalEndereco = document.getElementById('modalEditarEndereco');
    if (modalEndereco) {
        // ---- Seletores dos campos dentro do Modal 2 ----
        const cepInput = modalEndereco.querySelector('#edit-cepInput');
        const ruaInput = modalEndereco.querySelector('#edit-ruaInput');
        const bairroInput = modalEndereco.querySelector('#edit-bairroInput');
        const cidadeSelect = modalEndereco.querySelector('#edit-cidadeSelect');
        const estadoSelect = modalEndereco.querySelector('#edit-estadoSelect');
        const numeroInput = modalEndereco.querySelector('#edit-numeroInput');
        const complementoInput = modalEndereco.querySelector('#edit-complementoInput');
        const cepErrorDiv = modalEndereco.querySelector('#edit-cepError');
        const btnConfirmar = modalEndereco.querySelector('#btnConfirmarEndereco');

        // ---- Seletores dos campos no Modal 1 (perfil) ----
        const enderecoDisplayInput = document.getElementById('editEnderecoDisplay');
        const enderecoHiddenInput = document.getElementById('editEnderecoHidden');

        const bootstrapModalEndereco = new bootstrap.Modal(modalEndereco);

        // POPULAÇÃO DOS ESTADOS (LISTA FIXA DE UF)

        const estados = [
            { sigla: 'AC', nome: 'Acre' },
            { sigla: 'AL', nome: 'Alagoas' },
            { sigla: 'AP', nome: 'Amapá' },
            { sigla: 'AM', nome: 'Amazonas' },
            { sigla: 'BA', nome: 'Bahia' },
            { sigla: 'CE', nome: 'Ceará' },
            { sigla: 'DF', nome: 'Distrito Federal' },
            { sigla: 'ES', nome: 'Espírito Santo' },
            { sigla: 'GO', nome: 'Goiás' },
            { sigla: 'MA', nome: 'Maranhão' },
            { sigla: 'MT', nome: 'Mato Grosso' },
            { sigla: 'MS', nome: 'Mato Grosso do Sul' },
            { sigla: 'MG', nome: 'Minas Gerais' },
            { sigla: 'PA', nome: 'Pará' },
            { sigla: 'PB', nome: 'Paraíba' },
            { sigla: 'PR', nome: 'Paraná' },
            { sigla: 'PE', nome: 'Pernambuco' },
            { sigla: 'PI', nome: 'Piauí' },
            { sigla: 'RJ', nome: 'Rio de Janeiro' },
            { sigla: 'RN', nome: 'Rio Grande do Norte' },
            { sigla: 'RS', nome: 'Rio Grande do Sul' },
            { sigla: 'RO', nome: 'Rondônia' },
            { sigla: 'RR', nome: 'Roraima' },
            { sigla: 'SC', nome: 'Santa Catarina' },
            { sigla: 'SP', nome: 'São Paulo' },
            { sigla: 'SE', nome: 'Sergipe' },
            { sigla: 'TO', nome: 'Tocantins' }
        ];

        if (estadoSelect) {
            estadoSelect.innerHTML = '<option value="" disabled selected>UF</option>';
            estados.sort((a, b) => a.sigla.localeCompare(b.sigla));
            estados.forEach((uf) => {
                const option = document.createElement('option');
                option.value = uf.sigla;
                option.textContent = uf.sigla;
                estadoSelect.appendChild(option);
            });
        }

        // CARREGAMENTO DINÂMICO DE CIDADES (API IBGE)

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

                cidades.forEach((cidade) => {
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

        // Evento para atualizar cidades ao trocar o estado
        estadoSelect.addEventListener('change', (e) => carregarCidades(e.target.value));

        // ===============================================================
        // === LÓGICA DE BUSCA DE CEP (API VIACEP)
        // ===============================================================
        const preencherCamposViaCep = (dados) => {
            if (dados.erro) {
                cepErrorDiv.textContent = 'CEP não encontrado. Preencha manualmente.';
                cepInput.closest('.input-group').classList.add('error');
                ruaInput.readOnly = false;
                bairroInput.readOnly = false;
                estadoSelect.disabled = false;
                cidadeSelect.disabled = true;
                estadoSelect.value = '';
                cidadeSelect.innerHTML = '<option value="" disabled selected>Selecione o Estado</option>';
                ruaInput.focus();
                return;
            }

            // Preenche automaticamente os campos retornados
            ruaInput.value = dados.logradouro || '';
            ruaInput.readOnly = !!dados.logradouro;
            bairroInput.value = dados.bairro || '';
            bairroInput.readOnly = !!dados.bairro;
            estadoSelect.value = dados.uf || '';
            estadoSelect.disabled = !!dados.uf;
            cepErrorDiv.textContent = '';
            cepInput.closest('.input-group').classList.remove('error');

            if (dados.uf) {
                carregarCidades(dados.uf).then(() => {
                    if (dados.localidade) {
                        cidadeSelect.value = dados.localidade;
                        cidadeSelect.disabled = true;
                    } else {
                        cidadeSelect.disabled = false;
                    }
                    numeroInput.focus();
                    atualizarEnderecoCompleto();
                });
            } else {
                estadoSelect.disabled = false;
                cidadeSelect.disabled = true;
                cidadeSelect.innerHTML = '<option value="" disabled selected>Selecione o Estado</option>';
                numeroInput.focus();
                atualizarEnderecoCompleto();
            }
        };

        const buscarCep = async (cep) => {
            const cepLimpo = cep.replace(/\D/g, '');
            if (cepLimpo.length !== 8) return;

            const url = `https://viacep.com.br/ws/${cepLimpo}/json/`;
            try {
                const response = await fetch(url);
                if (!response.ok) throw new Error('Erro');
                const dados = await response.json();
                preencherCamposViaCep(dados);
            } catch (error) {
                cepErrorDiv.textContent = 'Erro ao buscar CEP. Preencha manualmente.';
                cepInput.closest('.input-group').classList.add('error');
                ruaInput.readOnly = false;
                bairroInput.readOnly = false;
                estadoSelect.disabled = false;
                cidadeSelect.disabled = true;
                cidadeSelect.innerHTML = '<option value="" disabled selected>Selecione o Estado</option>';
                ruaInput.focus();
            }
        };

        // Input com debounce para evitar múltiplas chamadas à API
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

        // MONTA O ENDEREÇO COMPLETO E ATUALIZA MODAL 1

        // (Dentro do arquivo profile.js)

        // MONTA O ENDEREÇO COMPLETO E ATUALIZA MODAL 1
        const atualizarEnderecoCompleto = () => {
            if (!enderecoHiddenInput || !enderecoDisplayInput) return;

            // --- INÍCIO DA CORREÇÃO ---
            // Captura os valores individuais
            const cidadeFormatada = cidadeSelect.value || '';
            const estadoFormatado = estadoSelect.value ? `/${estadoSelect.value}` : '';
            const cidadeEstado =
                cidadeFormatada || estadoFormatado ? [cidadeFormatada, estadoFormatado].filter(Boolean).join('') : '';
            const bairroFormatado = bairroInput.value || '';

            // Constrói a parte de localização COM O SEPARADOR CORRETO
            let localizacao = '';
            if (bairroFormatado && cidadeEstado) {
                // ESTA É A CORREÇÃO: Adiciona " - " entre o bairro e a cidade
                localizacao = ` - ${bairroFormatado} - ${cidadeEstado}`;
            } else if (bairroFormatado) {
                localizacao = ` - ${bairroFormatado}`;
            } else if (cidadeEstado) {
                localizacao = ` - ${cidadeEstado}`;
            }
            // --- FIM DA CORREÇÃO ---

            const partes = [
                ruaInput.value,
                numeroInput.value ? `, ${numeroInput.value}` : '',
                complementoInput.value ? ` - ${complementoInput.value}` : '',
                localizacao, // Usa a variável 'localizacao' corrigida
                cepInput.value ? ` (CEP: ${cepInput.value})` : ''
            ];

            const enderecoCompleto = partes.filter((p) => p && p.trim() !== '').join('');

            // Atualiza os dois campos do Modal 1 (exibição e hidden)
            enderecoDisplayInput.value = enderecoCompleto;
            enderecoHiddenInput.value = enderecoCompleto;

            console.log('Endereço Completo (hidden) atualizado:', enderecoCompleto);
        };

        // MUDANÇA PRINCIPAL: CONFIRMAÇÃO DO MODAL 2

        btnConfirmar.addEventListener('click', () => {
            // 1. Encontra o formulário principal (Modal 1)
            const formModal1 = document.querySelector('#modalEditarPerfil form');
            if (!formModal1) {
                console.error('ERRO: Formulário principal #modalEditarPerfil não encontrado!');
                return;
            }

            // 2. Atualiza o endereço completo antes de submeter
            atualizarEnderecoCompleto();

            // 3. Validação dos campos obrigatórios do endereço
            if (
                !ruaInput.value ||
                !numeroInput.value ||
                !bairroInput.value ||
                !cidadeSelect.value ||
                !estadoSelect.value
            ) {
                alert('Por favor, preencha todos os campos de endereço obrigatórios (*).');
                // Restaura o botão
                btnConfirmar.disabled = false;
                btnConfirmar.innerHTML = 'Confirmar';
                return;
            }

            // 4. Feedback visual de carregamento
            btnConfirmar.disabled = true;
            btnConfirmar.innerHTML =
                '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Salvando...';

            // 5. Submete o formulário principal (Modal 1)
            console.log('Submetendo formulário principal (Modal 1) com o novo endereço...');
            formModal1.submit();
        });
    }
});
