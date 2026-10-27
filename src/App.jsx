import React, { useState, useRef, useEffect } from 'react'
import Hls from 'hls.js'
import './App.css'

function parseM3U(text){
  const lines = text.split('\n')
  const channels = []
  let currentName = ''
  for(let i=0; i<lines.length; i++){
    const line = lines[i].trim()
    if(line.startsWith('#EXTINF')){
      const m = line.match(/,(.*)$/)
      currentName = m ? m[1] : `قناة ${channels.length+1}`
    } else if(line && !line.startsWith('#') && line.startsWith('http')){
      channels.push({ name: currentName, url: line })
      currentName = ''
    }
  }
  return channels
}

export default function App(){
  const [channels, setChannels] = useState([])
  const [current, setCurrent] = useState(null)
  const videoRef = useRef(null)
  const hlsRef = useRef(null)

  const playChannel = (ch) => {
    setCurrent(ch)
    const video = videoRef.current
    if(Hls.isSupported() && ch.url.includes('m3u8')){
      if(hlsRef.current) hlsRef.current.destroy()
      const hls = new Hls()
      hls.loadSource(ch.url)
      hls.attachMedia(video)
      hlsRef.current = hls
    } else {
      video.src = ch.url
    }
    video.play()
  }

  useEffect(()=>{
    const demo = `#EXTM3U
#EXTINF:-1,Tahar TV Demo
https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8`
    setChannels(parseM3U(demo))
  }, [])

  return (
    <div className="app">
      <div className="video-wrapper">
        <video ref={videoRef} controls playsInline className="player"></video>
        {current && <div className="now-playing">{current.name}</div>}
      </div>
      <div className="list">
        {channels.map((ch,i)=>(
          <div key={i} className={`channel ${current?.url===ch.url?'active':''}`} onClick={()=>playChannel(ch)}>
            <span>{i+1}</span><span>{ch.name}</span><span>▶</span>
          </div>
        ))}
      </div>
    </div>
  )
}
