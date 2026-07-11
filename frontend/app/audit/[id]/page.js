"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";

export default function AuditResult() {
  const params = useParams();
  const id = params.id;
  const [audit, setAudit] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let interval;
    const fetchAudit = async () => {
      try {
        const response = await fetch(`/api/v1/audits/${id}`);
        if (response.ok) {
          const data = await response.json();
          setAudit(data);
          
          if (data.status === "COMPLETED" || data.status === "FAILED") {
            clearInterval(interval);
          }
        }
      } catch (err) {
        console.error("Error fetching audit:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchAudit();
    // Poll every 3 seconds while pending
    interval = setInterval(() => {
      if (!audit || audit.status === "PENDING") {
        fetchAudit();
      }
    }, 3000);

    return () => clearInterval(interval);
  }, [id, audit?.status]);

  if (loading) {
    return (
      <div className="container">
        <main className="hero">
          <h2>Loading Audit Results...</h2>
        </main>
      </div>
    );
  }

  if (!audit) {
    return (
      <div className="container">
        <main className="hero">
          <h2 className="status-error">Audit Not Found</h2>
          <Link href="/" className="btn-primary" style={{ display: 'inline-block', marginTop: '2rem' }}>
            Go Back
          </Link>
        </main>
      </div>
    );
  }

  return (
    <div className="container">
      <main className="hero" style={{ textAlign: "left", margin: "2rem 0" }}>
        <h1 style={{ fontSize: "2.5rem" }}>Audit Results</h1>
        <p style={{ marginBottom: "2rem" }}>Project: {audit.projectName}</p>
        
        <div className="glass-panel" style={{ marginBottom: "2rem" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.5rem" }}>
            <span style={{ 
              padding: "0.5rem 1rem", 
              borderRadius: "20px", 
              fontWeight: "bold",
              background: audit.status === "COMPLETED" ? "rgba(34, 197, 94, 0.2)" : "rgba(234, 179, 8, 0.2)",
              color: audit.status === "COMPLETED" ? "#4ade80" : "#facc15"
            }}>
              {audit.status}
            </span>
            <span style={{ color: "var(--text-muted)", fontSize: "0.9rem" }}>
              ID: {audit.id}
            </span>
          </div>

          {audit.status === "COMPLETED" ? (
            <>
              <div style={{ marginBottom: "2rem" }}>
                <h3 style={{ marginBottom: "0.5rem", color: "var(--accent)" }}>Vulnerabilities Found</h3>
                <div style={{ fontSize: "2rem", fontWeight: "800" }}>{audit.vulnerabilities}</div>
              </div>
              
              <div>
                <h3 style={{ marginBottom: "1rem", color: "var(--accent)" }}>AI Analysis Report</h3>
                <div style={{ 
                  background: "rgba(0,0,0,0.3)", 
                  padding: "1.5rem", 
                  borderRadius: "12px",
                  whiteSpace: "pre-wrap",
                  lineHeight: "1.6"
                }}>
                  {audit.report}
                </div>
              </div>
            </>
          ) : (
            <div style={{ textAlign: "center", padding: "3rem" }}>
              <div style={{ 
                display: "inline-block", 
                width: "40px", 
                height: "40px", 
                border: "4px solid rgba(255,255,255,0.1)", 
                borderTopColor: "var(--accent)", 
                borderRadius: "50%",
                animation: "spin 1s infinite linear"
              }}></div>
              <p style={{ marginTop: "1rem", color: "var(--text-muted)" }}>AI is analyzing the contract...</p>
              <style>{`
                @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
              `}</style>
            </div>
          )}
        </div>
        
        <div style={{ textAlign: "center" }}>
          <Link href="/" className="btn-primary" style={{ display: 'inline-block', textDecoration: "none" }}>
            Audit Another Contract
          </Link>
        </div>
      </main>
    </div>
  );
}
