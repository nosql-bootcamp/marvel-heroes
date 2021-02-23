const csv = require("csv-parser");
const fs = require("fs");

const { Client } = require("@elastic/elasticsearch");
const esClient = new Client({ node: "http://localhost:9200" });
const heroesIndexName = "heroes";

// Création de l'indice
esClient.indices.create({ index: heroesIndexName }, (err, resp) => {
  if (err) console.trace(err.message);
});

let heroes = [];
fs.createReadStream("all-heroes.csv")
  .pipe(csv())
  // Pour chaque ligne on créé un document JSON pour l'acteur correspondant
  .on("data", (data) => {
    heroes.push({
      id: data.id,
      name: data.name,
      description: data.description,
      imageUrl: data.imageUrl,
      backgroundImageUrl: data.backgroundImageUrl,
      externalLink: data.externalLink,
      identity: {
        secretIdentities: data.secretIdentities.split(","),
        birthPlace: data.birthPlace,
        occupation: data.occupation,
        aliases: data.aliases.split(","),
        alignment: data.alignment,
        firstAppearance: data.firstAppearance,
        yearAppearance: data.yearAppearance,
        universe: data.universe,
      },
      appearance: {
        gender: data.gender,
        race: data.race,
        type: data.type,
        height: parseInt(data.height),
        weight: parseInt(data.weight),
        eyeColor: data.eyeColor,
        hairColor: data.hairColor,
      },
      teams: data.teams.split(","),
      powers: data.powers.split(","),
      partners: data.partners.split(","),
      skills: {
        intelligence: parseInt(data.intelligence),
        strength: parseInt(data.strength),
        speed: parseInt(data.speed),
        durability: parseInt(data.durability),
        power: parseInt(data.power),
        combat: parseInt(data.combat),
      },
      creators: data.creators.split(","),
    });
  })
  // A la fin on créé l'ensemble des acteurs dans ElasticSearch
  .on("end", () => {
    esClient.bulk(createBulkInsertQuery(heroes), (err, resp) => {
      if (err) console.trace(err.message);
      else console.log(`Inserted ${resp.body.items.length} heroes`);
      esClient.close();
    });
  });

// Fonction utilitaire permettant de formatter les données pour l'insertion "bulk" dans elastic
function createBulkInsertQuery(heroes) {
  const body = heroes.reduce((acc, hero) => {
    const {
      name,
      description,
      imageUrl,
      backgroundImageUrl,
      externalLink,
      identity,
      appearance,
      teams,
      powers,
      partners,
      skills,
      creators,
    } = hero;
    acc.push({
      index: { _index: heroesIndexName, _type: "_doc", _id: hero.id },
    });
    acc.push({
      name,
      description,
      imageUrl,
      backgroundImageUrl,
      externalLink,
      identity,
      appearance,
      teams,
      powers,
      partners,
      skills,
      creators,
    });
    return acc;
  }, []);

  return { body };
}
