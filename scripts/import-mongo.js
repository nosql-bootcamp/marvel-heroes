var mongodb = require("mongodb");
var csv = require("csv-parser");
var fs = require("fs");
const { Server } = require("http");

var MongoClient = mongodb.MongoClient;
var mongoUrl = "mongodb://localhost:27017";
const dbName = "marvel";
const collectionName = "heroes";

importMongo();

async function importMongo() {
  const client = await MongoClient.connect(mongoUrl);
  const db = client.db(dbName);
  if (db.listCollections({ name: collectionName }).hasNext()) {
    await db.dropCollection(collectionName);
  }

  db.createCollection(collectionName);

  const heroesColl = db.collection(collectionName);

  fs.createReadStream("./all-heroes.csv")
    .pipe(csv())
    // Pour chaque ligne on créé un document JSON pour l'acteur correspondant
    .on("data", async (data) => {
      for (const key of [
        "secretIdentities",
        "aliases",
        "teams",
        "powers",
        "partners",
        "creators",
      ]) {
        data[key] = data[key].split(",");
      }

      for (const key of [
        "yearAppearance",
        "height",
        "weight",
        "intelligence",
        "strength",
        "speed",
        "durability",
        "power",
        "combat",
      ]) {
        data[key] = parseInt(data[key]) || "";
      }

      const {
        id,
        name,
        description,
        imageUrl,
        backgroundImageUrl,
        externalLink,
        secretIdentities,
        birthPlace,
        occupation,
        aliases,
        alignment,
        firstAppearance,
        yearAppearance,
        universe,
        gender,
        race,
        type,
        height,
        weight,
        eyeColor,
        hairColor,
        teams,
        powers,
        partners,
        intelligence,
        strength,
        speed,
        durability,
        power,
        combat,
        creators,
      } = data;

      const hero = {
        id,
        name,
        description,
        imageUrl,
        backgroundImageUrl,
        externalLink,
        teams,
        powers,
        partners,
        creators,
      };

      hero["identity"] = {
        secretIdentities,
        birthPlace,
        occupation,
        aliases,
        alignment,
        firstAppearance,
        yearAppearance,
        universe,
      };

      hero["appearance"] = {
        gender,
        race,
        type,
        height,
        weight,
        eyeColor,
        hairColor,
      };

      hero["skills"] = {
        intelligence,
        strength,
        speed,
        durability,
        power,
        combat,
      };

      await heroesColl.insertMany([hero]);
    });
}
