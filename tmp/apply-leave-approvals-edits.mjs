// Applies the verified frontend edits for the leave PAID/LOP + approver work.
// Each replacement asserts the old string occurs exactly once so a drifted
// file fails loudly instead of silently no-op'ing.
import { readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

const root = join(dirname(fileURLToPath(import.meta.url)), '..', 'HRMS', 'src', 'pages');

function edit(file, replacements) {
  const path = join(root, file);
  let text = readFileSync(path, 'utf8');
  const crlf = text.includes('\r\n');
  const fix = (s) => (crlf ? s.replace(/\n/g, '\r\n') : s);
  for (const [rawOld, rawNew] of replacements) {
    const oldStr = fix(rawOld);
    const newStr = fix(rawNew);
    const first = text.indexOf(oldStr);
    if (first === -1) throw new Error(`${file}: old string not found:\n${rawOld.slice(0, 160)}...`);
    if (text.indexOf(oldStr, first + 1) !== -1) throw new Error(`${file}: old string not unique:\n${rawOld.slice(0, 160)}...`);
    text = text.replace(oldStr, newStr);
  }
  writeFileSync(path, text, 'utf8');
  console.log(`OK ${file} (${replacements.length} replacement(s), ${crlf ? 'CRLF' : 'LF'})`);
}

// --------------------------------------------------------------------------
// LeaveApprovals.jsx - manager view: split chip, approver, LOP warning,
// past-start-date gate, drawer detail items.
// --------------------------------------------------------------------------
edit('LeaveApprovals.jsx', [
  [
    `  const { page, setPage, pageItems, pageSize } = usePagination(filteredOrdered, 6);`,
    `  const { page, setPage, pageItems, pageSize } = usePagination(filteredOrdered, 6);

  // yyyy-MM-dd of today in local time, for comparing row start dates.
  const todayISO = new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
    .toISOString()
    .slice(0, 10);`,
  ],
  [
    `                  <td>{row.startDate} – {row.endDate}</td>
                  <td><strong>{row.totalDays}</strong></td>
                  <td className="reason-cell"><span title={row.reason}>{row.reason}</span></td>
                  <td><span className={\`status-pill \${String(row.status || '').toLowerCase()}\`}><StatusBadge>{row.status}</StatusBadge></span></td>`,
    `                  <td>{row.startDate} – {row.endDate}</td>
                  <td>
                    <strong>{row.totalDays}</strong>
                    {row.status === 'APPROVED' && Number(row.lopDays) > 0 && (
                      <span
                        className="leave-split"
                        title={\`\${row.paidDays ?? 0} day(s) deducted from the balance, \${row.lopDays} LOP day(s) (not deducted)\`}
                      >
                        {row.paidDays ?? 0}P · {row.lopDays}L
                      </span>
                    )}
                  </td>
                  <td className="reason-cell"><span title={row.reason}>{row.reason}</span></td>
                  <td>
                    <span className={\`status-pill \${String(row.status || '').toLowerCase()}\`}><StatusBadge>{row.status}</StatusBadge></span>
                    {row.status === 'APPROVED' && row.approvedByName && (
                      <span
                        className="leave-approver"
                        title={row.approvedByCode ? \`Approved by \${row.approvedByName} (\${row.approvedByCode})\` : \`Approved by \${row.approvedByName}\`}
                      >
                        by {row.approvedByName}
                      </span>
                    )}
                  </td>`,
  ],
  [
    `                        disabled={acting || String(row.status || '').toUpperCase() !== 'PENDING'}
                        onClick={() => setApproveTarget(row)}`,
    `                        disabled={acting || String(row.status || '').toUpperCase() !== 'PENDING' || String(row.startDate || '') < todayISO}
                        title={String(row.startDate || '') < todayISO && String(row.status || '').toUpperCase() === 'PENDING'
                          ? 'Cannot be approved — the start date has passed.'
                          : undefined}
                        onClick={() => setApproveTarget(row)}`,
  ],
  [
    `              <h3>Approve This Request?</h3>
              <p>Confirm Approval For <strong>{capitalizeName(approveTarget.employeeName)}</strong>'s <strong>{approveTarget.leaveType}</strong> Leave From <strong>{approveTarget.startDate}</strong> to <strong>{approveTarget.endDate}</strong>.</p>`,
    `              <h3>Approve This Request?</h3>
              <p>Confirm Approval For <strong>{capitalizeName(approveTarget.employeeName)}</strong>'s <strong>{approveTarget.leaveType}</strong> Leave From <strong>{approveTarget.startDate}</strong> to <strong>{approveTarget.endDate}</strong>.</p>
              <p className="lop-warning">
                Days beyond the monthly paid guideline, or beyond the employee's remaining balance,
                will be marked <strong>LOP (Loss of Pay)</strong> and are <strong>not deducted</strong> from
                their balance. The exact split is computed by the server after approval.
              </p>`,
  ],
  [
    `                  <div className="dg-item dg-wide"><CalendarRange size={15} /><span>Date Range</span><strong>{drawerItem.startDate} – {drawerItem.endDate}</strong></div>
                </div>`,
    `                  <div className="dg-item dg-wide"><CalendarRange size={15} /><span>Date Range</span><strong>{drawerItem.startDate} – {drawerItem.endDate}</strong></div>
                  {drawerItem.status === 'APPROVED' && drawerItem.paidDays != null && (
                    <div className="dg-item"><CheckCircle2 size={15} /><span>Balance Days</span><strong>{drawerItem.paidDays}</strong></div>
                  )}
                  {drawerItem.status === 'APPROVED' && drawerItem.lopDays != null && (
                    <div className="dg-item"><AlertTriangle size={15} /><span>LOP Days</span><strong>{drawerItem.lopDays}</strong></div>
                  )}
                  {drawerItem.status === 'APPROVED' && drawerItem.approvedByName && (
                    <div className="dg-item"><History size={15} /><span>Approved By</span><strong title={drawerItem.approvedByCode || undefined}>{drawerItem.approvedByName}</strong></div>
                  )}
                </div>`,
  ],
]);

// --------------------------------------------------------------------------
// Leave.jsx - employee history tooltip wording (balance language).
// --------------------------------------------------------------------------
edit('Leave.jsx', [
  [
    `title={\`\${row.paidDays ?? 0} paid day(s), \${row.lopDays} LOP day(s)\`}`,
    `title={\`\${row.paidDays ?? 0} day(s) deducted from the balance, \${row.lopDays} LOP day(s) (unpaid, not deducted)\`}`,
  ],
]);

// --------------------------------------------------------------------------
// LeaveApprovals.css - styles for the split chip / approver line + warning.
// --------------------------------------------------------------------------
edit('LeaveApprovals.css', [
  [
    `.status-pill {`,
    `/* PAID / LOP split and approver shown on an approved leave request row. */
.leave-split {
  display: inline-block;
  margin-left: 6px;
  padding: 2px 7px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.02em;
  color: #92400e;
  background: rgba(245, 158, 11, 0.16);
  border: 1px solid rgba(245, 158, 11, 0.32);
  cursor: help;
}
.leave-approver {
  display: block;
  margin-top: 4px;
  font-size: 11px;
  font-weight: 600;
  color: #64748b;
  cursor: help;
}
.lop-warning {
  font-size: 13px;
  color: #92400e;
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.3);
  border-radius: 8px;
  padding: 8px 10px;
}

.status-pill {`,
  ],
]);
