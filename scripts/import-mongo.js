var mongodb = require("mongodb");
var csv = require("csv-parser");
var fs = require("fs");

var MongoClient = mongodb.MongoClient;
var mongoUrl = "mongodb://localhost:27017";
const dbName = "marvel";
const collectionName = "heroes";

// TODO
console.log("TODO ;-)");

const dataset = [];

fs.createReadStream('all-heroes.csv')
    .pipe(csv())
    .on('data', (data) => dataset.push(data))
    .on('end', () => {
        console.log(`Imported ${dataset.length} lines`);
        MongoClient.connect(mongoUrl, function (err, db) {
            if (err) throw err;
            var dbo = db.db(dbName);
            dbo.collection(collectionName).insertMany(dataset, function (err, res) {
                if (err) throw err;
                console.log("Number of documents inserted: " + res.insertedCount);
                db.close();
            });
        });
        
    });

