(() => {
  'use strict';

  const body = document.body;
  const toggle = document.querySelector('[data-sidebar-toggle]');
  const closeTargets = document.querySelectorAll('[data-sidebar-close]');

  if (toggle) {
    toggle.addEventListener('click', () => body.classList.toggle('sidebar-open'));
  }
  closeTargets.forEach((target) => target.addEventListener('click', () => body.classList.remove('sidebar-open')));
  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') body.classList.remove('sidebar-open');
  });

  const currentPath = window.location.pathname.replace(/\/$/, '') || '/';
  document.querySelectorAll('[data-nav-link]').forEach((link) => {
    const url = new URL(link.href, window.location.origin);
    const path = url.pathname.replace(/\/$/, '') || '/';
    const active = path === '/dashboard'
      ? currentPath === '/' || currentPath === '/dashboard'
      : currentPath === path || currentPath.startsWith(path + '/');
    if (active) {
      link.classList.add('active');
      link.setAttribute('aria-current', 'page');
    }
  });

  const search = document.getElementById('pageSearch');
  if (search) {
    search.addEventListener('input', () => {
      const query = search.value.trim().toLocaleLowerCase();
      const tables = document.querySelectorAll('table');
      tables.forEach((table) => {
        table.querySelectorAll('tbody tr').forEach((row) => {
          if (row.querySelector('td[colspan]')) return;
          row.hidden = query.length > 0 && !row.innerText.toLocaleLowerCase().includes(query);
        });
      });
    });
  }
})();
