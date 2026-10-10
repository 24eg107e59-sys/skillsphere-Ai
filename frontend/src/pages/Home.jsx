
import { Link } from "react-router-dom";
import "./Home.css";

function Home() {
  return (
    <div className="home-page">
      <h1>Welcome to SkillSphere AI</h1>
      <p>Your personalized AI-powered learning platform.</p>

      <Link to="/login">Login</Link>
      {" | "}
      <Link to="/register">Register</Link>
    </div>
  );
}

export default Home;