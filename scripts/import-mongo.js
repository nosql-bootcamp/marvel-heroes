var mongodb = require("mongodb");
var csv = require("csv-parser");
var fs = require("fs");

const MongoClient = mongodb.MongoClient;
var mongoUrl = "mongodb://localhost:27017";
const dbName = "marvel";
const collectionName = "heroes";

const insertHeroes = (db, callback) => {
    var heroesCol = db.collection(collectionName);


    let heroes = [];
    // Read CSV file
    fs.createReadStream('all-heroes.csv')
        .pipe(csv({
            separator: ','
        }))
        .on('data', (data) => {
            heroes.push({
                "id": data.id,
                "name": data.name,
                "description": data.description,
                "imageUrl": data.imageUrl,
                "backgroundImageUrl": data.backgroundImageUrl,
                "externalLink": data.externalLink,
                "identity": {
                    "secretIdentities": data.secretIdentities.split(","),
                    "birthPlace": data.birthPlace,
                    "occupation": data.occupation,
                    "aliases": data.aliases.split(","),
                    "alignment": data.alignment,
                    "firstAppearance": data.firstAppearance,
                    "yearAppearance": data.yearAppearance,
                    "universe": data.universe
                },
                "appearance": {
                    "gender": data.gender,
                    "race": data.race,
                    "type": data.type,
                    "height": data.height,
                    "weight": data.weight,
                    "eyeColor": data.eyeColor,
                    "hairColor": data.hairColor
                },
                "teams": data.teams.split(","),
                "powers": data.powers.split(","),
                "partners": data.partners.split(","),
                "skills": {
                    "intelligence": data.intelligence,
                    "strength": data.strength,
                    "speed": data.speed,
                    "durability": data.durability,
                    "power": data.power,
                    "combat": data.combat
                },
                "creators": data.creators.split(",")
            });
        })
        .on('end', async () => {
            heroesCol.insertMany(heroes, (err, result) => {
                callback(result);
            });
        });
}

async function run() {
    MongoClient.connect(mongoUrl, (err, client) => {
        if (err) {
            console.error(err);
            throw err;
        }
        const db = client.db(dbName);
        insertHeroes(db, result => {
            console.log(`${result.insertedCount} heroes inserted`);
            client.close();
        });
    });
}

run().catch(console.error);