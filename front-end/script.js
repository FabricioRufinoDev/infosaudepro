const API_BASE_URL = 'http://localhost:8080/api/pacientes';
const USER_TOKEN_KEY = 'jwt_token_infosaudepro';
const AUTH_URL = 'http://localhost:8080/auth/login';

// Variável global para armazenar o nome de usuário (necessário para checagem de role)
let currentUsername = ''; 

// --- FUNÇÕES BÁSICAS DE SEGURANÇA (Token e Status) ---

function setToken(token) {
    localStorage.setItem(USER_TOKEN_KEY, token);
}

function getToken() {
    return localStorage.getItem(USER_TOKEN_KEY);
}

function updateStatus(message, isError = false) {
    const msgElement = document.getElementById('login-message') || document.getElementById('auth-status');
    if (msgElement) {
        msgElement.textContent = message;
        // Certifique-se de que a cor está sendo aplicada corretamente (depende do seu CSS)
        msgElement.style.color = isError ? 'var(--color-error)' : 'var(--color-secondary)';
    }
}

// --- AUTENTICAÇÃO (Login) ---

async function handleLogin() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const basicAuth = 'Basic ' + btoa(username + ':' + password);

    try {
        const response = await fetch(AUTH_URL, {
            method: 'post',
            headers: {
                'Authorization': basicAuth,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            setToken(basicAuth);
            currentUsername = username; 
            
            // 🔑 CORREÇÃO CRÍTICA: Salva o username para persistir o Role na inicialização
            localStorage.setItem('currentUsername', username); 
            
            updateStatus(`Login de ${username} realizado com sucesso!`, false);
            
            // Decide qual seção mostrar (Admin ou Consulta simples)
            if (username.toLowerCase() === 'admin') {
                showAdminSection(username); 
            } else if (username.toLowerCase() === 'medico') {
                showConsultaSection(username); 
            } else {
                showLoginSection(); 
                updateStatus('Usuário não reconhecido. Faça login com admin ou medico.', true);
            }
        } else if (response.status === 401) {
            updateStatus('Erro de autenticação! Credenciais inválidas.', true);
        } else {
            updateStatus('Erro ao tentar conectar com a API.', true);
        }
    } catch (error) {
        updateStatus('Erro de rede ou servidor. Verifique o Back-end (8080).', true);
    }
}

function logout() {
    localStorage.removeItem(USER_TOKEN_KEY);
    localStorage.removeItem('currentUsername'); // 🚨 Limpa o username salvo
    currentUsername = '';
    showLoginSection();
    document.getElementById('paciente-data').innerHTML = '';
    updateStatus('Sessão encerrada.', false);
}


// --- FUNÇÕES DE CRUD DO ADMIN ---

/**
 * Função utilitária para montar os headers de autenticação.
 */
function getAuthHeaders() {
    const token = getToken();
    return {
        'Authorization': token, // Envio do Basic Auth
        'Content-Type': 'application/json'
    };
}

// 1. CONSULTAR/READ
async function buscarPaciente() {
    const pacienteId = document.getElementById('pacienteId').value;
    const token = getToken();

    if (!token) {
        updateStatus("Erro: Não autenticado. Faça login.", true);
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${pacienteId}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });

        if (response.ok) {
            const data = await response.json();
            displayPaciente([data]); 
            updateStatus("Consulta realizada com sucesso.", false);

        } else if (response.status === 403) {
            updateStatus('ERRO 403: Acesso Negado. Você não tem permissão para consultar.', true);
            
        } else if (response.status === 404) {
            displayPaciente([]); 
            updateStatus('Paciente não encontrado.', true);
            
        } else {
            const errorText = await response.text();
            updateStatus(`Erro ao buscar paciente: ${response.status} ${response.statusText}. Detalhe: ${errorText.substring(0, 100)}...`, true);
        }
    } catch (error) {
        updateStatus('Erro de comunicação com o servidor.', true);
    }
}

