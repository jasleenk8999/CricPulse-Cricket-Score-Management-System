let activeMatchId = null;
let selectedRunValue = 0;
let autoPlayInterval = null;
let allTeamsCache = [];

document.addEventListener('DOMContentLoaded', () => {
  loadMatchesSelector();
  loadTeams();
  // Poll active scorecard every 3 seconds
  setInterval(() => {
    if (activeMatchId) {
      loadScorecard(activeMatchId);
    }
  }, 3000);
});

function switchTab(tabName) {
  document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));
  document.querySelectorAll('.view-section').forEach(sec => sec.classList.remove('active'));
  
  const targetSec = document.getElementById(`view-${tabName}`);
  if (targetSec) targetSec.classList.add('active');

  const btnMap = { dashboard: 0, matches: 1, teams: 2 };
  document.querySelectorAll('.nav-btn')[btnMap[tabName]]?.classList.add('active');

  if (tabName === 'matches') renderAllMatchesView();
  if (tabName === 'teams') renderTeamsView();
}

// Load Match Carousel Chips
async function loadMatchesSelector() {
  try {
    const res = await fetch('/api/matches');
    const matches = await res.json();
    
    const bar = document.getElementById('matchSelectorBar');
    bar.innerHTML = '';

    if (matches.length === 0) {
      bar.innerHTML = '<div style="color: var(--text-muted); font-size: 0.9rem;">No matches scheduled. Create one to start!</div>';
      return;
    }

    if (!activeMatchId) {
      activeMatchId = matches[0].id;
    }

    for (const m of matches) {
      const chip = document.createElement('div');
      chip.className = `match-chip ${m.id === activeMatchId ? 'selected' : ''}`;
      chip.onclick = () => selectMatch(m.id);

      const statusColor = m.status === 'LIVE' ? 'var(--accent-red)' : (m.status === 'COMPLETED' ? 'var(--accent-green)' : 'var(--text-muted)');

      chip.innerHTML = `
        <div class="chip-header">
          <span>${m.matchFormat} • ${m.venue.split(',')[0]}</span>
          <span style="color: ${statusColor}; font-weight: 800;">${m.status}</span>
        </div>
        <div class="chip-scores">
          <div class="chip-team">
            <span>${m.teamA.shortName}</span>
            <span class="chip-score-val" id="chip-score-${m.id}-A">--</span>
          </div>
          <div class="chip-team">
            <span>${m.teamB.shortName}</span>
            <span class="chip-score-val" id="chip-score-${m.id}-B">--</span>
          </div>
        </div>
      `;
      bar.appendChild(chip);

      // Fetch quick score summary for chip
      fetchChipScore(m.id);
    }

    loadScorecard(activeMatchId);
  } catch (e) {
    console.error('Error loading matches selector:', e);
  }
}

async function fetchChipScore(matchId) {
  try {
    const res = await fetch(`/api/matches/${matchId}/scorecard`);
    const card = await res.json();
    if (card && card.allInnings) {
      const inn1 = card.allInnings[0];
      const inn2 = card.allInnings[1];

      if (inn1) {
        const el = document.getElementById(`chip-score-${matchId}-${inn1.battingTeam.id === card.match.teamA.id ? 'A' : 'B'}`);
        if (el) el.textContent = `${inn1.totalRuns}/${inn1.totalWickets} (${inn1.completedOvers}.${inn1.ballsInCurrentOver})`;
      }
      if (inn2) {
        const el = document.getElementById(`chip-score-${matchId}-${inn2.battingTeam.id === card.match.teamA.id ? 'A' : 'B'}`);
        if (el) el.textContent = `${inn2.totalRuns}/${inn2.totalWickets} (${inn2.completedOvers}.${inn2.ballsInCurrentOver})`;
      }
    }
  } catch(e) {}
}

function selectMatch(id) {
  activeMatchId = id;
  loadMatchesSelector();
}

// Load Scorecard and Render Dashboard
async function loadScorecard(matchId) {
  try {
    const res = await fetch(`/api/matches/${matchId}/scorecard`);
    if (!res.ok) return;
    const card = await res.json();
    renderDashboardHero(card);
    renderActivePlayers(card);
    renderScorecardTables(card);
    renderRecentBalls(card.recentBalls || []);
    renderCommentary(card.recentBalls || []);
  } catch (e) {
    console.error('Error loading scorecard:', e);
  }
}

