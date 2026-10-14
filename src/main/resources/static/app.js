'use strict';
const $ = (id) => document.getElementById(id);
const roles = { Ricardo: 'ADMIN', Ana: 'USER', Luis: 'USER' };
let products = [];
let editingId = null;
let busy = false;
let generation = 0;
const isAdmin = () => roles[$('identity').value] === 'ADMIN';
const money = new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN' });

function message(text, error = false) {
  $('message').textContent = text;
  $('message').classList.toggle('error', error);
  $('message').hidden = !text;
}
async function api(path, method = 'GET', body) {
  const user = $('identity').value;
  const response = await fetch(path, {
    method,
    headers: { Usuario: user, Rol: roles[user], ...(body ? { 'Content-Type': 'application/json' } : {}) },
    ...(body ? { body: JSON.stringify(body) } : {})
  });
  const data = response.status === 204 ? null : await response.json();
  if (!response.ok) throw new Error(data.error || Object.entries(data).map(([field, detail]) => `${field}: ${detail}`).join(' · '));
  return data;
}
function cell(row, text) {
  const td = document.createElement('td');
  td.textContent = text;
  row.append(td);
  return td;
}
function setBusy(value) {
  busy = value;
  for (const id of ['save', 'refresh', 'refresh-audit', 'identity', 'cancel']) $(id).disabled = value;
  renderProducts();
}
function resetForm() {
  editingId = null;
  $('product-form').reset();
  $('form-title').textContent = 'Nuevo producto';
  $('save').textContent = 'Crear producto';
  $('cancel').hidden = true;
}
function renderProducts() {
  const query = $('search').value.trim().toLocaleLowerCase('es');
  const filtered = products.filter(p => p.nombre.toLocaleLowerCase('es').includes(query));
  $('products-body').replaceChildren();
  $('product-count').textContent = products.length;
  $('stock-count').textContent = products.reduce((total, p) => total + p.stock, 0);
  $('products-empty').hidden = filtered.length !== 0;
  $('products-empty').textContent = products.length ? 'No hay productos que coincidan con la búsqueda.' : 'Aún no hay productos. Crea el primero con el formulario.';
  for (const p of filtered) {
    const row = document.createElement('tr');
    cell(row, `#${p.id}`); cell(row, p.nombre); cell(row, money.format(p.precio)); cell(row, p.stock);
    const actions = cell(row, '');
    if (isAdmin()) {
      const group = document.createElement('div'); group.className = 'actions';
      const edit = document.createElement('button'); edit.textContent = 'Editar'; edit.disabled = busy;
      edit.setAttribute('aria-label', `Editar ${p.nombre}`);
      edit.onclick = () => {
        editingId = p.id;
        $('name').value = p.nombre; $('price').value = p.precio; $('stock').value = p.stock;
        $('form-title').textContent = `Editar producto #${p.id}`; $('save').textContent = 'Guardar cambios'; $('cancel').hidden = false;
        $('name').focus();
      };
      const remove = document.createElement('button'); remove.textContent = 'Eliminar'; remove.className = 'delete'; remove.disabled = busy;
      remove.setAttribute('aria-label', `Eliminar ${p.nombre}`);
      remove.onclick = () => deleteProduct(p);
      group.append(edit, remove); actions.append(group);
    } else actions.textContent = 'Solo ADMIN';
    $('products-body').append(row);
  }
}
async function loadProducts() {
  const version = generation;
  const data = await api('/api/productos');
  if (version !== generation) return;
  products = data; renderProducts();
}
async function deleteProduct(p) {
  if (busy || !confirm(`¿Eliminar "${p.nombre}" (ID ${p.id})?`)) return;
  setBusy(true);
  try {
    await api(`/api/productos/${p.id}`, 'DELETE');
    if (editingId === p.id) resetForm();
    await loadProducts(); message(`Producto #${p.id} eliminado. La operación quedó auditada.`);
  } catch (error) { message(error.message, true); }
  finally { setBusy(false); }
}
async function loadAudit() {
  const version = generation;
  const data = await api('/api/auditoria');
  if (version !== generation || !isAdmin()) return;
  $('audit-body').replaceChildren(); $('audit-empty').hidden = data.length !== 0;
  for (const log of data) {
    const row = document.createElement('tr');
    cell(row, new Date(log.fecha).toLocaleString('es-PE'));
    const badge = document.createElement('span'); badge.className = `badge${log.accion === 'ERROR' ? ' error' : ''}`; badge.textContent = log.accion;
    cell(row, '').append(badge); cell(row, log.usuario); cell(row, log.metodo); cell(row, log.detalle);
    $('audit-body').append(row);
  }
}
async function refresh(task) {
  if (busy) return;
  setBusy(true); message('');
  try { await task(); } catch (error) { message(`No se pudo cargar: ${error.message}`, true); }
  finally { setBusy(false); }
}
function showPanel(audit) {
  $('products-panel').hidden = audit; $('audit-panel').hidden = !audit;
  $('page-title').textContent = audit ? 'Auditoría' : 'Productos';
  for (const [id, active] of [['products-tab', !audit], ['audit-tab', audit]]) {
    $(id).classList.toggle('active', active); $(id).setAttribute('aria-pressed', String(active));
  }
}
$('product-form').onsubmit = async (event) => {
  event.preventDefault(); if (busy) return;
  const name = $('name').value.trim();
  if (!name) { message('El nombre no puede estar vacío.', true); $('name').focus(); return; }
  const body = { nombre: name, precio: Number($('price').value), stock: Number($('stock').value) };
  const updating = editingId !== null; setBusy(true);
  try {
    await api(updating ? `/api/productos/${editingId}` : '/api/productos', updating ? 'PUT' : 'POST', body);
    resetForm(); await loadProducts(); message(updating ? 'Producto actualizado y auditado.' : 'Producto creado y auditado.');
  } catch (error) { message(error.message, true); }
  finally { setBusy(false); }
};
$('cancel').onclick = resetForm;
$('search').oninput = renderProducts;
$('refresh').onclick = () => refresh(loadProducts);
$('refresh-audit').onclick = () => refresh(loadAudit);
$('products-tab').onclick = () => showPanel(false);
$('audit-tab').onclick = () => { if (!isAdmin()) return; showPanel(true); refresh(loadAudit); };
$('identity').onchange = () => {
  generation++; resetForm(); message(''); showPanel(false);
  products = []; $('audit-body').replaceChildren(); renderProducts();
  $('audit-tab').disabled = !isAdmin(); $('permissions').textContent = isAdmin() ? 'CRUD completo' : 'Crear y listar';
  refresh(loadProducts);
};
refresh(loadProducts);
