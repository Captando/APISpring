const API_BASE = '/products';
let editingId = null;
let page = 0;
const pageSize = 20;

const productsBody = document.getElementById('productsBody');
const feedback = document.getElementById('feedback');

window.addEventListener('DOMContentLoaded', () => {
  loadProducts(0);
});

function setFeedback(message, ok = true) {
  feedback.className = `feedback ${ok ? 'ok' : 'err'}`;
  feedback.textContent = message;
}

function escapeHtml(value) {
  if (value === null || value === undefined) return '';
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;');
}

function toQuery(filters) {
  const q = new URLSearchParams();
  if (filters.name) q.set('name', filters.name);
  if (filters.category) q.set('category', filters.category);
  if (filters.minPrice !== '') q.set('minPrice', filters.minPrice);
  if (filters.maxPrice !== '') q.set('maxPrice', filters.maxPrice);
  if (filters.active !== '') q.set('active', filters.active);
  q.set('page', String(filters.page));
  q.set('size', String(filters.size));
  q.set('sort', 'id,asc');
  return q.toString();
}

async function loadProducts(nextPage = 0) {
  const payload = {
    name: document.getElementById('filterName').value.trim(),
    category: document.getElementById('filterCategory').value.trim(),
    minPrice: document.getElementById('filterMinPrice').value,
    maxPrice: document.getElementById('filterMaxPrice').value,
    active: document.getElementById('filterActive').value,
    page: nextPage,
    size: pageSize
  };

  page = nextPage;
  const query = toQuery(payload);

  const resp = await fetch(`${API_BASE}?${query}`);
  if (!resp.ok) {
    setFeedback(`Erro ao carregar produtos (${resp.status})`, false);
    return;
  }

  const data = await resp.json();
  renderProducts(data.content || []);
  renderPager(data);
}

function renderPager(pageData) {
  const total = pageData.totalPages || 1;
  const controls = document.createElement('div');
  controls.style.display = 'flex';
  controls.style.gap = '8px';
  controls.style.marginTop = '10px';

  controls.innerHTML = `
    <button onclick="loadProducts(${Math.max(0, page - 1)})" ${page <= 0 ? 'disabled' : ''}>Anterior</button>
    <span> Página ${page + 1} de ${total} </span>
    <button onclick="loadProducts(${Math.min(total - 1, page + 1)})" ${page >= total - 1 ? 'disabled' : ''}>Próxima</button>
  `;

  const container = productsBody.closest('.card');
  const old = container.querySelector('.pager');
  if (old) old.remove();
  controls.className = 'pager';
  container.appendChild(controls);
}

function renderProducts(items) {
  productsBody.innerHTML = '';

  if (!items.length) {
    productsBody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#6b7280;">Nenhum produto encontrado</td></tr>';
    return;
  }

  items.forEach((item) => {
    const tr = document.createElement('tr');

    tr.innerHTML = `
      <td>${item.id}</td>
      <td>${escapeHtml(item.name)}</td>
      <td>${escapeHtml(item.category || '-')}</td>
      <td>R$ ${Number(item.price || 0).toFixed(2)}</td>
      <td>${item.stockQuantity}</td>
      <td><span class="badge">${item.active ? 'Ativo' : 'Inativo'}</span></td>
      <td>
        <button class="inline-btn" onclick="editProduct(${item.id})">Editar</button>
        <button class="inline-btn" onclick="deleteProduct(${item.id})">Excluir</button>
      </td>
    `;

    productsBody.appendChild(tr);
  });
}

function readProductForm() {
  return {
    name: document.getElementById('name').value.trim(),
    description: document.getElementById('description').value.trim(),
    price: Number(document.getElementById('price').value),
    category: document.getElementById('category').value.trim() || null,
    stockQuantity: Number(document.getElementById('stockQuantity').value),
    active: document.getElementById('active').checked
  };
}

function writeProductForm(item) {
  editingId = item.id;
  document.getElementById('productId').value = item.id;
  document.getElementById('name').value = item.name || '';
  document.getElementById('description').value = item.description || '';
  document.getElementById('price').value = item.price ?? '';
  document.getElementById('category').value = item.category || '';
  document.getElementById('stockQuantity').value = item.stockQuantity ?? '';
  document.getElementById('active').checked = !!item.active;
  document.getElementById('formTitle').textContent = `Editar produto #${item.id}`;
}

async function editProduct(id) {
  const resp = await fetch(`${API_BASE}/${id}`);
  if (!resp.ok) {
    setFeedback('Erro ao carregar produto para edição', false);
    return;
  }
  const item = await resp.json();
  writeProductForm(item);
  setFeedback('Produto carregado para edição.', true);
}

async function saveProduct(event) {
  event.preventDefault();
  const payload = readProductForm();
  const method = editingId ? 'PUT' : 'POST';
  const url = editingId ? `${API_BASE}/${editingId}` : API_BASE;

  const resp = await fetch(url, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (!resp.ok) {
    const body = await resp.json().catch(() => ({}));
    const msg = body.message || `Erro ${resp.status} ao salvar`;
    setFeedback(msg, false);
    return;
  }

  const text = editingId ? 'Produto atualizado com sucesso.' : 'Produto criado com sucesso.';
  setFeedback(text, true);
  clearForm();
  loadProducts(page);
}

async function deleteProduct(id) {
  if (!confirm(`Confirma a exclusão do produto ${id}?`)) return;
  const resp = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
  if (!resp.ok && resp.status !== 204) {
    const body = await resp.json().catch(() => ({}));
    setFeedback(body.message || `Erro ${resp.status} ao excluir`, false);
    return;
  }
  setFeedback(`Produto ${id} removido.`, true);
  loadProducts(page);
}

async function stockAdjust(delta) {
  if (!editingId) {
    setFeedback('Selecione um produto para ajustar o estoque.', false);
    return;
  }

  const resp = await fetch(`${API_BASE}/${editingId}/stock`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ delta })
  });

  if (!resp.ok) {
    const body = await resp.json().catch(() => ({}));
    setFeedback(body.message || `Erro ${resp.status} ao ajustar estoque`, false);
    return;
  }

  const updated = await resp.json();
  writeProductForm(updated);
  setFeedback('Estoque ajustado.', true);
  loadProducts(page);
}

function clearForm() {
  editingId = null;
  document.getElementById('productForm').reset();
  document.getElementById('productId').value = '';
  document.getElementById('formTitle').textContent = 'Novo produto';
}

function resetFilters() {
  document.getElementById('filterName').value = '';
  document.getElementById('filterCategory').value = '';
  document.getElementById('filterMinPrice').value = '';
  document.getElementById('filterMaxPrice').value = '';
  document.getElementById('filterActive').value = '';
  loadProducts(0);
}
