const csv = require('csv-parser')
const fs = require('fs')

const { Client } = require('@elastic/elasticsearch')
const client = new Client({ node: 'http://localhost:9200' })
// /!\ si vous utilisez une version cloud, la chaîne de connexion devrait ressembler à ça
// const esClient = new Client({
//   node: 'https://<endpoint>:<port>',
//   auth: {
//     username: 'elastic',
//     password: '<password>'
//   }
// });

// const ELASTIC_SEARCH_URI = process.env.ELASTIC_SEARCH_URI

const INDEX_NAME = 'heroes'

async function run() {
  // const client = new Client({ node: ELASTIC_SEARCH_URI })

  await client.deleteByQuery({
    index: INDEX_NAME,
    body: { query: { match_all: {} } },
    ignore_unavailable: true
  })

  // console.log(delRes)
  // Drop index if exists
  await client.indices.delete({
    index: INDEX_NAME,
    ignore_unavailable: true
  })

  await client.indices.create({
    index: INDEX_NAME,

    body: {
      // mappings: {
      //   properties: {
      //     yearMonth: { type: 'keyword' },
      //     title: { type: 'keyword' },
      //     twp: { type: 'keyword' },
      //     category: { type: 'keyword' },
      //     location: { type: 'geo_point' },
      //     timeStamp: { type: 'date' }
      //   }
      // }
      // TODO configurer l'index https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html
    }
  })

  const calls = []
  fs.createReadStream('./all-heroes.csv')
    .pipe(csv())
    .on('data', data => {
      // const call = {
      //   ...data,
      // }
      // console.log(call)
      calls.push(data)
    })
    .on('end', async () => {
      // TODO insérer les données dans ES en utilisant l'API de bulk https://www.elastic.co/guide/en/elasticsearch/reference/7.x/docs-bulk.html
      client.bulk(createBulkInsertQuery(calls), (err, resp) => {
        if (err) console.trace(err.message)
        else console.log(`Inserted ${resp.body.items.length} lines`)
        client.close()
      })
    })
}

// Fonction utilitaire permettant de formatter les données pour l'insertion "bulk" dans elastic
function createBulkInsertQuery(rows) {
  const body = rows.reduce((acc, row) => {
    acc.push({ index: { _index: INDEX_NAME /*, _type: '_doc'*/ } })
    // acc.push({ title: row.title, yearMonth: row.yearMonth, twp: row.twp })
    acc.push({ ...row })
    return acc
  }, [])

  return { body }
}

run().catch(console.log)