function renderDashboardHero(card) {
  const m = card.match;
  const inn = card.currentInnings;

  document.getElementById('heroMatchTitle').textContent = m.title;
  document.getElementById('heroVenue').textContent = m.venue;
  document.getElementById('heroFormat').textContent = m.matchFormat + ` (${m.totalOvers} Overs)`;
  document.getElementById('heroToss').textContent = `${m.tossWinner.shortName} won toss & elected to ${m.tossDecision}`;
  
  const statusBadge = document.getElementById('heroStatusBadge');
  statusBadge.textContent = m.status;
  statusBadge.style.borderColor = m.status === 'LIVE' ? 'var(--accent-red)' : 'var(--accent-green)';

  document.getElementById('teamAName').textContent = m.teamA.name;
  document.getElementById('teamBName').textContent = m.teamB.name;
  document.getElementById('teamABadge').style.backgroundColor = m.teamA.color || '#1E88E5';
  document.getElementById('teamBBadge').style.backgroundColor = m.teamB.color || '#FDD835';

  if (card.allInnings && card.allInnings.length > 0) {
    const inn1 = card.allInnings[0];
    const isTeamABatting1 = inn1.battingTeam.id === m.teamA.id;
    const targetA = isTeamABatting1 ? 'A' : 'B';
    const targetB = isTeamABatting1 ? 'B' : 'A';

    document.getElementById(`team${targetA}Score`).textContent = `${inn1.totalRuns}/${inn1.totalWickets}`;
    document.getElementById(`team${targetA}Overs`).textContent = `(${inn1.completedOvers}.${inn1.ballsInCurrentOver} ov)`;

    if (card.allInnings.length > 1) {
      const inn2 = card.allInnings[1];
      document.getElementById(`team${targetB}Score`).textContent = `${inn2.totalRuns}/${inn2.totalWickets}`;
      document.getElementById(`team${targetB}Overs`).textContent = `(${inn2.completedOvers}.${inn2.ballsInCurrentOver} ov)`;
    } else {
      document.getElementById(`team${targetB}Score`).textContent = `Yet to Bat`;
      document.getElementById(`team${targetB}Overs`).textContent = `(0.0 ov)`;
    }
  }

  if (inn) {
    document.getElementById('heroCrr').textContent = (inn.runRate || 0.00).toFixed(2);

    const rrrBox = document.getElementById('heroRrrBox');
    const targetBox = document.getElementById('heroTargetBox');

    if (inn.inningsNumber === 2 && inn.targetRuns) {
      targetBox.style.display = 'flex';
      rrrBox.style.display = 'flex';
      document.getElementById('heroTarget').textContent = inn.targetRuns;

      const runsNeeded = Math.max(0, inn.targetRuns - inn.totalRuns);
      const ballsRemaining = (m.totalOvers * 6) - (inn.completedOvers * 6 + inn.ballsInCurrentOver);
      const rrr = ballsRemaining > 0 ? (runsNeeded / (ballsRemaining / 6.0)) : 0;
      document.getElementById('heroRrr').textContent = rrr.toFixed(2);
    } else {
      targetBox.style.display = 'none';
      rrrBox.style.display = 'none';
    }
  }

  document.getElementById('heroResultSummary').textContent = m.resultSummary || (m.status === 'LIVE' ? 'Match In Progress' : m.status);
}

function renderActivePlayers(card) {
  const striker = card.currentStrikerStat;
  const nonStriker = card.currentNonStrikerStat;
  const bowler = card.currentBowlerStat;

  if (striker) {
    document.getElementById('strikerName').textContent = striker.player.name + ' *';
    document.getElementById('strikerRuns').textContent = striker.runsScored;
    document.getElementById('strikerBalls').textContent = striker.ballsFaced;
    document.getElementById('striker4s').textContent = striker.fours;
    document.getElementById('striker6s').textContent = striker.sixes;
    document.getElementById('strikerSR').textContent = striker.strikeRate.toFixed(1);
  } else {
    document.getElementById('strikerName').textContent = 'None';
  }

  if (nonStriker) {
    document.getElementById('nonStrikerName').textContent = nonStriker.player.name;
    document.getElementById('nonStrikerRuns').textContent = nonStriker.runsScored;
    document.getElementById('nonStrikerBalls').textContent = nonStriker.ballsFaced;
    document.getElementById('nonStriker4s').textContent = nonStriker.fours;
    document.getElementById('nonStriker6s').textContent = nonStriker.sixes;
    document.getElementById('nonStrikerSR').textContent = nonStriker.strikeRate.toFixed(1);
  } else {
    document.getElementById('nonStrikerName').textContent = 'None';
  }

  if (bowler) {
    document.getElementById('bowlerName').textContent = bowler.player.name;
    document.getElementById('bowlerOvers').textContent = bowler.oversBowledFormatted;
    document.getElementById('bowlerRuns').textContent = bowler.runsConceded;
    document.getElementById('bowlerWickets').textContent = bowler.wicketsTaken;
    document.getElementById('bowlerEcon').textContent = bowler.economyRate.toFixed(2);
  } else {
    document.getElementById('bowlerName').textContent = 'None';
  }
}

