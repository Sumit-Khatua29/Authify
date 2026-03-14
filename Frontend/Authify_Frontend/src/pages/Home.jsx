import React from 'react'
import Menubar from '../components/Menubar'
import Header from '../components/Header'
import { assets } from '../assets/assets'
const Home = () => {
  return (
    <div className=' flex flex-col items-center min-vh-100 position-relative' style={{ backgroundImage: `url(${assets.BackgroundImage})`, backgroundSize: 'cover', backgroundPosition: 'center', backgroundAttachment: 'fixed' }}>
      <div className='position-absolute top-0 start-0 w-100 h-100' style={{
        background: 'radial-gradient(circle, rgba(255,255,255,0.6) 0%, rgba(255,255,255,0.95) 100%)',
        backdropFilter: 'blur(3px)',
        zIndex: 0
      }}></div>
      <div className='position-relative' style={{ zIndex: 1, width: '100%' }}>
        <Menubar />
        <Header></Header>
      </div>
    </div>
  )
}

export default Home
