
// Referencias del Modal
const modal = document.getElementById('profileModal');
const openBtn = document.getElementById('openProfileBtn');
const closeBtn = document.getElementById('closeProfileBtn');

openBtn.addEventListener('click', (event) => {
    event.preventDefault(); // Evita comportamientos raros del botón
    console.log("¡Clic detectado en el botón!");
    console.log("Modal encontrado:", modal);
    modal.classList.add('active');
});
closeBtn.addEventListener('click', () => modal.classList.remove('active'));



// En la lógica del botón de cerrar (la X o el clic fuera):
function closeModal() {
    const modal = document.getElementById('profileModal');
    modal.classList.remove('active');

    // Si hubo cambios, recargamos al salir
    if (perfilEditado) {
        window.location.reload();
    }
}
document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('profileModal');
    const openBtn = document.getElementById('openProfileBtn');
    const closeBtn = document.getElementById('closeProfileBtn');

    // 1. Evento para abrir (Aislado)
    if (openBtn && modal) {
        openBtn.addEventListener('click', (e) => {
            e.preventDefault();
            modal.classList.add('active');
        });
    }



    // 2. Evento para cerrar con la X (Aislado)
    if (closeBtn && modal) {
        closeBtn.addEventListener('click', (e) => {
            e.preventDefault();
            closeModal(); // Lógica de cierre común
        });
    }



    

    // 4. Listas dinámicas
    const addressList = document.getElementById('addressList');
    if (addressList) {
        addressList.addEventListener('change', function () {
            const input = document.getElementById('addressInput');
            if (input) input.value = this.options[this.selectedIndex].text;
        });
    }

    const phoneList = document.getElementById('phoneList');
    if (phoneList) {
        phoneList.addEventListener('change', function () {
            const input = document.getElementById('phoneInput');
            if (input) input.value = this.options[this.selectedIndex].text;
        });
    }

    // 5. Lógica de Edición de Contraseña
    const passInput = document.getElementById('userPass');
    const editPassBtn = document.getElementById('editPassBtn');
    const cancelPassBtn = document.getElementById('cancelPassBtn');

    if (passInput && editPassBtn && cancelPassBtn) {

        // Al hacer clic en el lápiz
        editPassBtn.addEventListener('click', (e) => {
            e.preventDefault();
            passInput.removeAttribute('readonly'); // Desbloquea el input
            passInput.value = '';                  // Limpia el input
            passInput.focus();                     // Coloca el cursor adentro

            // Intercambia botones
            editPassBtn.classList.add('hidden');
            cancelPassBtn.classList.remove('hidden');
        });

        // Al hacer clic en la flecha de retroceso
        cancelPassBtn.addEventListener('click', (e) => {
            e.preventDefault();
            passInput.setAttribute('readonly', true); // Vuelve a bloquear
            passInput.value = '********';             // Restaura los asteriscos

            // Intercambia botones
            cancelPassBtn.classList.add('hidden');
            editPassBtn.classList.remove('hidden');
        });
    }
});
// Sincronizar select con el input al hacer clic
document.getElementById('addressList').addEventListener('change', function () {
    document.getElementById('addressInput').value = this.options[this.selectedIndex].text;
});
document.getElementById('phoneList').addEventListener('change', function () {
    document.getElementById('phoneInput').value = this.options[this.selectedIndex].text;
});

// Lógica dinámica para listas
function addToList(type) {
    const input = document.getElementById(`${type}Input`);
    const list = document.getElementById(`${type}List`);
    if (input.value.trim() !== "") {
        const newOption = new Option(input.value.trim(), input.value.trim());
        list.add(newOption);
        input.value = ""; // Limpiar input
    }
}

function editInList(type) {
    const input = document.getElementById(`${type}Input`);
    const list = document.getElementById(`${type}List`);
    if (list.selectedIndex >= 0 && input.value.trim() !== "") {
        list.options[list.selectedIndex].text = input.value.trim();
        list.options[list.selectedIndex].value = input.value.trim();
    } else {
        alert("Selecciona un elemento de la lista para renombrar.");
    }
}

function removeFromList(type) {
    const list = document.getElementById(`${type}List`);
    if (list.selectedIndex >= 0) {
        list.remove(list.selectedIndex);
        document.getElementById(`${type}Input`).value = "";
    }
}

// ── INTEGRACIÓN REAL CON BACKEND ──
// Función Helper para obtener los headers necesarios para Spring Security
function getAuthHeaders() {
    const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    let headers = {
        'Content-Type': 'application/json'
    };
    headers[csrfHeader] = csrfToken;
    return headers;
}
let perfilEditado = false; // Bandera de control
async function saveGeneralData() {
    const name = document.getElementById('userName').value;
    const pass = document.getElementById('userPass').value;

    try {
        const response = await fetch('/api/perfil/general', {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify({ nombre: name, password: pass })
        });

        if (response.ok) {
            perfilEditado = true; // Marcamos que hubo cambios
            alert("Datos generales guardados correctamente.");
        } else {
            alert("Error al guardar los datos.");
        }
    } catch (error) {
        console.error("Error en la petición:", error);
    }
}

async function saveAddresses() {
    const list = document.getElementById('addressList');
    const addresses = Array.from(list.options).map(opt => opt.value);

    try {
        const response = await fetch('/api/perfil/direcciones', {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(addresses)
        });


        // En tu función de guardado exitoso:
        if (response.ok) {
            perfilEditado = true; // Marcamos que hubo cambios
            alert("Direcciones guardadas.");
        }

        else alert("Error al guardar direcciones.");
    } catch (error) {
        console.error(error);
    }
}

async function savePhones() {
    const list = document.getElementById('phoneList');
    const phones = Array.from(list.options).map(opt => opt.value);

    try {
        const response = await fetch('/api/perfil/telefonos', {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(phones)
        });

        // En tu función de guardado exitoso:
        if (response.ok) {
            perfilEditado = true; // Marcamos que hubo cambios
            alert("Teléfonos guardados.");
        }


        else alert("Error al guardar teléfonos.");
    } catch (error) {
        console.error(error);
    }
}
