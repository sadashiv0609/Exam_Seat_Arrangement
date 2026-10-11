// Group Management
document.getElementById('addGroupBtn').addEventListener('click', () => {
  const count = document.querySelectorAll('.group-card').length + 1;
  const div = document.createElement('div');
  div.className = 'card group-card';
  div.innerHTML = `
    <h3>Group ${count}</h3>
    <label>Department:</label><input type="text" class="g-dept" value="AI">
    <label>Division:</label><input type="text" class="g-div" value="A">
    <label>Subject / Exam:</label><input type="text" class="g-sub" value="ML">
  `;
  document.getElementById('groupsList').appendChild(div);
});

document.getElementById('removeGroupBtn').addEventListener('click', () => {
  const groups = document.querySelectorAll('.group-card');
  if (groups.length > 1) groups[groups.length - 1].remove();
});

// Room Management
document.getElementById('addRoomBtn').addEventListener('click', () => {
  const div = document.createElement('div');
  div.className = 'room-row';
  div.innerHTML = `
    <input type="text" class="room-name" value="Room 10X" placeholder="Room Name">
    <input type="number" class="room-cap" value="10" placeholder="Benches">
  `;
  document.getElementById('roomsList').appendChild(div);
});

document.getElementById('removeRoomBtn').addEventListener('click', () => {
  const rooms = document.querySelectorAll('.room-row');
  if (rooms.length > 1) rooms[rooms.length - 1].remove();
});

// Generation
document.getElementById('generateBtn').addEventListener('click', async () => {
  const container = document.getElementById('resultsContainer');
  const printBtn = document.getElementById('printBtn');
  container.innerHTML = '<p class="no-print">Generating...</p>';
  printBtn.style.display = 'none';

  // 1. Gather Data
  const examType = document.getElementById('exam-type').value;
  const groupLines = [];
  const groupSummaries = [];
  
  document.querySelectorAll('.group-card').forEach(card => {
    const d = card.querySelector('.g-dept').value.trim();
    const v = card.querySelector('.g-div').value.trim();
    const s = card.querySelector('.g-sub').value.trim();
    groupLines.push(`${d}|${v}|${s}`);
    groupSummaries.push(`${d}-${v} ${s}`);
  });

  const roomLines = [];
  document.querySelectorAll('.room-row').forEach(row => {
    const n = row.querySelector('.room-name').value.trim();
    const c = row.querySelector('.room-cap').value.trim();
    if (n && c > 0) roomLines.push(`${n}|${c}`);
  });

  const line3Header = groupSummaries.join(' & ');
  const payload = `META\n${examType}\n===\nGROUPS\n${groupLines.join('\n')}\n===\nROOMS\n${roomLines.join('\n')}`;

  try {
    const res = await fetch('http://localhost:8080/generate', { method: 'POST', body: payload });
    const data = await res.json();
    container.innerHTML = '';

    if (Object.keys(data).length === 0) {
      container.innerHTML = '<p style="color:orange;">No allocations made.</p>';
      return;
    }

    // 2. Render Print-Ready Layout
    for (const [room, seats] of Object.entries(data)) {
      const roomCard = document.createElement('div');
      roomCard.className = 'room-section';

      let html = `
        <div class="print-header">
          <h2>VPPCOE</h2>
          <h3>${examType}</h3>
          <h4>${line3Header}</h4>
          <h4>Seating Arrangement for ${room}</h4>
        </div>
        <table>
          <thead>
            <tr>
              <th>Bench No.</th>
              <th>Seat 1 (Roll No)</th>
              <th>Seat 2 (Roll No)</th>
            </tr>
          </thead>
          <tbody>
      `;

      seats.forEach(s => {
        const r1 = s.rollNo1 !== '-' ? `${s.dept1}-${s.rollNo1}` : '-';
        const r2 = s.rollNo2 !== '-' ? `${s.dept2}-${s.rollNo2}` : '-';
        html += `
          <tr>
            <td><b>${s.benchNo}</b></td>
            <td>${r1}</td>
            <td>${r2}</td>
          </tr>
        `;
      });

      html += `</tbody></table>`;
      roomCard.innerHTML = html;
      container.appendChild(roomCard);
    }
    
    printBtn.style.display = 'block';
  } catch (err) {
    container.innerHTML = '<p style="color:red;" class="no-print">Error connecting to server.</p>';
  }
});