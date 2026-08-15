/**
 * CUST News & Announcements CMS Logic
 */

let currentCategoryId = 0;
let currentSearchQuery = '';
let currentPage = 0;

document.addEventListener('DOMContentLoaded', () => {
  loadCategories();
  loadNews();

  // Search input handler
  const searchInput = document.getElementById('news-search-input');
  if (searchInput) {
    let timeout = null;
    searchInput.addEventListener('input', (e) => {
      clearTimeout(timeout);
      timeout = setTimeout(() => {
        currentSearchQuery = e.target.value;
        currentPage = 0;
        loadNews();
      }, 400);
    });
  }
});

async function loadCategories() {
  const container = document.getElementById('category-tabs-container');
  if (!container) return;

  try {
    const res = await fetch('/api/categories');
    if (res.ok) {
      const categories = await res.json();
      let html = `<button class="cat-tab active" onclick="selectCategory(0, this)">全部公告 (All)</button>`;
      categories.forEach(cat => {
        html += `<button class="cat-tab" onclick="selectCategory(${cat.id}, this)">${cat.nameZh}</button>`;
      });
      container.innerHTML = html;

      // Also fill category select in modal if present
      const modalSelect = document.getElementById('create-news-category');
      if (modalSelect) {
        modalSelect.innerHTML = categories.map(c => `<option value="${c.id}">${c.nameZh} (${c.nameEn})</option>`).join('');
      }
    }
  } catch (err) {
    console.error('Error loading categories:', err);
  }
}

function selectCategory(catId, btnElement) {
  currentCategoryId = catId;
  currentPage = 0;
  document.querySelectorAll('.cat-tab').forEach(b => b.classList.remove('active'));
  if (btnElement) btnElement.classList.add('active');
  loadNews();
}

async function loadNews() {
  const container = document.getElementById('news-list-container');
  if (!container) return;

  container.innerHTML = `<div style="text-align:center; padding:3rem; color:var(--text-muted);">正在載入公告資訊...</div>`;

  let url = `/api/news?page=${currentPage}&size=10`;
  if (currentCategoryId > 0) url += `&categoryId=${currentCategoryId}`;
  if (currentSearchQuery) url += `&search=${encodeURIComponent(currentSearchQuery)}`;

  try {
    const res = await fetch(url);
    if (res.ok) {
      const data = await res.json();
      renderNewsList(data.content || data);
      renderPagination(data.totalPages || 1, data.number || 0);
    }
  } catch (err) {
    container.innerHTML = `<div style="text-align:center; padding:2rem; color:var(--danger);">載入失敗，請檢查網路連線。</div>`;
  }
}

function renderNewsList(newsItems) {
  const container = document.getElementById('news-list-container');
  if (!container) return;

  if (!newsItems || newsItems.length === 0) {
    container.innerHTML = `<div style="text-align:center; padding:4rem; color:var(--text-muted); background:var(--bg-surface); border-radius:var(--radius-md); border:1px solid var(--border-color);">查無相關公告消息</div>`;
    return;
  }

  container.innerHTML = newsItems.map(item => `
    <article class="news-card ${item.pinned ? 'pinned' : ''}" onclick="viewNewsDetail(${item.id})">
      <div class="news-meta-row">
        ${item.pinned ? '<span class="pinned-badge">📌 置頂公告</span>' : ''}
        <span class="badge" style="background-color: ${item.category ? item.category.badgeColor + '20' : '#e2e8f0'}; color: ${item.category ? item.category.badgeColor : '#475569'};">
          ${item.category ? item.category.nameZh : '一般公告'}
        </span>
        <span>發布單位：${item.authorDepartment || '中華科技大學'}</span>
        <span>•</span>
        <span>${formatDate(item.publishedAt)}</span>
      </div>
      <h2 class="news-title">${item.title}</h2>
      <p class="news-summary">${item.summary || item.content.substring(0, 140) + '...'}</p>
      <div class="news-footer-row">
        <span>👁️ 瀏覽人次: ${item.viewCount || 0}</span>
        ${item.attachmentName ? `<span style="color:var(--primary-500); font-weight:600;">📎 附件: ${item.attachmentName}</span>` : '<span></span>'}
      </div>
    </article>
  `).join('');
}

