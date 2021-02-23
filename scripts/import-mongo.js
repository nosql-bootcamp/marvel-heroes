var mongodb = require("mongodb");
var csv = require("csv-parser");
var fs = require("fs");

var MongoClient = mongodb.MongoClient;
var mongoUrl = "mongodb://localhost:27017";
const dbName = "marvel";
const collectionName = "heroes";

const insertHeores = (db, callback) => {
    const collection = db.collection(collectionName);

    const heroes = [];
    fs.createReadStream('./all-heroes.csv')
        .pipe(csv())
        // Pour chaque ligne on créé un document JSON pour l'acteur correspondant
        .on('data', data => {
            heroes.push({
                id: data.id,
                name: data.name,
                description: data.description,
                imageUrl: data.imageUrl,
                backgroundImageUrl: data.backgroundImageUrl,
                externalLink: data.externalLink,
                identity: {
                    secretIdentities: data.hasOwnProperty("secretIdentities") ? data.secretIdentities.split(',').map(x => x.trim()) : [],
                    birthPlace: data.birthPlace,
                    occupation: data.occupation,
                    aliases: data.hasOwnProperty("aliases") ? data.aliases.split(',').map(x => x.trim()) : [],
                    alignment: data.alignment,
                    firstAppearance: data.firstAppearance,
                    yearAppearance: parseInt(data.yearAppearance) || 0,
                    universe: data.universe
                },
                appearance: {
                    gender: data.gender,
                    race: data.race,
                    type: data.type,
                    height: parseInt(data.height),
                    weight: parseInt(data.weight),
                    eyeColor: data.eyeColor,
                    hairColor: data.hairColor
                },
                teams: data.hasOwnProperty("teams") ? data.teams.split(',').map(x => x.trim()) : [],
                powers: data.hasOwnProperty("powers") ? data.powers.split(',').map(x => x.trim()) : [],
                partners: data.hasOwnProperty("partners") ? data.partners.split(',').map(x => x.trim()) : [],
                skills: {
                    intelligence: parseInt(data.intelligence) || 0,
                    strength: parseInt(data.strength) || 0,
                    speed: parseInt(data.speed) || 0,
                    durability: parseInt(data.durability) || 0,
                    power: parseInt(data.power) || 0,
                    combat: parseInt(data.combat) || 0
                },
                creators: data.hasOwnProperty("creators") ? data.creators.split(',').map(x => x.trim()) : [],
            });
        })
        // A la fin on créé l'ensemble des acteurs dans MongoDB
        .on('end', () => {
            collection.insertMany(heroes, (err, result) => {
                callback(result);
            });
        });
}

MongoClient.connect(mongoUrl, (err, client) => {
    if (err) {
        console.error(err);
        throw err;
    }
    const db = client.db(dbName);
    insertHeores(db, result => {
        console.log(`${result.insertedCount} heroes inserted`);
        client.close();
    });
});
