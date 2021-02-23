const csv = require("csv-parser");
const fs = require("fs");

const { Client } = require("@elastic/elasticsearch");
const esClient = new Client({ node: "http://localhost:9200" });
const heroesIndexName = "heroes";

async function run() {
  esClient.indices.create(
    {
      index: heroesIndexName,
    },
    (err, resp) => {
      if (err) {
        console.log(err);
        console.log(err.message);
      }
    }
  );

  let heroes = [];
  fs.createReadStream("all-heroes.csv")
    .pipe(csv())
    .on("data", (data) => {
      heroes.push({
        id: data.id,
        name: data.name,
        description: data.description,
        imageUrl: data.imageUrl,
        backgroundImageUrl: data.backgroundImageUrl,
        externalLink: data.externalLink,
        secretIdentities: data.secretIdentities,
        birthplace: data.birthPlace,
        occupation: data.occupation,
        aliases: data.aliases,
        alignment: data.alignment,
        firstAppearance: data.firstAppearance,
        yearAppearance: data.yearAppearance,
        universe: data.universe,
        gender: data.gender,
        race: data.race,
        type: data.type,
        height: data.height,
        weight: data.weight,
        eyeColor: data.eyeColor,
        hairColor: data.hairColor,
        teams: data.teams,
        powers: data.powers,
        partners: data.partners,
        intelligence: data.intelligence,
        strength: data.strength,
        speed: data.speed,
        durability: data.durability,
        power: data.power,
        combat: data.combat,
        creators: data.creators,
      });
    })
    .on("end", () => {
      esClient.bulk(createBulkInsertQuery(heroes), (err, resp) => {
        if (err) console.trace(err.message);
        else console.log(`Inserted ${resp.body.items.length} heroes`);
        esClient.close();
      });
    });
}

function createBulkInsertQuery(heroes) {
  const body = heroes.reduce((her, hero) => {
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
    } = hero;
    her.push({ index: { _index: "heroes", _type: "_doc", _id: hero.id } });
    her.push({
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
    });
    return her;
  }, []);

  return { body };
}

run().catch(console.error);
