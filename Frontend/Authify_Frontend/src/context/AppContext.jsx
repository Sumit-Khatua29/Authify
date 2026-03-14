import { createContext } from "react";
import { AppConstants } from "../utils/constants";
import { useState, useEffect } from "react";
import axios from "axios";
import { toast } from "react-toastify";
export const AppContext = createContext();
axios.defaults.withCredentials = true;

export const AppContextProvider = (props) => {

    const backendURL = AppConstants.BACKEND_URL;
    const [isLoggedin, setIsLoggedin] = useState(false);
    const [userData, setUserData] = useState(false);

    const getUserData = async () => {
        try {
            const response = await axios.get(backendURL + "/profile");
            if (response.status === 200) {
                setUserData(response.data);
            } else {
                toast.error("Unable to fetch user data");
            }
        } catch (error) {
            if (error.response?.status !== 401) {
                toast.error(error.message);
            }
        }
    }

    const getAuthState = async () => {
        try {
            const response = await axios.get(backendURL + "/isAuthenticated");
            if (response.status === 200) {
                if (response.data) {
                    setIsLoggedin(true);
                    getUserData();
                } else {
                    setIsLoggedin(false);
                    setUserData(false);
                }
            }
        } catch (error) {
            setIsLoggedin(false);
            setUserData(false);
            if (error.response?.status !== 401) {
                toast.error(error.message);
            }
        }
    }

    useEffect(() => {
        getAuthState();
    }, []);

    const contextValue = {
        backendURL,
        isLoggedin,
        setIsLoggedin,
        userData,
        setUserData, getUserData
    }
    return (
        <AppContext.Provider value={contextValue}>
            {props.children}
        </AppContext.Provider>
    )
}