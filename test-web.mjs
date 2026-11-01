// Integration checks use a temporary data folder; your tasks are untouched.
import {spawn,spawnSync} from 'node:child_process';
import {mkdtemp,rm,symlink} from 'node:fs/promises';
import {tmpdir} from 'node:os';
import {resolve,join} from 'node:path';
import assert from 'node:assert/strict';
const root=resolve('task-manager');
const temp=await mkdtemp(join(tmpdir(),'task-manager-test-'));
await symlink(join(root,'web'),join(temp,'web'),'junction');
const port=18080;
const settings=spawnSync('java',['-XshowSettings:properties','-version'],{encoding:'utf8',windowsHide:true});
const javaHome=settings.stderr.match(/java.home\s*=\s*(.+)/)?.[1].trim();
const java=javaHome?join(javaHome,'bin',process.platform==='win32'?'java.exe':'java'):'java';
let child;
async function start(){
 child=spawn(java,['-cp',join(root,'target/classes'),'ru.portfolio.taskmanager.WebMain',String(port)],{cwd:temp,windowsHide:true,stdio:['ignore','pipe','pipe']});
 await new Promise((ok,no)=>{child.stdout.on('data',d=>{if(String(d).includes('Task Manager:'))ok();});child.on('error',no);child.on('exit',code=>no(Error('Server exited: '+code)));});
}
async function stop(){if(!child)return;const current=child;child=null;if(current.exitCode!==null)return;const p=new Promise(ok=>current.once('exit',ok));current.kill('SIGKILL');await p;}
const url=`http://127.0.0.1:${port}`;
async function api(data,expected=200){const r=await fetch(url+'/api/tasks',data?{method:'POST',body:new URLSearchParams(data)}:{});assert.equal(r.status,expected);return r.json();}
try{
 await start();
 assert.equal((await fetch(url)).status,200);
 assert.equal((await fetch(url+'/style.css')).status,200);
 assert.equal((await fetch(url+'/unknown')).status,404);
 assert.deepEqual(await api(),[]);
 await api({action:'create',title:'   ',priority:'LOW'},400);
 let tasks=await api({action:'create',title:'Тест <script> & "кавычки"',description:'Строка 1\nСтрока 2',priority:'HIGH'});
 assert.equal(tasks[0].title,'Тест <script> & "кавычки"');assert.equal(tasks[0].status,'NEW');
 const id=tasks[0].id;
 tasks=await api({action:'status',id,status:'IN_PROGRESS'});assert.equal(tasks[0].status,'IN_PROGRESS');
 tasks=await api({action:'status',id,status:'DONE'});assert.equal(tasks[0].status,'DONE');
 await stop();await start();
 tasks=await api();assert.equal(tasks[0].status,'DONE');assert.equal(tasks[0].description,'Строка 1\nСтрока 2');
 await api({action:'status',id,status:'INVALID'},400);
 const forbidden=await fetch(url+'/api/tasks',{method:'POST',headers:{Origin:'https://example.com'},body:new URLSearchParams({action:'delete',id})});assert.equal(forbidden.status,403);
 assert.deepEqual(await api({action:'delete',id}),[]);
 await stop();await start();assert.deepEqual(await api(),[]);
 console.log('PASS: assets, validation, create, status, delete, Unicode, restart persistence, origin checks.');
}finally{await stop();await rm(temp,{recursive:true,force:true,maxRetries:10,retryDelay:200});}
