import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import AdminDashboard from "./pages/AdminDashboard";
import Profile from "./pages/Profile";
import SkillGapAnalysis from "./pages/SkillGapAnalysis";
import Roadmap from "./pages/Roadmap";
import Courses from "./pages/Courses";
import Assessments from "./pages/Assessments";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/admin" element={<AdminDashboard />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/skill-gap" element={<SkillGapAnalysis />} />
        <Route path="/roadmap" element={<Roadmap />} />
        <Route path="/courses" element={<Courses />} />
        <Route path="/assessments" element={<Assessments />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;