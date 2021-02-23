const csv = require('csv-parser');
const fs = require('fs');

const { Client } = require('@elastic/elasticsearch')
const esClient = new Client({ node: 'http://localhost:9200' })
const heroesIndexName = 'marvel'

function createBulkInsertQuery(heroes) {
    const body = heroes.reduce((acc, hero) => {
        acc.push({ index: { _index: heroesIndexName, _type: '_doc', _id: hero.id } })
        acc.push(hero)
        return acc
    }, []);

    return { body };
}


const heroes = [];
fs.createReadStream('./all-heroes.csv')
    .pipe(csv())
    .on('data', data => {
        heroes.push({
            id: data.id,
            name: data.name,
            gender: data.gender,
            universe: data.universe,
            imageUrl: data.imageUrl,
            secretIdentities: data.hasOwnProperty("secretIdentities") && data.secretIdentities !== "" ? data.secretIdentities.split(',').map(x => x.trim()) : [],
            aliases: data.hasOwnProperty("aliases") && data.aliases !== "" ? data.aliases.split(',').map(x => x.trim()) : [],
            description: data.description,
            partners: data.hasOwnProperty("partners") && data.partners !== "" ? data.partners.split(',').map(x => x.trim()) : [],
        })
    })
    .on('end', () => {
        esClient.bulk(createBulkInsertQuery(heroes), (err, resp) => {
            if (err) console.trace(err.message);
            else console.log(`Inserted ${resp.body.items.length} heroes`);
        });
    });

