import React, { useContext } from "react";
import { assets } from "../assets/assets";
import { AppContext } from "../context/AppContext";

const Header = () => {
  const { userData } = useContext(AppContext);
  return (
    <div
      className="text-center d-flex flex-column align-items-center justify-content-center py-5 px-3"
      style={{ minHeight: "80vh" }}
    >
      <img src={assets.Header} alt="Header" width={120} className=" mb-4 " />

      <h5 className=" fw-semibold">
        Hey {userData ? userData.name : "Developer"},{" "}
        <span role=" img " aria-label="wave">
          👋 Welcome to Authify!{" "}
        </span>
        <br />
      </h5>

      <h1 className=" fw-bold display-5 mb-3">
        Secure Authentication Made Simple
      </h1>

      <p className="text-muted mb-4" style={{ maxWidth: " 500px" }}>
        Authify provides a secure and easy-to-use authentication system, and can
        setup the authentication flow for your applications in no time!
      </p>
      <button className=" btn  btn-outline-dark rounded-pill px-4 py-2">
        Get Started <i className="bi bi-arrow-right ms-2"></i>
      </button>
    </div>
  );
};

export default Header;
