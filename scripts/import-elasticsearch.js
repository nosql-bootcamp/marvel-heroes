const csv = require('csv-parser');
const fs = require('fs');

const { Client } = require('@elastic/elasticsearch')
const heroesIndexName = 'heroes'


async function run() {
    const client = new Client({ node: 'http://localhost:9200' })

    await client.indices.delete({ index: heroesIndexName })
    await client.indices.create({
        index: heroesIndexName,
    });


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
            while (heroes.length > 0) {
                let smaller = []
                while (smaller.length < 10000 && heroes.length > 0) {
                    smaller.push(heroes.pop());
                }
                let bulk = createBulkInsertQuery(smaller);
                let result = await client.bulk(bulk).catch(console.error)
                if (result.body.errors || result.statusCode != 200) {
                    console.log(result)
                }
            }
            client.close();
        });

}

function createBulkInsertQuery(heroes) {
    const body = heroes.reduce((acc, hero) => {
        acc.push({ index: { _index: heroesIndexName, _type: '_doc', _id: hero.id } });
        acc.push(hero)
        return acc
    }, []);

    return { body };
}

run().catch(console.error);