// ── Session stored in sessionStorage so each browser tab is independent ────
// This means two tabs can be logged in as two different users simultaneously
const TOKEN_KEY = 'ss_tab_token';
const USER_KEY  = 'ss_tab_user';

function saveSession(data) {
    sessionStorage.setItem(TOKEN_KEY, data.token);
    const { token, ...user } = data;
    sessionStorage.setItem(USER_KEY, JSON.stringify(user));
}

function getToken()    { return sessionStorage.getItem(TOKEN_KEY); }
function getUser()     { const u = sessionStorage.getItem(USER_KEY); return u ? JSON.parse(u) : null; }
function updateUser(u) { sessionStorage.setItem(USER_KEY, JSON.stringify(u)); }

function logout() { doLogout(); }
function doLogout() {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
    window.location.href = 'login.html';
}

function requireAuth() {
    if (!getToken()) window.location.href = 'login.html';
}

// ── Authenticated fetch ───────────────────────────────────────────────────
async function api(method, path, body = null) {
    const headers = { 'Authorization': 'Bearer ' + getToken() };
    if (body) headers['Content-Type'] = 'application/json';
    const opts = { method, headers };
    if (body) opts.body = JSON.stringify(body);
    const res  = await fetch(path, opts);
    const json = await res.json();
    if (!res.ok || !json.success) throw new Error(json.message || 'Request failed');
    return json.data;
}

const GET  = (path)       => api('GET',  path);
const POST = (path, body) => api('POST', path, body);
const PUT  = (path, body) => api('PUT',  path, body);

// ── SSE — token as query param (EventSource can't set headers) ────────────
function connectSSE(onSwapRequest, onSwapUpdate, onChatMessage) {
    const token = getToken();
    if (!token) return null;
    const es = new EventSource('/notifications/subscribe?token=' + encodeURIComponent(token));
    es.addEventListener('swap_request', e => onSwapRequest?.(JSON.parse(e.data)));
    es.addEventListener('swap_update',  e => onSwapUpdate?.(JSON.parse(e.data)));
    es.addEventListener('chat_message', e => onChatMessage?.(JSON.parse(e.data)));
    es.onerror = () => es.close();
    return es;
}

// ── Toast ─────────────────────────────────────────────────────────────────
let _toastTimer;
function toast(msg, type = 'success') {
    let el = document.getElementById('_toast');
    if (!el) {
        el = document.createElement('div'); el.id = '_toast';
        el.style.cssText = [
            'position:fixed','bottom:24px','right:24px','padding:13px 20px',
            'border-radius:12px','font-size:14px','font-weight:600','color:#fff',
            'box-shadow:0 8px 24px rgba(0,0,0,.18)','z-index:9999',
            'transform:translateY(80px)','opacity:0',
            'transition:transform .3s ease,opacity .3s ease',
            'max-width:320px',"font-family:'Segoe UI',system-ui,sans-serif"
        ].join(';');
        document.body.appendChild(el);
    }
    el.textContent = msg;
    el.style.background = type==='error'?'#ef4444':type==='warn'?'#f59e0b':'#10b981';
    el.style.transform = 'translateY(0)'; el.style.opacity = '1';
    clearTimeout(_toastTimer);
    _toastTimer = setTimeout(() => { el.style.transform='translateY(80px)'; el.style.opacity='0'; }, 3500);
}

// ── Helpers ───────────────────────────────────────────────────────────────
function initials(name) {
    if (!name) return '?';
    return name.trim().split(/\s+/).map(w=>w[0]).join('').toUpperCase().slice(0,2);
}
function escHtml(s) {
    return String(s??'').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}
function timeAgo(iso) {
    if (!iso) return '';
    const diff = Date.now() - new Date(iso).getTime();
    const m = Math.floor(diff/60000);
    if (m < 1)  return 'just now';
    if (m < 60) return m+'m ago';
    const h = Math.floor(m/60);
    if (h < 24) return h+'h ago';
    return Math.floor(h/24)+'d ago';
}
function stringToColor(str) {
    const p=['#7c3aed','#6d28d9','#4f46e5','#2563eb','#0891b2','#059669','#d97706','#dc2626'];
    let hash=0;
    for(let i=0;i<(str||'').length;i++) hash=str.charCodeAt(i)+((hash<<5)-hash);
    return p[Math.abs(hash)%p.length];
}
function avatarHtml(user, size=38) {
    const s=size+'px';
    if (user?.avatarBase64) {
        return `<img src="${escHtml(user.avatarBase64)}"
                     style="width:${s};height:${s};border-radius:50%;object-fit:cover;flex-shrink:0;"/>`;
    }
    const bg=stringToColor(user?.name||'?'), fs=Math.floor(size*.38)+'px';
    return `<div style="width:${s};height:${s};border-radius:50%;background:${bg};
                display:flex;align-items:center;justify-content:center;color:#fff;
                font-weight:700;font-size:${fs};flex-shrink:0;">${initials(user?.name)}</div>`;
}