// 2. INSERIR/CREATE (ADMIN)
async function inserirPaciente() {
    const nome = document.getElementById('newNome').value;
    const cpf = document.getElementById('newCpf').value;
    const diagnostico = document.getElementById('newDiagnostico').value;

    if (!nome || !cpf || !diagnostico) {
        updateStatus("Preencha todos os campos para cadastro.", true);
        return;
    }
    
    const pacienteDTO = { nome, cpf, diagnostico };

    try {
        const response = await fetch(`${API_BASE_URL}/cadastrarSeguro`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(pacienteDTO)
        });

        if (response.ok) {
            const novoPaciente = await response.json();
            updateStatus(`Paciente ${novoPaciente.nome} cadastrado com sucesso! ID: ${novoPaciente.id}`, false);
            document.getElementById('newNome').value = '';
            document.getElementById('newCpf').value = '';
            document.getElementById('newDiagnostico').value = '';

        } else if (response.status === 403) {
            updateStatus('ERRO 403: Acesso Negado. Apenas ADMIN pode cadastrar.', true);
        } else {
            const errorText = await response.text();
            updateStatus(`Falha no cadastro (Status: ${response.status}). Causa: ${errorText.substring(0, 100)}...`, true);
        }
    } catch (error) {
        updateStatus('Erro de comunicação ao tentar inserir paciente.', true);
    }
}

// 3. EDITAR/UPDATE (ADMIN)
async function editarPaciente() {
    const id = document.getElementById('editId').value;
    const nome = document.getElementById('editNome').value;
    const cpf = document.getElementById('editCpf').value;
    const diagnostico = document.getElementById('editDiagnostico').value;

    if (!id || !nome || !cpf || !diagnostico) {
        updateStatus("Preencha todos os campos para edição (incluindo o ID).", true);
        return;
    }

    const pacienteDTO = { nome, cpf, diagnostico }; 

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'PUT', // Requisição PUT para atualização
            headers: getAuthHeaders(),
            body: JSON.stringify(pacienteDTO)
        });

        if (response.ok) {
            const pacienteAtualizado = await response.json();
            updateStatus(`Paciente ID ${pacienteAtualizado.id} editado com sucesso!`, false);
        } else if (response.status === 403) {
            updateStatus('ERRO 403: Acesso Negado. Apenas ADMIN pode editar.', true);
        } else if (response.status === 404) {
            updateStatus(`ERRO 404: Paciente ID ${id} não encontrado para edição.`, true);
        } else {
            const errorText = await response.text();
            updateStatus(`Falha na edição (Status: ${response.status}). Causa: ${errorText.substring(0, 100)}...`, true);
        }
    } catch (error) {
        updateStatus('Erro de comunicação ao tentar editar paciente.', true);
    }
}

// 4. EXCLUIR/DELETE (ADMIN)
async function excluirPaciente() {
    const id = document.getElementById('deleteId').value;

    if (!id) {
        updateStatus("Informe o ID do paciente para exclusão.", true);
        return;
    }

    if (!confirm(`Tem certeza que deseja excluir o paciente com ID ${id}? Esta ação é irreversível.`)) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE', // Requisição DELETE para exclusão
            headers: getAuthHeaders()
        });

        if (response.ok || response.status === 204) { // 200 OK ou 204 No Content
            updateStatus(`Paciente ID ${id} excluído com sucesso!`, false);
            document.getElementById('deleteId').value = '';
        } else if (response.status === 403) {
            updateStatus('ERRO 403: Acesso Negado. Apenas ADMIN pode excluir.', true);
        } else if (response.status === 404) {
             updateStatus(`ERRO 404: Paciente ID ${id} não encontrado para exclusão.`, true);
        } else {
            updateStatus(`Falha na exclusão: ${response.status} ${response.statusText}`, true);
        }
    } catch (error) {
        updateStatus('Erro de comunicação ao tentar excluir paciente.', true);
    }
}


// --- CONTROLE DE TELA E DISPLAY ---

function displayPaciente(pacientes) {
    const dataBox = document.getElementById('paciente-data');
    if (!pacientes || pacientes.length === 0) {
        dataBox.innerHTML = '<h3>Nenhum paciente encontrado.</h3>';
        return;
    }
    
    dataBox.innerHTML = pacientes.map(paciente => `
        <div class="card">
            <h3>Prontuário #${paciente.id}</h3>
            <p><strong>Nome:</strong> ${paciente.nome}</p>
            <p><strong>CPF:</strong> <span class="dado-sensivel">${paciente.cpfCriptografado}</span></p>
            <p><strong>Diagnóstico:</strong> <span class="dado-sensivel">${paciente.diagnosticoCriptografado}</span></p>
            <p class="nota-seguranca">* Dados descriptografados pelo backend (em trânsito).</p>
        </div>
    `).join('');
}

