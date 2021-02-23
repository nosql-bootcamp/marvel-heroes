const csv = require("csv-parser");
const fs = require("fs");
require("dotenv").config();

const { Client } = require("@elastic/elasticsearch");

const clientOps = {
  cloud: {
    id:
      "IMT-marvel-heroes:dXMtY2VudHJhbDEuZ2NwLmNsb3VkLmVzLmlvJDJhM2RkNmFlMjFjZjQxMzFhN2IzNDJmOWY5OWM2Mzc4JDE2YTBjMzUwNGVhMDRjNjdhYzRjMGEyMjRiOGIwZGNl",
  },
};

if (process.env.ES_USERNAME && process.env.ES_PASSWORD) {
  clientOps["auth"] = {
    username: process.env.ES_USERNAME,
    password: process.env.ES_PASSWORD,
  };
}

const esClient = new Client(clientOps);
const heroesIndexName = "heroes";

importElasticSearch();

async function importElasticSearch() {
  if (esClient.indices.exists({ index: heroesIndexName })) {
    /* Delete index */
    try {
      await esClient.indices.delete({
        index: heroesIndexName,
      });
      console.log(`index ${heroesIndexName} deleted`);
    } catch (err) {
      console.trace(err.message);
    }
  }

  // Création de l'indice
  esClient.indices.create({ index: heroesIndexName }, (err, resp) => {
    if (err) console.trace(err.message);
  });

  let heroes = [];
  fs.createReadStream("./all-heroes.csv")
    .pipe(csv())
    // Pour chaque ligne on créé un document JSON pour l'acteur correspondant
    .on("data", (data) => {
      const hero = (({
        id,
        name,
        aliases,
        secretIdentities,
        description,
        partners,
        imageUrl,
        universe,
        gender,
      }) => ({
        id,
        name,
        aliases,
        secretIdentities,
        description,
        partners,
        imageUrl,
        universe,
        gender,
      }))(data);

      heroes.push(hero);
    })
    // A la fin on créé l'ensemble des acteurs dans ElasticSearch
    .on("end", () => {
      esClient.bulk(createBulkInsertQuery(heroes), (err, resp) => {
        if (err) console.trace(err.message);
        else console.log(`Inserted ${resp.body.items.length} actors`);
        esClient.close();
      });
    });
}

// Fonction utilitaire permettant de formatter les données pour l'insertion "bulk" dans elastic
function createBulkInsertQuery(heroes) {
  const body = heroes.reduce((acc, hero) => {
    acc.push({
      index: { _index: heroesIndexName, _type: "_doc", _id: hero.id },
    });

    acc.push(hero);
    return acc;
  }, []);

  return { body };
}
