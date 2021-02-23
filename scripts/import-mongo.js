var mongodb = require("mongodb");
var csv = require("csv-parser");
var fs = require("fs");

var MongoClient = mongodb.MongoClient;
var mongoUrl = "mongodb://localhost:27017";
const dbName = "marvel";
const collectionName = "heroes";

const heroes = [];

const addHeroes = (db, callback) => {
    fs.createReadStream('all-heroes.csv')
    .pipe(csv())
    .on('data', (data) => heroes.push(data))
    .on('end', () => {
        db.collection(collectionName).insertMany(heroes, (err, res) => {
            callback(res)
        })        
    });
}

    MongoClient.connect(mongoUrl, function (err, client) {
        if (err) {
            throw err;
        }
        
        const db = client.db(dbName);
        addHeroes (db, (res) => {
            console.log("Number of documents inserted: " + res.insertedCount);
            client.close();
        })
    });