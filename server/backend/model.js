const redis = require('./redistest.js')

exports.transferCoins = function (json) {
    var idFrom = getProperty('id', json);
    var idTo = getProperty('sendToId', json);
    var amount = getProperty('sendAmount', json);
    // getCurrentBalance('100').then(x => console.log(x)).catch(x => console.log(x));
    getCurrentBalance(idFrom).then((x) => {
        getCurrentBalance(idTo).then((y) => {
            if(x >= amount && amount >= 0 && idFrom != idTo){
                var from = x - amount;
                from = '{"barcoins":' + from + '}';
                from = JSON.parse(JSON.stringify(from));
                var to = y + amount;
                to = '{"barcoins":' + to + '}';
                to = JSON.parse(JSON.stringify(to));
                console.log(from);
                console.log(to);
                redis.set(idFrom, from, (err, reply) => {
                    // console.log(reply);
                });
                redis.set(idTo, to, (err, reply) => {
                    // console.log(reply);
                });
            }
            else {
                console.log('Insufficient funds');
            }
        });
    });


}

function getProperty (property, json) {
    console.log(json[property]);
    return json[property];
}

function getCurrentBalance (id) {
    return new Promise ((resolve, reject) => {
        redis.get(id, (err, reply) => {
            if(err){
                reject(0);
            }
            else{
                resolve(JSON.parse(reply).barcoins);
            }
        });
    });
}

    // var currentBalanceFrom;
    // var currentBalanceTo;
    // redis.get(idFrom, function (err, reply) {
    //     currentBalanceFrom = JSON.parse(reply).barcoins;
    //     console.log(currentBalanceFrom);
    // });
    // redis.get(idTo, (err, reply) => {
    //     currentBalanceTo = JSON.parse(reply).barcoins;
    //     console.log(currentBalanceTo);
    // });
    //
    // var newBalanceFrom;
    // var newBalanceTo;
    // setTimeout(() => {
    //     if (currentBalanceFrom >= amount && amount >= 0) {
    //         newBalanceFrom = currentBalanceFrom - amount;
    //         newBalanceTo = currentBalanceTo + amount;
    //     }
    // }, 400);
    //
    // // redis.set(idFrom, '{"barcoins":' + newBalanceFrom + '}', (err, reply) => {
    // //     console.log(reply);
    // // });
    // // redis.set(idTo, '{"barcoins":' + newBalanceTo + '}', (err, reply) => {
    // //     console.log(reply);
    // // });
    // redis.get(idFrom, (err, reply) => {
    //     console.log(reply);
    // });
    // redis.get(idTo, (err, reply) => {
    //     console.log(reply);
    // });



    // var parse = JSON.parse(json);
    // var currentBalanceFrom;
    // var currentBalanceTo
    // var amount = parse.sendAmount;
    // redis.get(parse.id, function (err, reply) {
    //     currentBalanceFrom = reply;
    // });
    // redis.get(parse.sendToId, function (err, reply) {
    //     currentBalanceTo = reply;
    // });
    // var newBalanceFrom;
    // var newBalanceTo;
    // if (currentBalanceFrom >= amount && amount >= 0) {
    //     newBalanceFrom = currentBalanceFrom - amount;
    //     newBalanceTo = currentBalanceTo + amount;
    // }
    // redis.set(parse.id, JSON.stringify('{"barcoins":' + newBalanceFrom + '}'), function (err, reply) {
    //     console.log(reply);
    // });
    // redis.set(parse.sendToId, newBalanceFrom, function (err, reply) {
    //     console.log(reply);
    // });
    // redis.get(parse.id, function (err, reply) {
    //     console.log(reply);
    // });
    // redis.get(parse.sendToId, function (err, reply) {
    //     console.log(reply);
    // });
