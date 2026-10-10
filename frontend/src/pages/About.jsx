import { Link } from "react-router-dom";

function About() {
  return (
    <div className="page-shell about-page">
      <header className="topbar">
        <div className="brand-wrap">
          <Link to="/" className="brand">SkillSphere AI</Link>
        </div>
        <nav className="top-links" aria-label="Main navigation">
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
          <Link to="/about">About</Link>
        </nav>
      </header>

      <main className="content-panel about-content">
        <section className="hero-card">
          <p className="eyebrow">About the platform</p>
          <h1>Build skills with clarity, confidence, and data.</h1>
          <p>
            SkillSphere AI helps students compare their current abilities with real-world
            career benchmarks, discover missing skills, and follow a clear learning roadmap.
          </p>
        </section>

        <section className="info-grid">
          <article className="info-card">
            <h2>Career-driven learning</h2>
            <p>
              Students select a target role and the platform highlights the most important
              skill gaps to focus on first.
            </p>
          </article>
          <article className="info-card">
            <h2>Actionable roadmap</h2>
            <p>
              From fundamentals to advanced modules, every step maps to a skill, resource,
              and assessment milestone.
            </p>
          </article>
          <article className="info-card">
            <h2>Transparent progress</h2>
            <p>
              Students can track course completion, assessment results, and readiness trends
              across their personalized learning journey.
            </p>
          </article>
        </section>
      </main>
    </div>
  );
}

export default About;