function getFieldsets() {
    // Retorna todos os fieldsets para manipulação (C=0, R=1, U=2, D=3)
    return document.getElementById('data-section').getElementsByTagName('fieldset');
}

function showAdminSection(username) {
    const fieldsets = getFieldsets();
    
    // 1. Mostrar todos os fieldsets (ADMIN tem acesso total)
    for (let i = 0; i < fieldsets.length; i++) {
        fieldsets[i].style.display = 'block'; 
    }
    
    // 2. Controle de Exibição de Seções
    document.getElementById('login-section').style.display = 'none';
    document.getElementById('data-section').style.display = 'block';
    document.getElementById('logout-button').style.display = 'block';
    
    // 3. Status
    document.getElementById('auth-status').textContent = `Autenticado como ADMIN (${username}). Acesso total.`;
    
    // 4. Garante que o título seja de ADMIN
    document.getElementById('data-section').getElementsByTagName('h2')[0].innerHTML = '<span class="icon">🛡️</span> Painel de Pacientes - ADMIN';
}

function showConsultaSection(username) {
    const fieldsets = getFieldsets();

    // 1. Ocultar fieldsets de ADMIN (CREATE, UPDATE, DELETE)
    // Ordem: [0: Cadastrar], [1: Consultar], [2: Editar], [3: Excluir]
    
    fieldsets[0].style.display = 'none'; // Cadastrar
    fieldsets[1].style.display = 'block'; // Consultar (READ) - Manter visível
    fieldsets[2].style.display = 'none'; // Editar
    fieldsets[3].style.display = 'none'; // Excluir

    // 2. Controle de Exibição de Seções
    document.getElementById('login-section').style.display = 'none';
    document.getElementById('data-section').style.display = 'block';
    document.getElementById('logout-button').style.display = 'block';
    
    // 3. Status
    document.getElementById('auth-status').textContent = `Autenticado como Médico (${username}). Acesso apenas de consulta.`;
    
    // 4. Mudar o título da seção para refletir o acesso
    document.getElementById('data-section').getElementsByTagName('h2')[0].innerHTML = '<span class="icon">🛡️</span> Consulta de Prontuário';
}


function showLoginSection() {
    const fieldsets = getFieldsets();
    
    // 1. Garante que todos os fieldsets estejam visíveis para o próximo login de ADMIN
    for (let i = 0; i < fieldsets.length; i++) {
        fieldsets[i].style.display = 'block'; 
    }
    
    // 2. Controle de Exibição de Seções
    document.getElementById('login-section').style.display = 'block';
    document.getElementById('data-section').style.display = 'none';
    document.getElementById('logout-button').style.display = 'none';
    document.getElementById('login-message').textContent = '';
    
    // 3. Restaura o título da seção de dados para ADMIN
    document.getElementById('data-section').getElementsByTagName('h2')[0].innerHTML = '<span class="icon">🛡️</span> Painel de Pacientes - ADMIN';
}

// --- Inicialização e Listeners de Eventos ---

document.addEventListener('DOMContentLoaded', () => {
    // Liga os Listeners
    const loginButton = document.getElementById('login-button');
    if (loginButton) loginButton.addEventListener('click', handleLogin);
    
    const logoutBtn = document.getElementById('logout-button');
    if (logoutBtn) logoutBtn.addEventListener('click', logout);

    const searchButton = document.getElementById('search-button');
    if (searchButton) searchButton.addEventListener('click', buscarPaciente);
    
    const createButton = document.getElementById('create-button');
    if (createButton) createButton.addEventListener('click', inserirPaciente);

    const updateButton = document.getElementById('update-button');
    if (updateButton) updateButton.addEventListener('click', editarPaciente);

    const deleteButton = document.getElementById('delete-button');
    if (deleteButton) deleteButton.addEventListener('click', excluirPaciente);
    
    // 🚨 Lógica de Re-inicialização 🚨
    const token = getToken();
    const storedUsername = localStorage.getItem('currentUsername');
    
    if (token && storedUsername) {
        currentUsername = storedUsername; 
        if (currentUsername.toLowerCase() === 'admin') {
            showAdminSection(currentUsername); 
        } else if (currentUsername.toLowerCase() === 'medico') {
            showConsultaSection(currentUsername); 
        }
    } else {
        showLoginSection();
    }
});