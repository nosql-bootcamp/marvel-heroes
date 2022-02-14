const csv = require('csv-parser');
const fs = require('fs');

const { Client } = require('@elastic/elasticsearch')
const esClient = new Client({ node: 'http://localhost:9200' })
// /!\ si vous utiliser une version cloud, la chaîne de connexion devrait ressembler à ça
// const esClient = new Client({
//   node: 'https://<endpoint>:<port>',
//   auth: {
//     username: 'elastic',
//     password: '<password>'
//   }
// });

const heroesIndexName = 'heroes'

// TODO
console.log("TODO ;-)");
