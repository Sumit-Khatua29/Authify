import React, { useContext, useRef, useState } from "react";
import { assets } from "../assets/assets";
import { Link, useNavigate } from "react-router-dom";
import { AppContext } from "../context/AppContext";
import { toast } from "react-toastify";
import axios from "axios";

const EmailVerify = () => {

  const inputRef = useRef([]);

  const [loading, setLoading] = useState(false);

  const { getUserData, isLoggedin, userData, backendURL } = useContext(AppContext);

  const navigate = useNavigate();

  const handleChange = (e, index) => {
    const value = e.target.value.replace(/\D/, "");
    e.target.value = value;
    if (value && index < 5) {
      inputRef.current[index + 1].focus();
    }
  }

  const handleKeyDown = (e, index) => {
    if (e.key === "Backspace" && !e.target.value && index > 0) {
      inputRef.current[index - 1].focus();
    }
  }

  const handlePaste = (e) => {
    e.preventDefault();
    const pasteData = e.clipboardData.getData("text").slice(0, 6).split("");
    pasteData.forEach((digit, i) => {
      if (inputRef.current[i]) {
        inputRef.current[i].value = digit;
      }
    });

    const next = pasteData.length < 6 ? pasteData.length : 5;
    inputRef.current[next].focus();
  }

  const handleVerify = async () => {
    const otp = inputRef.current.map(input => input.value).join("");
    if (otp.length !== 6) {
      toast.error("Please enter a valid 6 digit OTP");
      return;
    }

    setLoading(true);
    try {
      axios.defaults.withCredentials = true;
      const response = await axios.post(backendURL + "/verify-otp", { otp });
      if (response.status === 200) {
        toast.success("Email verified successfully");
        getUserData();
        navigate("/");
      } else {
        toast.error("Invalid OTP");
      }
    } catch (error) {
      toast.error(error.response?.data?.message || "Something went wrong, or server is unreachable.");
    } finally {
      setLoading(false);
    }
  }




  return (
    <div
      className=" email-verify-container d-flex align-items-center justify-content-center vh-100 position-relative"
      style={{
        background: "linear-gradient(90deg, #6a5af9, #8268f9)",
        borderRadius: "0"
      }}
    >
      <Link
        to="/"
        className="position-absolute top-0 start-0, p-4 d-flex align-items-center gap-2 text-decoration-none"
      >
        <img src={assets.logo} alt="logo" height={32} width={32} />
        <span className="fs-4 fw-semibold text-light">Authify</span>
      </Link>

      <div className=" p-5 rounded-4 shadow bg-white" style={{ width: "400px" }}>
        <h4 className=" text-center fw-bold mb-2">Verify Your Email</h4>
        <p className=" text-center text-muted mb-4">
          Enter 6 digit code sent to your email address to verify your account
        </p>

        <div className=" d-flex justify-content-between gap-2 mb-4 text-center text-white-50 mb-2">
          {[...Array(6)].map((_, i) => (
            <input
              key={i}
              type="text"
              maxLength={1}
              className="form-control text-center fs-4 otp-input"
              ref={(el) => (inputRef.current[i] = el)}
              onChange={(e) => handleChange(e, i)}
              onKeyDown={(e) => handleKeyDown(e, i)}
              onPaste={handlePaste}
            />
          ))}
        </div>

        <button className="btn btn-primary w-100 fw-semibold" disabled={loading} onClick={handleVerify}>
          {loading ? "Verifying..." : "Verify Email"}
        </button>
      </div>
    </div>
  );
};

export default EmailVerify;
