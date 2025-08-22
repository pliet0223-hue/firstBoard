document.addEventListener('DOMContentLoaded', () => {
  const articleIdEl = document.getElementById('articleId');
  const listEl = document.getElementById('comment-list');
  const formEl = document.getElementById('comment-form');
  if (!articleIdEl || !listEl) return;

  const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
  const articleId = articleIdEl.value;
  const API_LIST = `/api/articles/${articleId}/comments`;

  const render = (comments=[]) => {
    listEl.innerHTML = '';
    if (comments.length === 0) {
      const empty = document.createElement('div');
      empty.className = 'text-secondary small';
      empty.innerText = '댓글이 없습니다.';
      listEl.appendChild(empty);
      return;
    }
    comments.forEach(c => {
      const item = document.createElement('div');
      item.className = 'nb-card p-3';
      const nickname = (c.nickname && c.nickname.trim()) ? c.nickname : '익명';
      item.innerHTML = `
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <strong class="me-2">${nickname}</strong>
            <small class="text-secondary">${c.createdAt ?? ''}</small>
          </div>
          ${c.own ? '<button class="btn btn-sm btn-outline-danger" data-id="'+c.id+'">삭제</button>' : ''}
        </div>
        <div class="mt-2" style="white-space:pre-wrap;">${(c.content || '').replace(/</g,'&lt;')}</div>
      `;
      listEl.appendChild(item);
      const delBtn = item.querySelector('button[data-id]');
      if (delBtn) {
        delBtn.addEventListener('click', async () => {
          if (!confirm('삭제하시겠습니까?')) return;
          await fetch(`/api/comments/${delBtn.dataset.id}`, {
            method: 'DELETE',
            headers: csrfHeader && csrfToken ? { [csrfHeader]: csrfToken } : {}
          });
          await load();
        });
      }
    });
  };

  const load = async () => {
    const res = await fetch(API_LIST);
    if (!res.ok) { listEl.innerHTML = '<div class="text-danger small">댓글을 불러오지 못했습니다.</div>'; return; }
    const data = await res.json();
    render(data);
  };

  if (formEl) {
    formEl.addEventListener('submit', async (e) => {
      e.preventDefault();
      const contentEl = document.getElementById('commentContent');
      const nickEl = document.getElementById('commentNickname');
      const content = contentEl.value.trim();
      const nickname = nickEl.value.trim();
      if (!content) return;
      await fetch(API_LIST, {
        method: 'POST',
        headers: Object.assign(
          { 'Content-Type': 'application/json' },
          (csrfHeader && csrfToken) ? { [csrfHeader]: csrfToken } : {}
        ),
        body: JSON.stringify({ content, nickname })
      });
      contentEl.value = '';
      await load();
    });
  }

  load();
});
