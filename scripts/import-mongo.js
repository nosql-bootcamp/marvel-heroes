var mongodb = require("mongodb");
var csv = require("csv-parser");
var fs = require("fs");
const dotenv = require('dotenv');
dotenv.config();

var MongoClient = mongodb.MongoClient;
console.log(process.env.MONGO_PASSWORD);
var mongoUrl = `mongodb+srv://myuser:${process.env.MONGO_PASSWORD}@cluster0.x7sqe.mongodb.net/myFirstDatabase?retryWrites=true&w=majority`;

const dbName = "marvel";
const collectionName = "heroes";
const datafile = "./all-heroes.csv";

const insertHeroes = (db, callback) => {
    const collection = db.collection(collectionName);

    const heroes = [];
    fs.createReadStream(datafile)
        .pipe(csv())
        .on('data', data => {
            heroes.push({
                id: data.id,
                name: data.name,
                description: data.description,
                imageUrl: data.imageUrl,
                backgroundImageUrl: data.backgroundImageUrl,
                externalLink: data.externalLink,
                identity: {
                    secretIdentities: data.secretIdentities.split(',').filter(x => x),
                    birthPlace: data.birthPlace,
                    occupation: data.occupation,
                    aliases: data.aliases.split(',').filter(x => x),
                    alignment: data.alignment,
                    firstAppearance: data.firstAppearance,
                    yearAppearance: data.yearAppearance ? +data.yearAppearance : undefined,
                    universe: data.universe
                },
                appearance: {
                    gender: data.gender,
                    race: data.race,
                    type: data.type,
                    height: data.height ? +data.height : undefined,
                    weight: data.weight ? +data.weight : undefined,
                    eyeColor: data.eyeColor,
                    hairColor: data.hairColor
                },
                teams: data.teams.split(',').filter(x => x),
                powers: data.powers.split(',').filter(x => x),
                partners: data.partners.split(',').filter(x => x),
                skills: {
                    intelligence: data.intelligence ? +data.intelligence : undefined,
                    strength: data.strength ? +data.strength : undefined,
                    speed: data.speed ? +data.speed : undefined,
                    durability: data.durability ? +data.durability : undefined,
                    power: data.power ? +data.power : undefined,
                    combat: data.combat ? +data.combat : undefined
                },
                creators: data.creators.split(',').filter(x => x)
            });
        })
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
    insertHeroes(db, result => {
        console.log(`${result.insertedCount} heroes inserted`);
        client.close();
    });
});