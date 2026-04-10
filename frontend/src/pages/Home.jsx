import { Link } from "react-router-dom";
import "./Home.css";

function Home() {
  return (
    <div className="page-wrap">
      <section className="card hero-card">
        <h1>Report a Civic Issue</h1>
        <p>
          CivicReport helps residents submit civic complaints quickly and track progress until
          closure. Each submission receives a unique tracking ID for follow-up.
        </p>
        <div className="hero-actions">
          <Link to="/report" className="hero-btn primary-link">
            Report an Issue
          </Link>
          <Link to="/track" className="hero-btn secondary-link">
            Track My Complaint
          </Link>
        </div>
      </section>

      <section className="card environment-visuals">
        <h2>Clean City Mission</h2>
        <p>
          Complaints are routed quickly so sanitation, roads, and electricity teams can keep neighborhoods
          safe, clean, and healthy.
        </p>
        <div className="visual-grid">
          <article className="visual-card">
            <div className="visual-icon">CLN</div>
            <h3>Clean Streets</h3>
            <p>Sanitation issues are tracked and assigned for timely clearance.</p>
          </article>
          <article className="visual-card">
            <div className="visual-icon">RD</div>
            <h3>Safer Roads</h3>
            <p>Road and transport complaints are monitored from report to fix.</p>
          </article>
          <article className="visual-card">
            <div className="visual-icon">EL</div>
            <h3>Better Lighting</h3>
            <p>Streetlight and electricity complaints are visible with status updates.</p>
          </article>
        </div>
        <div className="flow-diagram">
          <span>Citizen Report</span>
          <span>Zone Review</span>
          <span>Department Action</span>
          <span>Status Update</span>
        </div>
      </section>

      <section className="card how-it-works">
        <h2>How It Works</h2>
        <div className="steps-grid">
          <article className="step-card">
            <span className="step-icon">1</span>
            <h3>Report</h3>
            <p>Submit issue details including area, issue name, and location.</p>
          </article>
          <article className="step-card">
            <span className="step-icon">2</span>
            <h3>Get ID</h3>
            <p>Receive a tracking ID instantly after successful complaint submission.</p>
          </article>
          <article className="step-card">
            <span className="step-icon">3</span>
            <h3>Track Status</h3>
            <p>Check assignment, progress, and final closure using the tracking ID.</p>
          </article>
        </div>
      </section>
    </div>
  );
}

export default Home;
