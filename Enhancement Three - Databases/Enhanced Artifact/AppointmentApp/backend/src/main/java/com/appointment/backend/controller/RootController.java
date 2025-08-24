package com.appointment.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Developer-friendly HTML landing page at "/".
 * Lists endpoints, auth requirements, example payloads, and curl snippets.
 * This is for convenience/documentation only; the real API returns JSON.
 */
@Controller
public class RootController {

  /** Serves the static HTML that documents the API. */
  @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
  @ResponseBody
  public String index() {
    return """
                        <!doctype html>
                        <html lang="en">
                        <head>
                          <meta charset="utf-8"/>
                          <meta name="viewport" content="width=device-width, initial-scale=1"/>
                          <title>Appointment App API</title>
                          <link rel="icon" href="/favicon.ico">
                          <style>
                            :root{
                              --bg:#f6f9fc;--card:#fff;--ink:#1f2937;--muted:#4b5563;
                              --chip:#e5e7eb;--ok:#16a34a;--warn:#d97706;--err:#dc2626;--link:#0b7285;
                              --code:#0f172a10;
                            }
                            body{font-family: ui-sans-serif,system-ui,Segoe UI,Roboto,Arial;
                                 background:var(--bg); color:var(--ink); margin:0; padding:24px;}
                            .card{max-width:980px; margin:0 auto; background:var(--card); padding:28px;
                                  border-radius:12px; box-shadow:0 6px 18px rgba(0,0,0,.08);}
                            h1{margin:.2rem 0 1rem} h2{margin:1.5rem 0 .5rem}
                            p{margin:.4rem 0 .6rem; color:var(--muted)}
                            a{color:var(--link); text-decoration:none} a:hover{text-decoration:underline}
                            ul{line-height:1.8}
                            .mono{font-family: ui-monospace,SFMono-Regular,Menlo,Consolas,monospace}
                            code{background:var(--code); padding:2px 6px; border-radius:6px;}
                            pre{background:var(--code); padding:12px; border-radius:10px; overflow:auto}
                            .pill{display:inline-block; padding:2px 8px; border-radius:999px; font-size:.85rem;
                                  background:var(--chip); color:#111827; margin-left:6px}
                            .get{color:var(--ok); font-weight:600}
                            .post{color:#2563eb; font-weight:600}
                            .del{color:var(--err); font-weight:600}
                            .auth{background:#fee2e2; color:#991b1b}
                            .open{background:#dcfce7; color:#166534}
                            .grid{display:grid; grid-template-columns: 1fr 1fr; gap:18px}
                            @media (max-width:900px){ .grid{grid-template-columns:1fr} }
                            .hr{height:1px; background:#e5e7eb; margin:16px 0}
                            .badge{font-size:.75rem; padding:2px 8px; border-radius:999px; background:#eef2ff; color:#3730a3; margin-left:6px}
                          </style>
                        </head>
                        <body>
                          <div class="card">
                            <h1>Appointment App API</h1>
                            <p>This service powers a React frontend and exposes JSON endpoints for appointments and authentication.</p>

                            <div class="hr"></div>
                            <h2>Base URLs</h2>
                            <ul class="mono">
                              <li>API: <code>http://localhost:8080</code></li>
                              <li>Frontend (dev): <code>http://localhost:3000</code></li>
                            </ul>

                            <div class="hr"></div>
                            <h2>Authentication</h2>
                            <p>Protected endpoints require a JWT in the <code>Authorization</code> header:
                               <code>Authorization: Bearer &lt;token&gt;</code>.</p>
                            <ul>
                              <li>
                                <span class="post">POST</span>
                                <code>/auth/register</code>
                                <span class="pill open">open</span>
                                <div class="mono">
                                  <p><strong>Body (JSON)</strong></p>
                                  <pre>{
          "email": "user@example.com",
          "password": "at least 8 characters"
        }</pre>
                                  <p><strong>201/200</strong> on success, <strong>409</strong> if email already registered.</p>
                                  <p><strong>curl</strong></p>
                                  <pre>curl -X POST http://localhost:8080/auth/register \\
          -H "Content-Type: application/json" \\
          -d "{\\"email\\": \\"user@example.com\\", \\"password\\": \\"SecretPass1!\\"}"</pre>
                                </div>
                              </li>
                              <li style="margin-top:8px">
                                <span class="post">POST</span>
                                <code>/auth/login</code>
                                <span class="pill open">open</span>
                                <div class="mono">
                                  <p><strong>Body (JSON)</strong></p>
                                  <pre>{
          "email": "user@example.com",
          "password": "SecretPass1!"
        }</pre>
                                  <p><strong>Response</strong></p>
                                  <pre>{
          "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        }</pre>
                                  <p><strong>curl</strong></p>
                                  <pre>curl -X POST http://localhost:8080/auth/login \\
          -H "Content-Type: application/json" \\
          -d "{\\"email\\": \\"user@example.com\\", \\"password\\": \\"SecretPass1!\\"}"</pre>
                                </div>
                              </li>
                            </ul>

                            <div class="hr"></div>
                            <h2>Appointments</h2>
                            <p>Reading is open; creating and deleting require a valid JWT.</p>
                            <ul>
                              <li>
                                <span class="get">GET</span>
                                <a href="/appointments"><code>/appointments</code></a>
                                <span class="pill open">open</span>
                                — all appointments (unsorted).
                              </li>
                              <li>
                                <span class="get">GET</span>
                                <a href="/appointments/upcoming"><code>/appointments/upcoming</code></a>
                                <span class="pill open">open</span>
                                — date ≥ today (ascending).
                              </li>
                              <li>
                                <span class="get">GET</span>
                                <a href="/appointments/previous"><code>/appointments/previous</code></a>
                                <span class="pill open">open</span>
                                — date &lt; today (newest first).
                              </li>
                              <li>
                                <span class="get">GET</span>
                                <code>/appointments/range?start=yyyy-MM-dd&amp;end=yyyy-MM-dd</code>
                                <span class="pill open">open</span>
                                — exclusive range (both bounds excluded).
                                <div class="mono">
                                  <p><strong>Example</strong></p>
                                  <pre>curl "http://localhost:8080/appointments/range?start=2025-08-01&end=2025-08-31"</pre>
                                </div>
                              </li>
                              <li style="margin-top:8px">
                                <span class="post">POST</span>
                                <code>/appointments</code>
                                <span class="pill auth">auth required</span>
                                <div class="mono">
                                  <p><strong>Headers</strong></p>
                                  <pre>Authorization: Bearer &lt;token&gt;
        Content-Type: application/json</pre>
                                  <p><strong>Body (JSON)</strong></p>
                                  <pre>{
          "appointmentId": "A12345",         // 1–10 chars, unique
          "appointmentDate": "2025-08-10",   // yyyy-MM-dd, >= 2000-01-01
          "description": "Checkup"           // 1–50 chars
        }</pre>
                                  <p><strong>201 Created</strong> on success.</p>
                                  <p><strong>curl</strong></p>
                                  <pre>curl -X POST http://localhost:8080/appointments \\
          -H "Authorization: Bearer &lt;token&gt;" \\
          -H "Content-Type: application/json" \\
          -d "{\\"appointmentId\\": \\"A12345\\", \\"appointmentDate\\": \\"2025-08-10\\", \\"description\\": \\"Checkup\\"}"</pre>
                                </div>
                              </li>
                              <li style="margin-top:8px">
                                <span class="del">DELETE</span>
                                <code>/appointments/{id}</code>
                                <span class="pill auth">auth required</span>
                                <div class="mono">
                                  <p><strong>Headers</strong></p>
                                  <pre>Authorization: Bearer &lt;token&gt;</pre>
                                  <p><strong>curl</strong></p>
                                  <pre>curl -X DELETE http://localhost:8080/appointments/A12345 \\
          -H "Authorization: Bearer &lt;token&gt;"</pre>
                                </div>
                              </li>
                              <li style="margin-top:8px">
                                <span class="get">GET</span>
                                <code>/appointments/export?format=csv|json&amp;scope=all|upcoming|previous|range</code>
                                <span class="pill open">open</span>
                                <span class="badge mono">range requires start &amp; end</span>
                                <div class="mono">
                                  <p><strong>Examples</strong></p>
                                  <pre>curl -OJ "http://localhost:8080/appointments/export?format=csv&scope=all"
        curl -OJ "http://localhost:8080/appointments/export?format=json&scope=range&start=2025-08-01&end=2025-08-31"</pre>
                                </div>
                              </li>
                            </ul>

                            <div class="hr"></div>
                            <h2>Validation & Errors</h2>
                            <ul>
                              <li><strong>400 Bad Request</strong> — malformed JSON, invalid dates, or field lengths.</li>
                              <li><strong>401 Unauthorized</strong> — missing/invalid token for protected endpoints.</li>
                              <li><strong>409 Conflict</strong> — duplicate <code>appointmentId</code> or already-registered email.</li>
                              <li><strong>500 Internal Server Error</strong> — unexpected server-side issues.</li>
                            </ul>

                            <div class="hr"></div>
                            <h2>Quick Start</h2>
                            <ol class="mono">
                              <li>Register: <code>POST /auth/register</code> → then <code>POST /auth/login</code> to get a token.</li>
                              <li>Create: <code>POST /appointments</code> with <code>Authorization: Bearer &lt;token&gt;</code>.</li>
                              <li>Browse: <code>GET /appointments</code>, <code>/upcoming</code>, <code>/previous</code>, <code>/range</code>.</li>
                              <li>Export: <code>GET /appointments/export?format=csv&amp;scope=all</code> (or <code>json</code>).</li>
                            </ol>

                            <div class="hr"></div>
                            <p class="mono">Tip: Use Postman or curl for quick exploration. The React app on
                              <code>http://localhost:3000</code> calls these same endpoints.</p>
                          </div>
                        </body>
                        </html>
                        """;
  }
}
