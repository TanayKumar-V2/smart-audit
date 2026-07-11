"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function Home() {
  const router = useRouter();
  const [projectName, setProjectName] = useState("");
  const [contractCode, setContractCode] = useState("");
  const [status, setStatus] = useState({ type: "", message: "" });
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setStatus({ type: "", message: "" });

    try {
      const response = await fetch("/api/v1/audits/submit", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ projectName, contractCode }),
      });

      if (!response.ok) {
        throw new Error("Failed to submit audit request");
      }

      const data = await response.json();
      router.push(`/audit/${data.id}`);
    } catch (error) {
      setStatus({ type: "error", message: error.message || "An unexpected error occurred." });
      setIsLoading(false);
    }
  };

  return (
    <div className="container">
      <main className="hero">
        <h1>SmartAudit</h1>
        <p>AI-Powered Web3 Smart Contract Analysis</p>
        
        <div style={{ marginTop: '3rem', maxWidth: '600px', margin: '3rem auto' }}>
          <div className="glass-panel">
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="projectName">Project Name</label>
                <input
                  id="projectName"
                  type="text"
                  className="form-input"
                  placeholder="e.g. DeFi Exchange"
                  value={projectName}
                  onChange={(e) => setProjectName(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="contractCode">Contract Code (Solidity)</label>
                <textarea
                  id="contractCode"
                  className="form-textarea"
                  placeholder="pragma solidity ^0.8.0; ..."
                  value={contractCode}
                  onChange={(e) => setContractCode(e.target.value)}
                  required
                ></textarea>
              </div>

              <button type="submit" className="btn-primary" disabled={isLoading}>
                {isLoading ? "Analyzing..." : "Start Audit"}
              </button>

              {status.message && (
                <div className={`status-message status-${status.type}`}>
                  {status.message}
                </div>
              )}
            </form>
          </div>
        </div>
      </main>
    </div>
  );
}
