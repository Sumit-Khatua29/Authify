import React, { useState } from "react";
import { Link } from "react-router-dom";
import { assets } from "../assets/assets";
import { toast } from "react-toastify";
import axios from "axios";
import { useContext } from "react";
import { AppContext } from "../context/AppContext";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [isCreateAccount, setIsCreateAccount] = useState(false);
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const { backendURL, setIsLoggedin, getUserData } = useContext(AppContext);
  const navigate = useNavigate();

  const onSubmitHandler = async (e) => {
    e.preventDefault();
    axios.defaults.withCredentials = true;
    setLoading(true);
    try {
      if (isCreateAccount) {
        const response = await axios.post(`${backendURL}/register`, {
          name,
          email,
          password,
        });
        if (response.status === 201) {
          navigate("/");
          toast.success(
            "Account created successfully. Please verify your email before logging in.",
          );
        } else {
          toast.error("Wmail already exists");
        }
      } else {
        const response = await axios.post(`${backendURL}/login`, {
          email,
          password,
        });
        if (response.status === 200) {
          setIsLoggedin(true);
          getUserData();
          navigate("/");
          toast.success("Logged in successfully");
        } else {
          toast.error("Invalid credentials");
        }
      }
    } catch (err) {
      toast.error(err.response?.data?.message || "Something went wrong, or server is unreachable.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className=" position-relative min-vh-100 d-flex justify-content-center align-items-center"
      style={{
        background: "linear-gradient(90deg, #6a5af9, #8268f9)",
        border: "none",
      }}
    >
      <div
        style={{
          position: "absolute",
          top: "20px",
          left: "30px",
          display: "flex",
          alignItems: "center",
        }}
      >
        <Link
          to="/"
          style={{
            display: "flex",
            gap: 10,
            alignItems: "center",
            fontWeight: "bold",
            fontSize: "24px",
            textDecoration: "none",
          }}
        >
          <img
            src={assets.logo}
            alt="Logo"
            className="img-fluid"
            width="32"
            height="32"
          />
          <span className=" fw-bold fs-4 text-light">Authify</span>
        </Link>
      </div>

      <div className=" card p-4" style={{ maxWidth: "400px", width: "100%" }}>
        <h2 className=" text-center mb-4">
          {isCreateAccount ? "Create Account" : "Login to your account"}
        </h2>

        <form onSubmit={onSubmitHandler}>
          {isCreateAccount && (
            <div className=" mb-3">
              <label htmlFor="fullname" className=" form-label">
                Full Name
              </label>
              <input
                type="text"
                id="fullname"
                className="form-control"
                placeholder=" Enter Full Name"
                required
                onChange={(e) => setName(e.target.value)}
                value={name}
              />
            </div>
          )}
          <div className=" mb-3">
            <label htmlFor="email" className=" form-label">
              Email Id
            </label>
            <input
              type="text"
              id="email"
              className="form-control"
              placeholder=" Enter Email"
              required
              onChange={(e) => setEmail(e.target.value)}
              value={email}
            />
          </div>

          <div className=" mb-3">
            <label htmlFor="password" className=" form-label">
              Password
            </label>
            <input
              type="password"
              id="password"
              className="form-control"
              placeholder=" Enter Password"
              required
              onChange={(e) => setPassword(e.target.value)}
              value={password}
            />
          </div>

          <div className=" d-flex justify-content-between mb-3">
            <Link to="/reset-password" className=" text-decoration-none">
              Forgot Password ?
            </Link>
          </div>

          <div>
            <button
              type=" submit "
              className=" btn btn-primary w-100"
              disabled={loading}
            >
              {loading ? "Loading..." : isCreateAccount ? "Sign Up" : "Sign In"}
            </button>
          </div>
        </form>

        <div className=" text-center mt-3">
          <p className=" mb-0">
            {isCreateAccount ? (
              <>
                Already have an account?{"    "}
                <span
                  className=" text-decoration-underline "
                  style={{ cursor: "pointer" }}
                  onClick={() => setIsCreateAccount(false)}
                >
                  Login here
                </span>
              </>
            ) : (
              <>
                Don't have an account?{"    "}
                <span
                  className=" text-decoration-underline "
                  style={{ cursor: "pointer" }}
                  onClick={() => setIsCreateAccount(true)}
                >
                  Sign Up
                </span>
              </>
            )}
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
