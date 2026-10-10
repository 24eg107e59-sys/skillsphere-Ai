import { Link } from "react-router-dom";

function NotFound() {
  return (
    <div className="page-shell not-found-page">
      <div className="content-panel empty-state-card">
        <p className="eyebrow">404</p>
        <h1>Page not found</h1>
        <p>
          The page you are looking for does not exist or may have moved.
        </p>
        <Link to="/" className="primary-button">
          Return home
        </Link>
      </div>
    </div>
  );
}

export default NotFound;