function renderScorecardTables(card) {
  const batBody = document.getElementById('battingScorecardBody');
  batBody.innerHTML = '';
  (card.battingStats || []).forEach(stat => {
    const isStriker = card.currentInnings?.currentStriker?.id === stat.player.id;
    const isNonStriker = card.currentInnings?.currentNonStriker?.id === stat.player.id;
    const isCurrent = isStriker || isNonStriker;

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td style="font-weight: 700; color: ${isCurrent ? 'var(--accent-green)' : 'inherit'};">
        ${stat.player.name} ${isStriker ? '*' : ''}
      </td>
      <td style="color: var(--text-muted); font-size: 0.85rem;">${stat.dismissalInfo || (stat.isOut ? 'out' : 'not out')}</td>
      <td class="num-col" style="font-weight: 800; color: var(--accent-gold);">${stat.runsScored}</td>
      <td class="num-col">${stat.ballsFaced}</td>
      <td class="num-col">${stat.fours}</td>
      <td class="num-col">${stat.sixes}</td>
      <td class="num-col">${stat.strikeRate.toFixed(1)}</td>
    `;
    batBody.appendChild(tr);
  });

  const bowlBody = document.getElementById('bowlingScorecardBody');
  bowlBody.innerHTML = '';
  (card.bowlingStats || []).forEach(stat => {
    const isCurrentBowler = card.currentInnings?.currentBowler?.id === stat.player.id;

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td style="font-weight: 700; color: ${isCurrentBowler ? 'var(--accent-gold)' : 'inherit'};">
        ${stat.player.name} ${isCurrentBowler ? '⚾' : ''}
      </td>
      <td class="num-col">${stat.oversBowledFormatted}</td>
      <td class="num-col">${stat.maidenOvers}</td>
      <td class="num-col">${stat.runsConceded}</td>
      <td class="num-col" style="font-weight: 800; color: var(--accent-red);">${stat.wicketsTaken}</td>
      <td class="num-col">${stat.economyRate.toFixed(2)}</td>
    `;
    bowlBody.appendChild(tr);
  });
}

function renderRecentBalls(balls) {
  const container = document.getElementById('recentBallsList');
  container.innerHTML = '';
  const subset = balls.slice(0, 10);

  subset.forEach(b => {
    const badge = document.createElement('div');
    let text = b.runsScored;
    let cls = 'ball-badge';

    if (b.isWicket) {
      text = 'W';
      cls += ' b-w';
    } else if (b.extraType === 'WIDE') {
      text = 'WD';
      cls += ' b-extra';
    } else if (b.extraType === 'NO_BALL') {
      text = 'NB';
      cls += ' b-extra';
    } else if (b.runsScored === 4) {
      cls += ' b-4';
    } else if (b.runsScored === 6) {
      cls += ' b-6';
    }

    badge.className = cls;
    badge.textContent = text;
    container.appendChild(badge);
  });
}

function renderCommentary(balls) {
  const stream = document.getElementById('commentaryStream');
  stream.innerHTML = '';

  balls.forEach(b => {
    const div = document.createElement('div');
    let typeCls = '';
    if (b.isWicket) typeCls = 'wicket';
    else if (b.runsScored >= 4) typeCls = 'boundary';

    div.className = `commentary-item ${typeCls}`;
    div.innerHTML = `
      <span class="comm-over">${b.overNumber}.${b.ballNumber}</span>
      <span>${b.commentary || 'Ball delivered.'}</span>
    `;
    stream.appendChild(div);
  });
}