function renderPagination(totalPages, current) {
  const container = document.getElementById('news-pagination');
  if (!container || totalPages <= 1) {
    if (container) container.innerHTML = '';
    return;
  }

  let html = '';
  for (let i = 0; i < totalPages; i++) {
    html += `<button class="page-btn ${i === current ? 'active' : ''}" onclick="changePage(${i})">${i + 1}</button>`;
  }
  container.innerHTML = html;
}

function changePage(page) {
  currentPage = page;
  loadNews();
  window.scrollTo({ top: 350, behavior: 'smooth' });
}

async function viewNewsDetail(newsId) {
  try {
    const res = await fetch(`/api/news/${newsId}`);
    if (res.ok) {
      const news = await res.json();
      const modal = document.getElementById('news-detail-modal');
      if (modal) {
        document.getElementById('modal-news-title').innerText = news.title;
        document.getElementById('modal-news-dept').innerText = `${news.authorDepartment || '中華科技大學'} (${news.authorName || '管理員'})`;
        document.getElementById('modal-news-date').innerText = formatDate(news.publishedAt);
        document.getElementById('modal-news-views').innerText = news.viewCount;
        document.getElementById('modal-news-content').innerText = news.content;
        
        const attachBox = document.getElementById('modal-news-attachment');
        if (attachBox) {
          if (news.attachmentName) {
            attachBox.style.display = 'flex';
            document.getElementById('modal-attachment-name').innerText = news.attachmentName;
          } else {
            attachBox.style.display = 'none';
          }
        }

        modal.classList.add('active');
      }
    }
  } catch (err) {
    console.error('Error fetching detail:', err);
  }
}

function closeNewsDetailModal() {
  const modal = document.getElementById('news-detail-modal');
  if (modal) modal.classList.remove('active');
}

function openCreateNewsModal() {
  if (!AppState.currentUser || (AppState.currentUser.role !== 'ROLE_ADMIN' && AppState.currentUser.role !== 'ROLE_SUPER_ADMIN' && AppState.currentUser.role !== 'ROLE_FACULTY')) {
    showToast('請先以教職員或管理員身分登入', 'warning');
    openLoginModal();
    return;
  }
  const modal = document.getElementById('create-news-modal');
  if (modal) modal.classList.add('active');
}

function closeCreateNewsModal() {
  const modal = document.getElementById('create-news-modal');
  if (modal) modal.classList.remove('active');
}

async function handleCreateNews(e) {
  e.preventDefault();
  const title = document.getElementById('create-news-title').value.trim();
  const categoryId = parseInt(document.getElementById('create-news-category').value);
  const content = document.getElementById('create-news-content').value.trim();
  const summary = document.getElementById('create-news-summary').value.trim();
  const pinned = document.getElementById('create-news-pinned').checked;
  const attachmentName = document.getElementById('create-news-attach-name').value.trim();

  try {
    const res = await apiRequest('/api/news', {
      method: 'POST',
      body: JSON.stringify({
        title,
        categoryId,
        content,
        summary,
        pinned,
        authorDepartment: AppState.currentUser.department || '中華科技大學 秘書室',
        authorName: AppState.currentUser.fullName || AppState.currentUser.username,
        attachmentName: attachmentName || null,
        attachmentUrl: attachmentName ? '/assets/docs/sample.pdf' : null
      })
    });

    if (res.ok) {
      showToast('公告發布成功！', 'success');
      closeCreateNewsModal();
      loadNews();
    } else {
      showToast('發布失敗，請確認權限', 'error');
    }
  } catch (err) {
    showToast('網路連線異常', 'error');
  }
}

function formatDate(isoStr) {
  if (!isoStr) return '';
  const d = new Date(isoStr);
  return `${d.getFullYear()}/${(d.getMonth()+1).toString().padStart(2, '0')}/${d.getDate().toString().padStart(2, '0')}`;
}
