const {test} = require('node:test');
const assert = require('node:assert/strict');
const {spawn} = require('node:child_process');
const {randomBytes} = require('node:crypto');
const path = require('node:path');
const fs = require('node:fs');
const vm = require('node:vm');
const net = require('node:net');

test('API rejects forged sessions, wrong passwords, unauthorized roles and other users mail', async () => {
  const socket = net.createServer();
  await new Promise(r => socket.listen(0, '127.0.0.1', r));
  const port = socket.address().port; await new Promise(r => socket.close(r));
  const adminPassword = randomBytes(24).toString('hex');
  const studentPassword = randomBytes(24).toString('hex');
  const child = spawn(process.execPath, [path.join(__dirname, '../server.js')], {
    env: {...process.env, PORT: String(port), HOST: '127.0.0.1', PORTAL_ADMIN_PASSWORD: adminPassword, PORTAL_STUDENT_PASSWORD: studentPassword},
    stdio: ['ignore', 'pipe', 'pipe'], windowsHide: true,
  });
  const base = `http://127.0.0.1:${port}`;
  const request = (url, method = 'GET', body, token) => fetch(base + url, {
    method, headers: {'Content-Type':'application/json', ...(token ? {Authorization:`Bearer ${token}`} : {})},
    ...(body === undefined ? {} : {body: JSON.stringify(body)})
  });
  try {
    await new Promise((resolve, reject) => { child.stdout.once('data', resolve); child.once('error', reject); child.once('exit', c => reject(new Error(`Early exit ${c}`))); });
    assert.equal((await request('/api/news')).status, 200);
    assert.equal((await request('/api/mail/folder/INBOX')).status, 401);
    assert.equal((await request('/api/security/audit-logs', 'GET', undefined, 'cust-jwt-simulated-YWRtaW4=')).status, 401);
    for (const password of ['CustAdmin2026!', 'arbitrary-long-wrong-password', null]) {
      assert.equal((await request('/api/auth/login', 'POST', {username:'admin',password})).status, 401);
    }
    const login = await request('/api/auth/login', 'POST', {username:'admin',password:adminPassword});
    assert.equal(login.status, 200); const admin = (await login.json()).token;
    assert.match(admin, /^[0-9a-f]{64}$/);
    const studentResponse = await request('/api/auth/login', 'POST', {username:'student_lin',password:studentPassword});
    assert.equal(studentResponse.status, 200); const student = (await studentResponse.json()).token;
    assert.equal((await request('/api/security/audit-logs','GET',undefined,student)).status, 403);
    assert.equal((await request('/api/news','POST',{title:'t',content:'c'},student)).status, 403);
    assert.equal((await request('/api/news','POST',{title:'<img src=x onerror=alert(1)>',content:'c'},admin)).status, 200);
    assert.equal((await request('/api/mail/message/1','GET',undefined,student)).status, 404);
    const mails = await request('/api/mail/folder/INBOX','GET',undefined,student);
    assert.equal((await mails.json()).content.length,0);
    assert.equal((await request('/api/mail/message/1','GET',undefined,admin)).status, 200);
    assert.equal((await request('/api/news','POST',{},admin)).status, 400);
    assert.equal((await request('/api/news','POST',{title:'t',content:'x'.repeat(70000)},admin)).status, 413);
    const traversal = await new Promise((resolve,reject) => require('node:http').get(base+'/..%2f..%2fapplication.yml',r => {r.resume();resolve(r.statusCode);}).on('error',reject));
    assert.equal(traversal,403);
    assert.equal((await request('/api/auth/logout','POST',{},admin)).status,204);
    assert.equal((await request('/api/security/audit-logs','GET',undefined,admin)).status,401);
    for(let i=0;i<6;i++) await request('/api/auth/login','POST',{username:'admin',password:'wrong'});
    assert.equal((await request('/api/auth/login','POST',{username:'admin',password:adminPassword})).status,429);
  } finally { child.kill(); }
});

test('untrusted HTML is rendered as text in news, mail, audit, and user fields', () => {
  const elements = new Map();
  const element = id => { if (!elements.has(id)) elements.set(id,{innerHTML:'',style:{},classList:{add(){},remove(){}}}); return elements.get(id); };
  const sandbox = {console, localStorage:{getItem(){return null;}}, document:{addEventListener(){},getElementById:element,querySelectorAll(){return [];}}, setTimeout(){}, fetch(){return new Promise(()=>{});} };
  vm.createContext(sandbox);
  for(const name of ['app','news','mail']) vm.runInContext(fs.readFileSync(path.join(__dirname,`../src/main/resources/static/js/${name}.js`),'utf8'),sandbox);
  const payload='<img src=x onerror="alert(1)">';
  assert.equal(vm.runInContext(`escapeHTML(${JSON.stringify(payload)})`,sandbox).includes('<img'),false);
  sandbox.payload=payload;
  vm.runInContext(`renderMailDetail({subject:payload,senderName:payload,senderEmail:payload,recipientName:payload,recipientEmail:payload,body:payload,receivedAt:0});`,sandbox);
  assert.equal(element('mail-detail-container').innerHTML.includes('<img'),false);
  assert.match(element('mail-detail-container').innerHTML,/&lt;img/);
  vm.runInContext(`renderNewsList([{id:1,title:payload,summary:payload,content:payload,authorDepartment:payload,viewCount:1,publishedAt:0}]);`,sandbox);
  assert.equal([...elements.values()].some(x=>x.innerHTML.includes('<img')),false);
});
