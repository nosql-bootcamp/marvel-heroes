const csv = require('csv-parser');
const fs = require('fs');
const dotenv = require('dotenv');
dotenv.config();

const { Client } = require('@elastic/elasticsearch');
const client = new Client({
    node: `http://${process.env.ELASTICSEARCH_HOST}:${process.env.ELASTICSEARCH_PORT}`
});

const heroesIndexName = 'heroes'
client.indices.create({ index: heroesIndexName }, (err, resp) => {
    if (err) console.trace(err.message);
});

let heroes = [];
fs
    .createReadStream('./all-heroes.csv')
    .pipe(csv())
    .on('data', data => {
        heroes.push(data);
    })
    .on('end', () => {
        client.bulk(createBulkInsertQuery(heroes), (err, resp) => {
            if (err) console.trace(err.message);
            else console.log(`Inserted ${resp.body.items.length} heroes`);
            client.close();
        });
    });

// Fonction utilitaire permettant de formatter les données pour l'insertion "bulk" dans elastic
function createBulkInsertQuery(actors) {
    const body = actors.reduce((acc, hero) => {
        acc.push({ index: { _index: heroesIndexName, _type: '_doc', _id: hero.id } })
        acc.push(hero)
        return acc
    }, []);

    return { body };
}
