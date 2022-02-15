const csv = require('csv-parser');
const fs = require('fs');
const { Readable } = require('stream');
const {
  Client
} = require('@elastic/elasticsearch');

const ELASTIC_SEARCH_URI = 'http://localhost:9200';
const INDEX_NAME = 'heroes';

async function run() {
  const client = new Client({
    node: ELASTIC_SEARCH_URI
  });

  // Drop index if exists
  await client.indices.delete({
    index: INDEX_NAME,
    ignore_unavailable: true
  });

  await client.indices.create({
    index: INDEX_NAME,
    body: {
      // TODO configurer l'index https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html
    }
  });

  let heroes = [];

  fs.createReadStream('./all-heroes.csv')
    .pipe(csv())
    .on('data', data => {
      heroes.push(data);
      // TODO créer l'objet call à partir de la ligne
    })
    .on('end', async () => {
      // TODO insérer les données dans ES en utilisant l'API de bulk https://www.elastic.co/guide/en/elasticsearch/reference/7.x/docs-bulk.html
      const client = new Client({ node: ELASTIC_SEARCH_URI })

      const result = await client.helpers.bulk({
        datasource: Readable.from(heroes),
        onDocument (doc) {
          return {
            index: { _index: INDEX_NAME }
          }
        }
      })

      const {
        body: count
      } = await client.count();
      console.log(count)
    });

}

run().catch(console.log);