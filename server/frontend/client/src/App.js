import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

var axios = require('axios');

class App extends Component {
  render() {
    return (
      <div className="App">
        <div className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>Welcome to React!!!</h2>
        </div>
        <p className="App-intro">
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>
      </div>
    );
  }
  componentDidMount () {
    axios({
        method: 'get',
        url: '/get',
        headers: {'Accept': 'application/json'},
    })
    // axios.get('https://1d7f7cfb.ngrok.io/get')
    .then(function (response) {
    console.log(response.data);
  })
  .catch(function (error) {
    console.log(error);
  });
  }
}

export default App;
