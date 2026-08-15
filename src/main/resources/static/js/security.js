/**
 * CUST NIST CSF 2.0 Security Dashboard & Audit Console
 */

document.addEventListener('DOMContentLoaded', () => {
  loadSecurityMetrics();
  loadAuditLogs();
  loadIncidents();
});

async function loadSecurityMetrics() {
  try {
    const res = await apiRequest('/api/security/metrics');
    if (res.ok) {
      const data = await res.json();
      document.getElementById('metric-total-logs').innerText = data.totalLogs || 0;
      document.getElementById('metric-auth-success').innerText = data.loginSuccessCount || 0;
      document.getElementById('metric-auth-fail').innerText = data.loginFailureCount || 0;
      document.getElementById('metric-incidents').innerText = data.activeIncidentsCount || 0;
    }
  } catch (e) {
    console.error('Metrics fetch error:', e);
  }
}

async function loadAuditLogs() {
  const container = document.getElementById('audit-logs-body');
  if (!container) return;

  try {
    const res = await apiRequest('/api/security/audit-logs?page=0&size=15');
    if (res.ok) {
      const data = await res.json();
      const logs = data.content || data;
      container.innerHTML = logs.map(log => `
        <tr>
          <td class="log-time">${new Date(log.timestamp).toLocaleString()}</td>
          <td>
            <span class="badge ${getNistBadgeClass(log.nistCategory)}">${log.nistCategory || 'GOVERN'}</span>
          </td>
          <td><strong>${log.eventType}</strong></td>
          <td>${log.username || 'ANONYMOUS'}</td>
          <td><code>${log.ipAddress || '127.0.0.1'}</code></td>
          <td><span class="badge ${getSeverityBadge(log.severity)}">${log.severity}</span></td>
          <td style="font-size:0.8125rem; color:var(--text-muted);">${log.details || ''}</td>
        </tr>
      `).join('');
    }
  } catch (e) {
    container.innerHTML = `<tr><td colspan="7" style="text-align:center; color:var(--text-muted);">尚未取得稽核日誌</td></tr>`;
  }
}

async function loadIncidents() {
  const container = document.getElementById('incidents-list-container');
  if (!container) return;

  try {
    const res = await apiRequest('/api/security/incidents');
    if (res.ok) {
      const incidents = await res.json();
      if (!incidents || incidents.length === 0) {
        container.innerHTML = `<div style="padding:1.5rem; text-align:center; color:var(--text-muted);">目前無任何未處理之資安威脅事件。系統安全運作中。</div>`;
        return;
      }
      container.innerHTML = incidents.map(inc => `
        <div class="card" style="margin-bottom:1rem; border-left:4px solid ${inc.severity === 'CRITICAL' ? 'var(--danger)' : 'var(--warning)'};">
          <div class="card-body" style="padding:1.25rem;">
            <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:0.5rem;">
              <div>
                <span class="badge badge-red">${inc.incidentType}</span>
                <strong style="font-size:1.1rem; margin-left:0.5rem;">${inc.title}</strong>
              </div>
              <span class="badge ${inc.status === 'CLOSED' ? 'badge-green' : 'badge-amber'}">${inc.status}</span>
            </div>
            <p style="font-size:0.875rem; color:var(--text-muted); margin-bottom:0.75rem;">${inc.description}</p>
            <div style="display:flex; justify-content:space-between; align-items:center; font-size:0.8125rem; color:var(--text-muted);">
              <span>來源 IP: <code>${inc.sourceIp || 'N/A'}</code> | 偵測時間: ${new Date(inc.detectedAt).toLocaleString()}</span>
              ${inc.status === 'OPEN' ? `
                <button class="btn-header btn-gold" style="padding:0.25rem 0.75rem; font-size:0.75rem;" onclick="resolveIncident(${inc.id})">執行緩解並結案</button>
              ` : `<span style="color:var(--success);">緩解措施：${inc.mitigationAction || '已排除'}</span>`}
            </div>
          </div>
        </div>
      `).join('');
    }
  } catch (e) {
    console.error('Incidents load error:', e);
  }
}

async function resolveIncident(id) {
  const note = prompt('請輸入資安緩解紀錄說明 (Mitigation Note):', '已套用邊界 ACL 封鎖並重設憑證');
  if (!note) return;

  try {
    const res = await apiRequest(`/api/security/incidents/${id}/resolve`, {
      method: 'POST',
      body: JSON.stringify({ mitigationNote: note })
    });
    if (res.ok) {
      showToast('資安事件已成功緩解並記錄至 NIST 稽核軌跡', 'success');
      loadIncidents();
      loadSecurityMetrics();
      loadAuditLogs();
    }
  } catch (e) {
    showToast('操作失敗', 'error');
  }
}

function getNistBadgeClass(category) {
  switch (category) {
    case 'GOVERN': return 'badge-purple';
    case 'IDENTIFY': return 'badge-cyan';
    case 'PROTECT': return 'badge-blue';
    case 'DETECT': return 'badge-amber';
    case 'RESPOND': return 'badge-red';
    case 'RECOVER': return 'badge-green';
    default: return 'badge-blue';
  }
}

function getSeverityBadge(sev) {
  switch (sev) {
    case 'CRITICAL': return 'badge-red';
    case 'ERROR': return 'badge-red';
    case 'WARN': return 'badge-amber';
    case 'INFO':
    default: return 'badge-green';
  }
}
