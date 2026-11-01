'use strict';
const $ = s => document.querySelector(s);
const labels = {NEW:'Новая', IN_PROGRESS:'В работе', DONE:'Выполнена', LOW:'Низкий', MEDIUM:'Средний', HIGH:'Высокий'};
let tasks = [], view = 'all', busy = false;
const escapeHtml = s => String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
$('#date').textContent = new Intl.DateTimeFormat('ru',{day:'numeric',month:'long',year:'numeric'}).format(new Date());
function toast(message) { $('#toast').textContent=message; $('#toast').style.display='block'; clearTimeout(toast.timer); toast.timer=setTimeout(()=>$('#toast').style.display='none',4000); }
async function request(data) {
 const response = await fetch('/api/tasks', data ? {method:'POST',body:new URLSearchParams(data)} : {});
 const result = await response.json();
 if (!response.ok) throw Error(result.error || 'Не удалось выполнить действие');
 tasks=result; render();
}
function render() {
 const done=tasks.filter(t=>t.status==='DONE').length;
 $('#total').textContent=tasks.length; $('#nav-count').textContent=tasks.length;
 $('#progress').textContent=tasks.filter(t=>t.status==='IN_PROGRESS').length;
 $('#done').textContent=done;
 $('#completion').textContent=tasks.length ? `${Math.round(done/tasks.length*100)}% от всех задач` : 'Начало большого пути';
 const query=$('#search').value.trim().toLocaleLowerCase('ru');
 const status=$('#status-filter').value, priority=$('#priority-filter').value;
 const filtered=tasks.filter(t=>(view==='all'||t.status===view)&&(!status||t.status===status)&&(!priority||t.priority===priority)&&t.title.toLocaleLowerCase('ru').includes(query));
 $('#shown-count').textContent=filtered.length;
 if(!filtered.length){ $('#tasks').innerHTML=`<div class="empty"><div class="empty-symbol">✓</div><h3>${tasks.length ? 'Задачи не найдены' : 'Место для твоих планов'}</h3><p>${tasks.length ? 'Попробуй изменить поиск или фильтры.' : 'Добавь первую задачу, расставь приоритеты<br>и сделай день немного продуктивнее.'}</p>${!tasks.length?'<button class="primary" id="first-task">＋ Создать первую задачу</button>':''}</div>`; $('#first-task')?.addEventListener('click',openDialog); return; }
 $('#tasks').innerHTML=filtered.map(t=>`<div class="task-row ${t.status==='DONE'?'task-done':''}"><div class="task-main"><input type="checkbox" data-check="${t.id}" ${t.status==='DONE'?'checked':''} aria-label="Завершить: ${escapeHtml(t.title)}"><div><div class="task-title">${escapeHtml(t.title)}</div>${t.description?`<div class="task-description">${escapeHtml(t.description)}</div>`:''}</div></div><span class="priority ${t.priority}">${labels[t.priority]}</span><select class="status ${t.status}" data-status="${t.id}" aria-label="Статус: ${escapeHtml(t.title)}">${['NEW','IN_PROGRESS','DONE'].map(s=>`<option value="${s}" ${s===t.status?'selected':''}>${labels[s]}</option>`).join('')}</select><button class="icon" data-delete="${t.id}" aria-label="Удалить: ${escapeHtml(t.title)}">×</button></div>`).join('');
}
function openDialog(){ $('#form').reset(); $('#form-error').textContent=''; $('#dialog').showModal(); }
$('#add').onclick=openDialog;
$('#close').onclick=$('#cancel').onclick=()=>$('#dialog').close();
$('#dialog').addEventListener('click',e=>{if(e.target===$('#dialog')){const r=e.target.getBoundingClientRect();if(e.clientX<r.left||e.clientX>r.right||e.clientY<r.top||e.clientY>r.bottom)e.target.close();}});
$('#form').onsubmit=async e=>{ e.preventDefault(); if(busy)return; busy=true; $('#submit').disabled=true; try{ await request({action:'create',...Object.fromEntries(new FormData(e.target))}); $('#dialog').close(); toast('Задача добавлена'); }catch(err){$('#form-error').textContent=err.message;}finally{busy=false;$('#submit').disabled=false;} };
$('#search').oninput=render; $('#status-filter').onchange=render; $('#priority-filter').onchange=render;
document.querySelectorAll('[data-view]').forEach(button=>button.onclick=()=>{view=button.dataset.view;document.querySelectorAll('[data-view]').forEach(b=>b.classList.toggle('active',b===button));const title={all:'Все задачи',IN_PROGRESS:'В работе',DONE:'Выполненные'}[view];$('#page-title').innerHTML=title+'<span>.</span>';$('#crumb').textContent=title;$('#status-filter').value='';render();});
async function mutate(data){ if(busy){render();return;}busy=true;try{await request(data);toast(data.action==='delete'?'Задача удалена':'Статус обновлён');}catch(e){toast(e.message);render();}finally{busy=false;} }
$('#tasks').addEventListener('change',e=>{if(e.target.dataset.status)mutate({action:'status',id:e.target.dataset.status,status:e.target.value});if(e.target.dataset.check)mutate({action:'status',id:e.target.dataset.check,status:e.target.checked?'DONE':'NEW'});});
$('#tasks').addEventListener('click',e=>{const b=e.target.closest('[data-delete]');if(b&&confirm('Удалить эту задачу?'))mutate({action:'delete',id:b.dataset.delete});});
request().catch(e=>{$('#tasks').innerHTML='<div class="empty"><h3>Не удалось загрузить задачи</h3><p>Проверь, что Java-сервер запущен, и обнови страницу.</p></div>';toast(e.message);});
