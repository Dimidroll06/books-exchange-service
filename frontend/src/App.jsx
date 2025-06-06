import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import Header from "./components/Header";
import Footer from "./components/Footer";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Profile from "./pages/Profile";
import MyExchanges from "./pages/MyExchanges";
import AdminPanel from "./pages/AdminPanel";
import { getUser } from "./services/authService";
import Book from "./pages/Book";

console.log(getUser())

function App() {
  return (
    <div
      style={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}
    >
      <Router>
        <Header />
        <div style={{ flex: 1, paddingTop: 64 }}>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/profile/:id" element={<Profile />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/my-exchanges" element={<MyExchanges />} />
            <Route path="/admin" element={<AdminPanel />} />
            <Route path="/book/:id" element={<Book />} />
          </Routes>
        </div>
        <Footer />
      </Router>
    </div>
  );
}

export default App;
