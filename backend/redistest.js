var redis = require("redis");
var client = redis.createClient();

// module.exports = client;
module.exports = redis.createClient();
// if you'd like to select database 3, instead of 0 (default), call
// client.select(3, function() { /* ... */ });

client.on("error", function (err) {
    console.log("Error " + err);
});

// client.set("string key", "string val", function(err, reply){
//     console.log(reply);
// });
//
// client.get('string key', function(err, reply) {
//     console.log(reply);
// });
//
// client.exists('string key', function(err, reply) {
//     if (reply === 1) {
//         console.log('exists');
//     } else {
//         console.log('doesn\'t exist');
//     }
// });
//
// client.exists('another key', function(err, reply) {
//     if (reply === 1) {
//         console.log('exists');
//     } else {
//         console.log('doesn\'t exist');
//     }
// });

client.quit();