// Simulation Handlers
async function simulateOneBall() {
  if (!activeMatchId) return;
  try {
    await fetch(`/api/matches/${activeMatchId}/simulate-ball`, { method: 'POST' });
    loadScorecard(activeMatchId);
    loadMatchesSelector();
  } catch (e) {
    console.error('Error simulating ball:', e);
  }
}

async function simulateFullMatch() {
  if (!activeMatchId) return;
  try {
    await fetch(`/api/matches/${activeMatchId}/simulate-match`, { method: 'POST' });
    loadScorecard(activeMatchId);
    loadMatchesSelector();
  } catch (e) {
    console.error('Error simulating match:', e);
  }
}

function toggleAutoPlay() {
  const btn = document.getElementById('btnAutoPlay');
  if (autoPlayInterval) {
    clearInterval(autoPlayInterval);
    autoPlayInterval = null;
    btn.innerHTML = '<span>▶ Auto Play Broadcast (2s)</span>';
    btn.classList.remove('btn-danger');
    btn.classList.add('btn-gold');
  } else {
    autoPlayInterval = setInterval(() => {
      simulateOneBall();
    }, 2000);
    btn.innerHTML = '<span>⏸ Pause Auto Broadcast</span>';
    btn.classList.remove('btn-gold');
    btn.classList.add('btn-danger');
  }
}

// Manual Scoring Handler
function selectRun(val, el) {
  selectedRunValue = val;
  document.querySelectorAll('.run-pill').forEach(p => p.classList.remove('selected'));
  el.classList.add('selected');
}

function toggleWicketOptions(isWicket) {
  document.getElementById('wicketOptionsBox').style.display = isWicket ? 'block' : 'none';
}

