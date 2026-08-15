/**
 * CUST Enterprise Web Platform - Common Application Logic
 * Implements WCAG 2.1 AA Accessibility & NIST CSF User Authentication Context
 */

// State Management
const AppState = {
  theme: localStorage.getItem('cust_theme') || 'light',
  contrast: localStorage.getItem('cust_contrast') || 'normal',
  fontSize: localStorage.getItem('cust_fontsize') || '100%',
  currentUser: JSON.parse(localStorage.getItem('cust_user') || 'null'),
  token: localStorage.getItem('cust_token') || null
};

// Initialize on DOM Ready
document.addEventListener('DOMContentLoaded', () => {
  initAccessibility();
  initAuthUI();
  initMobileMenu();
});

// Accessibility Toolbar Handling
function initAccessibility() {
  applyTheme(AppState.theme);
  applyContrast(AppState.contrast);
  applyFontSize(AppState.fontSize);

  // Bind buttons
  const themeToggle = document.getElementById('theme-toggle-btn');
  if (themeToggle) {
    themeToggle.addEventListener('click', () => {
      AppState.theme = AppState.theme === 'light' ? 'dark' : 'light';
      localStorage.setItem('cust_theme', AppState.theme);
      applyTheme(AppState.theme);
      showToast(`已切換為 ${AppState.theme === 'dark' ? '深色模式' : '淺色模式'}`, 'info');
    });
  }

  const contrastToggle = document.getElementById('contrast-toggle-btn');
  if (contrastToggle) {
    contrastToggle.addEventListener('click', () => {
      AppState.contrast = AppState.contrast === 'normal' ? 'high' : 'normal';
      localStorage.setItem('cust_contrast', AppState.contrast);
      applyContrast(AppState.contrast);
      showToast(`已切換高對比度模式: ${AppState.contrast === 'high' ? '開啟' : '關閉'}`, 'info');
    });
  }

  const fontIncrease = document.getElementById('font-increase-btn');
  if (fontIncrease) {
    fontIncrease.addEventListener('click', () => {
      let current = parseInt(AppState.fontSize);
      if (current < 130) current += 10;
      AppState.fontSize = current + '%';
      localStorage.setItem('cust_fontsize', AppState.fontSize);
      applyFontSize(AppState.fontSize);
    });
  }

  const fontDecrease = document.getElementById('font-decrease-btn');
  if (fontDecrease) {
    fontDecrease.addEventListener('click', () => {
      let current = parseInt(AppState.fontSize);
      if (current > 90) current -= 10;
      AppState.fontSize = current + '%';
      localStorage.setItem('cust_fontsize', AppState.fontSize);
      applyFontSize(AppState.fontSize);
    });
  }
}

function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme);
}

function applyContrast(contrast) {
  document.documentElement.setAttribute('data-contrast', contrast);
}

function applyFontSize(size) {
  document.documentElement.style.setProperty('--font-scale', size === '100%' ? '1rem' : (parseInt(size)/100) + 'rem');
}

// Authentication & Header UI
function initAuthUI() {
  const authContainer = document.getElementById('header-auth-section');
  if (!authContainer) return;

  if (AppState.currentUser && AppState.token) {
    authContainer.innerHTML = `
      <div style="display:flex; align-items:center; gap:0.75rem;">
        <span style="font-size:0.875rem; color:#cbd5e1;">👤 ${AppState.currentUser.fullName || AppState.currentUser.username} (${AppState.currentUser.role.replace('ROLE_', '')})</span>
        <button class="btn-header btn-outline-light" onclick="handleLogout()" style="padding:0.35rem 0.75rem; font-size:0.8125rem;">登出</button>
      </div>
    `;
  } else {
    authContainer.innerHTML = `
      <button class="btn-header btn-gold" onclick="openLoginModal()">校園系統登入</button>
    `;
  }
}

function openLoginModal() {
  const modal = document.getElementById('login-modal');
  if (modal) {
    modal.classList.add('active');
  }
}

function closeLoginModal() {
  const modal = document.getElementById('login-modal');
  if (modal) {
    modal.classList.remove('active');
  }
}

async function handleLogin(event) {
  event.preventDefault();
  const username = document.getElementById('login-username').value.trim();
  const password = document.getElementById('login-password').value;

  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    if (res.ok) {
      const data = await res.json();
      AppState.token = data.token;
      AppState.currentUser = {
        username: data.username,
        fullName: data.fullName,
        email: data.email,
        role: data.role,
        department: data.department
      };
      localStorage.setItem('cust_token', data.token);
      localStorage.setItem('cust_user', JSON.stringify(AppState.currentUser));
      closeLoginModal();
      initAuthUI();
      showToast(`登入成功！歡迎 ${data.fullName}`, 'success');
      setTimeout(() => location.reload(), 800);
    } else {
      const err = await res.json();
      showToast(err.error || '帳號或密碼錯誤', 'error');
    }
  } catch (error) {
    console.error('Login error:', error);
    showToast('連線至認證伺服器失敗', 'error');
  }
}

function handleLogout() {
  localStorage.removeItem('cust_token');
  localStorage.removeItem('cust_user');
  AppState.token = null;
  AppState.currentUser = null;
  showToast('已安全登出', 'info');
  setTimeout(() => location.reload(), 500);
}

// Mobile Menu
function initMobileMenu() {
  const toggleBtn = document.querySelector('.mobile-menu-btn');
  const navLinks = document.querySelector('.nav-links');
  if (toggleBtn && navLinks) {
    toggleBtn.addEventListener('click', () => {
      navLinks.classList.toggle('open');
    });
  }
}

// Toast Notifications
function showToast(message, type = 'info') {
  let container = document.querySelector('.toast-container');
  if (!container) {
    container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  
  let icon = 'ℹ️';
  if (type === 'success') icon = '✅';
  if (type === 'error') icon = '❌';
  if (type === 'warning') icon = '⚠️';

  toast.innerHTML = `
    <span style="font-size:1.25rem;">${icon}</span>
    <div style="font-size:0.875rem; font-weight:500;">${message}</div>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

// Global API Helper with JWT Authorization
async function apiRequest(url, options = {}) {
  const headers = options.headers || {};
  if (AppState.token) {
    headers['Authorization'] = `Bearer ${AppState.token}`;
  }
  if (!headers['Content-Type'] && !(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
  }
  options.headers = headers;
  return fetch(url, options);
}
