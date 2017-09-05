const express = require('express')
const app = express()
var bodyParser = require("body-parser");

const redis = require('./redistest.js')

const model = require('./model.js')

app.get('/', function (req, res) {
  res.send('Hello World!')
})

// app.use(express.logger());
// app.use(function(req, res, next) {
//     //allows localhost:3000 to communicate with this server
//     // res.header("Access-Control-Allow-Origin", "http://localhost:3000");
//     res.header("Access-Control-Allow-Origin", "https://774b09a6.ngrok.io");
//     res.header("Access-Control-Allow-Credentials", "true");
//     res.header("Access-Control-Allow-Headers", "Origin,Content-Type, Authorization");
//     res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//     next();
// });
// app.get('/ass', function (req, res) {
//   res.send('Hello Ass!')
// })

// app.use(bodyParser.urlencoded({ extended: false }));
// app.use(bodyParser.json());

// app.post('/ass', function (req, res) {
//   //res.send('Add an ass!')
//   console.log(req.body);
//   res.send(req.body);
// })

// app.get('/*', function (req, res) {
//   res.send('Hello')
// })



// var msg = "default";
// redis.exists('string key', function(err, reply) {
//     if (reply === 1) {
//         // console.log('exists');
//         msg = 'wooow';
//     } else {
//         // console.log('doesn\'t exist');
//         msg = 'noooo'
//     }
// });
//
// app.get('/wooow', function (req, res) {
//   res.send(msg);
// });

app.get('/get', function (req, res) {
    redis.get('post', function (err, reply) {
        res.send(reply);
    });
});

//// redis.set("another key", "some string", function(err, reply){
////     // console.log(reply);
////
////     .then((x) => {
////         return model.transferCoin("", "");
////     }).then((x) => {
////     //    ...
////     }).then(())
//// });

app.post('/post', function (req, res) {
    var json_response = JSON.stringify(req.body);
    redis.set('post', json_response, function (err, reply) {
        console.log(json_response);
        var idFrom = JSON.parse(json_response).id;
        var idTo = JSON.parse(json_response).sendToId;
        var amount = JSON.parse(json_response).sendAmount;
        // model.transferCoins(idFrom, idTo, amount);
    });
    res.send(json_response);
});

// function asd(id) {
//     return new Promise((resolve, reject) => {
//         redis.get(id, (err, reply) => {
//             if(err){
//                 reject('Something went wrong');
//             }
//             else{
//                 resolve(reply);
//             }
//         });
//     });
// };
//
// asd('100').then((x) => console.log(x)).catch((x) => console.log(x));
var json =
{
"id":"100",
"name":"pe6o",
"company":"oracle",
"sendToId":"101",
"sendAmount":10
};
json = JSON.parse(JSON.stringify(json));
// model.transferCoins(json)


app.listen(3001, function () {
  console.log('Example app listening on port 3001!')
});

// redis.set('100', '{"barcoins":100}', function (err, reply) {
//     console.log(reply);
// });
// redis.set('101', '{"barcoins":100}', function (err, reply) {
//     console.log(reply);
// });
// redis.get('100', (err, reply) => {
//     console.log(reply);
// });
// redis.get('101', (err, reply) => {
//     console.log(reply);
// });

// redis.get('id100', function (err, reply) {
//     console.log(JSON.parse(reply).barcoins);
// });

// redis.get('id101', function (err, reply) {
//     console.log(JSON.parse(reply).barcoins);
// });

// redis.get('post', function (err, reply) {
//     console.log(JSON.parse(reply).barcoins);
// });

// redis.exists('post', function(err, reply){
//     if (reply) {
//         redis.get('post', function (err, reply) {
//             app.get ('/post', function (req, res) {
//                 res.send (reply);
//             });
//         })
//     }
//     else {
//         app.get ('/post', function (req, res) {
//             res.send ("Nothing here");
//         });
//     }
// });

// if(redis.exists('post', function(err, reply) {
//     if (reply === 1) {
//         var p;
//         redis.get('post', function(err, reply) {
//             p = reply;
//         });
//         app.get('/post', function (req, res) {
//             res.send(p);
//         });
//     } else {
//         app.get('/post', function (req, res) {
//             res.send('Nothing here');
//         });
//     }
// }))
// var redis = require("redis"),
//     client = redis.createClient();
//
// // if you'd like to select database 3, instead of 0 (default), call
// // client.select(3, function() { /* ... */ });
//
// client.on("error", function (err) {
//     console.log("Error " + err);
// });
//
// client.set("string key", "string val", function(err, reply){
//     console.log(reply);
// });
//
// client.get('string key', function(err, reply) {
//     console.log(reply);
// });
// var msg = "default";
// client.exists('string key', function(err, reply) {
//     if (reply === 1) {
//         console.log('exists');
//         msg = 'Wooow';
//     } else {
//         console.log('doesn\'t exist');
//     }
// });
//
// app.get('/wooow', function (req, res) {
//   res.send(msg);
// })
//
// client.quit();