async function handleManualBall(event) {
  event.preventDefault();
  if (!activeMatchId) return;

  const extraType = document.getElementById('extraTypeSelect').value;
  const isWicket = document.getElementById('isWicketCheck').checked;
  const dismissalType = document.getElementById('dismissalTypeSelect').value;
  const commentary = document.getElementById('customCommentaryInput').value;

  const payload = {
    runsScored: selectedRunValue,
    extraType: extraType,
    extraRuns: 0,
    isWicket: isWicket,
    dismissalType: isWicket ? dismissalType : 'NONE',
    commentary: commentary
  };

  try {
    await fetch(`/api/matches/${activeMatchId}/ball`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    document.getElementById('customCommentaryInput').value = '';
    loadScorecard(activeMatchId);
    loadMatchesSelector();
  } catch (e) {
    console.error('Error recording manual ball:', e);
  }
}

// Teams & Squads View
async function loadTeams() {
  try {
    const res = await fetch('/api/teams');
    allTeamsCache = await res.json();
    populateTeamDropdowns();
  } catch(e) {}
}

function populateTeamDropdowns() {
  const matchA = document.getElementById('newMatchTeamA');
  const matchB = document.getElementById('newMatchTeamB');
  const tossW = document.getElementById('newMatchTossWinner');
  const playerT = document.getElementById('newPlayerTeam');

  [matchA, matchB, tossW, playerT].forEach(sel => { if(sel) sel.innerHTML = ''; });

  allTeamsCache.forEach(t => {
    const opt = `<option value="${t.id}">${t.name} (${t.shortName})</option>`;
    if (matchA) matchA.innerHTML += opt;
    if (matchB) matchB.innerHTML += opt;
    if (tossW) tossW.innerHTML += opt;
    if (playerT) playerT.innerHTML += opt;
  });
}

async function renderTeamsView() {
  await loadTeams();
  const container = document.getElementById('teamsContainer');
  container.innerHTML = '';

  for (const t of allTeamsCache) {
    const playersRes = await fetch(`/api/players?teamId=${t.id}`);
    const players = await playersRes.json();

    const card = document.createElement('div');
    card.className = 'team-card';
    card.innerHTML = `
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1rem;">
        <h3 style="display: flex; align-items: center; gap: 0.5rem;">
          <span style="width: 16px; height: 16px; border-radius: 50%; background: ${t.color || '#00e5ff'};"></span>
          ${t.name} (${t.shortName})
        </h3>
        <span style="color: var(--text-muted); font-size: 0.85rem;">${t.country}</span>
      </div>
      <div style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 0.5rem; font-weight: 700;">PLAYER SQUAD (${players.length})</div>
      <ul style="list-style: none; padding: 0;">
        ${players.map(p => `
          <li style="display: flex; justify-content: space-between; padding: 0.4rem 0; border-bottom: 1px solid rgba(255,255,255,0.04); font-size: 0.9rem;">
            <span>#${p.jerseyNumber || '-'} <strong>${p.name}</strong></span>
            <span style="color: var(--accent-cyan); font-size: 0.75rem; text-transform: uppercase;">${p.role}</span>
          </li>
        `).join('')}
      </ul>
    `;
    container.appendChild(card);
  }
}

// All Matches View
async function renderAllMatchesView() {
  const res = await fetch('/api/matches/summaries');
  const summaries = await res.json();
  const grid = document.getElementById('allMatchesGrid');
  grid.innerHTML = '';

  summaries.forEach(m => {
    const card = document.createElement('div');
    card.className = 'team-card';
    card.innerHTML = `
      <div style="display: flex; justify-content: space-between; margin-bottom: 0.75rem;">
        <span style="color: var(--text-muted); font-size: 0.85rem;">${m.venue}</span>
        <span class="live-badge" style="border-color: var(--accent-green);">${m.status}</span>
      </div>
      <h3 style="margin-bottom: 1rem;">${m.title}</h3>
      <div style="background: rgba(10,14,23,0.5); padding: 0.75rem; border-radius: 8px; margin-bottom: 1rem;">
        <div style="display: flex; justify-content: space-between; margin-bottom: 0.25rem;">
          <span>${m.teamAName}</span>
          <strong style="color: var(--accent-gold); font-family: monospace;">${m.firstInningsScore || 'Yet to Bat'}</strong>
        </div>
        <div style="display: flex; justify-content: space-between;">
          <span>${m.teamBName}</span>
          <strong style="color: var(--accent-gold); font-family: monospace;">${m.secondInningsScore || 'Yet to Bat'}</strong>
        </div>
      </div>
      <div style="font-size: 0.85rem; color: var(--accent-green); font-weight: 700; margin-bottom: 1rem;">
        ${m.resultSummary || 'Match in Progress'}
      </div>
      <button class="btn btn-secondary" style="width: 100%;" onclick="selectMatch(${m.matchId}); switchTab('dashboard');">
        View Match Dashboard ↗
      </button>
    `;
    grid.appendChild(card);
  });
}

// Modal Controllers
function openCreateMatchModal() {
  document.getElementById('createMatchModal').classList.add('open');
}
function openCreatePlayerModal() {
  document.getElementById('createPlayerModal').classList.add('open');
}
function closeModal(modalId) {
  document.getElementById(modalId).classList.remove('open');
}

async function handleCreateMatch(e) {
  e.preventDefault();
  const payload = {
    title: document.getElementById('newMatchTitle').value,
    venue: document.getElementById('newMatchVenue').value,
    matchFormat: document.getElementById('newMatchFormat').value,
    totalOvers: parseInt(document.getElementById('newMatchOvers').value),
    teamAId: parseInt(document.getElementById('newMatchTeamA').value),
    teamBId: parseInt(document.getElementById('newMatchTeamB').value),
    tossWinnerId: parseInt(document.getElementById('newMatchTossWinner').value),
    tossDecision: document.getElementById('newMatchTossDecision').value
  };

  try {
    const res = await fetch('/api/matches', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    const match = await res.json();
    closeModal('createMatchModal');
    activeMatchId = match.id;
    loadMatchesSelector();
    switchTab('dashboard');
  } catch (err) {
    console.error('Error creating match:', err);
  }
}

async function handleCreatePlayer(e) {
  e.preventDefault();
  const teamId = parseInt(document.getElementById('newPlayerTeam').value);
  const payload = {
    name: document.getElementById('newPlayerName').value,
    role: document.getElementById('newPlayerRole').value,
    battingStyle: document.getElementById('newPlayerBattingStyle').value,
    bowlingStyle: document.getElementById('newPlayerBowlingStyle').value,
    jerseyNumber: parseInt(document.getElementById('newPlayerJersey').value || 0)
  };

  try {
    await fetch(`/api/players?teamId=${teamId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    closeModal('createPlayerModal');
    renderTeamsView();
  } catch (err) {
    console.error('Error creating player:', err);
  }
}
