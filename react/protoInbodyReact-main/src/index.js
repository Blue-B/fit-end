import React, { useEffect, useState } from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import "./index.css";
import reportWebVitals from "./reportWebVitals";
import Register from "./Component/Register";
import Login from "./Component/Login";
import Main from "./Component/Main"; // 메인 화면 컴포넌트
import RecordBody from "./Component/RecordBody";
import RankPage from "./Component/RankPage";
import FoodSearchR from "./Component/FoodSearchR";
import TodoCalender from "./Component/TodoCalender";
import ClickThis from "./Component/ClickThis";
const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/main" element={<Main />} />
        <Route path="/" element={<Login />} />
        <Route path="/recordbody" element={<RecordBody />} />
        <Route path="/rank" element={<RankPage />} />
        <Route path="/food" element={<FoodSearchR />} />
        <Route path="/todo" element={<TodoCalender />} />
        <Route path="/ClickThis" element={<ClickThis />} />
      </Routes>
    </Router>
  );
};

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

reportWebVitals();
